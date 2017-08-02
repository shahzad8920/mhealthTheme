package travis.thenewboston.com.mhealththeme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    private static final String DATABASE_NAME="Patient.db";
    //Table Name
    private static final String TABLE_NAME="patientDetail";

    //Table Columns Names
    private static final String ID="id";
    private static final String NAME="name";
    private static final String ADDRESS="address";
    private static final String PHONE_NUMBER="phone_number";

    //Constructor

    public DBHandler(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    //Creating Tables

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_PATIENT_DETAIL_TABLE="CREATE TABLE "+TABLE_NAME+"("
                + ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +NAME+" TEXT,"
                +ADDRESS  +" TEXT,"
                +PHONE_NUMBER + " TEXT )" ;

        db.execSQL(CREATE_PATIENT_DETAIL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older tables if exist

        db.execSQL("DROP TABLE IF EXIST "+TABLE_NAME);

        /// Create Table again
        onCreate(db);
    }
    public boolean insertData(String name,String address, String ph_no)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(NAME,name);
        values.put(ADDRESS,address);
        values.put(PHONE_NUMBER,ph_no);
        long result = db.insert(TABLE_NAME,null,values);
        if(result == -1)
            return  false;
        else
            return true;

    }

    public Cursor getalldata()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
    public Cursor getdata(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query(TABLE_NAME, null ,"id = ?", new String[]{ String.valueOf(id) },null,null,null);
        return res;
    }
    public boolean updatedata(int id,String name,String address, String ph_no)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(ID,id);
        values.put(NAME,name);
        values.put(ADDRESS,address);
        values.put(PHONE_NUMBER,ph_no);
        db.update(TABLE_NAME,values," id= ?",new String[] { String.valueOf(id) });
        return true;
    }

    public int deletedata(int id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete(TABLE_NAME,"id=?",new String []{String.valueOf(id)});

    }
}
