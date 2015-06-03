package com.novus.preuvirtual;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class Revision extends ActionBarActivity {

    ListView lista;
    SQLiteDatabase bd;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision);

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "preuVirtual", null,1);
        bd = admin.getReadableDatabase();
        cursor = bd.query("resEnsayo", new String[]{"_id", "idPregunta", "correcta"}, null, null, null, null, null);
        cursor.moveToFirst();
        AdapterRevision adapter = new AdapterRevision(this, cursor);
        lista = (ListView) findViewById(R.id.lista);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Revision.this, PlayTimeAttack.class);
                i.putExtra("idPregunta", id);
                i.putExtra("revision", 1);
                startActivity(i);
            }
        });


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
