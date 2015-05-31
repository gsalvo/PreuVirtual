package com.novus.preuvirtual;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class PreguntaFragment extends Fragment {
    ImageView zoomableImage;
    boolean isImageFitToScreen;
    private TextView textPregunta;
    private ImageView imagen;
    private RadioButton altA;
    private RadioButton altB;
    private RadioButton altC;
    private RadioButton altD;
    private RadioButton altE;
    private RadioGroup rGroup;

    @Override
    public void onAttach(Activity a){
        super.onAttach(a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_pregunta, container, false);
        textPregunta = (TextView) RootView.findViewById(R.id.textPregunta);
        imagen = (ImageView) RootView.findViewById(R.id.imagenAlter);
        altA = (RadioButton) RootView.findViewById(R.id.altA);
        altB = (RadioButton) RootView.findViewById(R.id.altB);
        altC = (RadioButton) RootView.findViewById(R.id.altC);
        altD = (RadioButton) RootView.findViewById(R.id.altD);
        altE = (RadioButton) RootView.findViewById(R.id.altE);
        rGroup = (RadioGroup) RootView.findViewById(R.id.contenidoRadioButton);

        //TO-DO: Cargar datos de pregunta desde la base de datos
        //TO-DO: Verificar si es una nueva pregunta o una pregunta ya contestada
        /*int Pregunta = getArguments().getInt("pregunta");
        if(Pregunta == 2){
            textPregunta.setText("2.- Alea iacta est.");
        }else if(Pregunta == 3){
            textPregunta.setText("3.- Ave cesar morituri te salutant");
        }else if(Pregunta == 4){
            textPregunta.setText("4.- Et toi Brutus");
        }
        */

        String vPregunta = getArguments().getString("pregunta");
        String vImagen = getArguments().getString("imagen");
        String vAltA = getArguments().getString("altA");
        String vAltB = getArguments().getString("altB");
        String vAltC = getArguments().getString("altC");
        String vAltD = getArguments().getString("altD");
        String vAltE = getArguments().getString("altE");
        int vCheck = getArguments().getInt("check");

        textPregunta.setText(Html.fromHtml(vPregunta));

        if(!vImagen.isEmpty()) {
            Log.d("Img", vImagen);
            Picasso.with(getActivity()).load(vImagen).into(imagen);
        }else{
            imagen.setImageDrawable(null);
        }

        altA.setText(Html.fromHtml(vAltA));
        altB.setText(Html.fromHtml(vAltB));
        altC.setText(Html.fromHtml(vAltC));
        altD.setText(Html.fromHtml(vAltD));
        altE.setText(Html.fromHtml(vAltE));

        if(vCheck != 0) {
            rGroup.check(vCheck);
        }

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

}
