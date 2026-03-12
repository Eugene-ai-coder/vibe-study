package com.example.vibestudy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public final class WfIdGenerator {

    private static final DateTimeFormatter ID_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final AtomicLong ID_COUNTER = new AtomicLong(0);

    private WfIdGenerator() {}

    public static String generate(String prefix) {
        long seq = ID_COUNTER.incrementAndGet() % 1000;
        return prefix + LocalDateTime.now().format(ID_FORMATTER) + String.format("%03d", seq);
    }
}
