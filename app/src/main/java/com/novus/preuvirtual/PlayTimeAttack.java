package com.novus.preuvirtual;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.floor;

/*
TO-DO: Transicion entre preguntas
Solución 1: Trabajar con una lista ordenada de fragments que vaya guardando el número de la pregunta y esté asociada al ensayo en
la base de datos. De esta manera siempre se sabe la posición en la que se encuentra y la navegación se realiza obteniendo
la posición y consultando la BD. Para esta solución es necesario hacer override de onBackButtonPressed()

Solución 2: Trabajar con el BackStack y un Stack de identificador de las siguientes preguntas que esté asociado a la base de datos.
Esta solución quita complejidad al tema de volver a una pregunta anterior, pero agrega complejidad a la hora de elegir la siguiente,
ya que implica manejar los comportamientos del BackStack manualmente a la hora de apretar siguiente.

Para cualquiera de las dos soluciones:
- Generar un ID que se guarde en el Stack creado de manera de poder buscar en la BD esta pregunta asociada al ID de la instancia del
ensayo.
o
- Guardar la instancia de la pregunta en un Bundle que a su vez se guarda en una lista, quitando la complejidad de acceder a la base
de datos. Dado que la pregunta en algún momento se tiene que guardar (por motivos de revisión y estadísticas) hacerlo de este modo
implica guardar el estado de la pregunta de dos maneras.
 */

public class PlayTimeAttack extends ActionBarActivity {

    Bundle bundle;
    SQLiteDatabase bd;
    Cursor cursor, cursorResp;
    private final String[] COLUMNS = {"pregunta", "imagen", "altA", "altB", "altC", "altD", "altE", "altCorrecta", "idPregunta"};
    private final String[] COLUMNSRESP = {"idPregunta", "respuesta", "correcta"};

    //Base de datos SQLite
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "preuVirtual", null, 1);

    TextView textTiempo;
    CountDownTimer backCount;
    int fragmentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_time_attack);
        bundle = getIntent().getExtras();
        //Jugar
        if(bundle.getInt("revision") == 0){
            //new CargarPreguntas().execute();
            bd = admin.getWritableDatabase();

            cursor = bd.query(
                    "pregunta", // a. table
                    COLUMNS, // b. column names
                    null, // c. selections
                    null, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null  // h. max count
            );

            if (cursor != null)
                cursor.moveToFirst();

            Bundle bundlePregunta = new Bundle();
            bundlePregunta.putString("pregunta", cursor.getString(0));
            bundlePregunta.putString("imagen", cursor.getString(1));
            bundlePregunta.putString("altA", cursor.getString(2));
            bundlePregunta.putString("altB", cursor.getString(3));
            bundlePregunta.putString("altC", cursor.getString(4));
            bundlePregunta.putString("altD", cursor.getString(5));
            bundlePregunta.putString("altE", cursor.getString(6));
            bundlePregunta.putString("altCorrecta", cursor.getString(7));

            PreguntaFragment preguntaFragment = new PreguntaFragment();
            preguntaFragment.setArguments(bundlePregunta);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.contentPregunta, preguntaFragment);
            fragmentTransaction.commit();

            textTiempo = (TextView)findViewById(R.id.textTiempo);
            setTimer(Integer.parseInt(bundle.getString("varTiempo")));
        //Revisar
        }else{
            bd = admin.getWritableDatabase();
            String query = "SELECT * from pregunta pr INNER JOIN resEnsayo res WHERE pr.idPregunta = res.idPregunta";

            cursor = bd.rawQuery(query, null);

            cursor.moveToPosition(-1);

            do{
                cursor.moveToNext();

                Bundle bundlePregunta = new Bundle();
                bundlePregunta.putString("pregunta", cursor.getString(1));
                bundlePregunta.putString("imagen", cursor.getString(2));
                bundlePregunta.putString("altA", cursor.getString(3));
                bundlePregunta.putString("altB", cursor.getString(4));
                bundlePregunta.putString("altC", cursor.getString(5));
                bundlePregunta.putString("altD", cursor.getString(6));
                bundlePregunta.putString("altE", cursor.getString(7));
                bundlePregunta.putString("altCorrecta", cursor.getString(8));

                bundlePregunta.putInt("revision", 1);
                bundlePregunta.putInt("check", cursor.getInt(12));

                PreguntaFragment preguntaFragment = new PreguntaFragment();
                preguntaFragment.setArguments(bundlePregunta);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.contentPregunta, preguntaFragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }while(cursor.getInt(0) != bundle.getLong("idPregunta"));

            textTiempo = (TextView)findViewById(R.id.textTiempo);
            textTiempo.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_time_attack, menu);
        return true;
    }

    @Override
    public void onDestroy(){
        if(backCount != null){
            backCount.cancel();
            backCount = null;
        }
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void setTimer(int minutes) {
        backCount = new CountDownTimer(minutes * 1000, 1000) {//new CountDownTimer(minutes * 1000 * 60, 1000) {
            TextView timer = (TextView) findViewById(R.id.textTiempo);

            public void onTick(long remaining) {
                long remainingMinutes = (long) floor((remaining / 1000) / 60);
                long remainingSeconds = (remaining / 1000) % 60;
                if (remainingSeconds < 10 && remainingMinutes < 10) {
                    timer.setText("0" + remainingMinutes + ":0" + remainingSeconds);
                } else if (remainingSeconds >= 10 && remainingMinutes < 10) {
                    timer.setText("0" + remainingMinutes + ":" + remainingSeconds);
                } else if (remainingSeconds < 10 && remainingMinutes > 10) {
                    timer.setText("" + remainingMinutes + ":0" + remainingSeconds);
                } else {
                    timer.setText("" + remainingMinutes + ":" + remainingSeconds);
                }
            }

            public void onFinish(){
                Intent i = new Intent(getBaseContext(), Resultados.class);
                i.putExtra("varTiempo", bundle.get("varTiempo").toString());
                bd.close();
                startActivity(i);
            }

        }.start();
    }

    public void nextPregunta(View view){
        if(bundle.getInt("revision") == 0) {
            ContentValues registro = new ContentValues();
            RadioGroup rGroup = (RadioGroup) findViewById(R.id.contenidoRadioButton);

            registro.put("idPregunta", cursor.getInt(8));
            registro.put("respuesta", (rGroup.getCheckedRadioButtonId()));

            if(rGroup.getCheckedRadioButtonId() == R.id.altA){
                if(cursor.getString(7).equalsIgnoreCase("altA")){
                    registro.put("correcta", 1);
                }else{
                    registro.put("correcta", 0);
                }
            }else if(rGroup.getCheckedRadioButtonId() == R.id.altB){
                if(cursor.getString(7).equalsIgnoreCase("altB")){
                    registro.put("correcta", 1);
                }else{
                    registro.put("correcta", 0);
                }
            }else if(rGroup.getCheckedRadioButtonId() == R.id.altC){
                if(cursor.getString(7).equalsIgnoreCase("altC")){
                    registro.put("correcta", 1);
                }else{
                    registro.put("correcta", 0);
                }
            }else if(rGroup.getCheckedRadioButtonId() == R.id.altD){
                if(cursor.getString(7).equalsIgnoreCase("altD")){
                    registro.put("correcta", 1);
                }else{
                    registro.put("correcta", 0);
                }
            }else if(rGroup.getCheckedRadioButtonId() == R.id.altE){
                if(cursor.getString(7).equalsIgnoreCase("altE")){
                    registro.put("correcta", 1);
                }else{
                    registro.put("correcta", 0);
                }
            }else{
                registro.put("correcta", 2);
            }

            if(exists("resEnsayo", "idPregunta", ""+cursor.getInt(8), bd)){
                bd.update("resEnsayo", registro, "idPregunta = " + cursor.getInt(8), null);
            }else{
                bd.insert("resEnsayo", null, registro);
            }

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Bundle bundlePregunta = new Bundle();
            PreguntaFragment newFragment = new PreguntaFragment();

            if(cursor.isLast()){
                cursor.moveToFirst();
            }else{
                cursor.moveToNext();
            }

            bundlePregunta.putString("pregunta", cursor.getString(0));
            bundlePregunta.putString("imagen", cursor.getString(1));
            bundlePregunta.putString("altA", cursor.getString(2));
            bundlePregunta.putString("altB", cursor.getString(3));
            bundlePregunta.putString("altC", cursor.getString(4));
            bundlePregunta.putString("altD", cursor.getString(5));
            bundlePregunta.putString("altE", cursor.getString(6));
            bundlePregunta.putString("altCorrecta", cursor.getString(7));

            if(exists("resEnsayo", "idPregunta", ""+cursor.getInt(8), bd)){
                String Query = "Select respuesta from resEnsayo where idPregunta = " + cursor.getInt(8);
                Cursor checked = bd.rawQuery(Query, null);
                checked.moveToFirst();
                bundlePregunta.putInt("check", checked.getInt(0));
            }else{
                bundlePregunta.putInt("check", 0);
            }

            newFragment.setArguments(bundlePregunta);

            fragmentTransaction.replace(R.id.contentPregunta, newFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        //Revisar
        }else{
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Bundle bundlePregunta = new Bundle();
            PreguntaFragment newFragment = new PreguntaFragment();

            if(cursor.isLast()){
                cursor.moveToFirst();
            }else{
                cursor.moveToNext();
            }

            bundlePregunta.putString("pregunta", cursor.getString(1));
            bundlePregunta.putString("imagen", cursor.getString(2));
            bundlePregunta.putString("altA", cursor.getString(3));
            bundlePregunta.putString("altB", cursor.getString(4));
            bundlePregunta.putString("altC", cursor.getString(5));
            bundlePregunta.putString("altD", cursor.getString(6));
            bundlePregunta.putString("altE", cursor.getString(7));
            bundlePregunta.putString("altCorrecta", cursor.getString(8));

            bundlePregunta.putInt("revision", 1);
            bundlePregunta.putInt("check", cursor.getInt(12));

            newFragment.setArguments(bundlePregunta);

            fragmentTransaction.replace(R.id.contentPregunta, newFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void backPregunta(View view){
        if(bundle.getInt("revision") == 0) {
            ContentValues registro = new ContentValues();
            RadioGroup rGroup = (RadioGroup) findViewById(R.id.contenidoRadioButton);
            registro.put("idPregunta", cursor.getInt(8));
            registro.put("respuesta", (rGroup.getCheckedRadioButtonId()));

            if(rGroup.getCheckedRadioButtonId() == R.id.altA){
                if(cursor.getString(7).equalsIgnoreCase("altA")){
                    registro.put("correcta", 1);
                }else{
                    registro.put("correcta", 0);
                }
            }else if(rGroup.getCheckedRadioButtonId() == R.id.altB){
                if(cursor.getString(7).equalsIgnoreCase("altB")){
                    registro.put("correcta", 1);
                }else{
                    registro.put("correcta", 0);
                }
            }else if(rGroup.getCheckedRadioButtonId() == R.id.altC){
                if(cursor.getString(7).equalsIgnoreCase("altC")){
                    registro.put("correcta", 1);
                }else{
                    registro.put("correcta", 0);
                }
            }else if(rGroup.getCheckedRadioButtonId() == R.id.altD){
                if(cursor.getString(7).equalsIgnoreCase("altD")){
                    registro.put("correcta", 1);
                }else{
                    registro.put("correcta", 0);
                }
            }else if(rGroup.getCheckedRadioButtonId() == R.id.altE){
                if(cursor.getString(7).equalsIgnoreCase("altE")){
                    registro.put("correcta", 1);
                }else{
                    registro.put("correcta", 0);
                }
            }

            if(exists("resEnsayo", "idPregunta", ""+cursor.getInt(8), bd)){
                bd.update("resEnsayo", registro, "idPregunta = " + cursor.getInt(8), null);
            }else{
                bd.insert("resEnsayo", null, registro);
            }

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentManager.popBackStack();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();

            cursor.moveToPrevious();
        }else {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentManager.popBackStack();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();

            cursor.moveToPrevious();
        }
    }

    public static boolean exists(String TableName, String dbfield, String fieldValue, SQLiteDatabase bd) {
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
        Cursor cursor = bd.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
