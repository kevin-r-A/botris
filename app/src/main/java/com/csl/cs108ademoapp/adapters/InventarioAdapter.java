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
import com.csl.cs108ademoapp.Web.Responses.InventarioDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;

import java.util.ArrayList;
import java.util.List;

public class InventarioAdapter extends ArrayAdapter<InventarioDto> {
    private final Context context;
    private final int resourceId;
    private final List<InventarioDto> objects;
    private boolean reporte = false;

    public void setReporte(boolean reporte) {
        this.reporte = reporte;
    }

    public InventarioAdapter(Context context, int resource, List<InventarioDto> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resourceId = resource;
        this.objects = objects;
    }

    public List<InventarioDto> getObjects() {
        return objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final InventarioDto reader = this.getItem(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(this.resourceId, null);
        }
        TextView txtCodigoBarras = convertView.findViewById(R.id.txtCodigoBarras);
        TextView txtProducto = convertView.findViewById(R.id.txtProducto);
        TextView txtCantidadDespachar = convertView.findViewById(R.id.txtCantidadDespachar);
        TextView lblcantidad = convertView.findViewById(R.id.lblcantidad);
        TextView txtCantidadLeida = convertView.findViewById(R.id.txtCantidadLeida);
        ImageButton imgView = convertView.findViewById(R.id.imgView);
        if (!reporte) {
            reader.CantidadFisica = reader.InventarioDetalleRfids != null ? reader.InventarioDetalleRfids.size() : 0;
            txtCantidadDespachar.setVisibility(View.GONE);
            lblcantidad.setVisibility(View.GONE);
            txtCantidadDespachar.setText("");
            lblcantidad.setText("");
            imgView.setVisibility(View.VISIBLE);
        } else {
            txtCantidadDespachar.setVisibility(View.VISIBLE);
            lblcantidad.setVisibility(View.VISIBLE);
            txtCantidadDespachar.setText(reader.CantidadSistema + "");
            lblcantidad.setText("Cantidad Sistema: ");
            imgView.setVisibility(View.GONE);
        }
        txtCodigoBarras.setText(reader.Producto.Codigo + " - " + reader.CodBarra);
        txtCantidadLeida.setText(reader.CantidadFisica + "");
        txtProducto.setText(reader.Producto != null ? reader.Producto.Nombre : "");


        imgView.setOnClickListener(v -> {
            final Dialogo dialog = new Dialogo(context);
            dialog.setContentView(R.layout.despacho_recepcion_rfid_dialog);
            Button btnAsignar = dialog.findViewById(R.id.btnAsignar);
            ListView lviewRfid = dialog.findViewById(R.id.lviewRfid);
            List<OrdenDespachoRecepcionRfidDto> rfidDtos = new ArrayList<>();
            reader.InventarioDetalleRfids.forEach(x -> {
                OrdenDespachoRecepcionRfidDto y = new OrdenDespachoRecepcionRfidDto();
                y.CodRfid = x.CodRfid;
                rfidDtos.add(y);
            });
            RfidItemAdapter rfidItemAdapter = new RfidItemAdapter(context, R.layout.rfid_item_list, rfidDtos);
            lviewRfid.setAdapter(rfidItemAdapter);

            btnAsignar.setOnClickListener(v1 -> {
                dialog.dismiss();
            });

            dialog.show();
        });
        return convertView;
    }
}
