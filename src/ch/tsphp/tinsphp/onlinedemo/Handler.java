/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.onlinedemo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Handler
{
    private static final int MAX_REQUESTS = 2;
    private static final int NUMBER_OF_WORKERS = 4;

    private final IWorkerPool workerPool;
    private final Map<String, CompileResponseDto> compileResponses = new HashMap<>();

    public Handler(IWorkerPoolFactory workerPoolFactory) {
        workerPool = workerPoolFactory.create(MAX_REQUESTS, compileResponses, NUMBER_OF_WORKERS);
        workerPool.start();
    }

    public void destroy() {
        workerPool.shutdown();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String[] values = request.getParameterValues("php");
        if (values != null && values.length == 1) {
            String tsphp = values[0].trim();
            if (tsphp.length() != 0) {
                if (workerPool.numberOfPendingRequests() < MAX_REQUESTS) {
                    String ticket = UUID.randomUUID().toString();
                    CountDownLatch latch = new CountDownLatch(1);
                    execute(out, latch, new CompileRequestDto(ticket, tsphp, latch));
                } else {
                    out.print("{\"error\": \"Too many requests at the moment. Please try it again in a moment.\"}");
                }
            } else {
                out.print("{\"error\": \"None or more than one TSPHP code defined.\"}");
            }
        } else {
            out.print("{\"error\": \"None or more than one TSPHP code defined.\"}");
        }
    }

    private void execute(PrintWriter out, CountDownLatch latch, CompileRequestDto requestDto) {
        workerPool.execute(requestDto);
        try {
            latch.await();
            CompileResponseDto responseDto = compileResponses.remove(requestDto.ticket);
            out.print("{");
            out.print("\"console\":\"" + jsonEscape(responseDto.console) + "\"");
            if (!responseDto.hasFoundError) {
                out.print(",\"tsphp\":\"" + jsonEscape(responseDto.tsphp) + "\"");
                out.print(",\"phpPlus\":\"" + jsonEscape(responseDto.phpPlus) + "\"");
            }
            out.print("}");
        } catch (InterruptedException e) {
            out.print("{\"error\": \"Exception occurred, compilation was interrupted, "
                    + "please try again.\"}");
        }
    }

    private String jsonEscape(String json) {
        return json.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
