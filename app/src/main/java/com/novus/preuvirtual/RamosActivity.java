package com.novus.preuvirtual;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class RamosActivity extends ActionBarActivity {
    static String modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ramos);
        if(getIntent().getStringExtra("modo") != null) {
            modo = getIntent().getStringExtra("modo");
        }
        if(modo.equals("timeattack")){
            getSupportActionBar().setTitle("Cuenta Regresiva");
        }else if(modo.equals("endless")){
            getSupportActionBar().setTitle("Infinito");
        }
    }

    public void buttonPress (View v){
        Intent i;
        if(modo.equals("timeattack")) {
            i = new Intent(this, TimeAttackActivity.class);
        }else if(modo.equals("endless")){
            i = new Intent(this, EndlessActivity.class);
        }else {
            i = new Intent(this, EndlessActivity.class); // TO-DO: GoalsActivity.class
        }

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
                i.putExtra("ramo", "biolog\u00eda");
                startActivity(i);
                break;
            case R.id.btnQuimica:
                i.putExtra("ramo", "qu\u00edmica");
                startActivity(i);
                break;
            case R.id.btnFisica:
                i.putExtra("ramo", "f\u00edsica");
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