package travis.thenewboston.com.mhealththeme;

import android.content.Context;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
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
    AlertDialog dialog,dialog1;
    //Media Player Variables

    Button buttonSaveRecord, buttonStart, buttonStop, buttonPlayLastRecordAudio,exit_btn,
            buttonStopPlayingRecording ;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    View audiolistview;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    String myRecording;
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    boolean save_flag;

    String spinner_text;

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

        save_flag=false;

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

                spinner_text="cuff";
                String item=(String) parent.getItemAtPosition(position);
                if(item.equals("Select"))
                {
                    //Toast.makeText(getActivity(), "Kindly Select an option first", Toast.LENGTH_LONG).show();
                }
                else if(item.equals("Record Sound")&& MainActivity.p_id!=0)
                {
                    Toast.makeText(getActivity(), item, Toast.LENGTH_LONG).show();
                    recordaudio();
                }
                else if(item.equals("Previous Recordings") && MainActivity.p_id!=0)
                {
                    filesview();
                }
                else if(MainActivity.p_id==0)
                {
                    Toast.makeText(getActivity(), "Kindly Select a Patient First", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lungs_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinner_text="lungs";
                String item=(String) parent.getItemAtPosition(position);
                if(item.equals("Select"))
                {
                    //Toast.makeText(getActivity(), "Kindly Select an option first", Toast.LENGTH_LONG).show();
                }
                else if(item.equals("Record Sound")&& MainActivity.p_id!=0)
                {
                    Toast.makeText(getActivity(), item, Toast.LENGTH_LONG).show();
                    recordaudio();
                }
                else if(item.equals("Previous Recordings") && MainActivity.p_id!=0)
                {
                    filesview();
                }
                else if(MainActivity.p_id==0)
                {
                    Toast.makeText(getActivity(), "Kindly Select a Patient First", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public void filesview()
    {

        try {

            if(getallfiles().length>0) {
                LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                audiolistview = inf.inflate(R.layout.recording_list, null);

                ListView listView = (ListView) audiolistview.findViewById(R.id.all_recordings_list);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.single_recording, R.id.recording_name, getallfiles());

                listView.setAdapter(adapter);

                AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(getActivity());


                listView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {


                        AudioSavePathInDevice=getfilepath().toString() +"/"+ a.getItemAtPosition(position);

                        //Toast.makeText(getActivity(), AudioSavePathInDevice, Toast.LENGTH_LONG).show();

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


                    }
                });

                mBuilder1.setView(audiolistview);
                dialog1 = mBuilder1.create();
                dialog1.show();

            }
            else
            {
                Toast.makeText(getActivity(), "No Record Found", Toast.LENGTH_LONG).show();
            }

/*
            AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(getActivity());
            View audiolistview = getActivity().getLayoutInflater().inflate(R.layout.recording_list, null);

            mBuilder1.setView(audiolistview);
            dialog1=mBuilder1.create();
            dialog1.show();
            //Toast.makeText(getActivity(), String.valueOf(names.length), Toast.LENGTH_LONG).show();
*/
/*
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.single_recording, array_list);

            AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(getActivity());
            View audiolistview = getActivity().getLayoutInflater().inflate(R.layout.recording_list, null);

            //Populating List
            final ListView listView = (ListView) audiolistview.findViewById(R.id.all_recordings_list);
            listView.setAdapter(adapter);


            listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                    Toast.makeText(getActivity(), String.valueOf(position)+" item selected", Toast.LENGTH_LONG).show();
                }
            });

            mBuilder1.setView(audiolistview);
            dialog1=mBuilder1.create();
            dialog1.show();*/
        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public String[] getallfiles()
    {
        File dir = new File(getfilepath().toString());

        String[] names = dir.list(
                new FilenameFilter() {
                    public boolean accept(File dir, String name) {

                        return name.endsWith(p.get_name() + String.valueOf(p.get_id())+ spinner_text + ".mp3");

                        // Example
                        // return name.endsWith(".mp3");
                    }
                });
        return names;
    }
    public void recordaudio()
    {
        AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(getActivity());
        final View audiorecordView = getActivity().getLayoutInflater().inflate(R.layout.audiorecord, null);

        buttonStart = (Button) audiorecordView.findViewById(R.id.record_btn);
        buttonStop = (Button) audiorecordView.findViewById(R.id.record_stop_btn);
        buttonPlayLastRecordAudio = (Button) audiorecordView.findViewById(R.id.play_btn);
        buttonStopPlayingRecording = (Button) audiorecordView.findViewById(R.id.play_stop_btn);
        exit_btn=(Button) audiorecordView.findViewById(R.id.exit_btn);
        buttonSaveRecord=(Button) audiorecordView.findViewById(R.id.save_button);
        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        random = new Random();


        exit_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                if(!save_flag && AudioSavePathInDevice!=null)
                {
                    new File(AudioSavePathInDevice).delete();
                }
                dialog.dismiss();

            }
        });
        buttonSaveRecord.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                save_flag = true;
                Toast.makeText(getActivity(), "Recording saved",
                        Toast.LENGTH_LONG).show();
            }
        });
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!save_flag && AudioSavePathInDevice!=null)
                {
                    new File(AudioSavePathInDevice).delete();
                }
                save_flag=false;
                if (checkPermission()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    String currentDateandTime = sdf.format(new Date());
                    myRecording = sdf.format(new Date());// + System.currentTimeMillis();

                    myRecording=myRecording+p.get_name()+String.valueOf(p.get_id());

                    File path=new File(getfilepath(),myRecording + spinner_text + ".mp3");

                    AudioSavePathInDevice = path.toString();
                    //AudioSavePathInDevice = getfilepath().toString() +
                     //       myRecording + ".mp3";

//                                        File video_file=new File(getfilepath().toString(),AudioSavePathInDevice);
                    Toast.makeText(getActivity(), getfilepath().toString() ,
                            Toast.LENGTH_LONG).show();
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

                    Toast.makeText(getActivity(), "Recording started ",
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
        File folder = new File("sdcard/Patient_App");
        if(!folder.exists())
        {
            folder.mkdirs();
        }

        return folder;
    }
}
