package com.prometteur.divaism.LocalDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.prometteur.divaism.PojoModels.SavedFormDataPojo;
import com.prometteur.divaism.Utils.CommonMethods;


import java.util.ArrayList;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DIVAISM";
    private static final String KEY_FORMID="FormID";
    private static final String KEY_USERID="UserID";
    private static final String KEY_DATA="FORM_JSON";


    Context context;

    public DatabaseHelper(@Nullable Context context) {
        super(context, "DivaismDB", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(" CREATE TABLE " + DATABASE_NAME + " (" +
                KEY_USERID + " TEXT NOT NULL, " +
                KEY_DATA + " TEXT NOT NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addDataToDatabase(String dataJson,String id) {
        SQLiteDatabase sq = this.getReadableDatabase();
        ContentValues cl = new ContentValues();
        cl.put(KEY_DATA, dataJson);
        cl.put(KEY_USERID, id);

        long i = sq.insert(DATABASE_NAME, null, cl);

        Toast.makeText(context, "Request Form is successfully saved", Toast.LENGTH_SHORT).show();
        sq.close();
    }

    public ArrayList<SavedFormDataPojo> getDataFromDatabase() {

        SQLiteDatabase sd = this.getReadableDatabase();

            Cursor cursor = sd.query(DATABASE_NAME, null, null, null, null, null, null);
            ArrayList<SavedFormDataPojo> arrayList = new ArrayList<>();


            if (cursor != null && cursor.moveToFirst() && cursor.getColumnIndex(KEY_USERID)!=-1) {
                do {
                    //int id = cursor.getInt(cursor.getColumnIndex(KEY_FORMID));
                    String data = cursor.getString(cursor.getColumnIndex(KEY_DATA));
                    String userID = cursor.getString(cursor.getColumnIndex(KEY_USERID));

//
                    arrayList.add(new SavedFormDataPojo(userID, data));
                } while (cursor.moveToNext());
            }
            cursor.close();
            Log.v("Size", arrayList.size() + "");
            return arrayList;


    }

    public void updateData(String dataJson,String id){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cl = new ContentValues();
        cl.put(KEY_DATA, dataJson);
        cl.put(KEY_USERID, id);
        database.update(DATABASE_NAME,cl,"UserID=?",new String[]{id});
    }

   /* public SavedFormDataPojo getSingleFormData(int FormId){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor c=database.rawQuery("SELECT * FROM" + DATABASE_NAME+ "WHERE" + KEY_FORMID+"= "+FormId+"'",null);

        c.moveToNext();
        database.close();
        return dataPojo;
    }*/

    public void removeDataFromDatabase(int id) {
        //Open the database
        SQLiteDatabase database = this.getWritableDatabase();

        database.execSQL("DELETE FROM " + DATABASE_NAME + " WHERE " + KEY_FORMID + "= '" + id + "'");
        database.close();
    }
}
