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
    FragmentCallback fragCB;

    @Override
    public void onAttach(Activity a){
        super.onAttach(a);
        fragCB = (FragmentCallback) a;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_pregunta, container, false);
        textPregunta = (TextView)RootView.findViewById(R.id.textPregunta);

        //TO-DO: Cargar datos de pregunta desde la base de datos
        //TO-DO: Verificar si es una nueva pregunta o una pregunta ya contestada
        int Pregunta = getArguments().getInt("pregunta");
        if(Pregunta == 2){
            textPregunta.setText("2.- Alea iacta est.");
        }else if(Pregunta == 3){
            textPregunta.setText("3.- Ave cesar morituri te salutant");
        }else if(Pregunta == 4){
            textPregunta.setText("4.- Et toi Brutus");
        }

        fragCB.setPreguntaNumber(Pregunta);

        return RootView;
    }


    public interface FragmentCallback{
        public void setPreguntaNumber(int number);
    }


}
