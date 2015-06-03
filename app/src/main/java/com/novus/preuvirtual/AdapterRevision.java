package com.novus.preuvirtual;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_preg_revision, null);
        }
        TextView titulo = (TextView) convertView.findViewById(R.id.titulo);
        ImageView icono = (ImageView) convertView.findViewById(R.id.icono);
        cursor.moveToPosition(position);
        titulo.setText("Pregunta " + cursor.getString(0));

        if(Integer.parseInt(cursor.getString(2))== 0){
            icono.setImageResource(R.drawable.ic_incorrecta_36dp);
        }else if (Integer.parseInt(cursor.getString(2))== 1){
            icono.setImageResource(R.drawable.ic_check_36dp);
        }else if (Integer.parseInt(cursor.getString(2))== 2){
            icono.setImageResource(R.drawable.ic_omitir_b_36dp);
        }
        return convertView;
    }


}
