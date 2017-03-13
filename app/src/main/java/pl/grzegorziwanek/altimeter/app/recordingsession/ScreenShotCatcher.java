package pl.grzegorziwanek.altimeter.app.recordingsession;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.ShareActionProvider;
import android.view.View;
import android.view.Window;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Grzegorz Iwanek on 12.03.2017.
 */
class ScreenShotCatcher {

    /**
     *
     * @param window
     * @param provider
     * @param resolver
     */
    static void captureAndShare(Window window, ShareActionProvider provider, ContentResolver resolver) {
        Uri uri = saveScreenShotDirectoryLocation(resolver);
        screenShotHandler(resolver, uri, window);
        setShareIntent(getDefaultScreenshotShareIntent(uri), provider);
    }

    /**
     *
     * @param cr
     * @return
     */
    private static Uri saveScreenShotDirectoryLocation(ContentResolver cr) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Session's picture");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     *
     * @param resolver
     * @param uri
     * @param window
     */
    private static void screenShotHandler(ContentResolver resolver, Uri uri, Window window) {
        Bitmap captureView = takeScreenShot(window);

        try {
            int quality = 100;
            OutputStream outputStream = resolver.openOutputStream(uri);
            captureView.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param window
     * @return
     */
    private static Bitmap takeScreenShot(Window window) {
        View view = window.getDecorView();
        view.buildDrawingCache();
        Bitmap bitmap = captureView(view);
        return Bitmap.createBitmap(bitmap, 0, 0, view.getWidth(), view.getHeight());
    }

    /**
     *
     * @param view
     * @return
     */
    private static Bitmap captureView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        if (view.getBackground() != null) {
            Drawable drawable = view.getBackground();
            drawable.draw(canvas);
        }

        view.draw(canvas);

        return bitmap;
    }

    private static Intent getDefaultScreenshotShareIntent(Uri uri) {
        long currTime = System.currentTimeMillis();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Session's picture" + currTime);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Session's picture");

        return intent;
    }

    private static void setShareIntent(Intent shareIntent, ShareActionProvider provider) {
        if (provider != null) {
            provider.setShareIntent(shareIntent);
        }
    }
}
