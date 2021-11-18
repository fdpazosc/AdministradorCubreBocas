package com.salud.admin.administradorcubrebocas;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class ReceptorAlarma extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (isRunning(context, DistanceAlarmService.class)) {
            Intent serviceIntent = new Intent(context, DistanceAlarmService.class);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                context.startService(serviceIntent);
            else
                context.startForegroundService(serviceIntent);
        } else {
            Toast.makeText(context.getApplicationContext(), "Alarm Manager just ran", Toast.LENGTH_LONG).show();
        }

        Log.d("SERVICIO", "Receptor");
    }

    private boolean isRunning(Context context, Class<? extends Service> serviceClass) {
        Intent intent = new Intent(context, serviceClass);
        return (PendingIntent.getService(context, 0, intent, 0) != null);
    }
}
