package com.salud.admin.administradorcubrebocas;

import android.content.Context;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ListViewAdapterUsos extends BaseAdapter {

    private Context context;
    private String[][] usos;
    private LayoutInflater inflater;
    private TextView txtFecha;
    private TextView txtHora;
    private TextView txtLavado;
    private CheckBox usosSeleccionado;
    private RelativeLayout fondo;
    private int seleccionado;
    private boolean[] itemsSeleccionados;
    private MenuItem itemDelete;
    private MenuItem itemEdit;

    public ListViewAdapterUsos(Context context, String[][] usos, int seleccionado, boolean[] itemsSeleccionados, MenuItem itemDelete, MenuItem itemEdit) {
        this.context = context;
        this.usos = usos;
        this.seleccionado = seleccionado;
        this.itemsSeleccionados = itemsSeleccionados;
        this.itemEdit = itemEdit;
        this.itemDelete = itemDelete;
    }

    public int getSeleccionado() {
        return this.seleccionado;
    }

    @Override
    public int getCount() {
        return usos.length;
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
        View itemView = inflater.inflate(R.layout.list_row_uso, parent, false);
        fondo = (RelativeLayout) itemView.findViewById(R.id.itemDeLista);
        txtFecha = (TextView) itemView.findViewById(R.id.uso_fecha);
        txtHora = (TextView) itemView.findViewById(R.id.uso_hora);
        txtLavado = (TextView) itemView.findViewById(R.id.uso_fue_lavado);
        usosSeleccionado = (CheckBox) itemView.findViewById(R.id.cubrebocas_seleccionado);
        if (this.seleccionado >= 0) {
            usosSeleccionado.setVisibility(View.VISIBLE);
            if (this.seleccionado == position)
                usosSeleccionado.setChecked(true);
        }
        itemsSeleccionados[position] = usosSeleccionado.isChecked();
        try {
            txtFecha.setText(new SimpleDateFormat("dd-MMM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(usos[position][2])));
            txtHora.setText(new SimpleDateFormat("HH:mm").format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(usos[position][2])));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //txtHora.setText(usos[position][2]);
        txtLavado.setText(Integer.valueOf(usos[position][3]) == 0 ? context.getText(R.string.text_no) : context.getText(R.string.text_si));
        usosSeleccionado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
