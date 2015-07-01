package com.novus.preuvirtual;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class TimeAttack extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_attack);
    }

    public void buttonPress (View v){
        Intent i = new Intent(this, Tiempo.class);
        switch (v.getId()){
            case R.id.btnMatematicas:
                i.putExtra("ramo", "matem\u00e1ticas");
                startActivity(i);
                break;
            case R.id.btnLenguaje:
                i.putExtra("ramo", "lenguaje");
                startActivity(i);
                break;
            case R.id.btnHistoria:
                i.putExtra("ramo", "historia");
                startActivity(i);
                break;
            case R.id.btnCiencias:
                i.putExtra("ramo", "ciencias");
                startActivity(i);
                break;
            case R.id.btnBiologia:
<<<<<<< HEAD
                i.putExtra("ramo", "biolog�a");
                startActivity(i);
                break;
            case R.id.btnQuimica:
                i.putExtra("ramo", "qu�mica");
                startActivity(i);
                break;
            case R.id.btnFisica:
                i.putExtra("ramo", "f�sica");
=======
                i.putExtra("ramo", "biolog\u00eda");
                startActivity(i);
                break;
            case R.id.btnQuimica:
                i.putExtra("ramo", "qu\u00edmica");
                startActivity(i);
                break;
            case R.id.btnFisica:
                i.putExtra("ramo", "f\u00edsica");
>>>>>>> 8081ac0489d08c59f06f690f785e7d3293820b8f
                startActivity(i);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_time_attack, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
