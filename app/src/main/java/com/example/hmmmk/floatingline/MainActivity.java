package com.example.hmmmk.floatingline;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements RecordResultHandler /* implements RecognitionListener*/ {

    private Context context = this;
    private MainActivity activity = this;

    private Button startBtn;
    //private FloatingView floatingView;
    private Spinner periodSpn;
    private Spinner wHeightSpn;
    private NewFloatingView newFloatingView;

    private final SendHelper sh = new SendHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.start_btn);
        //floatingView = findViewById(R.id.floating_view);
        newFloatingView = findViewById(R.id.new_floating_view);
        periodSpn = findViewById(R.id.wave_period_spn);
        wHeightSpn = findViewById(R.id.wave_height_spn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //floatingView.clear();
                //floatingView.start();

                //AudioReceiverRunnable audioReceiver = new AudioReceiverRunnable(context, activity);

                //new Thread(audioReceiver).start();
                newFloatingView.start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = -61; i <= 61; i++) {
                            final int l = i;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    newFloatingView.updateWithLevel(normalizedDecibels(l));
                                }
                            });

                            try {
                                Thread.sleep(100);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public void receiveResults(final short[] buffers) {
        int local = 0;

        for (int i = 0; i < buffers.length; i++) {
            local += Math.abs(buffers[i]);
        }

        local /= buffers.length;

       // Log.d("BUFFER", + local + "");
        sh.setCurrentValue(local);

        /*try {
            Thread.sleep(45);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        runOnUiThread(sh);
    }

    private Float normalizedDecibels(float db) {
        if (db < -60.0f || db == 0.0f) {
            return 0.0f;
        }

        return (float) Math.pow((Math.pow(10.0f, 0.05f * db) - Math.pow(10.0f, 0.05f * -60.0f)) *
                (1.0f / (1.0f - Math.pow(10.0f, 0.05f * -60.0f))), 1.0f / 2.0f );
    }

    class SendHelper implements Runnable {

        private double currentValue = 0;
        private int numberOfVals = 0;

        @Override
        public void run() {
            if (numberOfVals == 5) {
                //floatingView.addValue(currentValue);

                numberOfVals = 0;
                currentValue = 0;
            }
        }

        public double getCurrentValue() {
            return currentValue;
        }

        public void setCurrentValue(double currentValue) {
            if (numberOfVals == 4) {
                numberOfVals++;
                this.currentValue += currentValue;
                this.currentValue /= 5;
                run();
            }
            else {
                numberOfVals++;
                this.currentValue += currentValue;
            }
        }
    }
}


