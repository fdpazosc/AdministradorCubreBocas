package com.salud.admin.administradorcubrebocas;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ConfigActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    protected LocationManager locationManager;
    private GoogleMap mMap;
    protected LocationListener locationListener;
    private DataBaseManager manager;
    protected Context context;
    private String lat;
    private String provider;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;
    private EditText distanciaAlarma;
    private Switch activarAlarma;
    private MenuItem agregarUbicacion;
    private ArrayList<Circle> lista_circulos;
    private int distancia_alarma;
    private SupportMapFragment mapFragment;
    private Context yo;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        getSupportActionBar().setTitle(getString(R.string.dialog_message_configuracion));

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        activarAlarma = (Switch) findViewById(R.id.activacion_alarmas);
        distanciaAlarma = (EditText) findViewById(R.id.distancia_activacion_alarmas);
        distanciaAlarma.setFilters(new InputFilter[]{new MinMaxFilter("1", "99")});


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        yo = this;

        manager = new DataBaseManager(this);

        distancia_alarma = 1;

        Cursor cursor = manager.cargarCursorConfiguracion();

        if (cursor.moveToFirst()) {
            do {
                distancia_alarma = cursor.getInt(2);
            } while (cursor.moveToNext());
        }

        activarAlarma.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                guardarConfiguracion(activarAlarma.isChecked() ? 1 : 0, Integer.parseInt(distanciaAlarma.getText().toString().trim().equals("") ? "1" : distanciaAlarma.getText().toString().trim()));
                if (activarAlarma.isChecked())
                    //startService(new Intent(getApplicationContext(), DistanceAlarmService.class));
                    startAlarm();
                else
                    cancelAlarm();
                //stopService(new Intent(getApplicationContext(), DistanceAlarmService.class));
            }
        });

        distanciaAlarma.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                guardarConfiguracion(activarAlarma.isChecked() ? 1 : 0, Integer.parseInt(distanciaAlarma.getText().toString().trim().equals("") ? "1" : distanciaAlarma.getText().toString().trim()));
                /*Cursor cursor = manager.cargarCursorConfiguracion();
                if (cursor.moveToFirst()) {
                    do {
                        distancia_alarma = cursor.getInt(2);
                    } while (cursor.moveToNext());
                }*/
                distancia_alarma = Integer.parseInt(distanciaAlarma.getText().toString().trim().equals("") ? "1" : distanciaAlarma.getText().toString().trim());
                refrescarMapa();
                Log.d("distancia", distanciaAlarma.getText().toString());
                return false;
            }
        });

        MobileAds.initialize(this, getString(R.string.ad_app_id));

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this, ReceptorAlarma.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        //AdView mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder()/*.addTestDevice("D65AD9F826E1875E16ADFDF9B4CA727B")*/.build();
        //mAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        if (manager == null)
            manager = new DataBaseManager(this);

        Cursor cursor = manager.cargarCursorConfiguracion();

        if (cursor.moveToFirst()) {
            do {
                activarAlarma.setChecked(cursor.getInt(1) == 1);
                distancia_alarma = cursor.getInt(2);
                distanciaAlarma.setText("" + distancia_alarma);
            } while (cursor.moveToNext());
        }

        try {
            refrescarMapa();
            ajustarZoomTodosPuntos();
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }


        Log.d("TAG", "onResume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        guardarConfiguracion(activarAlarma.isChecked() ? 1 : 0, Integer.parseInt(distanciaAlarma.getText().toString().trim().equals("") ? "1" : distanciaAlarma.getText().toString().trim()));
        Log.d("onStop", "ok");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("onDestroy", "ok");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_config, menu);
        agregarUbicacion = (MenuItem) menu.findItem(R.id.action_agregar_ubicacion);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_agregar_ubicacion:
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("distancia_alarma", distancia_alarma);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d("Locacion:", "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        } else
            Log.d("Locacion:", "Error");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        refrescarMapa();

        ajustarZoomTodosPuntos();

        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    public void graficarPuntoConDistancia(LatLng punto, String titulo) {
        lista_circulos.add(mMap.addCircle(new CircleOptions()
                .center(punto)
                .radius(distancia_alarma)
                .strokeWidth(5)
                .strokeColor(Color.CYAN)
                .fillColor(Color.parseColor("#5571FF77"))
        ));
        mMap.addMarker(new MarkerOptions().position(punto).title(titulo));
        Log.d("TAG", "graficarPuntoConDistancia");
    }

    public void ajustarZoomTodosPuntos() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //manager = new DataBaseManager(this);

        Cursor cursor = manager.cargarCursorPuntosRecordatorio();

        if (cursor.moveToFirst()) {
            do {
                builder.include(new LatLng(cursor.getDouble(2), cursor.getDouble(3)));
            } while (cursor.moveToNext());
        } else {
            builder.include(new LatLng(0, 0));
        }

        LatLngBounds bounds = builder.build();

        int width = mapFragment.getView().getMeasuredWidth();
        int height = mapFragment.getView().getMeasuredHeight();
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);
    }

    public void refrescarMapa() {
        lista_circulos = new ArrayList<>();

        mMap.clear();

        //manager = new DataBaseManager(this);

        Cursor cursor = manager.cargarCursorPuntosRecordatorio();

        String[][] puntos_recor = new String[cursor.getCount()][];

        int contador = 0;
        if (cursor.moveToFirst()) {
            do {
                puntos_recor[contador++] = new String[]{"" + cursor.getString(1), "" + cursor.getDouble(2), "" + cursor.getDouble(3)};
            } while (cursor.moveToNext());
        }

        //puntos_recor[0] = new String[]{"Casa", "-0.0902", "-78.4397"};
        //puntos_recor[1] = new String[]{"Trabajo", "-0.09027333333333334", "-78.43978999999999"};
        //puntos_recor[2] = new String[]{"Prueba", "-0.09027", "-78.4397"};

        LatLng punto = null;
        for (int i = 0; i < puntos_recor.length; i++) {
            punto = new LatLng(Double.parseDouble(puntos_recor[i][1]), Double.parseDouble(puntos_recor[i][2]));
            graficarPuntoConDistancia(punto, puntos_recor[i][0]);
        }
    }

    public void guardarConfiguracion(int activarAlarma, int distanciaAlarma) {
        Cursor cursor = manager.cargarCursorConfiguracion();
        int id = -1;
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    id = cursor.getInt(0);
                } while (cursor.moveToNext());
            }
            manager.modificarConfiguracionPorId(id, activarAlarma, distanciaAlarma);
        } else {
            manager.insertarConfiguracion2(activarAlarma, distanciaAlarma);
        }
    }

    private void startAlarm() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
        }
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1, pendingIntent);*/
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("receptor");
        broadcastIntent.setClass(this, ReceptorAlarma.class);
        getBaseContext().sendBroadcast(broadcastIntent);
    }

    private void cancelAlarm() {
        try {
            /*Intent restartServiceIntent = new Intent(getApplicationContext(), DistanceAlarmService.class);
            restartServiceIntent.setPackage(getPackageName());
            PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmaManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmaManager.cancel(restartServicePendingIntent);*/
            stopService(new Intent(this, DistanceAlarmService.class));
        } catch (Exception e) {
            Log.d("SERVICIO", e.getMessage());
        }
    }
}