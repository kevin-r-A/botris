package com.csl.cs108ademoapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.csl.cs108ademoapp.Entities.POJO.OrdenDespachoRecepcionDetalleWithNavigation;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Web.Responses.MercaderiaDto;

import java.util.List;

public class InventarioInicialAdapter extends ArrayAdapter<MercaderiaDto> {

    public InventarioInicialAdapter(@NonNull Context context, int resource, @NonNull List<MercaderiaDto> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final MercaderiaDto mercaderiaDto = this.getItem(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.card_inventario, null);
        }

        ((TextView) convertView.findViewById(R.id.txt_descripcion)).setText(mercaderiaDto.ART_NOMBRE);
        ((TextView) convertView.findViewById(R.id.txt_codigo)).setText(mercaderiaDto.ART_CODIGOBARRA);
        ((TextView) convertView.findViewById(R.id.txt_ubicacion)).setText(mercaderiaDto.ART_CODIGO);
        ((TextView) convertView.findViewById(R.id.txt_bg)).setText(mercaderiaDto.CANTIDAD + "");

        return convertView;
    }
}
