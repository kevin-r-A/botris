package com.csl.cs108ademoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.csl.cs108ademoapp.Models.ListaInv;
import com.csl.cs108ademoapp.R;

import java.util.List;

/**
 * Created by Mauricio on 21/09/2018.
 */

public class AdapterConciliados extends RecyclerView.Adapter<AdapterConciliados.ConciliadosViewHolder> {
    static List<ListaInv> items;

    static AdapterConciliados.OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, List<ListaInv> items);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(AdapterConciliados.OnItemClickListener listener) {
        this.listener = listener;
    }


    public static class ConciliadosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Campos respectivos de un item
        public ImageView imagen;
        public TextView descripcion,codigo,ubicacion,bg;


        public ConciliadosViewHolder(View v) {
            super(v);


            descripcion = (TextView)v.findViewById(R.id.txt_descripcion);
            codigo = (TextView)v.findViewById(R.id.txt_codigo);
            ubicacion = (TextView)v.findViewById(R.id.txt_ubicacion);
            bg = v.findViewById(R.id.txt_bg);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(itemView, position,items);
                }
            }
        }
    }

    public AdapterConciliados(List<ListaInv> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public AdapterConciliados.ConciliadosViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_inventario, viewGroup, false);
        return new AdapterConciliados.ConciliadosViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterConciliados.ConciliadosViewHolder viewHolder, int i) {
        viewHolder.descripcion.setText(items.get(i).getDescripcion());
        viewHolder.codigo.setText(items.get(i).getCodbarras());
        viewHolder.ubicacion.setText(items.get(i).getUbicacion());
        viewHolder.bg.setText(items.get(i).getBg());
    }
}
