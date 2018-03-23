/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.onlinedemo;

/**
 * Represents the result of a compilation.
 */
public class CompileResponseDto
{
    public boolean hasFoundError = false;
    public String tsphp;
    public String phpPlus;
    public String console;

    public CompileResponseDto(boolean wereErrorsFound, String theTsphpCode,String thePhpPlusCode, String theConsoleOutput) {
        hasFoundError = wereErrorsFound;
        tsphp = theTsphpCode;
        phpPlus = thePhpPlusCode;
        console = theConsoleOutput;
    }
}
