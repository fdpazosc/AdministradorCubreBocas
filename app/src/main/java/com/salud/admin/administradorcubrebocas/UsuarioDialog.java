package com.salud.admin.administradorcubrebocas;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class UsuarioDialog extends AppCompatDialogFragment {

    private EditText editTextUsername;
    private CheckBox checkFavorito;
    private Dialog dialogo;
    private int tipo;
    private DataBaseManager manager;
    private String[][] listaUsuarios;
    private boolean[] usuariosSeleccionados;
    private MainActivity actividad;
    private Context aplicationContext;

    public UsuarioDialog(MainActivity actividad, int tipo, DataBaseManager manager, String[][] listaUsuarios, boolean[] usuariosSeleccionados, Context aplicationContext) {
        this.tipo = tipo;
        this.manager = manager;
        this.listaUsuarios = listaUsuarios;
        this.actividad = actividad;
        this.usuariosSeleccionados = usuariosSeleccionados;
        this.aplicationContext = aplicationContext;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_usuario, null);
        builder.setView(view)
                .setTitle(getString(tipo == 0 ? R.string.dialog_message_add_usuario : R.string.dialog_message_edit_usuario))
                .setNegativeButton(getString(R.string.text_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogo.cancel();
                    }
                })
                .setPositiveButton(getString(tipo == 0 ? R.string.dialog_message_add_usuario : R.string.dialog_message_edit_usuario), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!editTextUsername.getText().toString().trim().equals("")) {
                            String username = editTextUsername.getText().toString();
                            if (checkFavorito.isChecked())
                                manager.todosUsuariosComoNoFavoritos();
                            if (tipo == 0) {
                                try {
                                    manager.insertarUsuario2(username.trim().toUpperCase(), checkFavorito.isChecked() ? 1 : 0);
                                } catch (Exception e) {
                                    Toast.makeText(aplicationContext, getString(R.string.error_agregar_usuario) + " " + username, Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                for (int j = 0; j < listaUsuarios.length; j++)
                                    if (usuariosSeleccionados[j])
                                        try {
                                            manager.modificarUsuarioPorID(Integer.valueOf(listaUsuarios[j][0]), username.trim().toUpperCase(), checkFavorito.isChecked() ? 1 : 0);
                                        } catch (Exception e) {
                                            Toast.makeText(aplicationContext, getString(R.string.error_editar_usuario) + " " + listaUsuarios[j][1], Toast.LENGTH_SHORT).show();
                                        }
                            }
                            actividad.cargarUsuarios(-1);
                        } else
                            Toast.makeText(aplicationContext, getString(R.string.text_informacion_incompleta), Toast.LENGTH_LONG).show();
                    }
                });
        editTextUsername = view.findViewById(R.id.username);
        checkFavorito = view.findViewById(R.id.favorite);
        for (int j = 0; j < listaUsuarios.length; j++)
            if (usuariosSeleccionados[j])
                try {
                    editTextUsername.setText(listaUsuarios[j][1]);
                    editTextUsername.setSelection(editTextUsername.getText().length());
                    checkFavorito.setClickable(Integer.valueOf(listaUsuarios[j][2]) == 0);
                    checkFavorito.setChecked(!(Integer.valueOf(listaUsuarios[j][2]) == 0));
                } catch (Exception e) {
                }
        if (listaUsuarios.length <= 0) {
            checkFavorito.setClickable(false);
            checkFavorito.setChecked(true);
        }
        dialogo = builder.create();
        return dialogo;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}

