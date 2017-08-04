package travis.thenewboston.com.mhealththeme;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Shahzad Adil on 6/21/2017.
 */

public class diagnosePatient extends Fragment{
    DBHandler mydb=null;
    TextView name,diagnose;
    Button btn_scoredata;
    Cursor cursor;
    ViewPager viewPager;
    Spinner cuff_spinner,lungs_spinner;
    Patient p;
    AlertDialog dialog;
    //Media Player Variables

    Button buttonStart, buttonStop, buttonPlayLastRecordAudio,
            buttonStopPlayingRecording ;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    String myRecording;
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    @Override
    public void onResume() {
        try {
            if (cursor.moveToFirst()) {
                do {
                    p.set_name(cursor.getString(cursor.getColumnIndex("name")));//cursor.getString(cursor.getColumnIndex("name"));
                    p.set_id(MainActivity.p_id);
                    p.set_phone_number(cursor.getString(cursor.getColumnIndex("phone_number")));
                    p.set_address(cursor.getString(cursor.getColumnIndex("address")));
                } while (cursor.moveToNext());
            }

            if(MainActivity.p_id>0)
            {
                name.setText(p.get_name());
                diagnose.setText("Sample Diagnose");
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(), String.valueOf(e), Toast.LENGTH_LONG).show();
        }

        super.onResume();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.diagnose_patient, container, false);
        p= new Patient();
        mydb = new DBHandler(getActivity());
        cursor = mydb.getdata(MainActivity.p_id);
        name=(TextView) rootView.findViewById(R.id.p_name_tv);
        diagnose=(TextView) rootView.findViewById(R.id.p_diagnose_tv);
        btn_scoredata=(Button) rootView.findViewById(R.id.score_button);

        recordings(rootView);
        return rootView;
    }
    public void recordings(View rv)
    {
        cuff_spinner= (Spinner) rv.findViewById(R.id.cuff_spinner);
        lungs_spinner=(Spinner) rv.findViewById(R.id.lungs_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sound_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        cuff_spinner.setAdapter(adapter);
        lungs_spinner.setAdapter(adapter);

        cuff_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item=(String) parent.getItemAtPosition(position);
                if(item.equals("Select"))
                {
                    //Toast.makeText(getActivity(), "Kindly Select an option first", Toast.LENGTH_LONG).show();
                }
                else if(item.equals("Record Sound"))
                {
                    Toast.makeText(getActivity(), item, Toast.LENGTH_LONG).show();
                    recordaudio();
                }
                else
                {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void recordaudio()
    {
        AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(getActivity());
        final View audiorecordView = getActivity().getLayoutInflater().inflate(R.layout.audiorecord, null);

        buttonStart = (Button) audiorecordView.findViewById(R.id.record_btn);
        buttonStop = (Button) audiorecordView.findViewById(R.id.record_stop_btn);
        buttonPlayLastRecordAudio = (Button) audiorecordView.findViewById(R.id.play_btn);
        buttonStopPlayingRecording = (Button) audiorecordView.findViewById(R.id.play_stop_btn);
        Button exit_btn=(Button) audiorecordView.findViewById(R.id.exit_btn);
        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        random = new Random();


        exit_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermission()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    String currentDateandTime = sdf.format(new Date());
                    myRecording = sdf.format(new Date());// + System.currentTimeMillis();


                    AudioSavePathInDevice = getfilepath().toString() + "mhealthApp" +
                            myRecording + ".mp3";

//                                        File video_file=new File(getfilepath().toString(),AudioSavePathInDevice);

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);
                    buttonPlayLastRecordAudio.setEnabled(false);

                    Toast.makeText(getActivity(), "Recording started at " + currentDateandTime,
                            Toast.LENGTH_LONG).show();
                } else {
                    requestPermission();
                }
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);

                Toast.makeText(getActivity(), "Recording Completed",
                        Toast.LENGTH_LONG).show();
            }
        });

        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(getActivity(), "Recording Playing",
                        Toast.LENGTH_LONG).show();
                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        buttonPlayLastRecordAudio.setEnabled(true);
                        buttonStart.setEnabled(true);
                        buttonStopPlayingRecording.setEnabled(false);
                    }
                });
            }
        });

        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });
        mBuilder1.setView(audiorecordView);
        dialog=mBuilder1.create();
        dialog.show();
    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getActivity(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    public File getfilepath()
    {
        File folder = new File("sdcard/Patient_app");
        if(!folder.exists())
        {
            folder.mkdir();
        }

        return folder;
    }
}
