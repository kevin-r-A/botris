package com.csl.cs108ademoapp.Web;

import android.content.Context;
import android.os.Looper;
import android.widget.EditText;
import android.widget.Toast;

import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class JsonWebSt<T> extends Thread {
    Context context;
    EditText recvdref;
    JsonWebI onEvent;
    TypeToken typeToken;
    String metodo;
    JSONObject jsonObject = null;
    Boolean primitivo, cast;
    ByteArrayOutputStream byteArrayOutputStream;
    String URl;
    String REQUEST = "POST";
    String getParametros = "";
    boolean status = false;
    URL url;
    HttpURLConnection conn;

    public void setByteArrayOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    public void setREQUEST(String REQUEST) {
        this.REQUEST = REQUEST;
    }

    public void setGetParametros(String getParametros) {
        this.getParametros = getParametros;
    }

    public void setURl(String URl) {
        this.URl = URl;
    }

    public Boolean getStatus() {
        return status;
    }

    public JsonWebSt(Context context, TypeToken typeToken, String metodo, Boolean primitivo, boolean cast) {
        this.context = context;
        this.typeToken = typeToken;
        this.metodo = metodo;
        this.primitivo = primitivo;
        this.cast = cast;
        this.URl= MetodosGenerales.txtURL;
        //this.start();
    }

    public void setOnComplete(JsonWebI onEvent) {
        this.onEvent = onEvent;
    }

    public void run() {

        Looper.prepare();
        try {
            //URL url = new URL("http://192.168.1.25:1853/Site/Services/API.asmx/" + metodo);
            url = new URL(URl + "/API/" + metodo + getParametros);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(this.REQUEST);
            conn.setRequestProperty("Content-Type", "application/json");
            if (this.REQUEST.equals("POST"))
                PostMethod();
            else
                GetMethod();
            int codeResult = conn.getResponseCode();
            if (codeResult == 200) {
                InputStream in = conn.getInputStream();
                String resultstring = convertStreamToString(in);
                in.close();
                try {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                    if (primitivo) {
                        if (cast) {
                            T result = gson.fromJson(resultstring, typeToken.getType());
                            onEvent.onComplete(result);
                        } else {
                            JSONObject jsonObject2 = new JSONObject(resultstring);
                            resultstring = null;
                            onEvent.onComplete(jsonObject2);
                        }
                    } else {
                        if (cast) {
                            T result = gson.fromJson(resultstring, typeToken.getType());
                            onEvent.onComplete(result);
                        } else {
                            JSONArray json1 = new JSONArray(resultstring);
                            resultstring = null;
                            onEvent.onComplete(json1);
                        }

                    }
                    status = true;
                } catch (Exception ex) {
                    onEvent.onError(ex.getMessage());
                }
            } else if (codeResult == 202) {
                InputStream in = conn.getInputStream();
                String resultstring = convertStreamToString(in);
                in.close();
                onEvent.onAcepted(resultstring);
            } else {
                InputStream in = conn.getErrorStream();
                String resultstring = convertStreamToString(in);
                onEvent.onError("Error General: " + resultstring);
            }


            //Gson gson   = new Gson();

            //List<T> iList = gson.fromJson(resultstring, new TypeToken<List<T>>(){}.getType());


        } catch (Exception e) {
            e.printStackTrace();
            if (onEvent != null)
                onEvent.onError(e.getMessage());
            //createDialog("Error", "Cannot Estabilish Connection");
        }


        //Looper.loop();
    }

    private void GetMethod() {

    }

    private void PostMethod() throws IOException {
        this.conn.setDoOutput(true);
        OutputStream os = null;
        os = this.conn.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
        os.write(byteArrayOutputStream.toByteArray());
        os.flush();
    }

    public List<T> Convert(String result, TypeToken typeToken) {
        GsonBuilder gsonb = new GsonBuilder();
        //gsonb.registerTypeAdapter(CargarBDD.Requerimientos.class, new NetDateTimeAdapter());
        Gson gson = gsonb.create();

        return gson.fromJson(result, typeToken.getType());
    }

    private String convertStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Stream Exception", Toast.LENGTH_SHORT).show();
        }
        return total.toString();
    }

    private String convertStreamToString1(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Stream Exception", Toast.LENGTH_SHORT).show();
        }
        return total.toString();
    }
}
