package com.example.myapplication.ui.gallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GestorAlarma extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BusRecoleccionDatosService.acquireStaticLock(context);
        context.startService(new Intent(context, BusRecoleccionDatosService.class));
    }}
