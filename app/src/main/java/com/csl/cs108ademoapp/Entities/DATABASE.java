package com.csl.cs108ademoapp.Entities;

import com.csl.cs108ademoapp.Entities.Clases.ACTIVO;
import com.csl.cs108ademoapp.Entities.Clases.CUSTODIO;
import com.csl.cs108ademoapp.Entities.Clases.GRUPO;
import com.csl.cs108ademoapp.Entities.Clases.REPORTE;
import com.csl.cs108ademoapp.Entities.Clases.SERVER;
import com.csl.cs108ademoapp.Entities.Clases.STOCK;
import com.csl.cs108ademoapp.Entities.Clases.UORGANICA;
import com.csl.cs108ademoapp.Entities.DAO.*;
import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.Web.Responses.InventarioDetalleRfidDto;
import com.csl.cs108ademoapp.Web.Responses.InventarioDto;
import com.csl.cs108ademoapp.Web.Responses.MercaderiaDto;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDetalleDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;
import com.csl.cs108ademoapp.Web.Responses.ProductoDto;
import com.csl.cs108ademoapp.Web.Responses.UbicacionDto;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@android.arch.persistence.room.Database(entities = {ACTIVO.class, STOCK.class, REPORTE.class, UORGANICA.class,
        GRUPO.class, CUSTODIO.class, SERVER.class, ProductoDto.class, UbicacionDto.class, MovimientoProductoDto.class,
        OrdenDespachoRecepcionDto.class, OrdenDespachoRecepcionDetalleDto.class, OrdenDespachoRecepcionRfidDto.class,
        InventarioDetalleRfidDto.class, InventarioDto.class, MercaderiaDto.class}, version = 6, exportSchema = false)
public abstract class DATABASE extends RoomDatabase {

    public abstract Activo_DAO activo_dao();

    public abstract Reporte_DAO reporte_dao();

    public abstract Stock_Dao stock_dao();

    public abstract Grupo_DAO grupo_dao();

    public abstract Uorganica_DAO uorganica_dao();

    public abstract Custodio_DAO custodio_dao();

    public abstract Server_DAO server_dao();

    public abstract Producto_DAO producto_dao();

    public abstract Ubicacion_DAO ubicacion_dao();

    public abstract MovimientoProducto_DAO movimientoProducto_dao();

    public abstract OrdenDespachoRecepcion_DAO ordenDespachoRecepcion_dao();

    public abstract OrdenDespachoRecepcionDetalle_DAO ordenDespachoRecepcionDetalle_dao();

    public abstract OrdenDespachoRecepcionRfid_DAO ordenDespachoRecepcionRfid_dao();

    public abstract InventarioDetalleRfid_DAO inventarioDetalleRfid_dao();

    public abstract Inventario_DAO inventario_dao();

    public abstract Mercaderia_DAO mercaderia_dao();

    private static DATABASE INSTANCE;

    public static DATABASE getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DATABASE.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DATABASE.class, MetodosGenerales.DATABASE_PATH + "/cayBodegas")
                            .fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
