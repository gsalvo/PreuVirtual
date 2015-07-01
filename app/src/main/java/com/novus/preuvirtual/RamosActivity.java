package com.novus.preuvirtual;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class RamosActivity extends ActionBarActivity {
    String modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ramos);
        modo = getIntent().getStringExtra("modo");
    }

    public void buttonPress (View v){
        Intent i;
        if(modo == "timeattack") {
            i = new Intent(this, TimeAttackActivity.class);
        }else if(modo == "endless"){
            i = new Intent(this, EndlessActivity.class);
        }else {
            i = new Intent(this, TimeAttackActivity.class); // TO-DO: GoalsActivity.class
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