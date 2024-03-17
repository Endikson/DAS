package com.example.gastos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class DBHelper {

    public static final String NOMBRE_DB="InfoGastos";
    public static final int DATABASE_VERSION=1;
    public static final String NOMBRE_TABLA="Gastos";
    public static final String NOMBRE_GASTO="Nombre_gasto";
    public static final String ANO="Ano";
    public static final String DINERO="Dinero";
    public static final String FECHA="Fecha";

    public static final String NOMBRE_TABLA1="GastosTotales";
    public static final String GASTO_TOTAL_POR_ANO="GastoTotalPorAno";

    private static final String DB_CREATE="create table "+NOMBRE_TABLA+"("+NOMBRE_GASTO+" TEXT not null, "+
            ANO+" INTEGER not null, "+DINERO+" REAL not null check ("+DINERO+">0),"+FECHA+" TEXT not null);";

    private static final String DB_CREATE1="create table "+NOMBRE_TABLA1+"("+NOMBRE_GASTO+" TEXT not null, "+
            ANO+" INTEGER not null, "+GASTO_TOTAL_POR_ANO+" REAL not null check ("+GASTO_TOTAL_POR_ANO+">0));";

    private final Context context;
    private SQLiteDatabase sqLiteDatabase;
    private MyDBAdapter myDBAdapter;
    private DBHelper dbHelper;
    private Cursor c;
    private double total=0;
    public DBHelper open()
    {
        sqLiteDatabase=myDBAdapter.getWritableDatabase();
        return this;
    }
    public void close()
    {
        sqLiteDatabase.close();
    }

    public long insertdata(String gasto,int ano,double dinero,String fecha)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(NOMBRE_GASTO,gasto);contentValues.put(ANO,ano);contentValues.put(DINERO,dinero);contentValues.put(FECHA,fecha);
        ContentValues contentValues1=new ContentValues();
        contentValues1.put(NOMBRE_GASTO,gasto);contentValues1.put(ANO,ano);
        int k=0;
        try {
            c = sqLiteDatabase.query(NOMBRE_TABLA1,new String[]{GASTO_TOTAL_POR_ANO},NOMBRE_GASTO+"=? and "+ANO+"=?",
                    new String[]{gasto,String.valueOf(ano)},null,null,null);
            if (c.moveToFirst())
            {
                total=c.getDouble(c.getColumnIndex("GastoTotalPorAno"));
                dinero=total+dinero;
                k=1;
                c.close();

            }
        }
        catch (Exception e) { c.close(); }

        contentValues1.put(GASTO_TOTAL_POR_ANO,dinero);
        if(k==0)
        {
            sqLiteDatabase.insert(NOMBRE_TABLA1,null,contentValues1);
        }
        else
        {
            sqLiteDatabase.update(NOMBRE_TABLA1,contentValues1,NOMBRE_GASTO+"=? and "+ANO+"=?",new String[]{gasto,String.valueOf(ano)});
        }
        // Llamada al método para guardar en archivo de texto
        guardarEnArchivoTexto("Nombre del gasto: " + gasto + ", Año: " + ano + ", Dinero gastado: " + dinero + ", Fecha: " + fecha);


        return sqLiteDatabase.insert(NOMBRE_TABLA,null,contentValues);
    }

    public DBHelper(Context context) {
        this.context = context;
        myDBAdapter=new MyDBAdapter(context,NOMBRE_DB,null,DATABASE_VERSION);
    }

    public Cursor getAllEntries(String sql) {
        return sqLiteDatabase.rawQuery(sql,null);
    }

    public static class MyDBAdapter extends SQLiteOpenHelper{
        public MyDBAdapter(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
            db.execSQL(DB_CREATE1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists "+NOMBRE_TABLA);
            db.execSQL("drop table if exists "+NOMBRE_TABLA1);
            onCreate(db);

        }

    }

    public void guardarEnArchivoTexto(String texto) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("datos.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(texto.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String leerArchivoTexto() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fileInputStream = context.openFileInput("datos.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                stringBuilder.append(linea).append("\n");
            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
