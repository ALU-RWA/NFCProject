package com.example.nfcproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

class DataBaseHelper extends SQLiteOpenHelper {
    public static String DBName = "Attendande Database";
    public static final int VERSION = 1;

    public static final String DB_TABLE = "Students";
    public static final String TableID = "ID";
    public static final String COL0 = "TagNo";
    public static final String COL1 = "RegNo";
    public static final String COL2 = "Name";
    public static final String COL3 = "Password";
    public static final String COL4 = "Email";

    private static final String CREATE_TABLE = "create table " + DB_TABLE +
            "(" + TableID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL0 + " TEXT NOT NULL, " + COL1 + " TEXT NOT NULL, " + COL2 + " TEXT NOT NULL, " + COL3 + " TEXT, " + COL4 + " TEXT);";

    public DataBaseHelper(@Nullable Context context) {

        super(context, DBName, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);

    }
    public boolean addStudent(String Tag, String RegNo, String Name, String password, String email){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COL0, Tag);
        values.put(COL1, RegNo);
        values.put(COL2, Name);
        values.put(COL3, password);
        values.put(COL4, email);

        long result = db.insert(DB_TABLE, null, values);
        if (result==-1)
            return false;
        else
            return true;
    }
    public Cursor ReadData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dataToRead = db.rawQuery("select * from " + DB_TABLE, null);
        return dataToRead;

    }
    public Integer deleteData(String RegNo){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DB_TABLE, "RegNo=?", new String[] {RegNo});
    }

    public Cursor readIndividualData(CharSequence s) {
        String[]TagNumber = new String[]{String.valueOf(s)};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DB_TABLE + " WHERE TagNo = ? ", TagNumber);
        return res;
    }
    public Boolean UpdateRecord (String Tag, String RegNo, String Name, String password, String email){
     SQLiteDatabase db  = this.getReadableDatabase();
     ContentValues cv = new ContentValues();
        cv.put(COL0, Tag);
        cv.put(COL1, RegNo);
        cv.put(COL2, Name);
        cv.put(COL3, password);
        cv.put(COL4, email);
    long dataupdated=db.update(DB_TABLE, cv, "TagNo = ? ", new String[] { Tag });
//    System.out.println("testing this: " +dataupdated);
        if (dataupdated==1)
            return true;
        else
            return false;
    }
}
