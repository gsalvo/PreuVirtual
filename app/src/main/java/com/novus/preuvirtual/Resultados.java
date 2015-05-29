package com.novus.preuvirtual;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class Resultados extends ActionBarActivity {


    TextView tRespuesta, tPreguntas, rCorrectas, rIncorrectas,rOmitidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);


        tRespuesta = (TextView) findViewById(R.id.textTiempoRespuestaNum);
        tPreguntas = (TextView) findViewById(R.id.textTotalPreguntasNum);
        rCorrectas =(TextView) findViewById(R.id.textRespuestasCorrectasNum);
        rIncorrectas =(TextView) findViewById(R.id.textRespuestasIncorrectasNum);
        rOmitidas =(TextView) findViewById(R.id.textRespuestasOmitidasNum);

        int cantIncorrectas, cantCorrectas, cantOmitidas, cantTotal;
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"preuVirtual",null,1);


        SQLiteDatabase bd = admin.getReadableDatabase();

       /* SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("idPregunta", 1);
        registro.put("respuesta", "holo vengo a flotar");
        registro.put("correcta", 0);
        bd.insert("resEnsayo", null,registro);
        registro = new ContentValues();
        registro.put("idPregunta", 2);
        registro.put("respuesta", "holo vengo a flotar");
        registro.put("correcta", 1);
        bd.insert("resEnsayo", null,registro);
        registro = new ContentValues();
        registro.put("idPregunta", 3);
        registro.put("respuesta", "holo vengo a flotar");
        registro.put("correcta", 2);
        bd.insert("resEnsayo", null,registro);
        registro = new ContentValues();
        registro.put("idPregunta", 4);
        registro.put("respuesta", "holo vengo a flotar");
        registro.put("correcta", 2);
        bd.insert("resEnsayo", null,registro);*/


        String sqlPIncorrectas = "select idPregunta from resEnsayo where correcta = 0";
        String sqlPCorrectas = "select idPregunta from resEnsayo where correcta = 1";
        String sqlPOmitidas = "select idPregunta from resEnsayo where correcta = 2";
        String sqlPTotal = "select idPregunta from resEnsayo";

        Cursor data = bd.rawQuery(sqlPIncorrectas,null);
        cantIncorrectas = data.getCount();
        data = bd.rawQuery(sqlPCorrectas, null);
        cantCorrectas = data.getCount();
        data = bd.rawQuery(sqlPOmitidas, null);
        cantOmitidas = data.getCount();
        data = bd.rawQuery(sqlPTotal, null);
        cantTotal = data.getCount();

        bd.close();

        tRespuesta.setText("holo vengo a flotar");

        
        tPreguntas.setText(cantTotal+"");
        rCorrectas.setText(cantCorrectas+"");
        rIncorrectas.setText(cantIncorrectas+"");
        rOmitidas.setText(cantOmitidas+"");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_resultados, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void goRevision(View view){
        Intent i = new Intent(this, Revision.class);
        startActivity(i);
    }
}
