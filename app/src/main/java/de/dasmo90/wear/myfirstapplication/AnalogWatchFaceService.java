package de.dasmo90.wear.myfirstapplication;

import static de.dasmo90.wear.myfirstapplication.MainActivity.SHARED_PREF_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.view.SurfaceHolder;
import java.lang.ref.WeakReference;

public class AnalogWatchFaceService extends CanvasWatchFaceService {
    static final int MSG_UPDATE_TIME = 0;
    static final int INTERACTIVE_UPDATE_RATE_MS = 1000;

    @Override
    public Engine onCreateEngine() {
        /* provide your watch face implementation */
        return new Engine();
    }

    /* implement service callback methods */
    private class Engine extends CanvasWatchFaceService.Engine {


        // handler to update the time once a second in interactive mode
        final Handler updateTimeHandler = new UpdateTimeHandler(new WeakReference<>(this));
        final Paint black = new Paint();
        final SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            black.setColor(Color.BLACK);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            /* get device features (burn-in, low-bit ambient) */
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            /* the time changed */
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            /* the wearable switched between modes */
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long frameStartTimeMs = SystemClock.elapsedRealtime();

            // Drawing code here
            canvas.drawRect(bounds, black);
            int second = Calendar.getInstance().get(Calendar.SECOND);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            int hour = Calendar.getInstance().get(Calendar.HOUR);

            int radius = bounds.centerX();
            WatchDrawer watchDrawer = new WatchDrawer(canvas);
            watchDrawer.drawTicks(radius);
            watchDrawer.drawPointer(second, 60, radius, .5f, .5f);
            watchDrawer.drawPointer(minute, 60, radius, .25f, 1.f);
            watchDrawer.drawPointer((hour - 1) % 12 + 1, 12, radius, .125f, 1.f);

            if (shouldTimerBeRunning()) {
                long delayMs = SystemClock.elapsedRealtime() - frameStartTimeMs;
                if (delayMs > INTERACTIVE_UPDATE_RATE_MS) {
                    // This scenario occurs when drawing all of the components takes longer than an actual
                    // frame. It may be helpful to log how many times this happens, so you can
                    // fix it when it occurs.
                    // In general, you don't want to redraw immediately, but on the next
                    // appropriate frame (else block below).
                    delayMs = 0;
                } else {
                    // Sets the delay as close as possible to the intended framerate.
                    // Note that the recommended interactive update rate is 1 frame per second.
                    // However, if you want to include the sweeping hand gesture, set the
                    // interactive update rate up to 30 frames per second.
                    delayMs = INTERACTIVE_UPDATE_RATE_MS - delayMs;
                }
                updateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            // Whether the timer should be running depends on whether we're visible and
            // whether we're in ambient mode, so we may need to start or stop the timer
            updateTimer();
        }
        private void updateTimer() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                updateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }
    }

    private static class UpdateTimeHandler extends Handler {
        private WeakReference<Engine> engineReference;

        UpdateTimeHandler(WeakReference<Engine> engine) {
            this.engineReference = engine;
        }

        @Override
        public void handleMessage(Message message) {
            Engine engine = engineReference.get();
            if (engine != null) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        engine.invalidate();
                        if (engine.shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        }
    }
}