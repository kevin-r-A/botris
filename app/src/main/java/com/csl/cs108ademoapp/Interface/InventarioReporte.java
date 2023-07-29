package com.csl.cs108ademoapp.Interface;

import com.csl.cs108ademoapp.Models.ListaInv;
import com.csl.cs108ademoapp.Models.Sobrantes;

import java.util.ArrayList;

/**
 * Created by Mauricio on 17/09/2018.
 */

public interface InventarioReporte {

    void Inventario();
    void ListaConciliado(ArrayList<ListaInv> ListaConcliado, Integer contador);
    void ListaFaltante(ArrayList<ListaInv> ListaFaltante, Integer contador);
    void ListaSobrante(ArrayList<Sobrantes> ListaSobrante, Integer contador);
}
