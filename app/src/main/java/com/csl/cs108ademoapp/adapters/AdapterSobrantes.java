package com.csl.cs108ademoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csl.cs108ademoapp.Models.Sobrantes;
import com.csl.cs108ademoapp.R;

import java.util.List;

/**
 * Created by Mauricio on 21/09/2018.
 */

public class AdapterSobrantes extends RecyclerView.Adapter<AdapterSobrantes.SobrantesViewHolder> {
    static List<Sobrantes> items;


    static AdapterSobrantes.OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, List<Sobrantes> items);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(AdapterSobrantes.OnItemClickListener listener) {
        this.listener = listener;
    }


    public static class SobrantesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Campos respectivos de un item

        public TextView codigo;


        public SobrantesViewHolder(View v) {
            super(v);

            codigo = (TextView)v.findViewById(R.id.txt_codigo);

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

    public AdapterSobrantes(List<Sobrantes> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public AdapterSobrantes.SobrantesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_sobrantes, viewGroup, false);
        return new AdapterSobrantes.SobrantesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterSobrantes.SobrantesViewHolder viewHolder, int i) {
        viewHolder.codigo.setText(items.get(i).getCodigo());
    }
}
