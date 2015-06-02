package com.novus.preuvirtual;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class Resultados extends ActionBarActivity {

    int resultado[] = {150,163,176,190,203,237,268,296,322,346,367,387,405,422,437,451,464,475,485,
            495,503,511,518,524,530,536,541,546,551,555,559,564,568,572,575,579,583,587,590,594,597,
            601,605,608,612,615,619,622,626,630,634,638,641,645,650,654,658,663,667,672,677,683,688,
            694,700,707,715,722,731,740,751,771,791,811,830,850};

    static int tiempoEnsayo;

    Bundle bundle;

    TextView tRespuesta, tPreguntas, rCorrectas, rIncorrectas,rOmitidas, puntaje, tiempo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);
        bundle = getIntent().getExtras();
        puntaje = (TextView) findViewById(R.id.textPuntajeObtenido);
        tiempo = (TextView) findViewById(R.id.textTiempoEnsayo);
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

        if(bundle != null) {
            tiempoEnsayo = Integer.parseInt(bundle.getString("varTiempo"));
        }
        tRespuesta.setText(cantTotal / tiempoEnsayo + " preguntas x minutos");
        tiempo.setText("en un tiempo de " + tiempoEnsayo + "minutos");

        bd.close();

        tPreguntas.setText(cantTotal+"");
        rCorrectas.setText(cantCorrectas+"");
        rIncorrectas.setText(cantIncorrectas+"");
        rOmitidas.setText(cantOmitidas+"");
        puntaje.setText("Has obtenido "+ resultado[cantCorrectas] + " puntos");

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

    public void goNuevoEnsayo(View view){
        finish();
    }


}
