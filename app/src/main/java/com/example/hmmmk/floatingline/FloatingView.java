package com.example.hmmmk.floatingline;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;

import java.util.LinkedList;

/**
 * Created by hmmmk___ on 08.11.2017.
 */

public class FloatingView extends View {

    private LinkedList<Coordinates> coordinatesList = new LinkedList<>();
    private final LinkedList<Float> values = new LinkedList<>();

    private Paint paint = new Paint();

    private boolean isClearing = false;

    private float period = 0.05f;
    private float waveHeight = 5f;

    private Context context;

    public FloatingView(Context context) {
        super(context);

        initPaint();
        initCoordinates();
        this.context = context;
    }

    public FloatingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initPaint();
        initCoordinates();
        this.context = context;
    }

    Point size = new Point();
    Path path = new Path();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Display display = getDisplay();

        int width = 0;
        int height = 0;

        if (display != null && size != null) {
            display.getSize(size);

            height = size.y;
            width = size.x;
        }

        if (isClearing) {
            //coordinatesList.clear();
            canvas.drawColor(Color.WHITE);

            isClearing = false;
            //canvas.drawLine(0, 100, width, 100, paint);
        }
        else {
            if (!coordinatesList.isEmpty()) {
                for (int i = 2; i < coordinatesList.size(); i++) {

                    if (i > 0) {
                        float fX = coordinatesList.get(i - 1).x;
                        float fY = coordinatesList.get(i - 1).y;

                        float cX = coordinatesList.get(i).x;
                        float cY = coordinatesList.get(i).y;

                        canvas.drawLine(fX, fY, cX, cY, paint);
                        //path.quadTo(fX, fY, cX, cY);
                        //canvas.drawPath(path, paint);
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
        float x = 540;
        float y = 100;

        for (int i = 0; i <= 540; i++) {
            coordinatesList.add(new Coordinates(x, y));
            x -= 1;
        }

        invalidate();
    }

    float x = 0;

    public void setValue(float val) {

        /*Display display = getDisplay();
        display.getSize(size);
        int height = size.y;
        int width = size.x;

        int valTopBound = 200;
        int bottomBound = 0;
        int heightTopBound = 100;
        float periodTopBound = 0.3f;
        //int heightBottomBound = 0

        //sin (y) = a + bsin(cx + d)
        float y;
        //Высота над Ox (всегда константа для определенного девайса будет неизменяемой:
        // равна размер View по у/2)
        float a = 100;
        //Высота волны (крайним значением будет высота view/2)
        float b = waveHeight /*((val * 100)/200) + 0.0f*/;
        //Период(устанавливать значения от 0 и до 0.3)
        /*float c = /*(float) (val * (0.3 - 0) / 200) + 0.0f*/ /*period;*/
        //Сдвиг волны от нулевого уровня в начальный момент времени
        //(в данном случае ни на что не влияет)
        /*float d = 0;

        if (!coordinatesList.isEmpty()) {
            coordinateAdjustment();
        }

        y = (float) (a + (b * Math.sin(c * x + d)));

        Coordinates cd = new Coordinates(width, y);
        coordinatesList.add(cd);

        x += 1f;

        invalidate();*/
    }

    public void addValue(float value) {
        values.add(value);
    }

    public void start() {

        final DrawHelper helper = new DrawHelper();

        new Thread(new Runnable() {
            @Override
            public void run() {
                do {

                    //((MainActivity) context).runOnUiThread(helper);
                    if (!values.isEmpty()) {
                        modifyCoordinates(values.peekFirst(), true);
                    }
                    try {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while(true);
            }
        }).start();
    }

    private void coordinateAdjustment() {
        for (int i = coordinatesList.size() - 1; i > 0; i--) {
            float x = coordinatesList.get(i).x;

            if (x <= 0)
                coordinatesList.remove(i);
            else
                coordinatesList.get(i).x = x - 1f;
        }
    }

    final DrawHelper helper = new DrawHelper();

    private void modifyCoordinates(float val, boolean isDecrease) {
        //formula gfn(x) = (K/K+x^4)^k
        //formula line(att) = (A*gfn(x) * cos(Bx - f))/att

        float x;
        float K = 8;
        float att = 1;
        float A = 1;
        float B = 6;
        float F = 0;
        double gfn;

        for (int i = coordinatesList.size() - 1; i >= 0; i--) {
            x = coordinatesList.get(i).x;
            gfn = Math.pow(K / ( K + Math.pow(x, 5)), K);

            coordinatesList.get(i).y = (float) (A * gfn * Math.cos((B * x) - F))/att;
        }

        ((Activity) context).runOnUiThread(helper);
    }

    public void clear() {
        isClearing = true;
        invalidate();
    }

    private float convertNumber(float num) {

        return 0.0f;
    }

    public float getPeriod() {
        return period;
    }

    public void setPeriod(float period) {
        this.period = period;
    }

    public float getWaveHeight() {
        return waveHeight;
    }

    public void setWaveHeight(float waveHeight) {
        this.waveHeight = waveHeight;
    }

    class DrawHelper implements Runnable {

        private LinkedList<Float> values = new LinkedList<>();

        @Override
        public void run() {
            invalidate();
        }

        /*public void addToQueue(Float val) {
            values.add(val);
        }*/
    }

    class Coordinates {

        float x;
        float y;

        Coordinates () {

        }

        Coordinates (float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}
