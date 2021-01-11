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

        if (pitchInHz >= 27.5 && pitchInHz < 61.74) {

        } else if (pitchInHz >= 32.70 && pitchInHz < 36.) {
            noteText.setText("C1");
            thirdNote.setText("D1");
            fifthNote.setText("E1");
        } else if (pitchInHz >= 55 && pitchInHz < 58) {
            noteText.setText("D1");
            thirdNote.setText("E1");
            fifthNote.setText("F#1");
        } else if (pitchInHz >= 58 && pitchInHz < 63) {
            noteText.setText("E1");
            thirdNote.setText("F#1");
            fifthNote.setText("G#1");
        } else if (pitchInHz >= 63 && pitchInHz < 73.42) {
            noteText.setText("F1");
            thirdNote.setText("G1");
            fifthNote.setText("A1");
        } else if (pitchInHz >= 73.42 && pitchInHz < 75) {
            noteText.setText("G1");
            thirdNote.setText("A1");
            fifthNote.setText("B1");
        } else if (pitchInHz >=75&& pitchInHz < 98) {
            noteText.setText("E1");
            thirdNote.setText("F#1");
            fifthNote.setText("G#1");
        } else if (pitchInHz >= 98 && pitchInHz < 110) {
            noteText.setText("B1");
            thirdNote.setText("C#2");
            fifthNote.setText("D#2");
        }

        if (pitchInHz >= 110 && pitchInHz < 123.47) {
                //A
                noteText.setText("A2");
                thirdNote.setText("B2");
                fifthNote.setText("C#3");
            } else if (pitchInHz >= 123.47 && pitchInHz < 130.81) {
                //B
                noteText.setText("B2");
                thirdNote.setText("C#3");
                fifthNote.setText("D#3");
            }else if (pitchInHz >= 130.81 && pitchInHz < 140.83) {
                //C
                noteText.setText("C3");
                thirdNote.setText("D3");
                fifthNote.setText("E3");
            } else if (pitchInHz >= 140.83 && pitchInHz < 161.81) {
                //D
                noteText.setText("D3");
                thirdNote.setText("E3");
                fifthNote.setText("F#3");
            } else if (pitchInHz >= 161.81 && pitchInHz <= 174.61) {
                //E
                noteText.setText("E3");
                thirdNote.setText("F#3");
                fifthNote.setText("G#3");
            } else if (pitchInHz >= 174.61 && pitchInHz < 193) {
                //F
                noteText.setText("F3");
                thirdNote.setText("G3");
                fifthNote.setText("A3");
            } else if (pitchInHz >= 193 && pitchInHz < 217) {
                //G
                noteText.setText("G3");
                thirdNote.setText("A3");
                fifthNote.setText("B3");
            } else if (pitchInHz >= 217 && pitchInHz < 240.94) {
                //A
                noteText.setText("A3");
                thirdNote.setText("B3");
                fifthNote.setText("C#4");
            } else if (pitchInHz >= 240.94 && pitchInHz < 261.63) {
                //B
                noteText.setText("B3");
                thirdNote.setText("C#4");
                fifthNote.setText("D#4");
            }



        if (pitchInHz >= 261.63 && pitchInHz < 523.25) {
            if (pitchInHz >= 261.63 && pitchInHz < 293.66) {
                noteText.setText("C4");
                thirdNote.setText("D4");
                fifthNote.setText("E4");
            } else if (pitchInHz >= 293.66 && pitchInHz < 320.63) {
                noteText.setText("D4");
                thirdNote.setText("E4");
                fifthNote.setText("F#4");
            } else if (pitchInHz >= 320.63 && pitchInHz < 349.23) {
                noteText.setText("E4");
                thirdNote.setText("F#4");
                fifthNote.setText("G#4");
            } else if (pitchInHz >= 349.23 && pitchInHz < 392) {
                noteText.setText("F4");
                thirdNote.setText("G4");
                fifthNote.setText("A4");
            } else if (pitchInHz >= 392 && pitchInHz < 440) {
                noteText.setText("G4");
                thirdNote.setText("A4");
                fifthNote.setText("B4");
            } else if (pitchInHz >= 440.00 && pitchInHz < 493.88) {
                noteText.setText("A4");
                thirdNote.setText("B4");
                fifthNote.setText("C#4");
            } else if (pitchInHz >= 493.88 && pitchInHz < 523.25) {
                noteText.setText("B4");
                thirdNote.setText("C#5");
                fifthNote.setText("D#5");
            }
        }

        if (pitchInHz >= 523.25 && pitchInHz < 1046.50) {
            if (pitchInHz >= 523.25 && pitchInHz < 587.33) {
                noteText.setText("C5");
                noteText.setText("D5");
                noteText.setText("E5");
            } else if (pitchInHz >= 587.33 && pitchInHz < 659.25) {
                noteText.setText("D5");
                noteText.setText("E5");
                noteText.setText("F#5");
            } else if (pitchInHz >= 659.25 && pitchInHz < 698.46) {
                noteText.setText("E5");
                noteText.setText("F#5");
                noteText.setText("G#5");
            } else if (pitchInHz >= 698.46 && pitchInHz < 783.99) {
                noteText.setText("F5");
                noteText.setText("G5");
                noteText.setText("A5");
            } else if (pitchInHz >= 783.99 && pitchInHz < 880) {
                noteText.setText("G5");
                noteText.setText("A5");
                noteText.setText("B5");
            } else if (pitchInHz >= 880 && pitchInHz < 987.77) {
                noteText.setText("A5");
                noteText.setText("B5");
                noteText.setText("C#6");
            } else if (pitchInHz >= 987.77 && pitchInHz < 1046.50) {
                noteText.setText("B5");
                noteText.setText("C#6");
                noteText.setText("D#6");
            }
        }

        //not that it is likely possible for people to go this high but still serves them well to keep these here
        if (pitchInHz >= 1046.50 && pitchInHz <2093 ) {
            if (pitchInHz >= 1046.50 && pitchInHz < 1174.66) {
                noteText.setText("C6");
                noteText.setText("D6");
                noteText.setText("E6");
            } else if (pitchInHz >= 1174.66 && pitchInHz < 1318.51) {
                noteText.setText("D6");
                noteText.setText("E6");
                noteText.setText("F#6");
            } else if (pitchInHz >= 1318.51 && pitchInHz < 1396.91) {
                noteText.setText("E6");
                noteText.setText("F#6");
                noteText.setText("G#6");
            } else if (pitchInHz >= 1396.91 && pitchInHz < 1567.98) {
                noteText.setText("F6");
                noteText.setText("G6");
                noteText.setText("A6");
            } else if (pitchInHz >= 1567.98 && pitchInHz < 1760) {
                noteText.setText("G6");
                noteText.setText("A6");
                noteText.setText("B6");
            } else if (pitchInHz >= 1760 && pitchInHz < 1975.53) {
                noteText.setText("A6");
                noteText.setText("B6");
                noteText.setText("C#6");
            } else if (pitchInHz >= 1975.53 && pitchInHz < 2093) {
                noteText.setText("B6");
                noteText.setText("C#7");
                noteText.setText("D#7");
            }
        }

    }
}