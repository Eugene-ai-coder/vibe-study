package com.example.vibestudy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {

    private static final DateTimeFormatter ID_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final AtomicLong ID_COUNTER = new AtomicLong(0);

    public static final LocalDateTime MAX_DT =
            LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    private IdGenerator() {}

    public static String generate(String prefix) {
        long seq = ID_COUNTER.incrementAndGet() % 1000;
        return prefix + LocalDateTime.now().format(ID_FORMATTER) + String.format("%03d", seq);
    }
}
