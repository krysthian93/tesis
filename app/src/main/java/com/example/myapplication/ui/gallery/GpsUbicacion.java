package com.example.myapplication.ui.gallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;


public class GpsUbicacion extends SQLiteOpenHelper {
    /*SharedPreferences preferencia = getSharedPreferences("MiPreferencia",Context.MODE_PRIVATE);*/
    private static final String BDD = "tesis.db";
    private static final int DATABASE_VERSION = 1;

    private static final String GpsUbicacion_Nombre_Tabla = "Ubicacion";
    private static final String GpsUbicacion_Tiempo_Exacto = "Marca_Tiempo";
    private static final String GpsUbicacion_Proveedor = "Identificador";
    private static final String GpsUbicacion_Latitud = "latitude";
    private static final String GpsUbicacion_Longitud = "longitude";
    // @formatter:off


    private static final String LOCATION_TABLE_CREATE =
            "CREATE TABLE " + GpsUbicacion_Nombre_Tabla + " (" +
                    GpsUbicacion_Tiempo_Exacto + " INTEGER, " +
                    GpsUbicacion_Proveedor + " TEXT, " +
                    GpsUbicacion_Latitud + " TEXT, " +
                    GpsUbicacion_Longitud + " TEXT);";
    // @formatter: on

    public GpsUbicacion(Context context) {
        super(context, BDD, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LOCATION_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    public void insert(Location location) {
        ContentValues values = new ContentValues();
        values.put(GpsUbicacion_Proveedor, location.getProvider());
        values.put(GpsUbicacion_Tiempo_Exacto, location.getTime());
        values.put(GpsUbicacion_Latitud, String.valueOf(location.getLatitude()));
        values.put(GpsUbicacion_Longitud, String.valueOf(location.getLongitude()));

        SQLiteDatabase db = getWritableDatabase();
        try {
            if (db.insert(GpsUbicacion_Nombre_Tabla, null, values) == -1) {
                throw new SQLException("No se puede grabar [" + location + "]");
            }
        } finally {
            db.close();
        }
    }

/*
    public void respaldardatosInt()
	{
		try
		{
			OutputStreamWriter outputStreamWriter;
			outputStreamWriter = new OutputStreamWriter(
					openFileOutput("respaldobdd.txt", Context.MODE_PRIVATE));

	outputStreamWriter.write("Hola Mundo");
			outputStreamWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void respadarbddExt()
	{
		try
		{
			File ruta = Environment.getExternalStorageDirectory();

			File file = new File(ruta.getAbsolutePath(), "respaldosbdd.txt");

			OutputStreamWriter outputStreamWriter =
					new OutputStreamWriter(
							new FileOutputStream(file));

			outputStreamWriter.write("Hola Mundo.");
			outputStreamWriter.close();
		}
		catch (Exception e)

			e.printStackTrace();
		}

	}*/
}
