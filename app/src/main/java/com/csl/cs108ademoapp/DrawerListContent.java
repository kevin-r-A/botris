package com.csl.cs108ademoapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold the data for Navigation Drawer Items
 */
public class DrawerListContent {
    //An array of sample (Settings) items.
    public static List<DrawerItem> ITEMS = new ArrayList<>();

    //A map of sample (Settings) items, by ID.
    public static Map<String, DrawerItem> ITEM_MAP = new HashMap<>();

    public enum DrawerPositions {
        MAIN, SPECIAL,
        ABOUT, CONNECT,
        INVENTORY, SEARCH, MULTIBANK,
        SETTING, FILTER, READWRITE, SECURITY, REGISTER,
        COLDCHAIN, MICROTEMPERATURE, UCODE,
        WEDGE, BLANK, INICIO, FILTRAR,REPORTE, CARGA, DESPERDICIO, ASIGNACION, CHANGEUBICACION,
        DESPACHO, RECEPCION, NEWDESPACHO, INITIALINVENTORY;

        public static DrawerPositions toDrawerPosition(int x) {
            switch(x) {
                case 0: return INICIO;
                case 1: return INVENTORY;
                case 2: return REPORTE;
                case 3: return CONNECT;
                case 4: return SETTING;
                case 5: return ASIGNACION;
                case 6: return CHANGEUBICACION;
                case 7: return DESPACHO;
                case 8: return RECEPCION;
                case 9: return INITIALINVENTORY;
                //case 5: return CONNECT;
                //case 6: return SETTING;
                /*case 6: return FILTER;
                case 7: return READWRITE;
                case 8: return SECURITY;
                case 9: return REGISTER;
                case 10: return COLDCHAIN;
                case 11: return MICROTEMPERATURE;
                case 12: return UCODE;
                case 13: return WEDGE;*/
            }
            return null;
        }
    }

    static {
        // Add items.
        addItem(new DrawerItem("0", "Inicio", R.drawable.dl_about));
        //addItem(new DrawerItem("1", "Configuración", R.drawable.dl_rdl));
        addItem(new DrawerItem("1", "Inventario", R.drawable.dl_inv));
        //addItem(new DrawerItem("3", "Reporte", R.drawable.dl_loc));
        addItem(new DrawerItem("2", "Reporte", R.drawable.dl_inv));
        //addItem(new DrawerItem("4", "Carga/Descarga", R.drawable.dl_inv));
        addItem(new DrawerItem("3", "Conectar", R.drawable.dl_sett));
        addItem(new DrawerItem("4", "Configuracion", R.drawable.dl_sett));
        addItem(new DrawerItem("5", "Asignación Tags", R.drawable.dl_sett));
        addItem(new DrawerItem("6", "Cambiar Ubicacion", R.drawable.dl_sett));
        addItem(new DrawerItem("7", "Despachos", R.drawable.dl_sett));
        addItem(new DrawerItem("8", "Recepciones", R.drawable.dl_sett));
        addItem(new DrawerItem("9", "Inventario Sistema", R.drawable.dl_sett));
        /*addItem(new DrawerItem("6", "Filters", R.drawable.dl_filters));
        addItem(new DrawerItem("7", "Read/Write", R.drawable.dl_access));
        addItem(new DrawerItem("8", "Security", R.drawable.dl_access));
        addItem(new DrawerItem("9", "Register Tag", R.drawable.dl_rr));
        addItem(new DrawerItem("10", "Cold Chain CS8300", R.drawable.dl_loc));
        addItem(new DrawerItem("11", "RFMicron", R.drawable.dl_loc));
        addItem(new DrawerItem("12", "UCODE DNA", R.drawable.dl_loc));
        addItem(new DrawerItem("13", "Wedge", R.drawable.dl_rr));*/
    }

    private static void addItem(DrawerItem item) {

        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static class DrawerItem {
        public String id;
        public String content;
        public int icon;

        public DrawerItem(String id, String content, int icon_id) {
            this.id = id;
            this.content = content;
            this.icon = icon_id;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
