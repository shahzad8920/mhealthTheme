package travis.thenewboston.com.mhealththeme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Shahzad Adil on 6/21/2017.
 */

public class insertPatient extends Fragment {
    DBHandler mydb=null;
    EditText editname,editaddress,editph_no;
    Button btn_insertdata,btn_viewdata;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.insert_patient, container, false);

        mydb = new DBHandler(getActivity());

        editname=(EditText) rootView.findViewById(R.id.insert_name);
        editaddress=(EditText) rootView.findViewById(R.id.insert_address);
        editph_no=(EditText) rootView.findViewById(R.id.insert_ph_no);
        btn_insertdata=(Button) rootView.findViewById(R.id.insert_button);
        btn_insertdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertdata();
            }
        });
        return rootView;
    }

    public void insertdata()
    {
       try {
           boolean isinserted = mydb.insertData(editname.getText().toString(), editaddress.getText().toString(), editph_no.getText().toString());

           if (isinserted) {
               Toast.makeText(getActivity(), "data saved", Toast.LENGTH_LONG).show();
           } else {
               Toast.makeText(getActivity(), "data Not saved", Toast.LENGTH_LONG).show();
           }
       }
       catch (Exception e) {
           Toast.makeText(getActivity(), String.valueOf(e), Toast.LENGTH_LONG).show();
       }
    }
}
