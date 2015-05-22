package com.novus.preuvirtual;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class PreguntaFragment extends Fragment {

    private TextView textPregunta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_pregunta, container, false);
        textPregunta = (TextView)RootView.findViewById(R.id.textPregunta);


        return RootView;
    }


}
