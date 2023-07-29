package com.csl.cs108ademoapp.Generales;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import com.csl.cs108ademoapp.Dialogo;
import com.csl.cs108ademoapp.Efectos.Effectstype;
import com.csl.cs108ademoapp.Entities.DATABASE;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Web.Responses.ResponseToken;

import java.io.File;

public final class MetodosGenerales {
    public static DATABASE database;
    public static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DOB_FORMAT = "yyyy-MM-dd";
    public static String txtURL = "";
    public static String txtPPC_ID = "";
    public static int TIPO_OPERACION = 0;
    public static ResponseToken responseToken;
    public static String tenant;
    public static Effectstype effect;
    public static File DATABASE_PATH;
    public static File IMAGES_TEMP_PATH;
    public static File IMAGES_SAVED_PATH;
    public static File SIGNATURES_SAVED_PATH;

    public static DATABASE conectarBDD(File ExternalFileDir, Context context) {
        File bdd = new File("/storage/extSdCard/Android");
        File path = ExternalFileDir;
        if (!bdd.exists()) {
            database = Room.databaseBuilder(context, DATABASE.class, ExternalFileDir + "/CAYMANINV").build();
        } else {
            database = Room.databaseBuilder(context, DATABASE.class, "/storage/extSdCard/Android/data/com.inventariosbodegas.rommeltorres.inventariobodegas/files/database" + "/AVALBDDBODEGA").build();
        }
        return database;
    }

    public static void InitializeStorage(Context context) {
        if (context.getExternalFilesDirs("image").length == 1) {
            DATABASE_PATH = context.getExternalFilesDir("database");
            IMAGES_TEMP_PATH = context.getExternalFilesDir("image");
            SIGNATURES_SAVED_PATH = context.getExternalFilesDir("signatures");
        } else {
            if (context.getExternalFilesDirs("image")[1] != null) {
                DATABASE_PATH = context.getExternalFilesDirs("database")[0];
                SIGNATURES_SAVED_PATH = context.getExternalFilesDirs("signatures")[0];
                IMAGES_TEMP_PATH = context.getExternalFilesDirs("image")[0];
            } else {
                DATABASE_PATH = context.getExternalFilesDir("database");
                IMAGES_TEMP_PATH = context.getExternalFilesDir("image");
                SIGNATURES_SAVED_PATH = context.getExternalFilesDir("signatures");
            }

        }
        File root = new File(IMAGES_TEMP_PATH + "/SAVED");
        if (!root.exists())
            root.mkdirs();
        IMAGES_SAVED_PATH = root;
    }

    public static void Mensaje(String mensaje, Context context, Resources resources) {
        final Dialogo dialogBuilder = Dialogo.getInstance(context);

        dialogBuilder
                .withTitle("Cayman Aviso")
                .withMessage(mensaje)
                .withIcon(resources.getDrawable(R.drawable.informacion))
                .isCancelableOnTouchOutside(false)
                .withDuration(700)
                .withEffect(effect)
                .withButton1Text("OK")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.cancel();
                    }
                })
                .show();
    }
}
