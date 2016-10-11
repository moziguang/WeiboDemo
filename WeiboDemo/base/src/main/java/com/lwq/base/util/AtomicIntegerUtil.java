package com.lwq.base.util;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * Description : 统一AtomicInteger计数
 *
 * Creation    : 2016-10-11
 * Author      : moziguang@126.com
 */
public class AtomicIntegerUtil {
    private static final AtomicInteger SERIAL = new AtomicInteger(0);

    public static int getAtomicInteger()
    {
        return SERIAL.incrementAndGet();
    }
}
