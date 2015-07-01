package com.novus.preuvirtual;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goTimeAttack(View view) {
        Intent i = new Intent(this, RamosActivity.class);
        i.putExtra("modo", "timeattack");
        startActivity(i);
    }

    public void goEndless(View view) {
        Intent i = new Intent(this, RamosActivity.class);
        i.putExtra("modo", "endless");
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_revision, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
