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
 * Consists class responsible for taking care of screenshot share action calculations and operations.
 * Local range, is used only within {@link RecordingSessionFragment}
 */
class ScreenShotCatcher {

    /**
     * Captures current screen view as a screenshot.
     * @param window window of the activity which view is about to capture;
     * @param provider share action provider connected to menu item from a menu list (e.g. button "share");
     * @param resolver activity's (which view is captured) content resolver;
     */
    static void captureAndShare(Window window, ShareActionProvider provider, ContentResolver resolver) {
        Uri uri = saveScreenShotDirectoryLocation(resolver);
        screenShotHandler(resolver, uri, window);
        setShareIntent(getDefaultScreenshotShareIntent(uri), provider);
    }

    /**
     * Generates uri with picture directory location.
     * @param resolver activity's (which view is captured) content resolver;
     * @return uri to device's storage with captured picture
     */
    private static Uri saveScreenShotDirectoryLocation(ContentResolver resolver) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Session's picture");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * Capture screenshot and save it in external storage of the device.
     * @param resolver activity's (which view is captured) content resolver;
     * @param uri uri to device's storage with captured picture;
     * @param window window of the activity which view is about to capture;
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
     * Takes screenshot of given window's view.
     * @param window window of the activity which view is about to capture;
     * @return bitmap of view adjusted to the size of the view;
     */
    private static Bitmap takeScreenShot(Window window) {
        View view = window.getDecorView();
        view.buildDrawingCache();
        Bitmap bitmap = captureView(view);
        return Bitmap.createBitmap(bitmap, 0, 0, view.getWidth(), view.getHeight());
    }

    /**
     * Captures view of current window. Draws view's content on canvas and bitmap.
     * @param view view of current activity's window;
     * @return bitmap with given view's content;
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

    /**
     * Generates screenshot picture ACTION_SEND intent.
     * @param uri uri to device's storage with captured picture;
     * @return ACTION_SEND intent with captured picture;
     */
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

    /**
     * Executes screenshot share action.
     * @param shareIntent ACTION_SEND screenshot share intent;
     * @param provider share action provider connected to menu item from a menu list (e.g. button "share");
     */
    private static void setShareIntent(Intent shareIntent, ShareActionProvider provider) {
        if (provider != null) {
            provider.setShareIntent(shareIntent);
        }
    }
}
