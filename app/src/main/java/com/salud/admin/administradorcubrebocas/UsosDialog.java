package com.salud.admin.administradorcubrebocas;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class UsosDialog extends AppCompatDialogFragment {

    private EditText editTextFechaUso;
    private EditText editTextHoraUso;
    private CheckBox checkBoxUsoLavado;
    private Dialog dialogo;
    private Context aplicationContext;
    private int tipo;
    private DataBaseManager manager;
    private String[][] listaUsos;
    private boolean[] usosSeleccionados;
    private UsoActivity actividad;
    private ArrayList<String> tiposDeCubrebocas;
    private int id_cubrebocas;
    private Calendar calendario;

    public UsosDialog(UsoActivity actividad, int tipo, DataBaseManager manager, String[][] listaUsos, boolean[] usosSeleccionados, Context aplicationContext, int id_cubrebocas) {
        this.tipo = tipo;
        this.manager = manager;
        this.listaUsos = listaUsos;
        this.actividad = actividad;
        this.usosSeleccionados = usosSeleccionados;
        this.aplicationContext = aplicationContext;
        this.id_cubrebocas = id_cubrebocas;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_usos, null);
        builder.setView(view)
                .setTitle(getString(tipo == 0 ? R.string.dialog_message_add_uso : R.string.dialog_message_edit_uso))
                .setNegativeButton(getString(R.string.text_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogo.cancel();
                    }
                })
                .setPositiveButton(getString(tipo == 0 ? R.string.dialog_message_add_uso : R.string.dialog_message_edit_uso), new DialogInterface.OnClickListener() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        guardar();
                    }
                });
        editTextFechaUso = view.findViewById(R.id.editTextFechaUso);
        editTextFechaUso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPickerFecha();
            }
        });
        editTextHoraUso = view.findViewById(R.id.editTextHoraUso);
        editTextHoraUso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPickerHora();
            }
        });
        checkBoxUsoLavado = view.findViewById(R.id.checkBoxUsoLavado);
        for (int j = 0; j < listaUsos.length; j++)
            if (usosSeleccionados[j])
                try {
                    editTextFechaUso.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(listaUsos[j][2])));
                    editTextHoraUso.setText(new SimpleDateFormat("HH:mm").format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(listaUsos[j][2])));
                    editTextFechaUso.setSelection(editTextFechaUso.getText().length());
                    checkBoxUsoLavado.setChecked(Integer.valueOf(listaUsos[j][3]) == 1);
                } catch (Exception e) {
                }
        dialogo = builder.create();
        return dialogo;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void guardar() {
        if (!editTextFechaUso.getText().toString().trim().equals("") && !editTextHoraUso.getText().toString().trim().equals("")) {
            String fechaUso = editTextFechaUso.getText().toString();
            boolean lavado = checkBoxUsoLavado.isChecked();
            if (tipo == 0) {
                try {
                    manager.insertarUso2(id_cubrebocas, new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(editTextFechaUso.getText() + " " + editTextHoraUso.getText()), lavado ? 1 : 0);
                } catch (Exception e) {
                    Toast.makeText(aplicationContext, getString(R.string.error_agregar_uso) + " " + fechaUso, Toast.LENGTH_SHORT).show();
                    Log.d("error", e.getMessage());
                }

            } else {
                for (int j = 0; j < listaUsos.length; j++)
                    if (usosSeleccionados[j])
                        try {
                            manager.modificarUsoPorId2(Integer.valueOf(listaUsos[j][0]), id_cubrebocas, new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(editTextFechaUso.getText() + " " + editTextHoraUso.getText()), lavado ? 1 : 0);
                        } catch (Exception e) {
                            Toast.makeText(aplicationContext, getString(R.string.error_editar_uso), Toast.LENGTH_SHORT).show();
                            Log.d("error", e.getMessage());
                        }
            }
            actividad.cargarUsos(-1);
            actividad.aparecerInterstitial();
            actividad.cargarInterstitial();
        } else
            Toast.makeText(aplicationContext, getString(R.string.text_informacion_incompleta), Toast.LENGTH_LONG).show();
    }

    private void mostrarPickerHora() {
        calendario = Calendar.getInstance();
        int mHour = calendario.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendario.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        editTextHoraUso.setText((hourOfDay < 10 ? "0" : "") + hourOfDay + ":" + (minute < 10 ? "0" : "") + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void mostrarPickerFecha() {
        calendario = Calendar.getInstance();
        int mYear = calendario.get(Calendar.YEAR);
        int mMonth = calendario.get(Calendar.MONTH);
        int mDay = calendario.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        editTextFechaUso.setText((dayOfMonth < 10 ? "0" : "") + dayOfMonth + "-" + ((monthOfYear + 1) < 10 ? "0" : "") + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}

