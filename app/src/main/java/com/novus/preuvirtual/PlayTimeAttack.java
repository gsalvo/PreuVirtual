package com.novus.preuvirtual;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class PlayTimeAttack extends ActionBarActivity implements PreguntaFragment.FragmentCallback {

    //---------------------Inicio conexion BD----------------------------------------------
/*
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    //datos a capturar del JSON
    private static final String urlCargarPreguntas = "http://www.botis.cl/cargarPreguntas.php";
    private static final String TAG_P = "preguntas";
    private static final String TAG_P_ID_PREGUNTA = "idPregunta";
    private static final String TAG_P_ID_MATERIA = "idMateria";
    private static final String TAG_P_PREGUNTA = "pregunta";
    private static final String TAG_P_IMAGEN = "imagen";
    private static final String TAG_A = "alternativas";
    private static final String TAG_A_ALTERNATIVA = "alternativa";
    private static final String TAG_A_CORRECTA = "correcta";
    private static final String TAG_A_IMAGEN = "imagen";
    private static final String TAG_SUCCESS = "success";
    JSONArray preguntas = null;
    JSONArray alternativas = null;
*/
    //Base de datos SQLite
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "preuVirtual", null,1);




    //---------------------FIN----------------------------------------------

    TextView textTiempo;
    CountDownTimer backCount;
    List<Bundle> nextStack;
    int fragmentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_time_attack);

        //new CargarPreguntas().execute();
        SQLiteDatabase bd = admin.getReadableDatabase();
        String[] COLUMNS = {"pregunta", "imagen", "altA", "altB", "altC", "altD", "altE"};
        Cursor cursor = bd.query(
                "pregunta", // a. table
                COLUMNS, // b. column names
                null, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                "1"
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

        PreguntaFragment preguntaFragment = new PreguntaFragment();
        preguntaFragment.setArguments(bundlePregunta);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.contentPregunta, preguntaFragment);
        fragmentTransaction.commit();

        textTiempo = (TextView)findViewById(R.id.textTiempo);
        Bundle bundleTiempo = getIntent().getExtras();
        setTimer(Integer.parseInt(bundleTiempo.getString("varTiempo")));

        nextStack = new ArrayList<Bundle>();

        Log.d("BD", cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_time_attack, menu);
        return true;
    }

    @Override
    public void onDestroy(){
        SQLiteDatabase bd = admin.getWritableDatabase();
        bd.execSQL("delete from pregunta");
        bd.execSQL("delete from resEnsayo");

        backCount.cancel();
        backCount = null;
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

        backCount = new CountDownTimer(minutes * 1000 * 60, 1000) {
            TextView timer = (TextView) findViewById(R.id.textTiempo);

            public void onTick(long remaining) {
                Log.d("Tiempo", String.valueOf(remaining));
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
                startActivity(i);
            }

        }.start();
    }

    public void nextPregunta(View view){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundlePregunta = new Bundle();
        PreguntaFragment newFragment = new PreguntaFragment();
        if(nextStack.isEmpty()){
            //TO-DO: Elegir siguiente pregunta al azar
            //TO-DO: Obtener pregunta de la BD
            bundlePregunta.putInt("pregunta", fragmentID+1);
            newFragment.setArguments(bundlePregunta);
        }else{
            //TO-DO: Especificar que es una pregunta que ya fue vista en el ensayo
            Bundle nextFragment = nextStack.get(nextStack.size()-1);
            bundlePregunta.putInt("pregunta", nextFragment.getInt("pregunta"));
            newFragment.setArguments(bundlePregunta);

        }
        fragmentTransaction.replace(R.id.contentPregunta, newFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void backPregunta(View view){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle nextFragment = new Bundle();
        nextFragment.putInt("pregunta", fragmentID);
        nextStack.add(nextFragment);
        fragmentManager.popBackStack();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    @Override
    public void setPreguntaNumber(int number){
        fragmentID = number;
    }

    //---------------------Inicio conexion BD----------------------------------------------

    /*class CargarPreguntas extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(PlayTimeAttack.this);
            pDialog.setMessage("Preparate para empezar :)");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List params = new ArrayList();
            JSONObject json = jParser.makeHttpRequest(urlCargarPreguntas, "GET", params);
            SQLiteDatabase bd = admin.getWritableDatabase();
            bd.execSQL("delete from pregunta");
            bd.execSQL("delete from resEnsayo");


            Log.d("todas las preguntas", json.toString());

            try{
                int success = json.getInt(TAG_SUCCESS);
                if(success==1){
                    preguntas = json.getJSONArray(TAG_P);


                    for(int i = 0; i<preguntas.length();i++){
                        Log.d("cant", preguntas.length() +"");
                        JSONObject c = preguntas.getJSONObject(i);
                        String idPregunta = c.getString(TAG_P_ID_PREGUNTA);
                        String idMateria = c.getString(TAG_P_ID_MATERIA);
                        String pregunta = c.getString(TAG_P_PREGUNTA);
                        String imagen = c.getString(TAG_P_IMAGEN);

                        ContentValues registro = new ContentValues();
                        registro.put("idPregunta",Integer.parseInt(idPregunta));
                        //registro.put("idMateria", idMateria);
                        registro.put("pregunta", pregunta);
                        registro.put("imagen", imagen);


                        Log.d("tavororoidpregunta "+ i, idPregunta );
                        Log.d("tavororoidMateria "+ i, idMateria );
                        Log.d("tavororopregunta "+ i, pregunta );
                        Log.d("tavororoimagenPregunta "+ i, imagen );

                        alternativas = c.getJSONArray(TAG_A);
                        for (int j = 0; j < alternativas.length(); j++){

                            String altTipo[] = {"A", "B", "C", "D", "E"};

                            JSONObject a = alternativas.getJSONObject(j);
                            String alternativa = a.getString(TAG_A_ALTERNATIVA);
                            int correcta = a.getInt(TAG_A_CORRECTA);
                            int imagenAlternativa = a.getInt(TAG_A_IMAGEN);

                            registro.put("alt"+altTipo[j] , alternativa);
                            if(correcta == 1){
                                registro.put("altCorrecta", altTipo[j]);
                            }
                            if(imagenAlternativa == 1){
                                registro.put("altImagen", 1);
                            }

                            Log.d("tavororoalternativa "+ j, alternativa );
                            Log.d("tavororocorrecta "+ j, correcta +"" );
                            Log.d("tavororotieneImagen "+ j, imagenAlternativa +"" );
                        }
                        bd.insert("pregunta", null, registro);

                    }
                    bd.close();

                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url){
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }*/

}
