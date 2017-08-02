package travis.thenewboston.com.mhealththeme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Shahzad Adil on 7/31/2017.
 */

class CustomAdapter extends BaseAdapter {

    private ArrayList<Patient> _data;
    Context _c;

    CustomAdapter (ArrayList<Patient> data, Context c){
        _data = data;
        _c = c;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return _data.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return _data.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.single_itemview, null);
        }

        ImageView image = (ImageView) v.findViewById(R.id.icon);
        TextView name = (TextView)v.findViewById(R.id.patient_name);
        TextView id = (TextView)v.findViewById(R.id.patient_id);

        Patient p = _data.get(position);
        //image.setImageResource(msg.icon);
        name.setText(p.name);
        id.setText("ID: "+p.id);
        //descView.setText(msg.desc);
        //timeView.setText(msg.time);

        return v;
    }
}