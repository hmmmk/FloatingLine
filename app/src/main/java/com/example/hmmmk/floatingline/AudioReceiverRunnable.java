package com.example.hmmmk.floatingline;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by hmmmk___ on 13.11.2017.
 */

public class AudioReceiverRunnable implements Runnable {

    private Context context;
    private Activity activity;

    private boolean isRunning;
    private boolean isShouldContinue;
    private RecordResultHandler handler;

    private static final int SAMPLE_RATE = 44100;

    public AudioReceiverRunnable(Context context, RecordResultHandler handler) {
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
}
