package main.utilities;

import android.util.Log;
import main.app.App;

public class LogHelper {
    Class class_;

    public LogHelper(Class<?> class_) {
        this.class_ = class_;
    }

    public static void e(Class<?> class_, String message) {
        if (App.IS_DEBUGGING_MODE)
            Log.e(class_.getName(), toMessage(message));
    }

    public static void d(Class<?> class_, String message) {
        if (App.IS_DEBUGGING_MODE)
            Log.d(class_.getName(), toMessage(message));
    }

    public static void w(Class<?> class_, String message) {
        if (App.IS_DEBUGGING_MODE)
            Log.w(class_.getName(), toMessage(message));
    }

    public static void v(Class<?> class_, String message) {
        if (App.IS_DEBUGGING_MODE)
            Log.v(class_.getName(), toMessage(message));
    }

    public void e(String message) {
        if (App.IS_DEBUGGING_MODE)
            Log.e(class_.getName(), toMessage(message));
    }

    public void d(String message) {
        if (App.IS_DEBUGGING_MODE)
            Log.d(class_.getName(), message);
    }

    public void w(String message) {
        if (App.IS_DEBUGGING_MODE)
            Log.w(class_.getName(), message);
    }

    public void v(String message) {
        if (App.IS_DEBUGGING_MODE)
            Log.v(class_.getName(), toMessage(message));
    }

    private static String toMessage(String message) {
        return message == null ? "" : message;
    }

}
