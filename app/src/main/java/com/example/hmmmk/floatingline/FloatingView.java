package com.example.hmmmk.floatingline;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.speech.RecognitionListener;
import android.widget.Toast;

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
                        //float ffX = coordinatesList.get(i - 1).xDevice;
                        //float ffY = coordinatesList.get(i - 1).y + 200;

                        float cfX = coordinatesList.get(i).xDevice;
                        float cfY = coordinatesList.get(i).y + 200;

                        //float fsX = coordinatesList.get(i - 1).xDevice;
                        //float fsY = coordinatesList.get(i - 1).secY + 200;

                        float csX = coordinatesList.get(i).xDevice;
                        float csY = coordinatesList.get(i).secY + 200;

                        //float ftX = coordinatesList.get(i - 1).xDevice;
                        //float ftY = coordinatesList.get(i - 1).thrdY + 200;

                        float ctX = coordinatesList.get(i).xDevice;
                        float ctY = coordinatesList.get(i).thrdY + 200;

                        //if ((Math.abs(ffY - cfY) >= 1) | ffY - cfY == 0)
                            //canvas.drawLine(ffX, ffY, cfX, cfY, paint);
                        //if ((Math.abs(fsY - csY) >= 1) | fsY - csY == 0)
                            //canvas.drawLine(fsX, fsY, csX, csY, paint);
                        //if ((Math.abs(ftY - ctY) >= 1) | ftY - ctY == 0)
                            //canvas.drawLine(ftX, ftY, ctX, ctY, paint);
                        //canvas.drawLine(fX, fY, cX, cY, paint);
                        //path.quadTo(fX, fY, cX, cY);
                        //canvas.drawPath(path, paint);
                        //canvas.drawCircle(cfX, cfY, 2, paint);
                        //canvas.drawCircle(csX, csY, 2, paint);
                        //canvas.drawCircle(ctX, ctY, 2, paint);
                        //canvas.drawPoint(cfX, cfY, paint);
                        //canvas.drawPoint(csX, csY, paint);
                        //canvas.drawPoint(ctX, ctY, paint);
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
        int x = 540;
        int y = 100;
        int xBound = x/2;


        for (int i = -xBound; i <= xBound; i++) {
            coordinatesList.add(new Coordinates(x, i, y, y, y));
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
                            modifyCoordinates(values.pollFirst(), true);
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (true);
                }
            }).start();
        }

        isStarted = true;
    }

    /*private void coordinateAdjustment() {
        for (int i = coordinatesList.size() - 1; i > 0; i--) {
            float x = coordinatesList.get(i).x;

            if (x <= 0)
                coordinatesList.remove(i);
            else
                coordinatesList.get(i).x = x - 1f;
        }
    }*/

    final DrawHelper helper = new DrawHelper();

    private volatile float oldAtt = 3f;

    private synchronized void modifyCoordinates(float val, boolean a) {
        //formula gfn(x) = (K/K+x^4)^k
        //formula line(att) = (A*gfn(x) * cos(Bx - f))/att
        //Bounds of function is -1.5 n 1.5
        //Bounds of drawing axis are dynamic but in the current version they are fixed (0; 540)

        float x;
        float K = 8;
        float attF;//Must be set in range [0.05; 25]
        float oldAtt;
        float A = 10;
        float B = 10;
        float F = 0;
        double gfn;
        boolean isDecrease;

        oldAtt = this.oldAtt;
        attF = (float) ((val * (3 - 0.05) / 15) - 0.05f);

        this.oldAtt = attF;

        do {
            for (int i = 0; i < coordinatesList.size(); i++) {
                x = coordinatesList.get(i).xFunction;

                if (x >= 0)
                    x = (float) (((x * (1.5 - 0)) / 270) + 0);
                else
                    x = (float) -(((x * (1.5 - 0)) / 270) + 0);
                gfn = Math.pow(K / (K + Math.pow(x, 5)), K);

                coordinatesList.get(i).y = (float) (A * gfn * Math.cos((B * x) - F)) / oldAtt;
                coordinatesList.get(i).secY = (float) (A * gfn * Math.cos((B * x) - F)) / (oldAtt + 0.1f);
                coordinatesList.get(i).thrdY = (float) (A * gfn * Math.cos((B * x) - F)) / (oldAtt + 0.2f);
            }

            ((Activity) context).runOnUiThread(helper);

            if (oldAtt < attF) {
                isDecrease = false;
                oldAtt += 0.01;
            }
            else {
                isDecrease = true;
                oldAtt -= 0.01;
            }

            try {
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while ((isDecrease & oldAtt >= attF) | (!isDecrease & oldAtt <= attF));
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
