package com.csl.cs108ademoapp.adapters;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;

import java.util.List;

public class RfidItemAdapter extends ArrayAdapter<OrdenDespachoRecepcionRfidDto> {
    private int resourceId;
    private final MutableLiveData<OrdenDespachoRecepcionRfidDto> deleteItem = new MutableLiveData<>();

    public MutableLiveData<OrdenDespachoRecepcionRfidDto> getDeleteItem() {
        return deleteItem;
    }

    public RfidItemAdapter(Context context, int resource, List<OrdenDespachoRecepcionRfidDto> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrdenDespachoRecepcionRfidDto item = getItem(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(resourceId, null);
        }
        TextView txtCodigoRfid = convertView.findViewById(R.id.txtCodigoRfid);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);
        if (!item.Enviar) {
            btnDelete.setVisibility(View.GONE);
        } else {
            btnDelete.setVisibility(View.VISIBLE);
        }
        txtCodigoRfid.setText(item.CodRfid);
        btnDelete.setOnClickListener(v -> {
            deleteItem.setValue(item);
        });
        return convertView;
    }
}
