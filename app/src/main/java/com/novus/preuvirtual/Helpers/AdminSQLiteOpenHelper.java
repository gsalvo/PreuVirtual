package com.novus.preuvirtual.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    String preguntas = "CREATE TABLE pregunta(idPregunta integer primary key," +
            "pregunta text, imagen text, altA text, altB text, altC text, altD text, altE text,"+
            "altCorrecta text, altImagen integer)";
    String resEnsayo = "CREATE TABLE resEnsayo(_id integer primary key, idPregunta integer, respuesta integer, correcta integer)";
    String bPreguntas = "drop table if exists pregunta";
    String bResEnsayo = "drop table if exists resEnsayo";


    public AdminSQLiteOpenHelper(Context context, String nombre, SQLiteDatabase.CursorFactory factory, int version){
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(preguntas);
        db.execSQL(resEnsayo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva){
        db.execSQL(bPreguntas);
        db.execSQL(bResEnsayo);
    }
}
