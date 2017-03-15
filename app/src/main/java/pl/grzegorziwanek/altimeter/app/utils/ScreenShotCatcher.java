package pl.grzegorziwanek.altimeter.app.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;

import com.google.android.gms.maps.GoogleMap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import pl.grzegorziwanek.altimeter.app.BasicActivity;
import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.map.MapFragment;
import pl.grzegorziwanek.altimeter.app.recordingsession.RecordingSessionFragment;

/**
 * Created by Grzegorz Iwanek on 12.03.2017.
 * Consists class responsible for taking care of screenshot share action calculations and operations.
 * Used only within {@link RecordingSessionFragment} {@link MapFragment}
 */
public class ScreenShotCatcher {
    /**
     * Captures current screen view as a screenshot. Case with regular views, not Google Map.
     * @param window window of the activity which view is about to capture;
     * @param resolver activity's (which view is captured) content resolver;
     * @param textViewContent text view's texts required to created message in share action;
     * @return returns share intent with screenshot, and message if text view's content has been provided;
     */
    public static Intent captureAndShare(Window window, ContentResolver resolver, String[] textViewContent) {
        return captureAndShare(window, resolver, textViewContent, null);
    }

    /**
     * Captures current screen view as a screenshot. Case with regular views, not Google Map.
     * @param window window of the activity which view is about to capture;
     * @param resolver activity's (which view is captured) content resolver;
     * @param textViewContent text view's texts required to created message in share action;
     * @param currentMap google map object which map screen is going to be captured;
     * @return returns share intent with screenshot, and message if text view's content has been provided;
     */
    public static Intent captureAndShare(Window window, ContentResolver resolver,
                                         @Nullable String[] textViewContent, @Nullable GoogleMap currentMap) {
        String message = FormatAndValueConverter.buildMessage(textViewContent);
        Uri uri = saveScreenShotDirectoryLocation(resolver);
        screenShotHandler(resolver, uri, window, currentMap);
        return getDefaultScreenshotShareIntent(uri, message);
    }

    /**
     * Generates uri with picture's directory location.
     * @param resolver activity's (which view is being captured) content resolver;
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
    private static void screenShotHandler(ContentResolver resolver,
                                          Uri uri, Window window, @Nullable GoogleMap currentMap) {
        if (currentMap != null) {
            captureGoogleMap(resolver, uri, window, currentMap);
        } else {
            captureScreenshotOfView(resolver, uri, window, null);
        }
    }

    private static void captureScreenshotOfView(ContentResolver resolver,
                                                Uri uri, Window window, @Nullable Bitmap mapSnapshot) {
        Bitmap captureView = takeScreenShot(window);

        if (mapSnapshot != null) {
            captureView = mergeMapAndView(captureView, mapSnapshot, window);
        }

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
     * Uses xml id drawer_layout to get content view without redundant device window bars; instead of window.getDecorView().
     * All activities's layouts hold at least one DrawerLayout with id drawer_layout.
     * @param window window of the activity which view is about to capture;
     * @return bitmap of view adjusted to the size of the view;
     */
    private static Bitmap takeScreenShot(Window window) {
        View view = window.findViewById(R.id.drawer_layout);
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

    private static Bitmap mergeMapAndView(Bitmap viewSnapshot, Bitmap mapSnapshot, Window window) {
        Bitmap result = Bitmap.createBitmap(viewSnapshot.getWidth(),
                viewSnapshot.getHeight(), viewSnapshot.getConfig());
        float offsetY = window.findViewById(R.id.contentFrame).getY();
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(viewSnapshot, 0, 0, null);
        canvas.drawBitmap(mapSnapshot, 0, offsetY, null);
        return result;
    }

    /**
     * Generates screenshot picture ACTION_SEND intent.
     * @param uri uri to device's storage with captured picture;
     * @return ACTION_SEND intent with captured picture;
     */
    private static Intent getDefaultScreenshotShareIntent(Uri uri, String message) {
        long currTime = System.currentTimeMillis();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Session's picture" + currTime);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        return intent;
    }

    private static void captureGoogleMap(final ContentResolver resolver, final Uri uri, final Window window, GoogleMap currentMap) {
            GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap snapshot) {
                    try {
                        captureScreenshotOfView(resolver, uri, window, snapshot);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            currentMap.snapshot(callback);
    }
}
