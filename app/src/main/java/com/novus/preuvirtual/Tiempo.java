package com.novus.preuvirtual;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Tiempo extends ActionBarActivity {
    private EditText editMinutos;

    //---------------------Inicio conexion BD----------------------------------------------
    JSONParser jParser = new JSONParser();
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
    //Base de datos SQLite
    private ProgressDialog pDialog;
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "preuVirtual", null,1);
    //---------------------FIN----------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiempo);
        editMinutos = (EditText) findViewById(R.id.editMinutos);

    }

    public void goPlayTimeAttack(View view){
        //Intent i = new Intent(this, PlayTimeAttack.class);
        //i.putExtra("varTiempo", editMinutos.getText().toString());
        new CargarPreguntas().execute();
        //startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tiempo, menu);
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

    class CargarPreguntas extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(Tiempo.this);
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

                        /*Log.d("tavororoidpregunta "+ i, idPregunta );
                        Log.d("tavororoidMateria "+ i, idMateria );
                        Log.d("tavororopregunta "+ i, pregunta );
                        Log.d("tavororoimagenPregunta "+ i, imagen );*/

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
                            /*Log.d("tavororoalternativa "+ j, alternativa );
                            Log.d("tavororocorrecta "+ j, correcta +"" );
                            Log.d("tavororotieneImagen "+ j, imagenAlternativa +"" );*/
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
            Intent i = new Intent(Tiempo.this, PlayTimeAttack.class);
            i.putExtra("varTiempo", editMinutos.getText().toString());
            startActivity(i);
            pDialog.dismiss();
        }
    }
}
