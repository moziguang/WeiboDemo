package com.lwq.base.util;


import com.lwq.base.BuildConfig;

/*
 * Description : log config
 *
 * Creation    : 2016-10-11
 * Author      : moziguang@126.com
 */
public class LogConfig {
        public static final String DEFAULT_FILE_SUFFIX = "log";

        public String filePrefix;           // log文件名前缀
        public String fileSuffix;           // log文件名后缀
        //        public String fileDir;              // log文件目录，绝对路径
        public String filePath;             // log文件绝对路径，指定这个之后fileDir被忽略
        //public boolean useFileLock;         // log文件被多进程同时写的话，需要设为true
        public Log.LogFilePolicy policy;
        public Log.LogLevel outputLevel;        // 输出级别，大于等于此级别的log才会输出
        public Log.LogLevel fileLevel;          // 输出到文件的级别，大于等于此级别的log才会写入文件
        public int fileFlushCount;          // 每次累计log超过此条数时，会检查是否需要flush log文件
        public int fileFlushInterval;       // 定时每隔一定秒数检查是否需要flush log文件
        public int fileFlushMinInterval;    // 距离上次flush最少需要多少秒
        //public boolean isFileCreator;       // 多进程读写时使用，log文件创建者进程设为true
        public int fileKeepDays;            // Log文件保留天数，启动时检查

        public LogConfig() {
            policy = Log.LogFilePolicy.PerLaunch;
            outputLevel = Log.LogLevel.Verbose;
            if (BuildConfig.DEBUG) {
                fileLevel = Log.LogLevel.Debug;
            } else {
                fileLevel = Log.LogLevel.Info;
            }

            fileFlushCount = 10;
            fileFlushInterval = 10;
            fileFlushMinInterval = 10;
            //isFileCreator = true;
            fileKeepDays = 5;
            fileSuffix = DEFAULT_FILE_SUFFIX;
        }

        public LogConfig(LogConfig cfg) {
            this.filePrefix = cfg.filePrefix;
            this.fileSuffix = cfg.fileSuffix;
//            this.fileDir = cfg.fileDir;
            this.filePath = cfg.filePath;
            //this.useFileLock = cfg.useFileLock;
            this.policy = cfg.policy;
            this.outputLevel = cfg.outputLevel;
            this.fileLevel = cfg.fileLevel;
            this.fileFlushCount = cfg.fileFlushCount;
            this.fileFlushInterval = cfg.fileFlushInterval;
            this.fileFlushMinInterval = cfg.fileFlushMinInterval;
            //this.isFileCreator = cfg.isFileCreator;
            this.fileKeepDays = cfg.fileKeepDays;
        }
    }