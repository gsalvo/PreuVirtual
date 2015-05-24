package com.novus.preuvirtual;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class PreguntaFragment extends Fragment {
    ImageView zoomableImage;
    boolean isImageFitToScreen;
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
        textPregunta = (TextView) RootView.findViewById(R.id.textPregunta);

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

        zoomableImage = (ImageView) RootView.findViewById(R.id.imagenAlter);

        zoomableImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageFitToScreen) {
                    isImageFitToScreen = false;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    zoomableImage.setLayoutParams(params);
                    zoomableImage.setAdjustViewBounds(true);
                } else {
                    isImageFitToScreen = true;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    zoomableImage.setLayoutParams(params);
                    zoomableImage.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        });

        return RootView;
    }


    public interface FragmentCallback{
        public void setPreguntaNumber(int number);
    }


}
