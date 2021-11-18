package com.salud.admin.administradorcubrebocas;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UsoActivity extends AppCompatActivity {

    private Context contexto;
    private Activity actividad;
    private UsoActivity yo;
    private DataBaseManager manager;
    private ListView lista_usos;
    private TextView titulo_usos;
    private int id_cubrebocas;
    private String[][] lista;
    private boolean[] itemsSeleccionados;
    private ListViewAdapterUsos adapter;
    private MenuItem deleteMenuItem;
    private MenuItem editMenuItem;
    private Cursor cursor;
    private Dialog dialog;
    private FloatingActionButton fab_add;
    private Calendar calendario;
    private InterstitialAd interstitialAd;
    private int interstitialAdCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uso);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Usos");
        contexto = this;
        actividad = this;
        yo = this;
        MobileAds.initialize(this, getString(R.string.ad_app_id));
        manager = new DataBaseManager(this);
        lista_usos = (ListView) findViewById(R.id.lista_usos);
        titulo_usos = (TextView) findViewById(R.id.titulo_usos);
        try {
            id_cubrebocas = Integer.valueOf(getIntent().getStringExtra("ID_CUBREBOCAS"));
        } catch (Exception e) {
            id_cubrebocas = 0;
        }
        Log.d("mensaje_usos", "" + id_cubrebocas);
        fab_add = findViewById(R.id.fab_add_uso);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UsosDialog usosDialog = new UsosDialog(yo, 0, manager, lista, itemsSeleccionados, getApplicationContext(), id_cubrebocas);
                usosDialog.show(getSupportFragmentManager(), "Usos dialog");
                for (boolean itemsSeleccionado : itemsSeleccionados)
                    Log.d("clicked", "" + itemsSeleccionado);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        cargarUsos(-1);
        calendario = Calendar.getInstance();
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()/*.addTestDevice("D65AD9F826E1875E16ADFDF9B4CA727B")*/.build();
        mAdView.loadAd(adRequest);
        //aparecerInterstitial();
        //cargarInterstitial();
    }

    @Override
    protected void onStart() {
        //aparecerInterstitial();
        //cargarInterstitial();
        super.onStart();
    }

    private void mostrarPickerHora() {
        int mHour = calendario.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendario.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        //txtTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void mostrarPickerFecha() {
        int mYear = calendario.get(Calendar.YEAR);
        int mMonth = calendario.get(Calendar.MONTH);
        int mDay = calendario.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        //txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onBackPressed() {
        Log.d("ATRAS", "ATRAS");
        if (adapter.getSeleccionado() >= 0) {
            cargarUsos(-1);
            lista_usos.setClickable(true);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_uso, menu);
        deleteMenuItem = (MenuItem) menu.findItem(R.id.action_delete);
        editMenuItem = (MenuItem) menu.findItem(R.id.action_edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                UsosDialog usosDialog = new UsosDialog(yo, 1, manager, lista, itemsSeleccionados, getApplicationContext(), id_cubrebocas);
                usosDialog.show(getSupportFragmentManager(), "Usos dialog");
                return true;
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.dialog_message_delete_uso));
                builder.setMessage(R.string.confirmacion_eliminar);
                builder.setNegativeButton(getString(R.string.text_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dialogo.cancel();
                    }
                });
                builder.setPositiveButton(getString(R.string.dialog_message_delete_uso), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j = 0; j < lista.length; j++)
                            if (itemsSeleccionados[j])
                                try {
                                    manager.eliminarUsoPorId(Integer.valueOf(lista[j][0]));
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_eliminar_uso) + " " + lista[j][1], Toast.LENGTH_SHORT).show();
                                    Log.d("error", e.getMessage());
                                }
                        cargarUsos(-1);
                    }
                });

                dialog = builder.create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cargarUsos(int selecionado) {
        if (editMenuItem != null || deleteMenuItem != null)
            if (selecionado < 0) {
                editMenuItem.setVisible(false);
                deleteMenuItem.setVisible(false);
                fab_add.setVisibility(View.VISIBLE);
            } else {
                editMenuItem.setVisible(true);
                deleteMenuItem.setVisible(true);
                fab_add.setVisibility(View.GONE);
            }
        else Log.d("estado", "estado");

        cursor = manager.cargarCursorUsoPorCubrebocas(id_cubrebocas);
        lista = new String[cursor.getCount()][];
        itemsSeleccionados = new boolean[cursor.getCount()];
        int contador = 0;
        if (cursor.moveToFirst()) {
            do {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                lista[contador++] = new String[]{"" + cursor.getInt(0), "" + cursor.getInt(1), "" + cursor.getString(2), "" + cursor.getInt(3)};
            } while (cursor.moveToNext());
        }
        adapter = new ListViewAdapterUsos(contexto, lista, selecionado, itemsSeleccionados, deleteMenuItem, editMenuItem);
        lista_usos.setAdapter(adapter);
        lista_usos.setLongClickable(true);
        lista_usos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("User clicked ", lista[position][1]);
            }
        });
        lista_usos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                cargarUsos(position);
                /*if (lista.length <= 1) {
                    editMenuItem.setVisible(false);
                    deleteMenuItem.setVisible(false);
                }*/
                lista_usos.setClickable(false);
                Log.i("User long clicked ", lista[position][1]);
                return true;
            }
        });
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
                Log.d("mensaje",mensaje);
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