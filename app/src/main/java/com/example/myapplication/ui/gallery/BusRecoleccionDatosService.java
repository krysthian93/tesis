package com.example.myapplication.ui.gallery;

import java.util.Iterator;
import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;

public class BusRecoleccionDatosService extends Service {

    private static PowerManager.WakeLock wakeLock = null;

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private GpsUbicacion gpsUbicacion;

    private final class ServiceHandler extends Handler {
        private LocationListener locationListener;

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                createLocationListener();
                grabarGpsUbicacionListener();
                sleep(30);
                LiberarGpsUbicacionListener();
                ActualizarUltimaGpsUbicacion();
            } finally {
                getLock(getApplicationContext()).release();
            }

            stopSelf(msg.arg1);
        }

        /**

         * Se crea el location listener, para recibir la ubicacion se dbee llamar a este metodo.

         */
        private void createLocationListener() {


            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            this.locationListener = new LocationListener() {
                public void onStatusChanged(String provider, int status, Bundle extras) {
                /*locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);*/
                }

                public void onProviderEnabled(String provider) {/*
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this.locationListener);
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListener);
					locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this.locationListener);*/
                }

                public void onProviderDisabled(String provider) {
					/*locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);*/
                }

                public void onLocationChanged(Location location) {/*
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this.locationListener);
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListener);
					locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this.locationListener);*/
                }
            };
        }

        /**

         * Graba lo que esta en el listener GpsUbicacion en el el objeto del LocationManager, se grabara la ubicacion mientras se haga la llamada al metodo.

         */
        private void grabarGpsUbicacionListener() {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this.locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListener);
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this.locationListener);
        }


        private void LiberarGpsUbicacionListener() {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(this.locationListener);
        }

        private void sleep(long seconds) {
            long endTime = System.currentTimeMillis() + seconds * 1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }
        }

        @SuppressLint("MissingPermission")
        private void ActualizarUltimaGpsUbicacion() {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = locationManager.getAllProviders();
            for (Iterator<String> iterator = providers.iterator(); iterator.hasNext(); ) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                register(locationManager.getLastKnownLocation(iterator.next()));
            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        this.gpsUbicacion = new GpsUbicacion(getApplicationContext());

        HandlerThread thread = new HandlerThread("GpsUbicacionThread", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        this.serviceLooper = thread.getLooper();
        this.serviceHandler = new ServiceHandler(this.serviceLooper);

        Log.i("GbsUbicacion", "creado");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = this.serviceHandler.obtainMessage();
        msg.arg1 = startId;
        this.serviceHandler.sendMessage(msg);

        return START_STICKY;
    }

    /* (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    protected void register(Location location) {
        Log.i("GpsUbicacion", "grabando [" + location + "]");
        this.gpsUbicacion.insert(location);
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (wakeLock == null) {
            PowerManager ca = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

            wakeLock = ca.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GpsUbicacionWakeLock:");
            wakeLock.setReferenceCounted(false);
        }

        return wakeLock;
    }
}
