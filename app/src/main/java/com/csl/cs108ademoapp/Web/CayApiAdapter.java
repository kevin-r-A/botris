package com.csl.cs108ademoapp.Web;

import com.csl.cs108ademoapp.Convertidores.InventarioTypeDeserializer;
import com.csl.cs108ademoapp.Entities.Enums.InventarioType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CayApiAdapter {
    private static CayApiService API_SERVICE;

    public static <T> T getApiService(Class<T> serviceClass, boolean token) {
        T API_SERVICE;
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return;
            }
        }};

        // Creamos un interceptor y le indicamos el log level a usar
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        // Asociamos el interceptor a las peticiones
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(90, TimeUnit.SECONDS)
                .connectTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .sslSocketFactory(sc.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .cache(null);
        httpClient.addInterceptor(logging);
        String url1 = "https://192.168.0.6";
        //String puerto = ":35801/";
        String puerto = ":44392/";
        //url1 = "https://webcayman.com";
        //url1 = "http://192.168.5.122";
        //url1 = "http://192.168.100.14";
        //url1 = "http://192.168.100.7";
        //url1 = "http://192.168.1.17";
        //url1 = "http://172.16.0.195";
        String baseUrl = url1 + puerto;

        if (!token) {
            //baseUrl += "api/";
        } else {
            baseUrl = url1 + puerto;
        }
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .setLenient()
                .registerTypeAdapter(InventarioType.class, new InventarioTypeDeserializer())
                .create();
        //if (API_SERVICE == null) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build()) // <-- usamos el log level
                .build();
        //API_SERVICE = retrofit.create(serviceClass);
        //}

        return retrofit.create(serviceClass);
    }
}
