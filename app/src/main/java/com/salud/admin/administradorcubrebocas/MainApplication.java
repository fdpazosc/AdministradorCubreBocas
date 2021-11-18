package com.salud.admin.administradorcubrebocas;

import android.app.Application;
import android.content.Context;

/**
 * Created by FPAZOS390 on 24/08/2015.
 */
public class MainApplication extends Application {

    private static MainApplication instance = new MainApplication();

    //se asigna esta aplicacion como clase
    public MainApplication() {
        instance = this;
    }

    //devuelve el contexto de la aplicacion
    public static Context getContext() {
        return instance;
    }

    //se ejecuta al instalar e iniciar por primera vez la aplicacion. Aqui se asigna la instalacion al servidor de parse.com para recibir notificaciones
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
