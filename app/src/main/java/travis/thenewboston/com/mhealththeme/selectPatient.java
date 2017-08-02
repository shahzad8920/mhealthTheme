package travis.thenewboston.com.mhealththeme;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Shahzad Adil on 6/21/2017.
 */

public class selectPatient extends Fragment{
    DBHandler mydb=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.select_patient, container, false);
        mydb = new DBHandler(getActivity());
        getalldata(rootView);
        return rootView;
    }
    public void getalldata(View rv)
    {
        try {
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
            ArrayAdapter adapter = new ArrayAdapter<Patient>(getActivity(),
                    R.layout.single_itemview, array);
            final ListView listView = (ListView) rv.findViewById(R.id.patient_list);
            listView.setAdapter(new CustomAdapter(array,getActivity()));

            listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                    Patient p=(Patient)listView.getItemAtPosition(position);
                    String s=p.get_name();
                    MainActivity.p_id=p.get_id();
                    //Toast.makeText(getActivity(), s+" "+String.valueOf(MainActivity.p_id), Toast.LENGTH_SHORT).show();

                    ViewPager viewPager = (ViewPager) getActivity().findViewById(
                            R.id.container);
                    viewPager.setCurrentItem(2);
                }
            });
        }
        catch (Exception e)
        {
            //Toast.makeText(getActivity(), String.valueOf(e), Toast.LENGTH_SHORT).show();
        }
    }
}
