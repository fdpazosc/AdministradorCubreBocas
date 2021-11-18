package com.salud.admin.administradorcubrebocas;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CubrebocasDialog extends AppCompatDialogFragment {

    private EditText editTextDescripcionCubrebocas;
    private Spinner spinnerTipoCubrebocas;
    private EditText maximo_usos_cubrebocas;
    private Dialog dialogo;
    private Context aplicationContext;
    private int tipo;
    private DataBaseManager manager;
    private String[][] listaCubrebocas;
    private boolean[] cubrebocasSeleccionados;
    private CubrebocasActivity actividad;
    private ArrayList<String> tiposDeCubrebocas;
    private int id_usuario;

    public CubrebocasDialog(CubrebocasActivity actividad, int tipo, DataBaseManager manager, String[][] listaCubrebocas, boolean[] cubrebocasSeleccionados, Context aplicationContext, int id_usuario) {
        this.tipo = tipo;
        this.manager = manager;
        this.listaCubrebocas = listaCubrebocas;
        this.actividad = actividad;
        this.cubrebocasSeleccionados = cubrebocasSeleccionados;
        this.aplicationContext = aplicationContext;
        this.id_usuario = id_usuario;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_cubrebocas, null);
        builder.setView(view)
                .setTitle(getString(tipo == 0 ? R.string.dialog_message_add_cubrebocas : R.string.dialog_message_edit_cubrebocas))
                .setNegativeButton(getString(R.string.text_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogo.cancel();
                    }
                })
                .setPositiveButton(getString(tipo == 0 ? R.string.dialog_message_add_cubrebocas : R.string.dialog_message_edit_cubrebocas), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!editTextDescripcionCubrebocas.getText().toString().trim().equals("") && !maximo_usos_cubrebocas.getText().toString().trim().equals("") &&
                                spinnerTipoCubrebocas.getSelectedItemPosition() >= 0) {
                            String descripcionCubrebocas = editTextDescripcionCubrebocas.getText().toString();
                            int tipoCubreBocas = spinnerTipoCubrebocas.getSelectedItemPosition();
                            String txtTipo = tiposDeCubrebocas.get(tipoCubreBocas);
                            int maximo_usos = Integer.valueOf(String.valueOf(maximo_usos_cubrebocas.getText()));
                            if (tipo == 0) {
                                try {
                                    manager.insertarCubrebocas2(id_usuario, descripcionCubrebocas, "", txtTipo, maximo_usos);
                                } catch (Exception e) {
                                    Toast.makeText(aplicationContext, getString(R.string.error_agregar_cubrebocas) + " " + descripcionCubrebocas, Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                for (int j = 0; j < listaCubrebocas.length; j++)
                                    if (cubrebocasSeleccionados[j])
                                        try {
                                            manager.modificarCubrebocasPorId(Integer.valueOf(listaCubrebocas[j][0]), id_usuario, descripcionCubrebocas, "", txtTipo, maximo_usos);
                                        } catch (Exception e) {
                                            Toast.makeText(aplicationContext, getString(R.string.error_editar_cubrebocas) + " " + txtTipo, Toast.LENGTH_SHORT).show();
                                        }
                            }
                            actividad.cargarCubrebocas(-1);
                        } else
                            Toast.makeText(aplicationContext, getString(R.string.text_informacion_incompleta), Toast.LENGTH_LONG).show();
                    }
                });
        editTextDescripcionCubrebocas = view.findViewById(R.id.editTextDescripcionCubrebocas);
        spinnerTipoCubrebocas = view.findViewById(R.id.spinnerTipoCubrebocas);
        maximo_usos_cubrebocas = view.findViewById(R.id.maximo_usos_cubrebocas);

        String[] myResArray = getResources().getStringArray(R.array.tipos_cubrebocas_arreglo);
        List<String> myResArrayList = Arrays.asList(myResArray);


        tiposDeCubrebocas = new ArrayList<>(myResArrayList);

        //tiposDeCubrebocas.add("TIPO 1");
        //tiposDeCubrebocas.add("TIPO 2");
        //tiposDeCubrebocas.add("TIPO 3");
        //tiposDeCubrebocas.add("TIPO 4");
        //tiposDeCubrebocas.add("TIPO 5");

        spinnerTipoCubrebocas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (tipo == 0 && i == 0) {
                    maximo_usos_cubrebocas.setText("1");
                    maximo_usos_cubrebocas.setSelection(maximo_usos_cubrebocas.getText().length());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(aplicationContext, android.R.layout.simple_list_item_1, tiposDeCubrebocas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(aplicationContext,
                R.array.tipos_cubrebocas_arreglo, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoCubrebocas.setAdapter(adapter);
        for (int j = 0; j < listaCubrebocas.length; j++)
            if (cubrebocasSeleccionados[j])
                try {
                    editTextDescripcionCubrebocas.setText(listaCubrebocas[j][2]);
                    editTextDescripcionCubrebocas.setSelection(editTextDescripcionCubrebocas.getText().length());
                    maximo_usos_cubrebocas.setText(listaCubrebocas[j][5]);
                    maximo_usos_cubrebocas.setSelection(maximo_usos_cubrebocas.getText().length());

                    for (int k = 0; k < tiposDeCubrebocas.size(); k++) {
                        if (tiposDeCubrebocas.get(k).equals(listaCubrebocas[j][4]))
                            spinnerTipoCubrebocas.setSelection(k);
                    }
                } catch (Exception e) {
                }
        dialogo = builder.create();
        return dialogo;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}

