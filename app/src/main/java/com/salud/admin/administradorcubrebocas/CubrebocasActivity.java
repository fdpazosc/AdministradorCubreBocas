package com.salud.admin.administradorcubrebocas;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CubrebocasActivity extends AppCompatActivity {

    private ListView lista_cubrebocas;
    private Context contexto;
    private Activity actividad;
    private CubrebocasActivity yo;
    private DataBaseManager manager;
    private TextView titulo_cubrebocas;
    private int id_usuario;
    private MenuItem deleteMenuItem;
    private MenuItem editMenuItem;
    private boolean[] itemsSeleccionados;
    private FloatingActionButton fab_add;
    private Cursor cursor;
    private String[][] lista;
    private ListViewAdapterCubrebocas adapter;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cubrebocas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.toolbar_cubrebocas) + " " + getIntent().getStringExtra("NOMBRE_USUARIO"));
        contexto = this;
        actividad = this;
        yo = this;
        MobileAds.initialize(this, getString(R.string.ad_app_id));
        manager = new DataBaseManager(this);
        lista_cubrebocas = (ListView) findViewById(R.id.lista_cubrebocas);
        titulo_cubrebocas = (TextView) findViewById(R.id.titulo_cubrebocas);
        //titulo_cubrebocas.setTypeface(FontManager.getTypeface(contexto, FontManager.FONTAWESOME), Typeface.BOLD);
        //titulo_cubrebocas.setText(getText(R.string.fa_user) + " " + getText(R.string.encabezados_cubrebocas));
        try {
            id_usuario = Integer.valueOf(getIntent().getStringExtra("ID_USUARIO"));
        } catch (Exception e) {
            id_usuario = 0;
        }
        Log.d("mensaje", getIntent().getStringExtra("ID_USUARIO"));
        Log.d("mensaje", getIntent().getStringExtra("NOMBRE_USUARIO"));
        fab_add = findViewById(R.id.fab_add_cubrebocas);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CubrebocasDialog cubrebocasDialog = new CubrebocasDialog(yo, 0, manager, lista, itemsSeleccionados, getApplicationContext(), id_usuario);
                cubrebocasDialog.show(getSupportFragmentManager(), "Cubrebocas dialog");
                for (boolean itemsSeleccionado : itemsSeleccionados)
                    Log.d("clicked", "" + itemsSeleccionado);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        cargarCubrebocas(-1);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()/*.addTestDevice("D65AD9F826E1875E16ADFDF9B4CA727B")*/.build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        cargarCubrebocas(-1);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Log.d("ATRAS", "ATRAS");
        if (adapter.getSeleccionado() >= 0) {
            cargarCubrebocas(-1);
            lista_cubrebocas.setClickable(true);
        }
        else
            super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cubreboca, menu);
        deleteMenuItem = (MenuItem) menu.findItem(R.id.action_delete);
        editMenuItem = (MenuItem) menu.findItem(R.id.action_edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                CubrebocasDialog cubrebocasDialog = new CubrebocasDialog(yo, 1, manager, lista, itemsSeleccionados, getApplicationContext(), id_usuario);
                cubrebocasDialog.show(getSupportFragmentManager(), "Cubrebocas dialog");
                return true;
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.dialog_message_delete_cubrebocas));
                builder.setMessage(R.string.confirmacion_eliminar);
                builder.setNegativeButton(getString(R.string.text_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dialogo.cancel();
                    }
                });
                builder.setPositiveButton(getString(R.string.dialog_message_delete_cubrebocas), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j = 0; j < lista.length; j++)
                            if (itemsSeleccionados[j])
                                try {
                                    manager.eliminarCubrebocasPorId(Integer.valueOf(lista[j][0]));
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_eliminar_cubrebocas) + " " + lista[j][1], Toast.LENGTH_SHORT).show();
                                }
                        cargarCubrebocas(-1);
                    }
                });

                dialog = builder.create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cargarCubrebocas(int selecionado) {
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

        cursor = manager.cargrCubrebocasPorUsuario(id_usuario);
        lista = new String[cursor.getCount()][];
        itemsSeleccionados = new boolean[cursor.getCount()];
        int contador = 0;
        if (cursor.moveToFirst()) {
            do {
                lista[contador++] = new String[]{"" + cursor.getInt(0), "" + cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), "" + cursor.getInt(5)};
            } while (cursor.moveToNext());
        }
        adapter = new ListViewAdapterCubrebocas(contexto, lista, selecionado, itemsSeleccionados, deleteMenuItem, editMenuItem);
        lista_cubrebocas.setAdapter(adapter);
        lista_cubrebocas.setLongClickable(true);
        lista_cubrebocas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AbrirActividadUsoPorCubrebocasId(lista[position][0]);
                Log.i("User clicked ", lista[position][1]);
            }
        });
        lista_cubrebocas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                cargarCubrebocas(position);
                /*if (lista.length <= 1) {
                    editMenuItem.setVisible(false);
                    deleteMenuItem.setVisible(false);
                }*/
                lista_cubrebocas.setClickable(false);
                Log.i("User long clicked ", lista[position][1]);
                return true;
            }
        });
    }

    private void AbrirActividadUsoPorCubrebocasId(String id) {
        Intent intent = new Intent(this, UsoActivity.class);
        intent.putExtra("ID_CUBREBOCAS", id);
        startActivity(intent);
    }
}