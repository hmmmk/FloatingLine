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
    private WaveView waveView;

    private final SendHelper sh = new SendHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.start_btn);
        //floatingView = findViewById(R.id.floating_view);
        waveView = findViewById(R.id.new_floating_view);
        periodSpn = findViewById(R.id.wave_period_spn);
        wHeightSpn = findViewById(R.id.wave_height_spn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //floatingView.clear();
                //floatingView.start();

                AudioReceiverRunnable audioReceiver = new AudioReceiverRunnable(context, activity);

                new Thread(audioReceiver).start();
                waveView.start();
            }
        });
    }

    @Override
    public void receiveResults(final short[] buffers) {
        int local = 0;
        double decibels = 0;

        for (int i = 0; i < buffers.length; i++) {
            local += buffers[i];
        }

        local /= buffers.length;

        /*if (local > 0)
            decibels = (20 * Math.log10((double) local / Short.MAX_VALUE));
        if (local < 0)
            decibels = -(20 * Math.log10((double) local / Short.MAX_VALUE));*/

        sh.setCurrentValue(normalizedDecibels(local));

        try {
            Thread.sleep(40);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        runOnUiThread(sh);
    }

    private float normalizedDecibels(float db) {
        if (db < -60.0f) {
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
                waveView.addValue((float) currentValue);

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


