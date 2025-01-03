package com.wmccd.whatgoeson.utility.logger

import android.util.Log

interface ILogger {
    fun log(level:Int, tag: String, message: String)
    fun log(level:Int, tag: String, message: String, throwable: Throwable)
}

class Logger: ILogger {
    override fun log(level:Int, tag: String, message: String) {
        val messageWithArrows = ">>> $message"
        when(level){
            Log.VERBOSE -> Log.v(tag, messageWithArrows)
            Log.DEBUG -> Log.d(tag, messageWithArrows)
            Log.INFO -> Log.i(tag, messageWithArrows)
            Log.WARN -> Log.w(tag, messageWithArrows)
            Log.ERROR -> Log.e(tag, messageWithArrows)
            else -> Log.d(tag, messageWithArrows)
        }
    }

    override fun log(level:Int, tag: String, message: String, throwable: Throwable) {
        val messageWithArrows = ">>> $message"
        when(level){
            Log.VERBOSE -> Log.v(tag, messageWithArrows, throwable)
            Log.DEBUG -> Log.d(tag, messageWithArrows, throwable)
            Log.INFO -> Log.i(tag, messageWithArrows, throwable)
            Log.WARN -> Log.w(tag, messageWithArrows, throwable)
            Log.ERROR -> Log.e(tag, messageWithArrows, throwable)
            else -> Log.d(tag, messageWithArrows, throwable)
        }
    }

//    public static final int ASSERT = 7;
//    public static final int DEBUG = 3;
//    public static final int ERROR = 6;
//    public static final int INFO = 4;
//    public static final int VERBOSE = 2;
//    public static final int WARN = 5;

}