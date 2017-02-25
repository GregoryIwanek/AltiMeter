package pl.grzegorziwanek.altimeter.app.data;

import android.os.Handler;

/**
 * Created by Grzegorz Iwanek on 22.02.2017.
 */

public class StaticHandler extends Handler{
    private static StaticHandler handler;
    public static StaticHandler getInstance() {
        if (handler == null) {
            handler = new StaticHandler();
        }
        return handler;
    }
}
