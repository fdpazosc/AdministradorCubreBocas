package com.salud.admin.administradorcubrebocas;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListViewAdapterCubrebocas extends BaseAdapter {

    private Context context;
    private String[][] cubrebocas;
    private LayoutInflater inflater;
    private TextView txtTitle;
    private TextView txtTipo;
    private TextView cont_usos_max_usos;
    private CheckBox cubrebocasSeleccionado;
    private RelativeLayout fondo;
    private int seleccionado;
    private boolean[] itemsSeleccionados;
    private MenuItem itemDelete;
    private MenuItem itemEdit;
    private DataBaseManager manager;

    public ListViewAdapterCubrebocas(Context context, String[][] cubrebocas, int seleccionado, boolean[] itemsSeleccionados, MenuItem itemDelete, MenuItem itemEdit) {
        this.context = context;
        this.cubrebocas = cubrebocas;
        this.seleccionado = seleccionado;
        this.itemsSeleccionados = itemsSeleccionados;
        this.itemEdit = itemEdit;
        this.itemDelete = itemDelete;
        manager = new DataBaseManager(context);
    }

    public int getSeleccionado() {
        return this.seleccionado;
    }

    @Override
    public int getCount() {
        return cubrebocas.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.list_row_cubrebocas, parent, false);
        fondo = (RelativeLayout) itemView.findViewById(R.id.itemDeLista);
        txtTitle = (TextView) itemView.findViewById(R.id.list_row_title);
        txtTipo = (TextView) itemView.findViewById(R.id.list_tipo_cubrebocas);
        cont_usos_max_usos = (TextView) itemView.findViewById(R.id.cont_usos_max_usos);
        cubrebocasSeleccionado = (CheckBox) itemView.findViewById(R.id.cubrebocas_seleccionado);
        if (this.seleccionado >= 0) {
            cubrebocasSeleccionado.setVisibility(View.VISIBLE);
            if (this.seleccionado == position)
                cubrebocasSeleccionado.setChecked(true);
        }
        itemsSeleccionados[position] = cubrebocasSeleccionado.isChecked();
        txtTitle.setText(cubrebocas[position][2]);
        txtTipo.setText(cubrebocas[position][4]);
        Cursor cursor = manager.cargarCursorUsoPorCubrebocas(Integer.valueOf(cubrebocas[position][0]));
        cont_usos_max_usos.setText(cursor.getCount() + " " + context.getText(R.string.text_de) + " " + cubrebocas[position][5]);
        cubrebocasSeleccionado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                itemsSeleccionados[position] = isChecked;
                comprobarSeleccionados();
                Log.d("clicked", "cambio a: " + isChecked);
            }
        });
        return itemView;
    }

    private void comprobarSeleccionados() {
        int contadorItemsSeleccionados = 0;
        for (int i = 0; i < itemsSeleccionados.length; i++)
            if (itemsSeleccionados[i])
                contadorItemsSeleccionados++;
        switch (contadorItemsSeleccionados) {
            case 0:
                itemEdit.setVisible(false);
                itemDelete.setVisible(false);
                break;
            case 1:
                itemEdit.setVisible(true);
                itemDelete.setVisible(true);
                break;
            default:
                itemEdit.setVisible(false);
                itemDelete.setVisible(true);
                break;
        }
    }
}
