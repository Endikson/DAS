package com.example.gastos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class InfoGastosActivity extends AppCompatActivity {

    // Declaración de variables de clase
    private RecyclerView myrecylerview;
    private Button boton_volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_gastos);

        // Asignación de vistas a las variables
        myrecylerview=findViewById(R.id.myrecycler);
        boton_volver=findViewById(R.id.Boton_volver);

        // Creación del adaptador y configuración del RecyclerView
        MyAdapter recycleadapter = new MyAdapter(this);
        myrecylerview.setAdapter(recycleadapter);
        myrecylerview.setLayoutManager(
                new LinearLayoutManager(this));

        // Añadir decoración de línea divisoria al RecyclerView
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this,
                        DividerItemDecoration.VERTICAL);
        myrecylerview.addItemDecoration(dividerItemDecoration);

        // Configuración del botón "Volver" para regresar a la actividad principal
        boton_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previousintent=new Intent(InfoGastosActivity.this,MainActivity.class);
                startActivity(previousintent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
            }
        });
    }

    // Método para inflar el menú de opciones en la ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_custom_actionbar, menu);
        return true;
    }

    // Método para manejar la selección de elementos del menú de opciones
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Identificar la acción del menú y realizar las operaciones correspondientes
        switch (id) {
            case R.id.menu_add_expense:
                // Acción para añadir un nuevo gasto
                Intent addExpenseIntent = new Intent(InfoGastosActivity.this, MainActivity.class);
                startActivity(addExpenseIntent);
                return true;
            case R.id.menu_view_history:
                // Acción para ver el historial de gastos
                Intent viewHistoryIntent = new Intent(InfoGastosActivity.this, InfoGastosActivity.class);
                startActivity(viewHistoryIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
