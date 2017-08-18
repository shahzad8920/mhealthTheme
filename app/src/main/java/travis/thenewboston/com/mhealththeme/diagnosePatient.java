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
import android.widget.EditText;
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
    Spinner cuff_spinner,lungs_spinner,temp_spinner,pulse_spinner,bp_spinner,resp_spinner,o2_spinner,concious_spinner;
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
    String tempval=null;

    String[] array,array1;
    ArrayAdapter<String> adapter,adapter1;
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
                cuff_spinner.setSelection(0);
                lungs_spinner.setSelection(0);
                array1=new String[]{"Select","Manual input Values","Use External Sensors","Use Internal Sensors"};
                adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,array1);
                // Specify the layout to use when the list of choices appears
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                temp_spinner.setAdapter(adapter1);
                pulse_spinner.setAdapter(adapter1);
                bp_spinner.setAdapter(adapter1);
                resp_spinner.setAdapter(adapter1);
                o2_spinner.setAdapter(adapter1);
                concious_spinner.setAdapter(adapter1);
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
        temp_spinner=(Spinner) rv.findViewById(R.id.temp_spinner);
        pulse_spinner=(Spinner) rv.findViewById(R.id.pulse_spinner);
        bp_spinner=(Spinner) rv.findViewById(R.id.bp_spinner);
        o2_spinner=(Spinner) rv.findViewById(R.id.o2_spinner);
        resp_spinner=(Spinner) rv.findViewById(R.id.resp_spinner);
        concious_spinner=(Spinner) rv.findViewById(R.id.concious_spinner);

        array=new String[]{"Select","Record Sound","Previous Recordings"};
        array1=new String[]{"Select","Manual input Values","Use External Sensors","Use Internal Sensors"};

        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,array);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,array1);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        cuff_spinner.setAdapter(adapter);
        lungs_spinner.setAdapter(adapter);
        temp_spinner.setAdapter(adapter1);
        pulse_spinner.setAdapter(adapter1);
        bp_spinner.setAdapter(adapter1);
        resp_spinner.setAdapter(adapter1);
        o2_spinner.setAdapter(adapter1);
        concious_spinner.setAdapter(adapter1);

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
        temp_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            String item=(String) parent.getItemAtPosition(position);
            if(item.equals("Select"))
            {
                //Toast.makeText(getActivity(), "Kindly Select an option first", Toast.LENGTH_LONG).show();
            }
            else if(item.equals("Manual input Values")&& MainActivity.p_id!=0)
            {
                recordtemp();
            }
            else if(item.equals("Use External Sensors") && MainActivity.p_id!=0)
            {

            }
            else if(item.equals("Use Internal Sensors") && MainActivity.p_id!=0)
            {

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
        pulse_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item=(String) parent.getItemAtPosition(position);
                if(item.equals("Select"))
                {
                    //Toast.makeText(getActivity(), "Kindly Select an option first", Toast.LENGTH_LONG).show();
                }
                else if(item.equals("Manual input Values")&& MainActivity.p_id!=0)
                {
                    recordpulse();
                }
                else if(item.equals("Use External Sensors") && MainActivity.p_id!=0)
                {

                }
                else if(item.equals("Use Internal Sensors") && MainActivity.p_id!=0)
                {

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
        bp_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item=(String) parent.getItemAtPosition(position);
                if(item.equals("Select"))
                {
                    //Toast.makeText(getActivity(), "Kindly Select an option first", Toast.LENGTH_LONG).show();
                }
                else if(item.equals("Manual input Values")&& MainActivity.p_id!=0)
                {
                    recordbp();
                }
                else if(item.equals("Use External Sensors") && MainActivity.p_id!=0)
                {

                }
                else if(item.equals("Use Internal Sensors") && MainActivity.p_id!=0)
                {

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
        resp_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item=(String) parent.getItemAtPosition(position);
                if(item.equals("Select"))
                {
                    //Toast.makeText(getActivity(), "Kindly Select an option first", Toast.LENGTH_LONG).show();
                }
                else if(item.equals("Manual input Values")&& MainActivity.p_id!=0)
                {
                    respiration();
                }
                else if(item.equals("Use External Sensors") && MainActivity.p_id!=0)
                {

                }
                else if(item.equals("Use Internal Sensors") && MainActivity.p_id!=0)
                {

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
        o2_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item=(String) parent.getItemAtPosition(position);
                if(item.equals("Select"))
                {
                    //Toast.makeText(getActivity(), "Kindly Select an option first", Toast.LENGTH_LONG).show();
                }
                else if(item.equals("Manual input Values")&& MainActivity.p_id!=0)
                {
                    recordo2();
                }
                else if(item.equals("Use External Sensors") && MainActivity.p_id!=0)
                {

                }
                else if(item.equals("Use Internal Sensors") && MainActivity.p_id!=0)
                {

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
        concious_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item=(String) parent.getItemAtPosition(position);
                if(item.equals("Select"))
                {
                    //Toast.makeText(getActivity(), "Kindly Select an option first", Toast.LENGTH_LONG).show();
                }
                else if(item.equals("Manual input Values")&& MainActivity.p_id!=0)
                {
                    recordconcious();

                }
                else if(item.equals("Use External Sensors") && MainActivity.p_id!=0)
                {

                }
                else if(item.equals("Use Internal Sensors") && MainActivity.p_id!=0)
                {

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
    public void recordconcious()
    {
        LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View conciousview = inf.inflate(R.layout.record_conciousness, null);

        Button concious_btn=(Button) conciousview.findViewById(R.id.concious_ok_button);
        array1=new String[]{"Alert","Confused","Lethargic"};
        adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,array1);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner concious_level_spinner= (Spinner) conciousview.findViewById(R.id.conciuosness_level_spinner_id);

        concious_level_spinner.setAdapter(adapter1);


        AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(getActivity());

        concious_level_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                tempval=(String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        concious_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {

                array1=new String[]{tempval,"Manual input Values","Use External Sensors","Use Internal Sensors"};
                adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,array1);
                // Specify the layout to use when the list of choices appears
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                concious_spinner.setAdapter(adapter1);
                concious_spinner.setSelection(0);
                dialog1.dismiss();
            }
        });
        mBuilder1.setView(conciousview);
        dialog1 = mBuilder1.create();
        dialog1.show();
    }
    public void recordo2()
    {
        LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View o2view = inf.inflate(R.layout.record_o2_saturation, null);

        final EditText o2_start_editText=(EditText) o2view.findViewById(R.id.o2_start_id);

        final EditText o2_end_editText=(EditText) o2view.findViewById(R.id.o2_end_id);

        Button o2_btn=(Button) o2view.findViewById(R.id.o2_ok_button);

        AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(getActivity());

        o2_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                tempval=o2_start_editText.getText().toString()+" up to "+o2_end_editText.getText().toString();

                array1=new String[]{tempval,"Manual input Values","Use External Sensors","Use Internal Sensors"};
                adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,array1);
                // Specify the layout to use when the list of choices appears
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                o2_spinner.setAdapter(adapter1);
                o2_spinner.setSelection(0);
                dialog1.dismiss();
            }
        });
        mBuilder1.setView(o2view);
        dialog1 = mBuilder1.create();
        dialog1.show();
    }
    public void recordtemp()
    {
        LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View tempview = inf.inflate(R.layout.record_temp, null);

        final EditText temp_editText=(EditText) tempview.findViewById(R.id.temp_id);

        Button temp_btn=(Button) tempview.findViewById(R.id.temp_ok_button);

        AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(getActivity());

        temp_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                tempval=temp_editText.getText().toString();

                array1=new String[]{tempval,"Manual input Values","Use External Sensors","Use Internal Sensors"};
                adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,array1);
                // Specify the layout to use when the list of choices appears
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                temp_spinner.setAdapter(adapter1);
                temp_spinner.setSelection(0);
                dialog1.dismiss();
            }
        });
        mBuilder1.setView(tempview);
        dialog1 = mBuilder1.create();
        dialog1.show();
    }
    public void recordpulse()
    {
        LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View tempview = inf.inflate(R.layout.record_pulse, null);

        final EditText pulse_editText=(EditText) tempview.findViewById(R.id.pulse_id);

        Button pulse_btn=(Button) tempview.findViewById(R.id.pulse_ok_button);

        AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(getActivity());

        pulse_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                tempval=pulse_editText.getText().toString();

                array1=new String[]{tempval,"Manual input Values","Use External Sensors","Use Internal Sensors"};
                adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,array1);
                // Specify the layout to use when the list of choices appears
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                pulse_spinner.setAdapter(adapter1);
                pulse_spinner.setSelection(0);
                dialog1.dismiss();
            }
        });
        mBuilder1.setView(tempview);
        dialog1 = mBuilder1.create();
        dialog1.show();
    }
    public void recordbp()
    {
        LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View tempview = inf.inflate(R.layout.record_bp, null);

        final EditText sysbp_editText=(EditText) tempview.findViewById(R.id.systolic_bp_id);

        final EditText diastolicbp_editText=(EditText) tempview.findViewById(R.id.diastolic_bp_id);

        Button bp_btn=(Button) tempview.findViewById(R.id.bp_ok_button);

        AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(getActivity());

        bp_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                tempval=sysbp_editText.getText().toString()+"/"+diastolicbp_editText.getText().toString();

                array1=new String[]{tempval,"Manual input Values","Use External Sensors","Use Internal Sensors"};
                adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,array1);
                // Specify the layout to use when the list of choices appears
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                bp_spinner.setAdapter(adapter1);
                bp_spinner.setSelection(0);
                dialog1.dismiss();
            }
        });
        mBuilder1.setView(tempview);
        dialog1 = mBuilder1.create();
        dialog1.show();
    }
    public void respiration()
    {
        LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View tempview = inf.inflate(R.layout.record_respiration, null);

        final EditText resp_start_editText=(EditText) tempview.findViewById(R.id.resp_start_id);

        final EditText resp_end_editText=(EditText) tempview.findViewById(R.id.resp_end_id);

        Button resp_btn=(Button) tempview.findViewById(R.id.resp_ok_button);

        AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(getActivity());

        resp_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                tempval=resp_start_editText.getText().toString()+" up to "+resp_end_editText.getText().toString();

                array1=new String[]{tempval,"Manual input Values","Use External Sensors","Use Internal Sensors"};
                adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,array1);
                // Specify the layout to use when the list of choices appears
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                resp_spinner.setAdapter(adapter1);
                resp_spinner.setSelection(0);
                dialog1.dismiss();
            }
        });
        mBuilder1.setView(tempview);
        dialog1 = mBuilder1.create();
        dialog1.show();
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
                    //Toast.makeText(getActivity(), getfilepath().toString() ,
                      //      Toast.LENGTH_LONG).show();
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
