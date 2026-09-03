package com.chandan.enterprise_banking_transaction_system.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public final class TransactionNumberGenerator {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate() {
        String timestamp = LocalDateTime.now().format(FORMATTER);   // millisecond precision
        int seq = SEQUENCE.updateAndGet(n -> (n + 1) % 10000);      // per-JVM atomic counter
        int rand = RANDOM.nextInt(100);                              // extra entropy

        return String.format("TXN%s%04d%02d", timestamp, seq, rand);
    }
}
