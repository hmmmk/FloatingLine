package com.example.hmmmk.floatingline;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created by hmmmk___ on 08.11.2017.
 */

public class FloatingView extends View {

    private LinkedList<Coordinates> coordinatesList = new LinkedList<>();
    private final LinkedList<Double> values = new LinkedList<>();

    private Paint paint = new Paint();

    private boolean isClearing = false;

    private Context context;

    int width = 0;
    int height = 0;

    public FloatingView(Context context) {
        super(context);

        this.context = context;
    }

    public FloatingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
        this.context = context;
    }

    private void init() {
        initPaint();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initCoordinates();
            }
        });
    }

    Point size = new Point();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Display display = getDisplay();

        if (display != null && size != null) {
            display.getSize(size);

            height = size.y;
            width = size.x;
        }

        if (isClearing) {
            canvas.drawColor(Color.WHITE);

            isClearing = false;
        }
        else {
            if (!coordinatesList.isEmpty()) {
                for (int i = 2; i < coordinatesList.size(); i++) {

                    if (i > 0) {
                        float ffX = coordinatesList.get(i - 1).xDevice;
                        float ffY = coordinatesList.get(i - 1).y + 200;

                        float cfX = coordinatesList.get(i).xDevice;
                        float cfY = coordinatesList.get(i).y + 200;

                        float fsX = coordinatesList.get(i - 1).xDevice;
                        float fsY = coordinatesList.get(i - 1).secY + 200;

                        float csX = coordinatesList.get(i).xDevice;
                        float csY = coordinatesList.get(i).secY + 200;

                        float ftX = coordinatesList.get(i - 1).xDevice;
                        float ftY = coordinatesList.get(i - 1).thrdY + 200;

                        float ctX = coordinatesList.get(i).xDevice;
                        float ctY = coordinatesList.get(i).thrdY + 200;

                        canvas.drawLine(ffX, ffY, cfX, cfY, paint);
                        //canvas.drawLine(fsX, fsY, csX, csY, paint);
                        //canvas.drawLine(ftX, ftY, ctX, ctY, paint);

                    }
                }

            }
        }
    }

    private void initPaint() {
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(4f);
    }

    private void initCoordinates() {
        Log.d("WIDTH", getWidth() + "");
        Log.d("HEIGHT", getHeight() + "");

        int x = getWidth() / 2;
        int y = getHeight() / 4;
        int xBound = x / 2;

        for (int i = -xBound; i < xBound; i++) {
            coordinatesList.add(new Coordinates(x, i, y, y, y));
            x -= 1;
        }

        invalidate();
    }


    public void addValue(double value) {
        values.add(value);
    }

    public boolean isStarted = false;

    public void start() {

        if (!isStarted) {

            final DrawHelper helper = new DrawHelper();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    do {

                        //((MainActivity) context).runOnUiThread(helper);
                        if (!values.isEmpty()) {
                            modifyCoordinates();

                            try {
                                recountValues(values.pollFirst());
                            }
                            catch (NoSuchElementException e) {
                                Log.d("ERROR", "NO SUCH ELEMENT");
                            }
                        }
                        /*try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                    } while (true);
                }
            }).start();
        }

        isStarted = true;
    }

    private double oldAtt = 5;
    private double newAtt = 5;
    private static final int TOP_DP_BOUND = Short.MAX_VALUE; //20000;6000

    boolean isDecrease;

    private void recountValues(double x) {
        if (x < 0)
            x = 0;

        if (x > TOP_DP_BOUND)
            x = TOP_DP_BOUND;

        x = TOP_DP_BOUND - x;

        double attF = ((x * (5 - 0.1) / TOP_DP_BOUND) - 0.1);

        //double attF = ((((x - 0) / (TOP_DP_BOUND - 0)) * (5 - 0.1)) + 0.1);

        attF = 5 - attF;

        if (attF < 0.1)
            attF = 0.1f;

        oldAtt = newAtt;
        newAtt = attF;

        if (oldAtt > newAtt)
            isDecrease = true;
        else
            isDecrease = false;

    }

    final DrawHelper helper = new DrawHelper();

    private static final double K = 8;
    private static final float A = 10;
    private static final float B = 6;
    private static final float F = 0;

    private boolean isModifyingStarted = false;

    ValueContainer vc = new ValueContainer(2f);

    private synchronized void modifyCoordinates() {
        //formula gfn(x) = (K/K+x^4)^k
        //formula line(att) = (A*gfn(x) * cos(Bx - f))/att
        //Bounds of function is -1.5 n 1.5
        //Bounds of drawing axis are dynamic but in the current version they are fixed (0; 540)

        if(!isModifyingStarted) {
            isModifyingStarted = true;

            double attF = newAtt;
            double attO = oldAtt;

            vc.setValue((float) newAtt);

            /*do {
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                //Log.d("VALUES", values.size() + "");

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ValueAnimator va = ValueAnimator.ofFloat((float) oldAtt, (float) newAtt);
                    va.setDuration(120);
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            double x;
                            double gfn;

                            for (int i = 0; i < coordinatesList.size(); i++) {
                                x = coordinatesList.get(i).xFunction;

                                if (x >= 0)
                                    x = (float) (((x * (1.5 - 0)) / (getWidth() / 4)) + 0);
                                else
                                    x = (float) -(((x * (1.5 - 0)) / (getWidth() / 4)) + 0);
                                gfn = Math.pow(K / (K + Math.pow(x, 5)), K);

                                coordinatesList.get(i).y = (float) ((A * gfn * Math.cos((B * x) - F)) / (Float) animation.getAnimatedValue());
                                //coordinatesList.get(i).secY = (float) ((A * gfn * Math.cos((B * x) - F)) / ((Float) animation.getAnimatedValue() + 0.07f));
                                //coordinatesList.get(i).thrdY = (float) ((A * gfn * Math.cos((B * x) - F)) / ((Float) animation.getAnimatedValue() + 0.03f));
                            }

                            //((Activity) context).runOnUiThread(helper);
                            invalidate();
                        }
                    });
                    va.start();
                }
            });

                /*if (isDecrease) {
                    attF -= 0.05;
                } else {
                    attF += 0.05;
                }*/

            //} while ((isDecrease & attF >= newAtt) | (!isDecrease & attF <= newAtt));
        }

        isModifyingStarted = false;
    }



    /*public void modifyCoordinates() {
        //formula gfn(x) = (K/K+x^4)^k
        //formula line(att) = (A*gfn(x) * cos(Bx - f))/att
        //Bounds of function is -1.5 n 1.5
        //Bounds of drawing axis are dynamic but in the current version they are fixed (0; 540)

        if(!isModifyingStarted) {
            isModifyingStarted = true;

            double x;
            double gfn;
            double attF = oldAtt;
            double step = Math.abs(attF - newAtt) / 5;
            Log.d("STEP", step + "");

            for (int l = 0; l < 3; l++) {
                try {
                    Thread.sleep(12);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("VALUES", values.size() + "");

                for (int i = 0; i < coordinatesList.size(); i++) {
                    x = coordinatesList.get(i).xFunction;

                    if (x >= 0)
                        x = (float) (((x * (1.5 - 0)) / 120) + 0);
                    else
                        x = (float) -(((x * (1.5 - 0)) / 120) + 0);
                    gfn = Math.pow(K / (K + Math.pow(x, 5)), K);

                    coordinatesList.get(i).y = (float) ((A * gfn * Math.cos((B * x) - F)) / attF);
                    coordinatesList.get(i).secY = (float) ((A * gfn * Math.cos((B * x) - F)) / (attF + 0.07f));
                    coordinatesList.get(i).thrdY = (float) ((A * gfn * Math.cos((B * x) - F)) / (attF + 0.03f));
                }

                ((Activity) context).runOnUiThread(helper);

                if (isDecrease) {
                    attF -= step;
                } else {
                    attF += step;
                }
            }
        }

        isModifyingStarted = false;
    }*/


    /*private void modifyCoordinates(double sample) {
        //formula k = 1/|x|
        //formula y = sin(x) * k

        double k;
        double y;

    }*/

    public void clear() {
        isClearing = true;
        invalidate();
    }

    class DrawHelper implements Runnable {

        private LinkedList<Float> values = new LinkedList<>();

        @Override
        public void run() {
            invalidate();
        }
    }

    class ValueContainer {

        float value;

        ValueContainer(float value) {
            this.value = value;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }

    class Coordinates {

        private float xDevice;
        private float xFunction;
        private float y;
        private float secY;
        private float thrdY;

        Coordinates () {

        }

        Coordinates (float xDevice, float xFunction, float y) {
            this.xDevice = xDevice;
            this.xFunction = xFunction;
            this.y = y;
        }

        public Coordinates(float xDevice, float xFunction, float y, float secY, float thrdY) {
            this.xDevice = xDevice;
            this.xFunction = xFunction;
            this.y = y;
            this.secY = secY;
            this.thrdY = thrdY;
        }

        public float getXDevice() {
            return xDevice;
        }

        public void setXDevice(float xDevice) {
            this.xDevice = xDevice;
        }

        public float getXFunction() {
            return xFunction;
        }

        public void setXFunction(float xFunction) {
            this.xFunction = xFunction;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getSecY() {
            return secY;
        }

        public void setSecY(float secY) {
            this.secY = secY;
        }

        public float getThrdY() {
            return thrdY;
        }

        public void setThrdY(float thrdY) {
            this.thrdY = thrdY;
        }
    }
}
