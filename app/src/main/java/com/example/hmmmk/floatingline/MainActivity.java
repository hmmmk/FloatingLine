package com.example.hmmmk.floatingline;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements RecordResultHandler /* implements RecognitionListener*/ {

    private Context context = this;
    private MainActivity activity = this;

    private Button startBtn;
    private FloatingView floatingView;
    private Spinner periodSpn;
    private Spinner wHeightSpn;

    private final SendHelper sh = new SendHelper();

    int[] rates = {8000, 11025, 22050, 44100, 48000/*, 96000 */};
    int[] channels = {AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO};
    int[] encodings  = {AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT};

    //private SpeechRecognizer sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.start_btn);
        floatingView = findViewById(R.id.floating_view);
        periodSpn = findViewById(R.id.wave_period_spn);
        wHeightSpn = findViewById(R.id.wave_height_spn);

        /*sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(this);*/


        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);

        //sr.startListening(intent);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingView.clear();
                floatingView.start();
                //floatingView.setPeriod(Float.valueOf(periodSpn.getSelectedItem().toString()));
                //floatingView.setWaveHeight(Float.valueOf(wHeightSpn.getSelectedItem().toString()));

                //final Random r = new Random(System.currentTimeMillis());

                AudioFormatInfo info = getFormat();
                
                AudioReceiverRunnable audioReceiver = new AudioReceiverRunnable(context, sh, /*info,*/
                        activity);

                new Thread(audioReceiver).start();

                /*new Thread(new Runnable() {

                    float count = 0.25f;

                    @Override
                    public void run() {
                        do {

                            //sh.setCurrentValue(r.nextInt(200));
                            sh.setCurrentValue((int) ((Math.random() * 15f) + 0));

                            //sh.setCurrentValue(count);

                            runOnUiThread(sh);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } while (true);
                    }
                }).start();*/
            }
        });
    }

    public AudioFormatInfo getFormat() {
        AudioFormatInfo info = new AudioFormatInfo();

        for(int enc : encodings)
        {
            for(int ch : channels)
            {
                for(int rate : rates)
                {
                    int t = AudioRecord.getMinBufferSize(rate, ch, enc);

                    if((t != AudioRecord.ERROR) && (t != AudioRecord.ERROR_BAD_VALUE)) {
                        info.setAudioFormat(enc);
                        info.setChannelConfig(ch);
                        info.setSampleRateInHz(rate);
                    }

                }
            }
        }

        return info;
    }

    short max = 0;
    short min = 0;

    @Override
    public void receiveResults(final short[] buffers) {
        final StringBuilder byteResult = new StringBuilder();

        //if (Collections.max(Arrays.asList(buffers)) > max)
        /*for (int i = 0; i < buffers.length; i++) {
            if (buffers[i] > max)
                max = buffers[i];

            if (buffers[i] < min)
                min = buffers[i];
        }*/

        for (short buff : buffers) {
            byteResult.append(String.valueOf(buff));
            byteResult.append(":");
        }

        for (int x = 0; x < 540; x++) {
            //int index = (int) (((x * 1.0f) / 540) * buffers.length);
            double db = 0;

            if (buffers[x] != 0) {
                db = (20 * Math.log10(((double) Math.abs(buffers[x]) / 8)))
                    /*SignalPower.calculatePowerDb(buffers, x, 1)
                    getAudioVolume(buffers)*/;
                //Log.d("BUFFERS", buffers[index] + "");
                //Log.d("TAG", db + "");
            }

            if (sh.getCurrentValue() != db) {
                Log.d("TAG", (db + 96) + "");
                sh.setCurrentValue(db + 96);
            }

            runOnUiThread(sh);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(context, /*byteResult.toString()*/ getAudioVolume(buffers) + "",
                  //      Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Functionality that gets the sound level out of the sample
     */
    private float getAudioVolume(short [] buffer) {

        float lastLevel = 0;

        try {

            int bufferReadResult;

            if (buffer != null && buffer.length > 0) {

                // Sense the voice…

                bufferReadResult = buffer.length;
                double sumLevel = 0;

                for (int i = 0; i < bufferReadResult; i++) {
                    sumLevel += buffer[i];
                }

                lastLevel = (float) Math.abs((sumLevel / bufferReadResult));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return lastLevel;
    }

    class SendHelper implements Runnable {

        private double currentValue = 0;

        @Override
        public void run() {
            floatingView.addValue(currentValue);
        }

        public double getCurrentValue() {
            return currentValue;
        }

        public void setCurrentValue(double currentValue) {
            this.currentValue = currentValue;
        }
    }

}


