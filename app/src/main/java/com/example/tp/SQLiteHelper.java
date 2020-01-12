package com.example.tp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {

    //Contructor
    public SQLiteHelper(Context context , String name , SQLiteDatabase.CursorFactory factory , int version)
    {
        super(context , name , factory , version);
    }

    public void queryData(String sql)
    {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    //insert Data

    public void insertData(String name , String adresse , String phone ,String formation, String specialite ,byte[] image)
    {
        SQLiteDatabase database = getWritableDatabase();
        //query to insert record in databse table ;
        String sql = "INSERT INTO RECORD VALUES(NULL , ? , ? , ?, ? , ? ,?)"; // where "RECORD is table name in database
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1 , name);
        statement.bindString(2,adresse);
        statement.bindString(3,phone);
        statement.bindString(4,formation);
        statement.bindString(5,specialite);
        statement.bindBlob(6,image);

        statement.executeInsert();

    }

    //updateData

    public void updateData(String name , String adresse , String phone ,String formation,String specialite, byte[] image , int id)
    {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE RECORD SET name =? , adresse=? , phone=? ,formation = ?,specialite = ?, image=? WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1 , name);
        statement.bindString(2,adresse);
        statement.bindString(3,phone);
        statement.bindString(4,formation);
        statement.bindString(5,specialite);
        statement.bindBlob(6,image);
        statement.bindDouble(7,(double)id);

        statement.execute();
        database.close();
    }

    //delete data
    public void deleteData(int id)
    {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM RECORD WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindDouble(1,(double)id);

        statement.execute();
        database.close();
    }

    public Cursor getData(String sql)
    {
        SQLiteDatabase database = getWritableDatabase();
        return database.rawQuery(sql , null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
