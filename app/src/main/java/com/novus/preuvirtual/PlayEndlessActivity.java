package com.novus.preuvirtual;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.novus.preuvirtual.Helpers.AdminSQLiteOpenHelper;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.floor;

public class PlayEndlessActivity extends ActionBarActivity {
    Bundle bundle;
    Timer contador;
    TextView textoTiempo;
    TextView textoCorrectas;
    CountDownTimer backCount;
    SQLiteDatabase bd;
    Cursor cursor;
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "preuVirtual", null, 1);
    private final String[] COLUMNS = {"pregunta", "imagen", "altA", "altB", "altC", "altD", "altE", "altCorrecta", "idPregunta"};
    int revision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_endless);
        bundle = getIntent().getExtras();
        revision = bundle.getInt("revision");

        //Jugar
        if(revision == 0){
            
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
            bundlePregunta.putInt("nPregunta", cursor.getPosition());

            PreguntaFragment preguntaFragment = new PreguntaFragment();
            preguntaFragment.setArguments(bundlePregunta);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.contenidoPregunta, preguntaFragment);
            fragmentTransaction.commit();

            textoTiempo = (TextView)findViewById(R.id.textoTiempo);
            textoCorrectas = (TextView)findViewById(R.id.textoCorrectas);
            iniciarTimer();
        //Revisar
        }else{
            final String query = "SELECT * from pregunta pr INNER JOIN resEnsayo res WHERE pr.idPregunta = res.idPregunta";
            ImageView passPregunta = (ImageView) findViewById(R.id.btnBorrarPregunta);
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
                bundlePregunta.putInt("nPregunta", cursor.getPosition());

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
        if(revision == 0) {
            getMenuInflater().inflate(R.menu.menu_play_time_attack, menu);
        }
        return super.onCreateOptionsMenu(menu);
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
        if(revision == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PlayEndlessActivity.this);
            AlertDialog dialog;
            switch (id) {
                case android.R.id.home:
                    builder.setMessage(R.string.close_message).setTitle(R.string.close_title);
                    builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PlayEndlessActivity.this.finish();
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, null);
                    dialog = builder.create();
                    dialog.show();
                    return true;
                case R.id.terminar:
                    builder.setMessage(R.string.finish_message).setTitle(R.string.finish_title);
                    builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            guardarPregunta();

                            Intent i = new Intent(getBaseContext(), ResultadosActivity.class);
                            i.putExtra("varTiempo", "1");
                            bd.close();

                            int tFinal = calculaMinutos(textoTiempo.getText().toString());
                            int tInicial = Integer.parseInt(bundle.getString("varTiempo"));
                            String tEnsayo = tInicial - tFinal + "";
                            Intent j = new Intent(getBaseContext(), ResultadosActivity.class);
                            j.putExtra("varTiempo", tEnsayo);
                            startActivity(j);
                            PlayEndlessActivity.this.finish();
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, null);
                    dialog = builder.create();
                    dialog.show();
                    return true;
            }
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    private int calculaMinutos(String hora){
        if(hora.length()== 8){  //si el formato es HH:MM:SS
            String aux[] = hora.split(":");
            return Integer.parseInt(aux[0])*60 + Integer.parseInt(aux[1]) + Integer.parseInt(aux[2])/60;
        }else{   //si el formato es MM:SS
            String aux[] = hora.split(":");
            return Integer.parseInt(aux[0]) + Integer.parseInt(aux[1])/60;
        }
    }

    @Override
    public void onBackPressed(){
        if(revision == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PlayEndlessActivity.this);
            AlertDialog dialog;
            builder.setMessage(R.string.close_message).setTitle(R.string.close_title);
            builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    PlayEndlessActivity.this.finish();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            dialog = builder.create();
            dialog.show();
        }else{
            super.onBackPressed();
        }
    }

    public boolean guardarPregunta(){
        ContentValues registro = new ContentValues();
        RadioGroup rGroup = (RadioGroup) findViewById(R.id.contenidoRadioButton);
        registro.put("idPregunta", cursor.getInt(8));
        registro.put("respuesta", (rGroup.getCheckedRadioButtonId()));

        if(rGroup.getCheckedRadioButtonId() == R.id.altA){
            if(cursor.getString(7).equalsIgnoreCase("altA")){
                registro.put("correcta", 1);
                textoCorrectas.setText(Integer.parseInt(textoCorrectas.getText().toString() + 1)+"");
            }else{
                finalizarEndless();
                return false;
            }
        }else if(rGroup.getCheckedRadioButtonId() == R.id.altB){
            if(cursor.getString(7).equalsIgnoreCase("altB")){
                registro.put("correcta", 1);
                textoCorrectas.setText(Integer.parseInt(textoCorrectas.getText().toString() + 1) + "");
            }else{
                finalizarEndless();
                return false;
            }
        }else if(rGroup.getCheckedRadioButtonId() == R.id.altC){
            if(cursor.getString(7).equalsIgnoreCase("altC")){
                registro.put("correcta", 1);
                textoCorrectas.setText(Integer.parseInt(textoCorrectas.getText().toString() + 1) + "");
            }else{
                finalizarEndless();
                return false;
            }
        }else if(rGroup.getCheckedRadioButtonId() == R.id.altD){
            if(cursor.getString(7).equalsIgnoreCase("altD")){
                registro.put("correcta", 1);
                textoCorrectas.setText(Integer.parseInt(textoCorrectas.getText().toString() + 1) + "");
            }else{
                finalizarEndless();
                return false;
            }
        }else if(rGroup.getCheckedRadioButtonId() == R.id.altE){
            if(cursor.getString(7).equalsIgnoreCase("altE")){
                registro.put("correcta", 1);
                textoCorrectas.setText(Integer.parseInt(textoCorrectas.getText().toString() + 1) + "");
            }else{
                finalizarEndless();
                return false;
            }
        }else{
            registro.put("correcta", 2);
        }
        if(hayRespuesta("resEnsayo", "idPregunta", "" + cursor.getInt(8), bd)){
            bd.update("resEnsayo", registro, "idPregunta = " + cursor.getInt(8), null);
        }else{
            bd.insert("resEnsayo", null, registro);
        }
        return true;
    }

    public void finalizarEndless() {
        bd.close();
        Intent j = new Intent(getBaseContext(), ResultadosActivity.class);
        j.putExtra("varTiempo", "1");
        startActivity(j);
        PlayEndlessActivity.this.finish();
    }

    public void iniciarTimer() {
        contador = new Timer(true);
        final TextView timer = (TextView) findViewById(R.id.textoTiempo);

        contador.scheduleAtFixedRate(new TimerTask() {
            long contador = 0;
            long segundos;
            long minutos;
            long horas;

            @Override
            public void run() {
                contador++;
                segundos = contador % 60;
                minutos = (long) floor(contador/60) % 60;
                horas = (long) floor((contador/60)/60);
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        if(segundos < 10 && minutos < 10 && horas >= 1){
                            timer.setText("0" + horas + ":0" + minutos + ":0" + segundos);
                        }else if(segundos > 10 && minutos < 10 && horas >= 1){
                            timer.setText("0" + horas + ":0" + minutos + ":" + segundos);
                        }else if(segundos < 10 && minutos > 10 && horas >= 1){
                            timer.setText("0" + horas + ":" + minutos + ":0" + segundos);
                        }else if(segundos > 10 && minutos > 10 && horas >= 1){
                            timer.setText("0" + horas + ":" + minutos + ":" + segundos);
                        }else if (segundos < 10 && minutos < 10) {
                            timer.setText("00" + ":0" + minutos + ":0" + segundos);
                        } else if (segundos >= 10 && minutos < 10) {
                            timer.setText("00" + ":0" + minutos + ":" + segundos);
                        } else if (segundos < 10 && minutos > 10) {
                            timer.setText("00" + ":" + minutos + ":0" + segundos);
                        } else {
                            timer.setText("00" + ":" + minutos + ":" + segundos);
                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    public void preguntaSiguiente(View view){
        if(revision == 0) {
            if(!guardarPregunta()){
                return;
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
            bundlePregunta.putInt("nPregunta", cursor.getPosition());

            if(hayRespuesta("resEnsayo", "idPregunta", "" + cursor.getInt(8), bd)){
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
            bundlePregunta.putInt("nPregunta", cursor.getPosition());

            newFragment.setArguments(bundlePregunta);

            fragmentTransaction.replace(R.id.contenidoPregunta, newFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void preguntaAnterior(View view){
        if(revision == 0) {
            guardarPregunta();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if(fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
                if(cursor.isFirst()) {
                    cursor.moveToLast();
                }else{
                    cursor.moveToPrevious();
                }
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

    public void borrarPregunta(View view){
        RadioGroup rGroup = (RadioGroup) findViewById(R.id.contenidoRadioButton);
        rGroup.clearCheck();
    }

    public static boolean hayRespuesta(String TableName, String dbfield, String fieldValue, SQLiteDatabase bd) {
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