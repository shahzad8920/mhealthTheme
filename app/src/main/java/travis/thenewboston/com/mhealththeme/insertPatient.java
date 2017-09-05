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

import java.util.ArrayList;

/**
 * Created by Shahzad Adil on 6/21/2017.
 */

public class insertPatient extends Fragment {
    DBHandler mydb=null;
    EditText editname,editaddress,editph_no;
    Button btn_insertdata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.insert_patient, container, false);
        mydb = new DBHandler(getActivity());
        editname=(EditText) rootView.findViewById(R.id.insert_name);
        editaddress=(EditText) rootView.findViewById(R.id.insert_address);
        editph_no=(EditText) rootView.findViewById(R.id.insert_ph_no);
        btn_insertdata=(Button) rootView.findViewById(R.id.insert_button);
        insertdata();
        return rootView;
    }

    public void insertdata()
    {
       try {
           btn_insertdata.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {


                   boolean isinserted = mydb.insertData(editname.getText().toString(), editaddress.getText().toString(), editph_no.getText().toString());

                   if (isinserted)
                   {
                       Cursor cursor = mydb.getalldata();
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
                       adapter.notifyDataSetChanged();
                       Toast.makeText(getActivity(), "Patient data saved", Toast.LENGTH_LONG).show();
                       editname.setText("");
                       editaddress.setText("");
                       editph_no.setText("");

                       ViewPager viewPager = (ViewPager) getActivity().findViewById(
                               R.id.container);

                       viewPager.setCurrentItem(0);

                   }
                   else
                   {
                       Toast.makeText(getActivity(), "Patient data does Not saved", Toast.LENGTH_LONG).show();
                       editname.setText("");
                       editaddress.setText("");
                       editph_no.setText("");
                   }
       }
       });
       }
       catch (Exception e) {
           //Toast.makeText(getActivity(), String.valueOf(e), Toast.LENGTH_LONG).show();
       }
    }
}
