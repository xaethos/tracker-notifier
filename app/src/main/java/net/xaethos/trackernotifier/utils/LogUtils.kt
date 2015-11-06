package net.xaethos.trackernotifier.utils

import net.xaethos.trackernotifier.BuildConfig
import android.util.Log as ALog

const val TAG = "Quicker"

inline fun tryLog(verbosity: Int,
                  tag: String,
                  messageFactory: () -> String) {
    if (ALog.isLoggable(tag, verbosity)) {
        ALog.println(verbosity, tag, messageFactory())
    }
}

inline fun tryLog(verbosity: Int,
                  tag: String,
                  messageFactory: () -> String,
                  tr: Throwable?) {
    if (ALog.isLoggable(tag, verbosity)) {
        ALog.println(verbosity, tag, messageFactory() + '\n' + ALog.getStackTraceString(tr))
    }
}

object Log {

    inline fun debug(tag: String = TAG, messageFactory: () -> String) =
            if (BuildConfig.DEBUG) ALog.d(tag, messageFactory()) else -1

    inline fun v(tag: String = TAG, messageFactory: () -> String) =
            tryLog(ALog.VERBOSE, tag, messageFactory)

    inline fun d(tag: String = TAG, messageFactory: () -> String) =
            tryLog(ALog.DEBUG, tag, messageFactory)

    inline fun i(tag: String = TAG, messageFactory: () -> String) =
            tryLog(ALog.INFO, tag, messageFactory)

    inline fun w(tr: Throwable?, tag: String = TAG, messageFactory: () -> String) =
            tryLog(ALog.WARN, tag, messageFactory, tr)

    inline fun e(tr: Throwable?, tag: String = TAG, messageFactory: () -> String) =
            tryLog(ALog.ERROR, tag, messageFactory, tr)

}
