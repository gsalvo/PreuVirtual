package com.novus.preuvirtual;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DensityScreen {

    private String densidad;
    private int densidadEntero;

    public DensityScreen (Context c){
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        this.densidadEntero = metrics.densityDpi;
        if(this.densidadEntero == DisplayMetrics.DENSITY_LOW){
            this.densidad = "ldpi";
        }else if(this.densidadEntero == DisplayMetrics.DENSITY_MEDIUM){
            this.densidad = "mdpi";
        }else if(this.densidadEntero == DisplayMetrics.DENSITY_HIGH){
            this.densidad = "hdpi";
        }else if(this.densidadEntero == DisplayMetrics.DENSITY_XHIGH){
            this.densidad = "xhdpi";
        }else if(this.densidadEntero == DisplayMetrics.DENSITY_XXHIGH){
            this.densidad = "xxhdpi";
        }else {
            this.densidad = "nodpi";
        }
    }

    public String getDensidad() {
        return densidad;
    }
}
