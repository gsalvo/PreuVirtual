package com.novus.preuvirtual;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
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

    TextView textTiempo;
    CountDownTimer backCount;
    List<Bundle> nextStack;
    int fragmentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_time_attack);

        Bundle bundlePregunta = new Bundle();
        bundlePregunta.putInt("pregunta", 1);

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_time_attack, menu);
        return true;
    }

    @Override
    public void onDestroy(){
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

}
