package com.novus.preuvirtual;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import static java.lang.Math.floor;

public class PlayTimeAttack extends ActionBarActivity {

    Bundle bundle;
    SQLiteDatabase bd;
    Cursor cursor, cursorResp;
    private final String[] COLUMNS = {"pregunta", "imagen", "altA", "altB", "altC", "altD", "altE", "altCorrecta", "idPregunta"};

    //Base de datos SQLite
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "preuVirtual", null, 1);

    TextView textoTiempo;
    CountDownTimer backCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_time_attack);
        bundle = getIntent().getExtras();

        //Jugar
        if(bundle.getInt("revision") == 0){
            
            bd = admin.getWritableDatabase();

            cursor = bd.query(
                    "pregunta", // Tabla
                    COLUMNS, // Nombre de columnas
                    null, // Selects
                    null, // Argumentos de selects
                    null, // Group by
                    null, // Having
                    null, // Order by
                    null  // Max Count
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
            bundlePregunta.putInt("respuesta", -1);

            PreguntaFragment preguntaFragment = new PreguntaFragment();
            preguntaFragment.setArguments(bundlePregunta);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.contenidoPregunta, preguntaFragment);
            fragmentTransaction.commit();

            textoTiempo = (TextView)findViewById(R.id.textoTiempo);
            setTiempo(Integer.parseInt(bundle.getString("varTiempo")));

        //Revisar
        }else{
            final String query = "SELECT * from pregunta pr INNER JOIN resEnsayo res WHERE pr.idPregunta = res.idPregunta";
            ImageView passPregunta = (ImageView) findViewById(R.id.btnOmitirPregunta);
            bd = admin.getWritableDatabase();

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
                bundlePregunta.putInt("respuesta", cursor.getInt(12));

                PreguntaFragment preguntaFragment = new PreguntaFragment();
                preguntaFragment.setArguments(bundlePregunta);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.contenidoPregunta, preguntaFragment);
                if(!cursor.isFirst()) {
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.addToBackStack(null);
                }
                fragmentTransaction.commit();
            }while(cursor.getInt(0) != bundle.getLong("idPregunta"));

            textoTiempo = (TextView)findViewById(R.id.textoTiempo);
            textoTiempo.setVisibility(View.GONE);
            passPregunta.setEnabled(false);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void setTiempo(int minutos) {
        backCount = new CountDownTimer(minutos * 1000, 1000) {//new CountDownTimer(minutes * 1000 * 60, 1000) {
            TextView timer = (TextView) findViewById(R.id.textoTiempo);

            public void onTick(long restante) {
                long minutosRestantes = (long) floor((restante / 1000) / 60);
                long segundosRestantes = (restante / 1000) % 60;
                if (segundosRestantes < 10 && minutosRestantes < 10) {
                    timer.setText("0" + minutosRestantes + ":0" + segundosRestantes);
                } else if (segundosRestantes >= 10 && minutosRestantes < 10) {
                    timer.setText("0" + minutosRestantes + ":" + segundosRestantes);
                } else if (segundosRestantes < 10 && minutosRestantes > 10) {
                    timer.setText("" + minutosRestantes + ":0" + segundosRestantes);
                } else {
                    timer.setText("" + minutosRestantes + ":" + segundosRestantes);
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

    public void preguntaSiguiente(View view){
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

            if(existe("resEnsayo", "idPregunta", ""+cursor.getInt(8), bd)){
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

            if(existe("resEnsayo", "idPregunta", ""+cursor.getInt(8), bd)){
                String Query = "Select respuesta from resEnsayo where idPregunta = " + cursor.getInt(8);
                Cursor checked = bd.rawQuery(Query, null);
                checked.moveToFirst();
                bundlePregunta.putInt("respuesta", checked.getInt(0));
            }else{
                bundlePregunta.putInt("respuesta", -1);
            }

            newFragment.setArguments(bundlePregunta);

            fragmentTransaction.replace(R.id.contenidoPregunta, newFragment);
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
            bundlePregunta.putInt("respuesta", cursor.getInt(12));

            newFragment.setArguments(bundlePregunta);

            fragmentTransaction.replace(R.id.contenidoPregunta, newFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void preguntaAnterior(View view){
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

            if(existe("resEnsayo", "idPregunta", ""+cursor.getInt(8), bd)){
                bd.update("resEnsayo", registro, "idPregunta = " + cursor.getInt(8), null);
            }else{
                bd.insert("resEnsayo", null, registro);
            }
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if(fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();

                cursor.moveToPrevious();
            }
        }else {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if(fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();

                cursor.moveToPrevious();
            }
        }
    }

    public void omitirPregunta(View view){
        ContentValues registro = new ContentValues();
        RadioGroup rGroup = (RadioGroup) findViewById(R.id.contenidoRadioButton);

        rGroup.clearCheck();

        registro.put("idPregunta", cursor.getInt(8));
        registro.put("respuesta", (rGroup.getCheckedRadioButtonId()));
        registro.put("correcta", 2);

        if(existe("resEnsayo", "idPregunta", ""+cursor.getInt(8), bd)){
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

        if(existe("resEnsayo", "idPregunta", ""+cursor.getInt(8), bd)){
            String Query = "Select respuesta from resEnsayo where idPregunta = " + cursor.getInt(8);
            Cursor checked = bd.rawQuery(Query, null);
            checked.moveToFirst();
            bundlePregunta.putInt("respuesta", checked.getInt(0));
        }else{
            bundlePregunta.putInt("respuesta", -1);
        }

        newFragment.setArguments(bundlePregunta);

        fragmentTransaction.replace(R.id.contenidoPregunta, newFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static boolean existe(String TableName, String dbfield, String fieldValue, SQLiteDatabase bd) {
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
