package com.example.nofretv5;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    private int AUDIO_PERMISSION_CODE = 1;

    TarsosDSPAudioFormat tarsosDSPAudioFormat;

    TextView pitchTextView, noteText, thirdView, thirdNote, fifthView, fifthNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestRecordAudioPermission();

        AudioDispatcher dispatcher =
                AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        tarsosDSPAudioFormat = new TarsosDSPAudioFormat(TarsosDSPAudioFormat.Encoding.PCM_SIGNED,
                22050,
                2 * 8,
                1,
                2 * 1,
                22050,
                ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()));

        pitchTextView = findViewById(R.id.textView);
        noteText = findViewById(R.id.noteText);

        thirdView = findViewById(R.id.thirdView);
        thirdNote = findViewById(R.id.thirdNote);
        fifthView = findViewById(R.id.fifthView);
        fifthNote = findViewById(R.id.fifthNote);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e) {
                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processPitch(pitchInHz);
                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();

    }

    private void requestRecordAudioPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to listen to your voice.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
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


    public void processPitch(float pitchInHz) {

        pitchTextView.setText("" + pitchInHz);
        thirdNote.setText("" + pitchInHz);
        fifthNote.setText("" + pitchInHz);

        /*if (pitchInHz >= 32.70 && pitchInHz < 65.41) {
            if (pitchInHz >= 32.70 && pitchInHz < 36.) {
                noteText.setText("C1");
            } else if (pitchInHz >= 55 && pitchInHz < 61.74) {
                noteText.setText("D1");
            } else if (pitchInHz >= 61.74 && pitchInHz < 110) {
                noteText.setText("E1");
            } else if (pitchInHz >= 61.74 && pitchInHz < 110) {
                noteText.setText("F1");
            } else if (pitchInHz >= 61.74 && pitchInHz < 110) {
                noteText.setText("G1");
            } else if (pitchInHz >= 61.74 && pitchInHz < 110) {
                noteText.setText("A1");
            } else if (pitchInHz >= 61.74 && pitchInHz < 110) {
                noteText.setText("B1");
            }
        }*/


            if (pitchInHz >= 110 && pitchInHz < 123.47) {
                //A
                noteText.setText("A2");
                thirdNote.setText("C3");
                fifthNote.setText("E3");
            } else if (pitchInHz >= 123.47 && pitchInHz < 130.81) {
                //B
                noteText.setText("B2");
                thirdNote.setText("D3");
                fifthNote.setText("F3");
        }
        if (pitchInHz >= 130.82 && pitchInHz <261.62) {
            if (pitchInHz >= 130.81 && pitchInHz < 146.83) {
                //C
                noteText.setText("C3");
                thirdNote.setText("E3");
                fifthNote.setText("G3");
            } else if (pitchInHz >= 146.83 && pitchInHz < 164.81) {
                //D
                noteText.setText("D3");
                thirdNote.setText("F3");
                fifthNote.setText("A3");
            } else if (pitchInHz >= 164.81 && pitchInHz <= 174.61) {
                //E
                noteText.setText("E3");
                thirdNote.setText("G3");
                fifthNote.setText("A4");
            } else if (pitchInHz >= 174.61 && pitchInHz < 185) {
                //F
                noteText.setText("F3");
                thirdNote.setText("A4");
                fifthNote.setText("C4");
            } else if (pitchInHz >= 185 && pitchInHz < 196) {
                //G
                noteText.setText("G3");
                thirdNote.setText("A");
                fifthNote.setText("C");
            } else if (pitchInHz >= 196 && pitchInHz < 220) {
                //G
                noteText.setText("A3");
                thirdNote.setText("C4");
                fifthNote.setText("E4");
            } else if (pitchInHz >= 220 && pitchInHz < 246.94) {
                //G
                noteText.setText("B3");
                thirdNote.setText("B4");
                fifthNote.setText("D4");
            }
        }


        if (pitchInHz >= 261.63 && pitchInHz < 523.25) {
            if (pitchInHz >= 261.63 && pitchInHz < 293.66) {
                noteText.setText("C4");
            } else if (pitchInHz >= 293.66 && pitchInHz < 329.63) {
                noteText.setText("D4");
            } else if (pitchInHz >= 329.63 && pitchInHz < 349.23) {
                noteText.setText("E4");
            } else if (pitchInHz >= 349.23 && pitchInHz < 392) {
                noteText.setText("F4");
            } else if (pitchInHz >= 392 && pitchInHz < 440) {
                noteText.setText("G4");
            } else if (pitchInHz >= 440.00 && pitchInHz < 493.88) {
                noteText.setText("A4");
            } else if (pitchInHz >= 493.88 && pitchInHz < 523.25) {
                noteText.setText("B4");
            }

        }
    }
}