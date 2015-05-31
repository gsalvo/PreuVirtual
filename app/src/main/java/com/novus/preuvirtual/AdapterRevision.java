package com.novus.preuvirtual;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Gustavo Salvo Lara on 31-05-2015.
 */
public class AdapterRevision extends BaseAdapter{
    private final Activity actividad;
    private final Cursor cursor;

    public AdapterRevision(Activity actividad, Cursor cursor){
        super();
        this.actividad = actividad;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return  cursor.moveToPosition(position);

    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return Integer.parseInt(cursor.getString(1));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = actividad.getLayoutInflater();
        View view = inflater.inflate(R.layout.list_preg_revision, null, true);
        TextView titulo = (TextView) view.findViewById(R.id.titulo);
        ImageView icono = (ImageView) view.findViewById(R.id.icono);
        cursor.moveToPosition(position);
        titulo.setText("Pregunta " + cursor.getString(0));
        if(Integer.parseInt(cursor.getString(2))== 0){
            icono.setImageResource(R.drawable.ic_close_black_36dp);

        }else if (Integer.parseInt(cursor.getString(2))== 1){

            icono.setImageResource(R.drawable.ic_check_black_36dp);
        }else if (Integer.parseInt(cursor.getString(2))== 2){
            icono.setImageResource(R.drawable.ic_center_focus_weak_black_36dp);
        }
        return view;
    }


}
