/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.onlinedemo;

import antlr.RecognitionException;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.tinsphp.common.ICompiler;
import ch.tsphp.tinsphp.common.issues.EIssueSeverity;
import ch.tsphp.tinsphp.config.HardCodedTinsInitialiser;
import ch.tsphp.tinsphp.translators.tsphp.config.HardCodedTSPHPTranslatorInitialiser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class Worker implements IWorker  {
    private static final Object LOG_LOCK = new Object();
    private static final Object EXCEPTION_LOCK = new Object();

    private final Map<String, CompileResponseDto> compileResponses;
    private final ICompiler compiler;
    private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    private final ExecutorService executorService;

    private final File requestsLog;
    private final File exceptionsLog;

    private CountDownLatch compilerLatch;
    private StringBuilder stringBuilder = new StringBuilder();

    private String tsphp;
    private boolean busy = true;

    public Worker(Map<String, CompileResponseDto> theCompileResponses,
                  File theRequestsLog, File theExceptionsLog) {
        compileResponses = theCompileResponses;
        requestsLog = theRequestsLog;
        exceptionsLog = theExceptionsLog;

        executorService = Executors.newSingleThreadExecutor();
        HardCodedTinsInitialiser hardCodedTinsInitialiser = new DemoTinsInitialiser(executorService);
        compiler = hardCodedTinsInitialiser.getCompiler();
        compiler.registerCompilerListener(this);
        compiler.registerIssueLogger(this);
    }

    @Override
    public void shutdown() {
        busy = false;
        executorService.shutdown();
    }

    @Override
    public void compile(CompileRequestDto dto) {
        try {
            stringBuilder = new StringBuilder();
            compilerLatch = new CountDownLatch(1);
            compiler.reset();
            tsphp = dto.php;
            compiler.addCompilationUnit("web", tsphp);
            writeToFile(LOG_LOCK, requestsLog, tsphp);
            compiler.compile();
            try {
                compilerLatch.await();
                if (!compiler.hasFound(EnumSet.of(EIssueSeverity.FatalError, EIssueSeverity.Error))) {
                    Map<String, String> translations = compiler.getTranslations();
                    String tsphpOutput = translations.get(HardCodedTSPHPTranslatorInitialiser.class.getCanonicalName() + "_web");
                    String phpPlusOutput = translations.get(PhpPlusTranslatorInitialiser.class.getCanonicalName() + "_web");
                    compileResponses.put(dto.ticket, new CompileResponseDto(false,
                            tsphpOutput,
                            phpPlusOutput,
                            stringBuilder.toString()));
                } else {
                    compileResponses.put(dto.ticket, new CompileResponseDto(true, "", "", stringBuilder.toString()));
                }
            } catch (InterruptedException e) {
                compileResponses.put(dto.ticket, new CompileResponseDto(true, "","", "Unexpected exception occurred, "
                        + "compilation was interrupted, please try again."));
            }
        } catch (RejectedExecutionException ex) {
            //if shutdown has not yet occurred it is an error, otherwise it is fine
            if (busy) {
                String txt = tsphp
                        + "\n---------------------------------------------------------------------------------------\n"
                        + "Unexpected shutdown";
                writeToFile(EXCEPTION_LOCK, exceptionsLog, txt);
            }
        }
        dto.latch.countDown();
    }

    private void writeToFile(Object lock, File file, String txt) {
        synchronized (lock) {
            if (file.exists()) {
                OutputStreamWriter writer = null;
                try {
                    writer = new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.ISO_8859_1);
                    writer.append(dateFormat.format(new Date()))
                            .append(" ----------------------------------------------------------------------\n")
                            .append(txt)
                            .append("\n");
                    writer.close();
                } catch (IOException e) {
                    //that's bad but we don't care
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException e1) {
                            //that's fine
                        }
                    }
                }
            }
        }
    }

    @Override
    public void afterParsingAndDefinitionPhaseCompleted() {
        stringBuilder.append(dateFormat.format(new Date())).append(": Parsing and Definition phase completed\n");
        stringBuilder.append("----------------------------------------------------------------------\n");
    }

    @Override
    public void afterReferencePhaseCompleted() {
        stringBuilder.append(dateFormat.format(new Date())).append(": Reference phase completed\n");
        stringBuilder.append("----------------------------------------------------------------------\n");
    }

    @Override
    public void afterTypecheckingCompleted() {
        stringBuilder.append(dateFormat.format(new Date())).append(": Inference phase completed\n");
        stringBuilder.append("----------------------------------------------------------------------\n");
    }

    @Override
    public void afterCompilingCompleted() {
        stringBuilder.append(dateFormat.format(new Date())).append(": Compilation completed\n");
        compilerLatch.countDown();
    }

    @Override
    public void log(TSPHPException exception, EIssueSeverity severity) {
        stringBuilder.append(dateFormat.format(new Date())).append(": [").append(severity).append("] ").append(exception.getMessage()).append("\n");
        Throwable throwable = exception.getCause();
        if (throwable != null && !(throwable instanceof RecognitionException)) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            String txt = tsphp
                    + "\n------------------------------------------------------------------------------------------\n"
                    + stringWriter.toString();
            writeToFile(EXCEPTION_LOCK, exceptionsLog, txt);
            stringBuilder.append(stringWriter.toString());
        }
    }
}
