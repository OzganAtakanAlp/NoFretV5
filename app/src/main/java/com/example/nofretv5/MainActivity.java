package com.example.nofretv5;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.writer.WriterProcessor;

import android.os.Environment;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {


    private int AUDIO_PERMISSION_CODE = 1;

    AudioDispatcher dispatcher;
    TarsosDSPAudioFormat tarsosDSPAudioFormat;

    File file;

    TextView textView;
    Button buttonRecord;
    Button buttonStop;

    boolean isRecording = false;
    String filename = "recorded_sound.wav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        File sdCard = Environment.getExternalStorageDirectory();
        file = new File(sdCard, filename);

         /*
        filePath = file.getAbsolutePath();
        Log.e("MainActivity", "File save path:" + filePath); //File Save path : /storage/emulated/0/recorded.mp4
        */

        tarsosDSPAudioFormat=new TarsosDSPAudioFormat(TarsosDSPAudioFormat.Encoding.PCM_SIGNED,
                22050,
                2 * 8,
                1,
                2 * 1,
                22050,
                ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()));

        textView = findViewById(R.id.textView);
        buttonRecord = findViewById(R.id.button);
        buttonStop = findViewById(R.id.button2);
        buttonRecord.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                    if(!isRecording){
                        recordAudio();
                        isRecording = true;
                    }else{
                        stopRecording();
                        isRecording = false;
                    }
                }else{
                    requestRecordAudioPermission();
                }
            }
        });

            buttonStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopRecording();
                }
            });
    }

    private void requestRecordAudioPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to listen to your voice.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION_CODE);
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == AUDIO_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void recordAudio(){
        releaseDispatcher();
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rw");
            AudioProcessor recordProcessor = new WriterProcessor(tarsosDSPAudioFormat, randomAccessFile);
            dispatcher.addAudioProcessor(recordProcessor);

            PitchDetectionHandler pitchDetectionHandler = new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult res, AudioEvent e){
                    final float pitchInHz = res.getPitch();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(pitchInHz + "");
                        }
                    });
                }
            };

            AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pitchDetectionHandler);
            dispatcher.addAudioProcessor(pitchProcessor);

            Thread audioThread = new Thread(dispatcher, "Audio Thread");
            audioThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseDispatcher() {

        if(dispatcher != null){

            if(!dispatcher.isStopped())
                dispatcher.stop();
            dispatcher = null;
        }

    }

    public void stopRecording(){
        releaseDispatcher();
    }

    @Override
    protected void onStop(){
        super.onStop();
        releaseDispatcher();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}