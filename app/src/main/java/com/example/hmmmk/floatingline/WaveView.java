package com.example.hmmmk.floatingline;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by hmmmk___ on 15.11.2017.
 */

public class WaveView extends View {

    private Context context;

    private static Float kDefaultFrequency = 1.5f;
    private static Float kDefaultAmplitude = 1.0f;
    private static Float kDefaultIdleAmplitude = 0.01f;
    private static Float kDefaultNumberOfWaves = 5.0f;
    private static Float kDefaultPhaseShift = -0.15f;
    private static Float kDefaultDensity = 5.0f;
    private static Float kDefaultPrimaryLineWidth = 5.0f;
    private static Float kDefaultSecondaryLineWidth = 3.0f;

    private int waveColor = Color.WHITE;
    private float frequency;
    private float amplitude;
    private float idleAmplitude;
    private float numberOfWaves;
    private float phaseShift;
    private float density;
    private float primaryWaveLineWidth;
    private float secondaryWaveLineWidth;
    private float phase;

    private float strokeLineWidth;

    private static final String TAG = "Float view";

    private int screenHeight;
    private int screenWidth;

    private float oldLevel;
    private float newLevel;

    private int ANIMATION_TOP_BOUND = 100;
    private int ANIMATION_BOTTOM_BOUND = 50;

    private LinkedList<Float> vals = new LinkedList<>();

    public WaveView(Context c) {
        super(c);

        context = c;
        setUp();
    }
    
    public WaveView(Context c, AttributeSet set) {
        super(c, set);

        context = c;
        setUp();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWave(canvas);
    }

    private boolean isStarted = false;

    public void start() {
        if (!isStarted) {
            final DrawHelper dh = new DrawHelper();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    /*updateWithLevel(5.1f);
                    ((Activity) context).runOnUiThread(dh);*/

                    do {
                        if (vals.peekFirst() != null) {
                            //updateWithLevel(vals.pollFirst());
                            dh.setLevel(vals.pollFirst());
                        } else
                            dh.setLevel(oldLevel /*- 0.01f*/);
                            //updateWithLevel(oldLevel - 0.02f);

                        ((Activity) context).runOnUiThread(dh);

                        try {
                            Thread.sleep(17);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (true);
                }
            }).start();

            isStarted = true;
        }
    }

    ValueAnimator animator = ValueAnimator.ofFloat();

    public synchronized void updateWithLevel(final float level) {
        phase += phaseShift;

        if (!animator.isRunning()) {

            float duration = Math.abs(oldLevel - level);

            animator.setFloatValues(oldLevel, level);
            duration = (float) (duration * (ANIMATION_TOP_BOUND - ANIMATION_BOTTOM_BOUND) / 0.9) + ANIMATION_BOTTOM_BOUND;
            oldLevel = level;

            animator.setDuration((int) duration);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    amplitude = Math.max((Float) valueAnimator.getAnimatedValue(), idleAmplitude);

                    invalidate();
                }
            });
            animator.start();
        }
    }

    public void addValue(float value) {
        vals.add(value);
    }
    
    private void setUp() {
        waveColor = Color.BLACK;

        frequency = kDefaultFrequency;

        amplitude = kDefaultAmplitude;
        idleAmplitude = kDefaultIdleAmplitude;

        numberOfWaves = kDefaultNumberOfWaves;
        phaseShift = kDefaultPhaseShift;
        density = kDefaultDensity;

        primaryWaveLineWidth = kDefaultPrimaryLineWidth;
        secondaryWaveLineWidth = kDefaultSecondaryLineWidth;

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                screenHeight = getHeight();

                screenWidth = getWidth();
            }
        });
    }

    Paint paint = new Paint();

    /**
     * I hate math
     * @param c canvas to draw.
     */
    public void drawWave(Canvas c) {
        Path path = new Path();

        // We draw multiple sinus waves, with equal phases but altered amplitudes, multiplied by a parable function.
        for (int i = 0; i < numberOfWaves; i++) {
            strokeLineWidth = (i == 0 ? primaryWaveLineWidth : secondaryWaveLineWidth);

            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.STROKE);

            Float halfHeight = screenHeight / 2.0f;
            Float width = (float) screenWidth;
            Float mid = width / 2.0f;

            Float maxAmplitude = halfHeight - (strokeLineWidth * 2);

            Float progress = 1.0f - i / numberOfWaves;
            Float normedAmplitude = (1.5f * progress - (2.0f / numberOfWaves)) * amplitude;

            //Double multiplier = Math.min(1.0, (progress / 3.0f * 2.0f) + (1.0f / 3.0f));

		    //paint.setAlpha((int) (paint.getAlpha() * multiplier));

            for (Float x = 0f; x < (width + density); x += density) {
                // We use a parable to scale the sinus wave, that has its peak in the middle of the view.
                Float scaling = (float) (-Math.pow(1 / mid * (x - mid), 2) + 1);

                Float y = (float) (scaling * maxAmplitude * normedAmplitude *
                        Math.sin((2 * Math.PI *(x / width) * frequency + phase)) + halfHeight);

                if (x == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            c.drawPath(path, paint);
        }
    }

    public void setWaveColor(int waveColor) {
        this.waveColor = waveColor;
    }

    private class DrawHelper implements Runnable {

        private float level = -0.1f;

        @Override
        public void run() {
            updateWithLevel(level);
            //invalidate();
        }

        public float getLevel() {
            return level;
        }

        public void setLevel(float level) {
            this.level = level;
        }
    }
}
