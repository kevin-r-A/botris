package com.csl.cs108ademoapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.csl.cs108ademoapp.Dialogo;
import com.csl.cs108ademoapp.Entities.POJO.OrdenDespachoRecepcionDetalleWithNavigation;
import com.csl.cs108ademoapp.R;

import java.util.List;
import java.util.stream.Collectors;

public class RecepcionAdapter extends ArrayAdapter<OrdenDespachoRecepcionDetalleWithNavigation> {

    private final Context context;
    private final int resourceId;
    private final List<OrdenDespachoRecepcionDetalleWithNavigation> objects;

    public RecepcionAdapter(Context context, int resource, List<OrdenDespachoRecepcionDetalleWithNavigation> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resourceId = resource;
        this.objects = objects;
    }

    public List<OrdenDespachoRecepcionDetalleWithNavigation> getObjects() {
        return objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final OrdenDespachoRecepcionDetalleWithNavigation reader = this.getItem(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(this.resourceId, null);
        }
        TextView txtCodigoBarras = convertView.findViewById(R.id.txtCodigoBarras);
        TextView txtProducto = convertView.findViewById(R.id.txtProducto);
        TextView txtCantidadDespachar = convertView.findViewById(R.id.txtCantidadDespachar);
        TextView txtCantidadLeida = convertView.findViewById(R.id.txtCantidadLeida);
        ImageButton imgView = convertView.findViewById(R.id.imgView);
        txtCodigoBarras.setText(reader.ordenDespachoRecepcionDetalleDto.CodBarras);
        txtCantidadLeida.setText(reader.ordenDespachoRecepcionRfidDtos != null ? (int) reader.ordenDespachoRecepcionRfidDtos.stream().filter(x -> x.Recepcion).count() + "" : "0");
        txtProducto.setText(reader.producto);
        txtCantidadDespachar.setText(reader.ordenDespachoRecepcionDetalleDto.CantidadDespachada + "");
        imgView.setOnClickListener(v -> {
            final Dialogo dialog = new Dialogo(context);
            dialog.setContentView(R.layout.despacho_recepcion_rfid_dialog);
            Button btnAsignar = dialog.findViewById(R.id.btnAsignar);
            ListView lviewRfid = dialog.findViewById(R.id.lviewRfid);
            RfidItemAdapter rfidItemAdapter = new RfidItemAdapter(context, R.layout.rfid_item_list, reader.ordenDespachoRecepcionRfidDtos);
            lviewRfid.setAdapter(rfidItemAdapter);
            rfidItemAdapter.getDeleteItem().observeForever(ordenDespachoRecepcionRfidDto -> {
                reader.ordenDespachoRecepcionRfidDtos.remove(ordenDespachoRecepcionRfidDto);
                notifyDataSetChanged();
            });
            btnAsignar.setOnClickListener(v1 -> {
                dialog.dismiss();
            });

            dialog.show();
        });
        return convertView;
    }
}
