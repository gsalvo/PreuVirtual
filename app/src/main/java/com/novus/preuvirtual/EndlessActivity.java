package com.novus.preuvirtual;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.novus.preuvirtual.Helpers.AdminSQLiteOpenHelper;
import com.novus.preuvirtual.Helpers.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EndlessActivity extends ActionBarActivity {
    private TextView textDesafioRamo;
    private String ramoSeleccionado;

    JSONParser jParser = new JSONParser();
    private static String urlCargarPreguntas;
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

    private ProgressDialog pDialog;
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "preuVirtual", null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endless);
        textDesafioRamo = (TextView) findViewById(R.id.textDesafioRamo);

        Bundle bundle = getIntent().getExtras();
        ramoSeleccionado = bundle.getString("ramo");
        urlCargarPreguntas = "http://preuvirtual.webcindario.com/cargarPreguntas"+ ramoSeleccionado.substring(0,1) +".php";
        textDesafioRamo.setText("¿Quieres ser puntaje nacional en "+ ramoSeleccionado + "?");
    }

    public void goEndless(View view) {
        new CargarPreguntas().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tiempo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    class CargarPreguntas extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EndlessActivity.this);
            pDialog.setMessage(getResources().getString(R.string.wait_message));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List params = new ArrayList();
            JSONObject json = jParser.makeHttpRequest(urlCargarPreguntas, "GET", params);
            SQLiteDatabase bd = admin.getWritableDatabase();
            bd.execSQL("delete from pregunta");
            bd.execSQL("delete from resEnsayo");
            try {
                int success = json.getInt(TAG_SUCCESS);
                if(success==1){
                    preguntas = json.getJSONArray(TAG_P);
                    for(int i = 0; i<preguntas.length();i++){
                        JSONObject c = preguntas.getJSONObject(i);
                        String idPregunta = c.getString(TAG_P_ID_PREGUNTA);
                        String pregunta = c.getString(TAG_P_PREGUNTA);
                        String imagen = c.getString(TAG_P_IMAGEN);

                        ContentValues registro = new ContentValues();
                        registro.put("idPregunta", Integer.parseInt(idPregunta));
                        registro.put("pregunta", pregunta);
                        registro.put("imagen", imagen);

                        alternativas = c.getJSONArray(TAG_A);
                        for (int j = 0; j < alternativas.length(); j++) {
                            String altTipo[] = {"A", "B", "C", "D", "E"};
                            JSONObject a = alternativas.getJSONObject(j);
                            String alternativa = a.getString(TAG_A_ALTERNATIVA);
                            int correcta = a.getInt(TAG_A_CORRECTA);
                            int imagenAlternativa = a.getInt(TAG_A_IMAGEN);
                            registro.put("alt"+altTipo[j] , alternativa);
                            if(correcta == 1){
                                registro.put("altCorrecta", "alt"+altTipo[j]);
                            }
                            if(imagenAlternativa == 1){
                                registro.put("altImagen", 1);
                            }
                        }
                        bd.insert("pregunta", null, registro);
                    }
                    bd.close();
                }
            }catch(JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            Intent i = new Intent(EndlessActivity.this, PlayEndlessActivity.class);
            i.putExtra("revision", 0);
            startActivity(i);
            pDialog.dismiss();
        }
    }
}
