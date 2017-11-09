package com.jlc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lokesh
 */

public class TestCounter {

    private static final AtomicInteger count = new AtomicInteger();

    public static void clear() {
        count.set(0);
    }

    public static int increment() {
        return count.incrementAndGet();
    }

    public static int get() {
        return count.get();
    }

}
