package com.salud.admin.administradorcubrebocas;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private ListView lista_usuarios;
    private String[][] lista;
    private ListViewAdapterUsuario adapter;
    private Context contexto;
    private Activity actividad;
    private Dialog dialog;
    private DataBaseManager manager;
    private Cursor cursor;
    private MainActivity yo;
    private MenuItem deleteMenuItem;
    private MenuItem editMenuItem;
    private boolean[] itemsSeleccionados;
    private FloatingActionButton fab_add;
    private Dialog dialogo;
    private TextView titulo_usuarios;
    private InterstitialAd interstitialAd;
    private int interstitialAdCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        contexto = this;
        actividad = this;
        yo = this;
        MobileAds.initialize(this, getString(R.string.ad_app_id));
        manager = new DataBaseManager(this);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()/*.addTestDevice("D65AD9F826E1875E16ADFDF9B4CA727B")*/.build();
        mAdView.loadAd(adRequest);
        lista_usuarios = (ListView) findViewById(R.id.lista_usuarios);
        titulo_usuarios = (TextView) findViewById(R.id.titulo_usuarios);
        //titulo_usuarios.setTypeface(FontManager.getTypeface(contexto, FontManager.FONTAWESOME), Typeface.BOLD);
        //titulo_usuarios.setText(getText(R.string.fa_user) + " " + getText(R.string.encabezados_usuarios));
        cargarUsuarios(-1);
        fab_add = findViewById(R.id.fab_add_user);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UsuarioDialog usuarioDialog = new UsuarioDialog(yo, 0, manager, lista, itemsSeleccionados, getApplicationContext());
                usuarioDialog.show(getSupportFragmentManager(), "Usuario dialog");
                for (boolean itemsSeleccionado : itemsSeleccionados)
                    Log.d("clicked", "" + itemsSeleccionado);
            }
        });
        aparecerInterstitial();
        cargarInterstitial();
    }

    @Override
    protected void onStart() {
        aparecerInterstitial();
        cargarInterstitial();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Log.d("ATRAS", "ATRAS");
        if (adapter.getSeleccionado() >= 0) {
            cargarUsuarios(-1);
            lista_usuarios.setClickable(true);
        } else
            super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        deleteMenuItem = (MenuItem) menu.findItem(R.id.action_delete);
        editMenuItem = (MenuItem) menu.findItem(R.id.action_edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                UsuarioDialog usuarioDialog = new UsuarioDialog(yo, 1, manager, lista, itemsSeleccionados, getApplicationContext());
                usuarioDialog.show(getSupportFragmentManager(), "Usuario dialog");
                return true;
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.dialog_message_delete_usuario));
                builder.setMessage(R.string.confirmacion_eliminar);
                builder.setNegativeButton(getString(R.string.text_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dialogo.cancel();
                    }
                });
                builder.setPositiveButton(getString(R.string.dialog_message_delete_usuario), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j = 0; j < lista.length; j++)
                            if (itemsSeleccionados[j])
                                try {
                                    manager.eliminarUsuarioPorId(Integer.valueOf(lista[j][0]));
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_eliminar_usuario) + " " + lista[j][1], Toast.LENGTH_SHORT).show();
                                }
                        cargarUsuarios(-1);
                    }
                });

                dialog = builder.create();
                dialog.show();
                return true;
            case R.id.action_config:
                Intent intent = new Intent(this, ConfigActivity.class);
                //intent.putExtra("ID_USUARIO", id);
                if (revisarPermisos())
                    startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cargarUsuarios(int selecionado) {
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

        cursor = manager.cargarCursorUsuarios();
        lista = new String[cursor.getCount()][];
        itemsSeleccionados = new boolean[cursor.getCount()];
        int contador = 0;
        if (cursor.moveToFirst()) {
            do {
                lista[contador++] = new String[]{"" + cursor.getInt(0), cursor.getString(1), "" + cursor.getInt(2)};
            } while (cursor.moveToNext());
        }
        adapter = new ListViewAdapterUsuario(contexto, lista, selecionado, itemsSeleccionados, deleteMenuItem, editMenuItem);
        lista_usuarios.setAdapter(adapter);
        lista_usuarios.setLongClickable(true);
        lista_usuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AbrirActividadCubrebocasPorUsuarioId(lista[position][0], lista[position][1]);
                Log.i("User clicked ", lista[position][1]);
            }
        });
        lista_usuarios.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                //if (Integer.valueOf(lista[position][2]) == 0)
                cargarUsuarios(position);
                if (lista.length <= 1) {
                    editMenuItem.setVisible(false);
                    deleteMenuItem.setVisible(false);
                }
                if (Integer.valueOf(lista[position][2]) != 0) {
                    deleteMenuItem.setVisible(false);
                    editMenuItem.setVisible(true);
                }
                lista_usuarios.setClickable(false);
                Log.i("User long clicked ", lista[position][1]);
                return true;
            }
        });
    }

    private void AbrirActividadCubrebocasPorUsuarioId(String id, String nombre) {
        Intent intent = new Intent(this, CubrebocasActivity.class);
        intent.putExtra("ID_USUARIO", id);
        intent.putExtra("NOMBRE_USUARIO", nombre);
        startActivity(intent);
    }

    private void aparecerInterstitial() {
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
                //Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                Log.d("mensaje", mensaje);
            }
        });
    }

    private void cargarInterstitial() {
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

    private boolean revisarPermisos() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            //Toast.makeText(getBaseContext(), getText(R.string.text_mensaje_permisos), Toast.LENGTH_LONG).show();
            Snackbar.make(findViewById(R.id.fab_add_user), getText(R.string.text_mensaje_permisos), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}