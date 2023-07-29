package com.csl.cs108ademoapp;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.csl.cs108ademoapp.Efectos.Effectstype;
import com.csl.cs108ademoapp.Entities.DATABASE;
import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.Sqlite.DBHelper;
import com.csl.cs108ademoapp.Web.Responses.ResponseToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit2.Call;

public class Login extends AppCompatActivity {

    Effectstype effect;
    TextView txt_usuario, txt_contrasenia;
    Button ingresar;
    //ArrayList<Usuario> usu= new ArrayList<>();
    public static String nombreusuario;
    static final int READ_BLOCK_SIZE = 100;
    String s = "";
    public static String url;
    static final String usuario = "cayman";
    static final String contrasenia = "1010";
    DBHelper db;
    public DATABASE database;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MetodosGenerales.InitializeStorage(getApplicationContext());

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.getSupportActionBar().hide();

        loginViewModel = new ViewModelProvider((ViewModelStoreOwner) this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(LoginViewModel.class)) {
                    return (T) LoginViewModel.getInstance(getApplication());
                } else {
                    throw new IllegalArgumentException("Unknown ViewModel class");
                }
            }
        }).get(LoginViewModel.class);

        //Verifica Base de datos SQLite
        DBHelper crear = new DBHelper(this, BASE().toString());

        try {
            crear.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        db = new DBHelper(this, "");
        txt_usuario = this.findViewById(R.id.txt_usuario);
        txt_contrasenia = this.findViewById(R.id.txt_contrasenia);
        ingresar = this.findViewById(R.id.btn_ingresar);

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(Login.this, R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Autenticando...");
                progressDialog.show();
                ResponseToken responseToken = loginViewModel.Login(txt_usuario.getText().toString().trim(), txt_contrasenia.getText().toString().trim(), null);
                if (responseToken != null) {
                    Intent menu = new Intent(Login.this, MainActivity.class);
                    menu.putExtra("token", responseToken);
                    startActivity(menu);
                    progressDialog.dismiss();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Mensaje("Ingrese Usuario y/o contraseña");
                }
            }
        });
        //database = MetodosGenerales.conectarBDD(getExternalFilesDir("database"), getApplicationContext());
    }


    public void Mensaje(String mensaje) {
        final Dialogo dialogBuilder = Dialogo.getInstance(this);

        dialogBuilder
                .withTitle("Cayman Aviso")
                .withMessage(mensaje)
                .withIcon(getResources().getDrawable(R.drawable.error))
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


    public String BASE() {
        try {
            FileInputStream fis = openFileInput("txt_base.txt");
            InputStreamReader isr = new InputStreamReader(fis);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];

            int charRead;
            while ((charRead = isr.read(inputBuffer)) > 0) {
                // Convertimos los char a String
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                s += readString;

                inputBuffer = new char[READ_BLOCK_SIZE];
            }
            isr.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return s;
    }

    public void VerificaBase() {
        File path = new File("/data/data/com.csl.caymanvozandes/files/txt_base.txt");
        String dato = "";

        if (path.exists()) {
            try {
                FileOutputStream file = new FileOutputStream(path);
                file.write(dato.getBytes());
                file.close();
            } catch (IOException e) {

            }

        }
    }
}
