package com.novus.preuvirtual;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DensityScreen {
    private String directorio;
    private String densidad;
    private int densidadEntero;


    public DensityScreen (Context c){
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        this.densidadEntero = metrics.densityDpi;
        if(this.densidadEntero == DisplayMetrics.DENSITY_LOW){
            this.densidad = "ldpi";
            this.directorio = "drawable-ldpi";
        }else if(this.densidadEntero == DisplayMetrics.DENSITY_MEDIUM){
            this.densidad = "mdpi";
            this.directorio = "drawable-mdpi";
        }else if(this.densidadEntero == DisplayMetrics.DENSITY_HIGH){
            this.densidad = "hdpi";
            this.directorio = "drawable-hdpi";
        }else if(this.densidadEntero == DisplayMetrics.DENSITY_XHIGH){
            this.densidad = "xhdpi";
            this.directorio = "drawable-xhdpi";
        }else if(this.densidadEntero == DisplayMetrics.DENSITY_XXHIGH){
            this.densidad = "xxhdpi";
            this.directorio = "drawable-xxhdpi";
        }else {
            this.densidad = "nodpi";
            this.directorio = "drawable-nodpi";
        }
    }

    public String getDirectorio() {
        return directorio;
    }

    public String getDensidad() {
        return densidad;
    }

    public int getDensidadEntero() {
        return densidadEntero;
    }
}
