package com.lwq.base.util;

/**
 * Created by Luoweiqiang on 15/06/08.
 */
public class StatusCodeDef {
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;

    public static final int HTTP_OK = 200;
    public static final int ERROR_NO_NETWORK = 10000;
    public static final int ERROR_CONNECTION_TIMEOUT = 10001;
    public static final int ERROR_READ_TIMEOUT = 10002;
    public static final int ERROR_READ_LOCAL_FILE_ERROR = 10003;
    public static final int ERROR_WRITE_DATA_TO_SERVICE_ERROR = 10004;
    public static final int ERROR_WRITE_DATA_TO_LOCAL_CACHE = 10005;
    public static final int ERROR_HANDLE_DATA_ERROR = 10006;
    public static final int ERROR_SOCKET_EXCEPTION = 10007;
    public static final int ERROR_URISYNTAXEXCEPTION = 10008;
    public static final int ERROR_UNKNOWN_HOST = 10009;
    public static final int ERROR_NOT_LOGINED = 10010;
    public static final int ERROR_OTHER_EXCEPTION = 10011;
    public static final int ERROR_ADD_HTTP_REQUEST = 10012;//添加HTTP请求到队列失败
    public static final int ERROR_HANDLE_DATA_UNSUPPORTED_ENCODING = 10013;

    //DB STATUS
    public static final int READ_WRITE_DB_NOT_FOUND = 10200;
    public static final int INSERT_FAIL_WITH_ERROR = 10201;
    public static final int UPDATE_FAIL_WITH_ERROR = 10202;
    public static final int DELETE_FAIL_WITH_ERROR = 10203;
}
