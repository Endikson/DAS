package com.example.gastos;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context context;
    List<DatosGasto> detallegastos = new ArrayList<>();

    private static final String TAG = "MyAdapter";

    public class DatosGasto {
        String gasto;
        int ano;
        double gastoanual;
    }

    public MyAdapter(Context context) {
        this.context = context;
        DBHelper db1= new DBHelper(context);
        db1.open();
        String sql="select * from GastosTotales";
        Cursor c= db1.getAllEntries(sql);
        c.moveToFirst();
        for (int i =0; i<c.getCount();i++)
        {
            DatosGasto dg = new DatosGasto();
            dg.gasto = c.getString(c.getColumnIndex("Nombre_gasto"));
            dg.ano = c.getInt(c.getColumnIndex("Ano"));
            dg.gastoanual = c.getDouble(c.getColumnIndex("GastoTotalPorAno"));
            detallegastos.add(dg);
            c.moveToNext();
        }
        c.close();
        db1.close();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.recyclerview_display_support,parent,
                false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.gasto.setText(detallegastos.get(position).gasto);
        String yeardetail= String.valueOf(detallegastos.get(position).ano);
        holder.ano.setText(yeardetail);
        String totalsales= String.valueOf(detallegastos.get(position).gastoanual);
        holder.total.setText(totalsales);
    }

    @Override
    public int getItemCount() {
        return detallegastos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView gasto,ano,total;
        RelativeLayout recyclerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gasto=itemView.findViewById(R.id.Gasto);
            ano=itemView.findViewById(R.id.Ano);
            total=itemView.findViewById(R.id.Dinero);
            recyclerView=itemView.findViewById(R.id.mylayout);
        }
    }
}
