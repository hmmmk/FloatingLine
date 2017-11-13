package com.example.hmmmk.floatingline;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    private Button startBtn;
    private FloatingView floatingView;
    private Spinner periodSpn;
    private Spinner wHeightSpn;

    private SpeechRecognizer sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.start_btn);
        floatingView = findViewById(R.id.floating_view);
        periodSpn = findViewById(R.id.wave_period_spn);
        wHeightSpn = findViewById(R.id.wave_height_spn);

        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(this);


        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);

        sr.startListening(intent);

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

                    float count = 0.25f;

                    @Override
                    public void run() {
                        do {

                            //sh.setCurrentValue(r.nextInt(200));
                            sh.setCurrentValue((float) ((Math.random() * 15f) + 0));

                            /*if (count < 10) {
                                count += 1f;
                            }
                            else {
                                count = 1f;
                            }*/

                            //sh.setCurrentValue(count);

                            runOnUiThread(sh);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } while (true);
                    }
                }).start();
            }
        });
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Toast.makeText(this, "WORKS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Toast.makeText(this, "RMS CHANGED " + rmsdB, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Toast.makeText(this, "WORKS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Toast.makeText(this, "WORKS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Toast.makeText(this, "WORKS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResults(Bundle results) {
        Toast.makeText(this, "WORKS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBeginningOfSpeech() {
        Toast.makeText(this, "WORKS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEndOfSpeech() {
        Toast.makeText(this, "WORKS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int error) {
        Toast.makeText(this, "ERROR " + error, Toast.LENGTH_SHORT).show();
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


