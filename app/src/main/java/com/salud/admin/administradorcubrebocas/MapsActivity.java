package com.salud.admin.administradorcubrebocas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private Marker punto_a_guardar;
    private Button buscar_lugar;
    private Context contexto;
    private EditText direccion;
    private SupportMapFragment mapFragment;
    private Location location_usuario;
    protected LocationManager locationManager;
    private FloatingActionButton guardar;
    private FloatingActionButton eliminar;
    private String id_pungo_a_guardar;
    private boolean add_Edit;
    private int distancia_alarma;
    private ArrayList<Circle> lista_circulos;
    private DataBaseManager manager;
    private MapsActivity yo;
    private InterstitialAd interstitialAd;
    private int interstitialAdCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        contexto = this;
        yo = this;

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
        distancia_alarma = 1;
        Log.d("midistancia", "" + getIntent().getIntExtra("distancia_alarma", 1));
        distancia_alarma = Integer.parseInt(String.valueOf(getIntent().getIntExtra("distancia_alarma", 1)));
        Cursor cursor = manager.cargarCursorConfiguracion();
        if (cursor.moveToFirst()) {
            do {
                distancia_alarma = cursor.getInt(2);
            } while (cursor.moveToNext());
        }
        direccion = (EditText) findViewById(R.id.direccion);
        guardar = (FloatingActionButton) findViewById(R.id.fab_save_punto);
        eliminar = (FloatingActionButton) findViewById(R.id.fab_delete_punto);
        buscar_lugar = (Button) findViewById(R.id.buscar_lugar);
        buscar_lugar.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME), Typeface.BOLD);
        buscar_lugar.setText(getText(R.string.fa_search));
        buscar_lugar.setTextColor(getApplication().getResources().getColor(R.color.blanco));

        buscar_lugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!direccion.getText().toString().trim().equals("")) {
                    Log.d("mensajes", "" + getLocationFromAddress(contexto, direccion.getText().toString().trim()));
                    LatLng busqueda = getLocationFromAddress(contexto, direccion.getText().toString().trim());
                    int height = 100;
                    int width = 100;
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.search_solid);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    //mMap.addMarker(new MarkerOptions().position(busqueda).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title(direccion.getText().toString()));
                    if(busqueda != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(busqueda));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                    } else {
                        Toast.makeText(getApplicationContext(), getText(R.string.text_locacion_no_encontrada), Toast.LENGTH_LONG).show();
                    }
                }
                if (punto_a_guardar != null)
                    if (add_Edit)
                        punto_a_guardar.remove();
                guardar.setVisibility(View.GONE);
                eliminar.setVisibility(View.GONE);
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (punto_a_guardar != null) {
                    PuntosDialog puntoDialogolo = null;
                    if (add_Edit) {
                        puntoDialogolo = new PuntosDialog(getApplicationContext(), yo, manager, 0, punto_a_guardar, guardar, eliminar);
                    } else {
                        puntoDialogolo = new PuntosDialog(getApplicationContext(), yo, manager, 1, punto_a_guardar, guardar, eliminar);
                    }
                    puntoDialogolo.show(getSupportFragmentManager(), "Punto dialog");
                }
                //aparecerInterstitial();
                //cargarInterstitial();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (punto_a_guardar != null) {
                    manager.eliminarPuntosRecordatorioPorLatLong(punto_a_guardar.getPosition().latitude, punto_a_guardar.getPosition().longitude);
                    for (int i = 0; i < lista_circulos.size(); i++) {
                        if (punto_a_guardar.getPosition().latitude == lista_circulos.get(i).getCenter().latitude && punto_a_guardar.getPosition().longitude == lista_circulos.get(i).getCenter().longitude) {
                            lista_circulos.get(i).remove();
                            lista_circulos.remove(i);
                            break;
                        }
                    }
                    punto_a_guardar.remove();
                    eliminar.setVisibility(View.GONE);
                    guardar.setVisibility(View.GONE);
                    refrescarMapa();
                }
                //aparecerInterstitial();
                //cargarInterstitial();
            }
        });

        MobileAds.initialize(this, getString(R.string.ad_app_id));

        //AdView mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder()/*.addTestDevice("D65AD9F826E1875E16ADFDF9B4CA727B")*/.build();
        //mAdView.loadAd(adRequest);

        //aparecerInterstitial();
        //cargarInterstitial();

        Log.d("TAG", "onCreate");
    }

    @Override
    protected void onResume() {
        if (manager == null)
            manager = new DataBaseManager(this);

        Cursor cursor = manager.cargarCursorConfiguracion();

        if (cursor.moveToFirst()) {
            do {
                distancia_alarma = cursor.getInt(2);
                //direccion.setText("" + distancia_alarma);
            } while (cursor.moveToNext());
        }

        try {
            refrescarMapa();
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        //aparecerInterstitial();
        //cargarInterstitial();
        super.onStart();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (add_Edit)
                    if (punto_a_guardar != null)
                        punto_a_guardar.remove();
                punto_a_guardar = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).title(""));
                id_pungo_a_guardar = punto_a_guardar.getId();
                guardar.setImageResource(R.drawable.plus_solid);
                add_Edit = true;
                Log.d("posicion", "" + latLng);
                guardar.setVisibility(View.VISIBLE);
                eliminar.setVisibility(View.GONE);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (punto_a_guardar != null)
                    if (add_Edit)
                        punto_a_guardar.remove();
                guardar.setVisibility(View.GONE);
                eliminar.setVisibility(View.GONE);
                Log.d("Localizacion", "CLICK");
            }
        });

        direccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (punto_a_guardar != null)
                    if (add_Edit)
                        punto_a_guardar.remove();
                guardar.setVisibility(View.GONE);
                eliminar.setVisibility(View.GONE);
            }
        });

        direccion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (punto_a_guardar != null)
                    if (add_Edit)
                        punto_a_guardar.remove();
                guardar.setVisibility(View.GONE);
                eliminar.setVisibility(View.GONE);
                return false;
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                try {
                    LatLng busqueda = new LatLng(location_usuario.getLatitude(), location_usuario.getLongitude());
                    int height = 100;
                    int width = 100;
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.street_view_solid);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    //mMap.addMarker(new MarkerOptions().position(busqueda).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Ubicación Actual"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(busqueda));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(0.0f));
                }catch (Exception e){
                    Log.d("Error Locacion", e.getMessage());
                }
                return false;
            }
        });

        /*mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //if (punto_a_guardar != null)
                //    punto_a_guardar.remove();

            }
        });*/

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (punto_a_guardar != null)
                    if (add_Edit)
                        punto_a_guardar.remove();
                if (!marker.getId().equals(id_pungo_a_guardar)) {
                    add_Edit = false;
                    punto_a_guardar = marker;
                    guardar.setImageResource(R.drawable.pen_solid);
                    guardar.setVisibility(View.VISIBLE);
                    eliminar.setVisibility(View.VISIBLE);
                    Log.d("Locale", "" + punto_a_guardar.getPosition());
                } else {
                    guardar.setVisibility(View.GONE);
                    eliminar.setVisibility(View.GONE);
                }
                return false;
            }
        });
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
        int height = mapFragment.getView().getMeasuredHeight() - 150;
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

        LatLng punto = new LatLng(0, 0);
        for (int i = 0; i < puntos_recor.length; i++) {
            punto = new LatLng(Double.parseDouble(puntos_recor[i][1]), Double.parseDouble(puntos_recor[i][2]));
            graficarPuntoConDistancia(punto, puntos_recor[i][0]);
        }
    }

    public void graficarPuntoConDistancia(LatLng punto, String titulo) {
        //manager = new DataBaseManager(this);
        distancia_alarma = Integer.parseInt(String.valueOf(getIntent().getIntExtra("distancia_alarma", 1)));
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

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    @Override
    public void onLocationChanged(Location location) {
        location_usuario = location;
        if (location != null) {
            Log.d("Localizacion:", "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        } else
            Log.d("Localizacion:", "Error");
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

    public void aparecerInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                showAdInstertitial();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                String mensaje = String.format("onAdFailedToLoad (%s)", getErrorReason(errorCode));
                Log.d("mensaje", mensaje);
            }
        });
    }

    public void cargarInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        interstitialAd.loadAd(adRequest);
    }

    private String getErrorReason(int errorCode) {
        String errorReason = "";
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Error Interno";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid Request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }

    private void showAdInstertitial() {
        if (interstitialAd.isLoaded())
            interstitialAd.show();
        else
            Log.d("mensaje", "El Interstitial no sirvio");
    }
}