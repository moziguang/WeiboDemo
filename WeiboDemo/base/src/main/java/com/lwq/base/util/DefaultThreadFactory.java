package com.lwq.base.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Description : Demo to show the weibo data
 *
 * Creation    : 2016-10-11
 * Author      : moziguang@126.com
 */
public class DefaultThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final int threadPriority;

    public DefaultThreadFactory( String threadNamePrefix) {
        this(Thread.NORM_PRIORITY,threadNamePrefix);
    }

    public DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
        this.threadPriority = threadPriority;
        group = Thread.currentThread().getThreadGroup();
        namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()) t.setDaemon(false);
        t.setPriority(threadPriority);
        return t;
    }
}