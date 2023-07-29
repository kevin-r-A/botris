package com.csl.cs108ademoapp.fragments;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.csl.cs108ademoapp.*;

public class HomeFragment extends CommonFragment {
    final boolean DEBUG = true;
    private ImageView imagen;
    @Override
    public void onAttach(Context context) {
        Log.i("Hello", "HomeFragment.onAttach");
        super.onAttach(context);
    }
    @Override
    public void onStart() {
        Log.i("Hello", "HomeFragment.onStart");
        super.onStart();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState, true);
        return inflater.inflate(R.layout.home_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            //actionBar.setIcon(android.R.drawable.ic_menu_save);
            actionBar.setTitle(R.string.title_activity_home);
        }

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

        mHandler.post(runnableConfiguring);
    }

    @Override
    public void onStop() {
        mHandler.removeCallbacks(runnableConfiguring);
        super.onStop();
    }

    public HomeFragment() {
        super("HomeFragment");
    }

    CustomProgressDialog progressDialog;
    void stopProgressDialog() {
        if (progressDialog != null) { if (progressDialog.isShowing()) progressDialog.dismiss(); }
    }
    Runnable runnableConfiguring = new Runnable() {
        @Override
        public void run() {
            if (false) MainActivity.mCs108Library4a.appendToLog("AAA: runnableConfiguring(): mrfidToWriteSize = " + MainActivity.mCs108Library4a.mrfidToWriteSize());
            boolean progressShown = false;
            if (progressDialog != null) if (progressDialog.isShowing()) progressShown = true;
            if (MainActivity.mCs108Library4a.isBleConnected() == false) {
                if (progressShown) {
                    stopProgressDialog();
                    String stringPopup = "Conexión Fallida, Por favor conecte lector.";
                    CustomPopupWindow customPopupWindow = new CustomPopupWindow((Context) getActivity());
                    customPopupWindow.popupStart(stringPopup, false);
                }
            } else if (MainActivity.mCs108Library4a.mrfidToWriteSize() != 0 && MainActivity.mCs108Library4a.isRfidFailure() == false) {
                mHandler.postDelayed(runnableConfiguring, 250);
                if (progressShown == false) {
                    progressDialog = new CustomProgressDialog(getActivity(), "Inicializando lector. Espere por favor.");
                    progressDialog.show();
                }
            } else {
                stopProgressDialog();
                if (MainActivity.sharedObjects.versioinWarningShown == false) {
                    String macVersion = MainActivity.mCs108Library4a.getMacVer();
                    String hostVersion = MainActivity.mCs108Library4a.hostProcessorICGetFirmwareVersion();
                    String bluetoothVersion = MainActivity.mCs108Library4a.getBluetoothICFirmwareVersion();
                    String strVersionRFID = "2.6.20"; String[] strRFIDVersions = strVersionRFID.split("\\.");
                    String strVersionBT = "1.0.14"; String[] strBTVersions = strVersionBT.split("\\.");
                    String strVersionHost = "1.0.9"; String[] strHostVersions = strVersionHost.split("\\.");
                    String stringPopup = "";
                    if (MainActivity.mCs108Library4a.isRfidFailure() == false && MainActivity.mCs108Library4a.checkHostProcessorVersion(macVersion, Integer.parseInt(strRFIDVersions[0].trim()), Integer.parseInt(strRFIDVersions[1].trim()), Integer.parseInt(strRFIDVersions[2].trim())) == false)
                        stringPopup += "\nRFID processor firmware: V" + strVersionRFID;
                    if (MainActivity.mCs108Library4a.checkHostProcessorVersion(hostVersion,  Integer.parseInt(strHostVersions[0].trim()), Integer.parseInt(strHostVersions[1].trim()), Integer.parseInt(strHostVersions[2].trim())) == false)
                        stringPopup += "\nSiliconLab firmware: V" + strVersionHost;
                    if (MainActivity.mCs108Library4a.checkHostProcessorVersion(bluetoothVersion, Integer.parseInt(strBTVersions[0].trim()), Integer.parseInt(strBTVersions[1].trim()), Integer.parseInt(strBTVersions[2].trim())) == false)
                        stringPopup += "\nBluetooth firmware: V" + strVersionBT;
                    if (stringPopup.length() != 0) {
                        stringPopup = "Firmware too old\nPlease upgrade frimware to at least:" + stringPopup;
                        CustomPopupWindow customPopupWindow = new CustomPopupWindow((Context)getActivity());
                        customPopupWindow.popupStart(stringPopup, false);
                    }
                    MainActivity.sharedObjects.versioinWarningShown = true;
                }
            }
        }
    };
}
