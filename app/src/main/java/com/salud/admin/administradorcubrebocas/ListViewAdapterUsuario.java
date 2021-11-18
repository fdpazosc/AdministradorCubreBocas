package com.salud.admin.administradorcubrebocas;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
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

import androidx.annotation.RequiresApi;

public class ListViewAdapterUsuario extends BaseAdapter {

    private Context context;
    private String[][] usuarios;
    private LayoutInflater inflater;
    private TextView txtTitle;
    private TextView iconoFavorito;
    private CheckBox usuario_seleccionado;
    private RelativeLayout fondo;
    private int seleccionado;
    private boolean[] itemsSeleccionados;
    private MenuItem itemDelete;
    private MenuItem itemEdit;

    public ListViewAdapterUsuario(Context context, String[][] usuarios, int seleccionado, boolean[] itemsSeleccionados, MenuItem itemDelete, MenuItem itemEdit) {
        this.context = context;
        this.usuarios = usuarios;
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
        return usuarios.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View getView(final int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.list_row_usuario, parent, false);
        fondo = (RelativeLayout) itemView.findViewById(R.id.itemDeLista);
        txtTitle = (TextView) itemView.findViewById(R.id.list_row_title);
        iconoFavorito = (TextView) itemView.findViewById(R.id.list_favorite_icon);
        usuario_seleccionado = (CheckBox) itemView.findViewById(R.id.usuario_seleccionado);
        int esFavorito = Integer.valueOf(usuarios[position][2]);
        if (this.seleccionado >= 0) {
            usuario_seleccionado.setVisibility(View.VISIBLE);
            //itemView.setEnabled(false);
            if (this.seleccionado == position) {
                usuario_seleccionado.setChecked(true);
            }
            /*if (esFavorito == 1) {
                //usuario_seleccionado.setEnabled(false);
                //usuario_seleccionado.setVisibility(View.INVISIBLE);
                //ColorDrawable viewColor = (ColorDrawable) itemView.getBackground();
                //int colorId = viewColor.getColor();
                //usuario_seleccionado.setButtonTintList(ColorStateList.valueOf(colorId));
                usuario_seleccionado.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFFFF")));
                usuario_seleccionado.setClickable(false);
                usuario_seleccionado.setChecked(false);
            }*/
        }
        itemsSeleccionados[position] = usuario_seleccionado.isChecked();
        txtTitle.setText(usuarios[position][1]);
        usuario_seleccionado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                itemsSeleccionados[position] = isChecked;
                comprobarSeleccionados();
                Log.d("clicked", "cambio a: " + isChecked);
            }
        });
        iconoFavorito.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME), Typeface.BOLD);
        iconoFavorito.setText(context.getString(R.string.fa_bell_o));
        if (esFavorito == 1) {
            iconoFavorito.setVisibility(View.VISIBLE);
        } else {
            iconoFavorito.setText(" ");
            iconoFavorito.setVisibility(View.INVISIBLE);
        }
        return itemView;
    }

    private void comprobarSeleccionados() {
        int contadorItemsSeleccionados = 0;
        int contadorItemsSeleccionadosFavoritos = 0;
        for (int i = 0; i < itemsSeleccionados.length; i++) {
            if (itemsSeleccionados[i]) {
                contadorItemsSeleccionados++;
                if(Integer.valueOf(usuarios[i][2]) == 1)
                    contadorItemsSeleccionadosFavoritos++;
            }
        }
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
        if (contadorItemsSeleccionadosFavoritos > 0)
            itemDelete.setVisible(false);
    }
}
