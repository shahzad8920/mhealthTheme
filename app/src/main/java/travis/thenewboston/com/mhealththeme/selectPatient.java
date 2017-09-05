package travis.thenewboston.com.mhealththeme;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Shahzad Adil on 6/21/2017.
 */

public class selectPatient extends Fragment{
    DBHandler mydb=null;
    AlertDialog dialog;
    Cursor cursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.select_patient, container, false);
        mydb = new DBHandler(getActivity());
        MainActivity.listView = (ListView) rootView.findViewById(R.id.patient_list);
        getalldata(rootView);
        return rootView;
    }
    public void getalldata(View rv)
    {
        try {

            cursor = mydb.getalldata();
            ArrayList<Patient> array = new ArrayList<Patient>();
            if (cursor.moveToFirst()) {
                do {
                    Patient p = new Patient();
                    p.set_name(cursor.getString(cursor.getColumnIndex("name")));//cursor.getString(cursor.getColumnIndex("name"));
                    p.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id"))));
                    p.set_phone_number(cursor.getString(cursor.getColumnIndex("phone_number")));
                    p.set_address(cursor.getString(cursor.getColumnIndex("address")));
                    array.add(p);
                } while (cursor.moveToNext());
            }
            CustomAdapter adapter=new CustomAdapter(array,getActivity());
            MainActivity.listView.setAdapter(adapter);
            MainActivity.listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                    Patient p=(Patient)MainActivity.listView.getItemAtPosition(position);
                    String s=p.get_name();
                    MainActivity.p_id=p.get_id();
                    //Toast.makeText(getActivity(), s+" "+String.valueOf(MainActivity.p_id), Toast.LENGTH_SHORT).show();



                    try {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                        final View mView = getActivity().getLayoutInflater().inflate(R.layout.action_alert, null);
                        final Button edit_btn = (Button) mView.findViewById(R.id.edit_button);
                        final Button delete_btn = (Button) mView.findViewById(R.id.delete_button);
                        final Button diagnose_btn = (Button) mView.findViewById(R.id.diagnose_button);
                        edit_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                ViewPager viewPager = (ViewPager) getActivity().findViewById(
                                        R.id.container);
                                viewPager.setCurrentItem(2);
                            }
                        });
                        delete_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                ViewPager viewPager = (ViewPager) getActivity().findViewById(
                                        R.id.container);
                                viewPager.setCurrentItem(3);
                            }
                        });
                        diagnose_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                ViewPager viewPager = (ViewPager) getActivity().findViewById(
                                        R.id.container);
                                viewPager.setCurrentItem(4);
                            }
                        });

                        mBuilder.setView(mView);
                        dialog = mBuilder.create();
                        dialog.show();
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getActivity(),String.valueOf(e), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        catch (Exception e)
        {
            //Toast.makeText(getActivity(), String.valueOf(e), Toast.LENGTH_SHORT).show();
        }
    }
}
