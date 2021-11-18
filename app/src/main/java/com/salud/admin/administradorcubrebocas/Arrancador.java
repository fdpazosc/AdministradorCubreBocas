package com.salud.admin.administradorcubrebocas;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class Arrancador extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        DataBaseManager manager = new DataBaseManager(context);
        Cursor cursor = manager.cargarCursorConfiguracion();
        boolean alarmaActivada = false;
        if (cursor.moveToFirst()) {
            do {
                alarmaActivada = cursor.getInt(1) == 1;
            } while (cursor.moveToNext());
        }
        if (alarmaActivada)
            if (isRunning(context, DistanceAlarmService.class)) {
                Intent serviceIntent = new Intent(context, DistanceAlarmService.class);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                    context.startService(serviceIntent);
                else
                    context.startForegroundService(serviceIntent);
            } else {
                Toast.makeText(context.getApplicationContext(), "Alarm Manager just ran", Toast.LENGTH_LONG).show();
            }
        Log.d("SERVICIO", "ARRANCADOR");
    }

    private boolean isRunning(Context context, Class<? extends Service> serviceClass) {
        Intent intent = new Intent(context, serviceClass);
        return (PendingIntent.getService(context, 0, intent, 0) != null);
    }
}
