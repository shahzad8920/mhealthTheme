package travis.thenewboston.com.mhealththeme;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Shahzad Adil on 6/21/2017.
 */

public class editPatient extends Fragment{
    DBHandler mydb=null;
    EditText editname,editaddress,editph_no,editid;
    Button btn_updatedata;
    Cursor cursor;
    Patient p;
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
                editname.setText(p.get_name());
                editid.setText(String.valueOf(MainActivity.p_id));
                editaddress.setText(p.get_address());
                editph_no.setText(p.get_phone_number());
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
        View rootView = inflater.inflate(R.layout.edit_patient, container, false);
        p= new Patient();
        mydb = new DBHandler(getActivity());
        cursor = mydb.getdata(MainActivity.p_id);
        editid=(EditText) rootView.findViewById(R.id.edit_id);
        editname=(EditText) rootView.findViewById(R.id.edit_name);
        editaddress=(EditText) rootView.findViewById(R.id.edit_address);
        editph_no=(EditText) rootView.findViewById(R.id.edit_number);
        btn_updatedata=(Button) rootView.findViewById(R.id.update_button);
        updatedata();
        return rootView;
    }
    public void updatedata()
    {
        try {

            btn_updatedata.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(MainActivity.p_id!=0) {

                        boolean isupdate = mydb.updatedata(Integer.parseInt(editid.getText().toString()),
                                editname.getText().toString(),
                                editaddress.getText().toString(),
                                editph_no.getText().toString());
                        if (isupdate) {
                            Toast.makeText(getActivity(), "ID:" + editid.getText().toString() + " data is updated", Toast.LENGTH_SHORT).show();
                            editid.getText().clear();
                            editname.getText().clear();
                            editph_no.getText().clear();
                            editaddress.getText().clear();
                            MainActivity.p_id = 0;

                            ViewPager viewPager;
                            viewPager = (ViewPager) getActivity().findViewById(
                                    R.id.container);
                            viewPager.setCurrentItem(0);
                        } else {
                            Toast.makeText(getActivity(), "ID:" + editid.getText().toString() + " data does not updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Kindly Select a Patient First", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (Exception e)
        {
            if(MainActivity.p_id==0)
            {
                Toast.makeText(getActivity(), "Kindly select any patient first", Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(getActivity(), String.valueOf(e)+" "+String.valueOf(MainActivity.p_id), Toast.LENGTH_LONG).show();
        }
    }
}
