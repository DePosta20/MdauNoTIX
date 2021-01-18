package mdausoft.co.tz.mdaunotix;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DB_helper extends SQLiteOpenHelper{
    public int delete=0;
    public Context cont;
    public static final String DATABASE_NAME = "mdausoftnotiX.db";
    public DB_helper(@Nullable Context context, int delete) {
        super(context, DATABASE_NAME, null, 1);
        this.cont = context;
        this.delete = delete;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.TABLE_NAME_user + " (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, full_name TEXT, username TEXT, email TEXT, password TEXT, phone_no TEXT, token TEXT, phone_brand TEXT, phone_model TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.TABLE_NAME_subjects + " (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, subject_name TEXT, subject_code TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.TABLE_NAME_notes + " (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, image_path TEXT, google_drive_id TEXT, prescription TEXT, subject_code TEXT, date_time TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ Constants.TABLE_NAME_user);
        db.execSQL("DROP TABLE IF EXISTS "+ Constants.TABLE_NAME_subjects);
        db.execSQL("DROP TABLE IF EXISTS "+ Constants.TABLE_NAME_notes);
        onCreate(db);
    }
    //////////////////////////////////////////////////////////////////////////////
    public boolean insert_notes(String image_path, String google_drive_id, String prescription, String subject_code){
        boolean bool;
        String date_time = Function.get_today_date();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("image_path", image_path);
        values.put("google_drive_id", google_drive_id);
        values.put("prescription", prescription);
        values.put("subject_code", subject_code);
        values.put("date_time", date_time);
        long result = db.insert(Constants.TABLE_NAME_notes, null, values);
        bool = result != -1;
        return bool;
    }
    //////////////////////////////////////////////////////////////////////////////
    public boolean insert_subject(String subject_name, String subject_code){
        boolean bool;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("subject_name", subject_name);
        values.put("subject_code", subject_code);
        long result = db.insert(Constants.TABLE_NAME_subjects, null, values);
        bool = result != -1;
        return bool;
    }
    //////////////////////////////////////////////////////////////////////////////
    public boolean insert_user_data(String full_name, String email, String phone_no, String password, String phone_brand, String phone_model, String token){
        boolean bool;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", full_name);
        values.put("email", email);
        values.put("password", password);
        values.put("phone_no", phone_no);
        values.put("phone_brand", phone_brand);
        values.put("phone_model", phone_model);
        values.put("token", token);
        long result = db.insert(Constants.TABLE_NAME_user, null, values);
        bool = result != -1;
        return bool;
    }
    //////////////////////////////////////////////////////////////////////////////
    public Cursor get_user_data(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + Constants.TABLE_NAME_user;
        return db.rawQuery(sql, null);
    }
    //////////////////////////////////////////////////////////////////////////////
    public boolean check_username(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME_user+" WHERE email=?",new String[]{email});
        return cursor.getCount() > 0;
    }
    //////////////////////////////////////////////////////////////////////////////
    public Integer delete_user_data(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Constants.TABLE_NAME_user, null,null);
    }
    //////////////////////////////////////////////////////////////////////////////
    public ArrayList<String> get_all_subjects() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME_subjects;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));//adding 2nd column data
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
    //////////////////////////////////////////////////////////////////////////////
    public Cursor get_from_table(String sqlQuery) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sqlQuery, null);
    }
}
