package com.manuelgarcia.pt16;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class TemperaturesHelper2 extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "OpenWeather.db";

    //constructor de la clase
    TemperaturesHelper2(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TemperaturesContract.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(TemperaturesContract.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    boolean estaCiutatDescarregada(String nomCiutat) {
        SQLiteDatabase db = this.getReadableDatabase();


        //db.query...

        //if (cursor.moveToFirst() és fals, o altres maneres...
        return false;

    }

    void guarda(String nomCiutat, List<Bloc> blocs) throws ParseException {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = null;
        for (int i = 0; i < blocs.size(); i++) {
            values = new ContentValues();
            values.put(TemperaturesContract.NOMBRE_COLUMNA_NOMCIUTAT, nomCiutat);
            values.put(TemperaturesContract.NOMBRE_COLUMNA_HORES, blocs.get(i).getData());
            values.put(TemperaturesContract.NOMBRE_COLUMNA_TEMPS, blocs.get(i).getTempe().replace(",", "."));

            db.insert(TemperaturesContract.TABLE_NAME, null, values);
        }
    }

    public List<Bloc> llegeix(String nomCiutat) throws ParseException {

        List<Bloc> mostrar = new ArrayList<>();
        Bloc ciutat;

        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                TemperaturesContract.NOMBRE_COLUMNA_NOMCIUTAT,
                TemperaturesContract.NOMBRE_COLUMNA_HORES,
                TemperaturesContract.NOMBRE_COLUMNA_TEMPS

        };

        String selection = TemperaturesContract.NOMBRE_COLUMNA_NOMCIUTAT + " = ?";
        String[] selectionArgs = {nomCiutat};


        return mostrar;

    }

    boolean estaActualitzada(String nomCiutat) throws ParseException {

        SQLiteDatabase db = this.getReadableDatabase();
        String hora = "";
        Date fechaBBDD = new Date();
        Date fechanow = new Date();


        return true;

    }


    public void eliminaDades(String nomCiutat) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(nomCiutat, null, null);

    }
}