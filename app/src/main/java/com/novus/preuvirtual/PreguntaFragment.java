package com.novus.preuvirtual;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
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
    private TextView numeroPregunta;
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

        View RootView = inflater.inflate(R.layout.fragment_pregunta, container, false);
        textPregunta = (TextView) RootView.findViewById(R.id.textoPregunta);
        numeroPregunta = (TextView) RootView.findViewById(R.id.numeroPregunta);
        imagen = (ImageView) RootView.findViewById(R.id.imagenAlter);
        altA = (RadioButton) RootView.findViewById(R.id.altA);
        altB = (RadioButton) RootView.findViewById(R.id.altB);
        altC = (RadioButton) RootView.findViewById(R.id.altC);
        altD = (RadioButton) RootView.findViewById(R.id.altD);
        altE = (RadioButton) RootView.findViewById(R.id.altE);
        rGroup = (RadioGroup) RootView.findViewById(R.id.contenidoRadioButton);

        String vPregunta = getArguments().getString("pregunta");
        String vImagen = getArguments().getString("imagen");
        String vAltA = getArguments().getString("altA");
        String vAltB = getArguments().getString("altB");
        String vAltC = getArguments().getString("altC");
        String vAltD = getArguments().getString("altD");
        String vAltE = getArguments().getString("altE");
        int respuesta = getArguments().getInt("respuesta");
        int nPregunta = getArguments().getInt("nPregunta");

        textPregunta.setText(Html.fromHtml(vPregunta));
        numeroPregunta.setText("Pregunta "+(nPregunta+1));

        if(!vImagen.isEmpty()) {
            Picasso.with(getActivity()).load(vImagen).into(imagen);
        }else{
            imagen.setImageDrawable(null);
        }

        altA.setText(Html.fromHtml(vAltA));
        altB.setText(Html.fromHtml(vAltB));
        altC.setText(Html.fromHtml(vAltC));
        altD.setText(Html.fromHtml(vAltD));
        altE.setText(Html.fromHtml(vAltE));

        if(respuesta != -1) {
            rGroup.check(respuesta);
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

        if(getArguments().getInt("revision") == 1){

            RadioGroup rGroup = (RadioGroup) RootView.findViewById(R.id.contenidoRadioButton);

            for (int i = 0; i < rGroup.getChildCount(); i++)
            {
                rGroup.getChildAt(i).setEnabled(false);
            }

            RadioButton rCorrecta;

            String correcta = getArguments().getString("altCorrecta");

            switch(correcta){
                case "altA":
                    rCorrecta = (RadioButton) RootView.findViewById(R.id.altA);
                    break;
                case "altB":
                    rCorrecta = (RadioButton) RootView.findViewById(R.id.altB);
                    break;
                case "altC":
                    rCorrecta = (RadioButton) RootView.findViewById(R.id.altC);
                    break;
                case "altD":
                    rCorrecta = (RadioButton) RootView.findViewById(R.id.altD);
                    break;
                case "altE":
                    rCorrecta = (RadioButton) RootView.findViewById(R.id.altE);
                    break;
                default:
                    rCorrecta = null;
                    break;
            }

            if(respuesta != rCorrecta.getId() && respuesta != -1){
                RadioButton rSeleccionada = (RadioButton) RootView.findViewById(respuesta);
                rSeleccionada.setTextColor(Color.parseColor("#C02400"));
                rSeleccionada.setTypeface(null, Typeface.BOLD);
                rCorrecta.setTextColor(Color.parseColor("#00a700"));
                rCorrecta.setTypeface(null, Typeface.BOLD);
            }else{
                rCorrecta.setTextColor(Color.parseColor("#00a700"));
                rCorrecta.setTypeface(null, Typeface.BOLD);
            }

        }

        return RootView;
    }

}
