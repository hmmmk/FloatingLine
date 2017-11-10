package com.example.hmmmk.floatingline;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button startBtn;
    private FloatingView floatingView;
    private Spinner periodSpn;
    private Spinner wHeightSpn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.start_btn);
        floatingView = findViewById(R.id.floating_view);
        periodSpn = findViewById(R.id.wave_period_spn);
        wHeightSpn = findViewById(R.id.wave_height_spn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingView.clear();
                floatingView.start();
                //floatingView.setPeriod(Float.valueOf(periodSpn.getSelectedItem().toString()));
                //floatingView.setWaveHeight(Float.valueOf(wHeightSpn.getSelectedItem().toString()));

                final Random r = new Random(System.currentTimeMillis());
                final SendHelper sh = new SendHelper();

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 0; i < 1000; i++) {

                            if (i % 5 == 0)
                                sh.setCurrentValue(r.nextInt(200));
                            else
                                sh.setCurrentValue(100f);

                            runOnUiThread(sh);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }

    class SendHelper implements Runnable {

        private float currentValue = 0;

        @Override
        public void run() {
            floatingView.addValue(currentValue);
        }

        public float getCurrentValue() {
            return currentValue;
        }

        public void setCurrentValue(float currentValue) {
            this.currentValue = currentValue;
        }
    }

}


