/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.onlinedemo;

import java.util.concurrent.CountDownLatch;

/**
 * Represents a compilation requests where the CountDownLatch is used as async completion indicator.
 */
public class CompileRequestDto
{
    public String ticket;
    public String php;
    public CountDownLatch latch;

    public CompileRequestDto(String theTicketNumber, String thePhpCode, CountDownLatch theLatch) {
        ticket = theTicketNumber;
        php = thePhpCode;
        latch = theLatch;
    }
}
