package travis.thenewboston.com.mhealththeme;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Shahzad Adil on 6/21/2017.
 */

public class deletePatient extends Fragment{
    Button btn_deletedata;
    DBHandler mydb=null;
    TextView id_tv,name_tv,address_tv,number_tv;
    EditText editid;
    Cursor cursor;
    Patient p;
    AlertDialog dialog;
    int id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.delete_patient, container, false);
        p= new Patient();
        mydb = new DBHandler(getActivity());
        editid=(EditText) rootView.findViewById(R.id.edit_id);
        btn_deletedata=(Button)rootView.findViewById(R.id.delete_button);
        deletedata();
        return rootView;
    }
    public void deletedata()
    {
        try {
            btn_deletedata.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    MainActivity.p_id = Integer.parseInt(editid.getText().toString());

                    cursor = mydb.getdata(MainActivity.p_id);

                    int i = cursor.getCount();
                    if (i == 0) {
                        Toast.makeText(getActivity(), "Kindly Enter A Valid ID", Toast.LENGTH_SHORT).show();
                    } else {
                        if (cursor.moveToFirst()) {
                            do {
                                p.set_name(cursor.getString(cursor.getColumnIndex("name")));//cursor.getString(cursor.getColumnIndex("name"));
                                p.set_id(Integer.parseInt(editid.getText().toString()));
                                p.set_phone_number(cursor.getString(cursor.getColumnIndex("phone_number")));
                                p.set_address(cursor.getString(cursor.getColumnIndex("address")));
                            } while (cursor.moveToNext());
                        }

                        //Toast.makeText(getActivity(), "ID:" + String.valueOf(MainActivity.p_id)+" "+p.get_name() , Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                        final View mView = getActivity().getLayoutInflater().inflate(R.layout.delete_alert, null);
                        final Button yes_btn = (Button) mView.findViewById(R.id.yes_button);
                        final Button no_btn = (Button) mView.findViewById(R.id.no_button);
                        id_tv = (TextView) mView.findViewById(R.id._id);
                        name_tv = (TextView) mView.findViewById(R.id._name);
                        address_tv = (TextView) mView.findViewById(R.id._address);
                        number_tv = (TextView) mView.findViewById(R.id._number);
                        id_tv.setText(String.valueOf(p.get_id()));
                        name_tv.setText(p.get_name());
                        address_tv.setText(p.get_address());
                        number_tv.setText(p.get_phone_number());

                        yes_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                int isdeleted = mydb.deletedata(MainActivity.p_id);
                                if (isdeleted > 0) {
                                    Toast.makeText(getActivity(), "ID:" + editid.getText().toString() + " data is deleted", Toast.LENGTH_SHORT).show();
                                    editid.getText().clear();
                                    MainActivity.p_id = 0;
                                    ViewPager viewPager = (ViewPager) getActivity().findViewById(
                                            R.id.container);
                                    viewPager.setCurrentItem(0);
                                } else {
                                    Toast.makeText(getActivity(), "ID:" + editid.getText().toString() + " data does not deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        no_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        mBuilder.setView(mView);
                        dialog = mBuilder.create();
                        dialog.show();
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
        }

    }
}
