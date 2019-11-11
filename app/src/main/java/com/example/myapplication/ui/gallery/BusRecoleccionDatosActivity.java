package com.example.myapplication.ui.gallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;



import com.example.myapplication.R;


public class BusRecoleccionDatosActivity extends Activity {



    private OnClickListener InicioServiceListener = new OnClickListener() {
        public void onClick(View v) {
            final BusRecoleccionDatosActivity context = BusRecoleccionDatosActivity.this;
            Toast.makeText(context, "Empezando recoleccion datos ", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(context, GestorAlarma.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setInexactRepeating(AlarmManager.RTC, 0, AlarmManager.INTERVAL_FIFTEEN_MINUTES, sender);
        }
    };

    private OnClickListener FinServiceListener = new OnClickListener() {
        public void onClick(View v) {
            final BusRecoleccionDatosActivity context = BusRecoleccionDatosActivity.this;
            Toast.makeText(context, "Finalizando recoleccion datos", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(context, GestorAlarma.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.cancel(sender);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gallery);

        final Button buttonInicio = (Button) findViewById(R.id.inicio);
        buttonInicio.setOnClickListener(this.InicioServiceListener);

        final Button buttonFin = (Button) findViewById(R.id.fin);
        buttonFin.setOnClickListener(this.FinServiceListener);


    }

}