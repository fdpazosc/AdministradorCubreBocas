package com.salud.admin.administradorcubrebocas;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class DistanceAlarmService extends Service implements LocationListener {

    protected LocationManager locationManager;
    private DataBaseManager manager;
    private boolean estoyEnPunto;
    private boolean estuveEnPunto;
    private Context context;
    private String ID_CANAL = "ubicacion_alarmas";
    private TextToSpeech t1;

    public DistanceAlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("SERVICIO", "Ejecución");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        manager = new DataBaseManager(this);
        context = this;


        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_MIN);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo_admin_cubrebocas_blanco)
                    //.setContentTitle("")
                    //.setContentText("")
                    .build();

            startForeground(1, notification);
        }


        Toast.makeText(this, getText(R.string.text_alarma_activada), Toast.LENGTH_LONG).show();

        /*new Thread(new Runnable() {
            public void run() {
                //txt1.setText("Thread!!");
                for (int i = 30; i > 0; i--) {
                    Log.d("hilo", "" + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(this);
        Toast.makeText(this, getText(R.string.text_alarma_cancelada), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, getText(R.string.text_alarma_activada), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        estuveEnPunto = estoyEnPunto;

        int distancia = 1;

        Cursor cursor = manager.cargarCursorConfiguracion();

        if (cursor.moveToFirst()) {
            do {
                distancia = cursor.getInt(2);
            } while (cursor.moveToNext());
        }

        cursor = manager.cargarCursorPuntosRecordatorio();

        if (cursor.moveToFirst()) {
            do {
                Location l = new Location(cursor.getString(1));
                l.setLatitude(cursor.getDouble(2));
                l.setLongitude(cursor.getDouble(3));
                if (location.distanceTo(l) <= distancia) {
                    estoyEnPunto = true;
                    break;
                } else {
                    estoyEnPunto = false;
                }
                Log.d("SERVICIO", "" + location.distanceTo(l));
            } while (cursor.moveToNext());
            if (estuveEnPunto && !estoyEnPunto) {

                Cursor cursorUsuario = manager.buscarUsuarioFavorito();
                String nombreUsuarioFavorito = "";
                if (cursorUsuario.moveToFirst()) {
                    do {
                        nombreUsuarioFavorito = cursorUsuario.getString(1);
                    } while (cursorUsuario.moveToNext());
                }
                Intent resultIntent = new Intent(context, MainActivity.class);
                //resultIntent.putExtra("abrenoti","S");
                int mNotificationId = 365;
                createNotificationChannel();
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder noti = new NotificationCompat.Builder(context, ID_CANAL)
                        .setSmallIcon(R.drawable.logo_admin_cubrebocas_blanco)
                        .setContentTitle(getText(R.string.text_recordatorio))
                        .setContentText(getText(R.string.text_mensaje_notificacion) + (nombreUsuarioFavorito.equals("") ? "" : " " + toTitleCase(nombreUsuarioFavorito)))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(getText(R.string.text_mensaje_notificacion) + (nombreUsuarioFavorito.equals("") ? "" : " " + toTitleCase(nombreUsuarioFavorito))))
                        .setPriority(Notification.PRIORITY_MAX)
                        .setVibrate(new long[]{1000, 1000})
                        .setContentIntent(resultPendingIntent)
                        .setSound(alarmSound)
                        .setAutoCancel(true);
                Notification notification = noti.build();
                notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                notificationManager.notify(mNotificationId, notification);

                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        //if (status == TextToSpeech.SUCCESS) {
                        //    t1.setLanguage(Locale.ENGLISH);
                        //}
                    }
                });

                String toSpeak = (String) getText(R.string.text_mensaje_notificacion_ingles);
                Toast.makeText(this, toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                Log.d("ALARMA", "ALARMA ACTIVADA");
            }
        }
        manager = new DataBaseManager(this);
        cursor = manager.cargarCursorConfiguracion();
        int activacion_alarma = 0;
        if (cursor.moveToFirst()) {
            do {
                activacion_alarma = cursor.getInt(1);
            } while (cursor.moveToNext());
        }

        //if (activacion_alarma == 0) {
        //    stopForeground(true);
        //    stopSelf();
        //}
        Log.d("SERVICIO", "" + location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ID_CANAL;
            String description = ID_CANAL;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(ID_CANAL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LOG_TAG", "In onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        manager = new DataBaseManager(this);
        Cursor cursor = manager.cargarCursorConfiguracion();
        int activacion_alarma = 0;
        if (cursor.moveToFirst()) {
            do {
                activacion_alarma = cursor.getInt(1);
            } while (cursor.moveToNext());
        }

        if (activacion_alarma != 0) {
            Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
            restartServiceIntent.setPackage(getPackageName());
            PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(),
                    restartServicePendingIntent);
        }
        Log.i("LOG_TAG", "In onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }
}
