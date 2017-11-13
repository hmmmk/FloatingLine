package com.example.hmmmk.floatingline;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;
import android.widget.Toast;

/**
 * Created by hmmmk___ on 13.11.2017.
 */

public class AudioReceiverRunnable implements Runnable {

    private Context context;
    private Activity activity;

    private MainActivity.SendHelper sh;
    private AudioFormatInfo info;
    private boolean isRunning;
    private boolean isShouldContinue;
    private AudioRecord audioRecord;
    private RecordResultHandler handler;

    private static final int SAMPLE_RATE = 8000;
    private static final int BUFF_COUNT = 32;

    public AudioReceiverRunnable(Context context, MainActivity.SendHelper sh, /*AudioFormatInfo info,*/
                                 RecordResultHandler handler) {
        this.sh = sh;
        //this.info = info;
        this.context = context;
        this.handler = handler;
        isRunning = true;
        isShouldContinue = true;

        activity = (Activity) context;
    }

    public void stop() {
        isShouldContinue = false;
        isRunning = false;
    }

    public void start() {
        isRunning = true;
        isShouldContinue = true;
        run();
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        if (isRunning) {
            record();
        }
    }

    private void record() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // buffer size in bytes
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

        short[] audioBuffer = new short[bufferSize / 2];

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            return;
        }

        record.startRecording();


        long shortsRead = 0;

        while (isShouldContinue) {
            int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
            shortsRead += numberOfShort;

            // Notify waveform
            handler.receiveResults(audioBuffer);
        }

        record.stop();
        record.release();
    }

    /*@Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

        isRunning = true;

        int buffSize = AudioRecord.getMinBufferSize(info.getSampleRateInHz(), info.getChannelConfig(),
                info.getAudioFormat());


        if(buffSize == AudioRecord.ERROR)
        {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "getMinBufferSize returned ERROR",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        if(buffSize == AudioRecord.ERROR_BAD_VALUE)
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "getMinBufferSize returned ERROR_BAD_VALUE",
                            Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        if(info.getAudioFormat() != AudioFormat.ENCODING_PCM_16BIT)
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "unknown format", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        short[][] buffers = new short[BUFF_COUNT][buffSize >> 1];

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                info.getSampleRateInHz(),
                info.getChannelConfig(), info.getAudioFormat(),
                buffSize);

        if(audioRecord.getState() != AudioRecord.STATE_INITIALIZED)
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "getState() != STATE_INITIALIZED", Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        try
        {
            audioRecord.startRecording();
        }
        catch(IllegalStateException e)
        {
            e.printStackTrace();
            return;
        }

        int count = 0;

        while(isRunning)
        {
            int samplesRead = audioRecord.read(buffers[count], 0, buffers[count].length);

            if(samplesRead == AudioRecord.ERROR_INVALID_OPERATION)
            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "read() returned ERROR_INVALID_OPERATION",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                return;
            }

            if(samplesRead == AudioRecord.ERROR_BAD_VALUE)
            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "read() returned ERROR_BAD_VALUE",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                return;
            }

            // посылаем оповещение обработчикам
            handler.receiveResults(buffers[count]);

            count = (count + 1) % BUFF_COUNT;
        }

        try
        {
            try
            {
                audioRecord.stop();
            }
            catch(IllegalStateException e)
            {
                e.printStackTrace();
                return;
            }
        }
        finally
        {
            // освобождаем ресурсы
            audioRecord.release();
            audioRecord = null;
        }
    }*/
}
