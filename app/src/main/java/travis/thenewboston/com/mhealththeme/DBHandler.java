package travis.thenewboston.com.mhealththeme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shahzad Adil on 6/22/2017.
 */

public class DBHandler extends SQLiteOpenHelper {
    //All static variable
    //Database Version

    //Database Version
    private static final int DATABASE_VERSION=1;
    // Database Name
    private static final String DATABASE_NAME="Patient";
    //Table Name
    private static final String TABLE_PATIENT_DETAIL="patientDetail";

    //Table Columns Names
    private static final String KEY_ID="id";
    private static final String KEY_NAME="name";
    private static final String KEY_ADDRESS="address";
    private static final String KEY_PHONE_NUMBER="phone_number";

    //Constructor

    public DBHandler(Context context){super(context,DATABASE_NAME,null,DATABASE_VERSION);}

    //Creating Tables


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_PATIENT_DETAIL_TABLE="CREATE TABLE "+TABLE_PATIENT_DETAIL+"("
                + KEY_ID+" INTEGER PRIMARY KEY,"
                +KEY_NAME+" TEXT,"
                +KEY_ADDRESS  +" TEXT,"
                + KEY_PHONE_NUMBER + " TEXT " + ")" ;

        db.execSQL(CREATE_PATIENT_DETAIL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older tables if exist

        db.execSQL("DROP TABLE IF EXIST "+TABLE_PATIENT_DETAIL);

        /// Create Table again
        onCreate(db);
    }
}
