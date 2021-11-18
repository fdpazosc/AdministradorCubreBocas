package com.salud.admin.administradorcubrebocas;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PuntosDialog extends AppCompatDialogFragment {

    private EditText editTextTitulo;
    private Context aplicationContext;
    private Marker marcador;
    private Dialog dialogo;
    private DataBaseManager manager;
    private MapsActivity actividad;
    private int tipo;
    private FloatingActionButton guardar;
    private FloatingActionButton eliminar;

    public PuntosDialog(Context aplicationContext, MapsActivity actividad, DataBaseManager manager, int tipo, Marker marcador, FloatingActionButton guardar, FloatingActionButton eliminar) {
        this.tipo = tipo;
        this.actividad = actividad;
        this.manager = manager;
        this.marcador = marcador;
        this.guardar = guardar;
        this.eliminar = eliminar;
        this.aplicationContext = aplicationContext;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_puntos_recordatorio, null);
        builder.setView(view)
                .setTitle(getString(tipo == 0 ? R.string.dialog_message_add_punto : R.string.dialog_message_edit_punto))
                .setNegativeButton(getString(R.string.text_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        actividad.refrescarMapa();
                        guardar.setVisibility(View.GONE);
                        eliminar.setVisibility(View.GONE);
                        dialogo.cancel();
                    }
                })
                .setPositiveButton(getString(tipo == 0 ? R.string.dialog_message_add_punto : R.string.dialog_message_edit_punto), new DialogInterface.OnClickListener() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!editTextTitulo.getText().toString().trim().equals("")) {
                            String titulo = editTextTitulo.getText().toString();
                            if (tipo == 0) {
                                try {
                                    manager.insertarPuntosRecordatorio2(titulo.trim(), marcador.getPosition().latitude, marcador.getPosition().longitude);
                                } catch (Exception e) {
                                    Toast.makeText(aplicationContext, getString(R.string.error_agregar_punto) + " " + titulo, Toast.LENGTH_SHORT).show();
                                    Log.d("error", e.getMessage());
                                }

                            } else {
                                try {
                                    manager.modificarPuntosRecordatorioPorLatLong2(titulo.trim(), marcador.getPosition().latitude, marcador.getPosition().longitude);
                                } catch (Exception e) {
                                    Toast.makeText(aplicationContext, getString(R.string.error_editar_punto) + " " + titulo, Toast.LENGTH_SHORT).show();
                                    Log.d("error", e.getMessage());
                                }
                            }
                        } else
                            Toast.makeText(aplicationContext, getString(R.string.text_informacion_incompleta), Toast.LENGTH_LONG).show();
                        actividad.refrescarMapa();
                        guardar.setVisibility(View.GONE);
                        eliminar.setVisibility(View.GONE);
                    }
                });
        editTextTitulo = view.findViewById(R.id.editTextTitulo);
        editTextTitulo.setText(marcador.getTitle());
        dialogo = builder.create();
        return dialogo;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}

