package travis.thenewboston.com.mhealththeme;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
    public void getalldata(View v)
    {
        int i=0;
        try {
            Cursor cursor = mydb.getalldata();
            if (cursor.moveToFirst()) {
                do {
                    i++;
                } while (cursor.moveToNext());
            }
            String[] array = new String[i];
            String[] array1 = new String[i];
                i=0;
            if (cursor.moveToFirst()) {
                do {
                    String s1 = cursor.getString(cursor.getColumnIndex("name"));
                    String s2 = cursor.getString(cursor.getColumnIndex("id"));
                    array[i++] = s1;
                    array1[i++] = s2;
                } while (cursor.moveToNext());
            }
            ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.single_itemview, array);
            ListView listView = (ListView) v.findViewById(R.id.patient_list);
            listView.setAdapter(adapter);
        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(), String.valueOf(e), Toast.LENGTH_LONG).show();
        }
    }
}
