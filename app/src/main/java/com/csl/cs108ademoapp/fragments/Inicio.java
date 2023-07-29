package com.csl.cs108ademoapp.fragments;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.R;

public class Inicio extends CommonFragment {

    private ImageView imagen;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState, true);
        return inflater.inflate(R.layout.activity_inicio, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Inicio");

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        imagen =(ImageView)getActivity().findViewById(R.id.imgv_logo);

        final AnimationDrawable animacion =(AnimationDrawable)imagen.getDrawable();
        imagen.post(new Runnable() {
            @Override
            public void run() {
                animacion.start();
            }
        });

        MainActivity.mSensorConnector.mLocationDevice.turnOn(true);
        MainActivity.mSensorConnector.mSensorDevice.turnOn(true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public Inicio() {

        super("Inicio");
    }

}