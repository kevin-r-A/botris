package com.csl.cs108ademoapp.adapters;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;
import com.csl.cs108library4a.ReaderDevice;

import java.util.ArrayList;

public class AsignacionBarrasListAdapter extends ArrayAdapter<MovimientoProductoDto> {
    private final Context context;
    private final int resourceId;
    private final ArrayList<MovimientoProductoDto> readersList;
    private final boolean select4detail;
    public MutableLiveData<MovimientoProductoDto> eliminacionEvent= new MutableLiveData<>();

    public AsignacionBarrasListAdapter(Context context, int resourceId, ArrayList<MovimientoProductoDto> readersList, boolean select4detail) {
        super(context, resourceId, readersList);
        this.context = context;
        this.resourceId = resourceId;
        this.readersList = readersList;
        this.select4detail = select4detail;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (false) MainActivity.mCs108Library4a.appendToLog("position = " + position);
        final MovimientoProductoDto reader = readersList.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(resourceId, null);
        }
        TextView txtCodigoBarras = convertView.findViewById(R.id.txtCodigoBarras);
        TextView txtProducto = convertView.findViewById(R.id.txtProducto);
        TextView txtCodigoRfid = convertView.findViewById(R.id.txtCodigoRfid);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);
        txtCodigoBarras.setText(reader.CodBarra);
        txtProducto.setText(reader.ProductoNombre);
        txtCodigoRfid.setText(reader.CodRfid);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reader.Eliminado = true;
                notifyDataSetChanged();
                remove(reader);
                eliminacionEvent.setValue(reader);
                /*if (!reader.CodRfid.equals("")){

                }*/
            }
        });

        if (reader.Eliminado) {
            convertView.setVisibility(View.GONE);
        } else {
            convertView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
