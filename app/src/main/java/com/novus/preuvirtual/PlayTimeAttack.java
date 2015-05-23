package com.novus.preuvirtual;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import static java.lang.Math.floor;


public class PlayTimeAttack extends ActionBarActivity {

    TextView textTiempo;
    CountDownTimer backCount;

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
                } else if (remainingSeconds > 10 && remainingMinutes < 10) {
                    timer.setText("0" + remainingMinutes + ":" + remainingSeconds);
                } else if (remainingSeconds < 10 && remainingMinutes > 10) {
                    timer.setText("" + remainingMinutes + ":0" + remainingSeconds);
                } else {
                    timer.setText("" + remainingMinutes + ":" + remainingSeconds);
                }
            }

            public void onFinish() {
                timer.setText("00:00");
            }

        }.start();
    }

    public void nextPregunta(View view){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundlePregunta = new Bundle();
        bundlePregunta.putInt("pregunta", 2);
        PreguntaFragment newFragment = new PreguntaFragment();
        newFragment.setArguments(bundlePregunta);
        fragmentTransaction.replace(R.id.contentPregunta, newFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack("previous");
        fragmentTransaction.commit();
    }

    public void backPregunta(View view){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack("previous", 1);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack("next");
        fragmentTransaction.commit();
    }

}
