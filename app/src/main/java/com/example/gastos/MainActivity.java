package com.example.gastos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    // Declaración de variables de clase
    private TextView input_fecha;
    private EditText input_gasto, input_ano, input_dinero;
    private Button boton_guardar, boton_sig;
    private ImageButton select_fecha;

    private DBHelper dbHelper;
    private DatePickerDialog dialog;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verificar y solicitar permiso si no está otorgado
        checkAndRequestPermission();

        // Crear NotificationManager y NotificationCompat.Builder
        createNotificationManagerAndBuilder();

        // Inicializar DBHelper y vistas
        dbHelper = new DBHelper(this);
        initializeViews();

        // Configurar selección de fecha
        select_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Configurar botón de guardar
        boton_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpenseData();
            }
        });

        // Configurar botón de siguiente
        boton_sig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInfoGastosActivity();
            }
        });
    }

    // Método para manejar la respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        handlePermissionRequestResult(requestCode, grantResults);
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
        handleMenuItemSelection(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    // Método para verificar y solicitar permisos si no están otorgados
    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
                // Si el permiso no está otorgado, solicitarlo
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 11);
            }
        }
    }

    // Método para manejar el resultado de la solicitud de permisos
    private void handlePermissionRequestResult(int requestCode, int[] grantResults) {
        if (requestCode == 11) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado
                Toast.makeText(this, "Permiso otorgado para mostrar notificaciones", Toast.LENGTH_SHORT).show();
            } else {
                // Permiso denegado
                Toast.makeText(this, "Permiso denegado para mostrar notificaciones", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para crear NotificationManager y NotificationCompat.Builder
    private void createNotificationManagerAndBuilder() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this, "IdCanal");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("IdCanal", "Canal gastos", NotificationManager.IMPORTANCE_DEFAULT);

            notificationBuilder.setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Gasto guardado")
                    .setContentText("Nuevo gasto guardado con exito.")
                    .setSubText("Operación exitosa")
                    .setVibrate(new long[]{0, 1000, 500, 1000})
                    .setAutoCancel(true);

            notificationChannel.setDescription("Canal para guardar gastos");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    // Método para inicializar las vistas
    private void initializeViews() {
        input_gasto = findViewById(R.id.Gasto);
        input_ano = findViewById(R.id.Ano);
        input_dinero = findViewById(R.id.Dinero);
        input_fecha = findViewById(R.id.Fecha);
        boton_guardar = findViewById(R.id.Boton_guardar);
        boton_sig = findViewById(R.id.Boton_siguiente);
        select_fecha = findViewById(R.id.Seleccion_fecha);
    }

    // Método para mostrar el diálogo de selección de fecha
    private void showDatePickerDialog() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                input_fecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day);
        dialog.show();
    }

    // Método para guardar los datos del gasto
    private void saveExpenseData() {
        String item = input_gasto.getText().toString();
        String year = input_ano.getText().toString();
        String price = input_dinero.getText().toString();
        String date = input_fecha.getText().toString();

        // Validar campos
        if (TextUtils.isEmpty(item)) {
            Toast.makeText(MainActivity.this, "Introduzca la descripción del gasto.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(year)) {
            Toast.makeText(MainActivity.this, "Introduzca el año.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(MainActivity.this, "Introduzca el dinero gastado.", Toast.LENGTH_SHORT).show();
        } else if (date.equals("Seleccione la fecha")) {
            Toast.makeText(MainActivity.this, "Seleccione la fecha.", Toast.LENGTH_SHORT).show();
        } else {
            double checkprice = Double.parseDouble(price);
            if (checkprice <= 0.0) {
                Toast.makeText(MainActivity.this, "Introduzca un valor válido mayor que 0.", Toast.LENGTH_SHORT).show();
            } else {
                int datelength = date.length();
                String checkyear = date.substring(datelength - 4);
                if (year.equals(checkyear)) {
                    dbHelper.open();
                    try {
                        long checkinsert = dbHelper.insertdata(item, Integer.parseInt(year), Double.parseDouble(price), date);
                        Toast.makeText(MainActivity.this, "Gasto guardado", Toast.LENGTH_SHORT).show();
                        notificationManager.notify(1, notificationBuilder.build());
                        input_gasto.setText("");
                        input_dinero.setText("");
                        input_ano.setText("");
                        input_fecha.setText("Seleccione la fecha");
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Intentelo de nuevo. Datos no guardados", Toast.LENGTH_SHORT).show();
                    }

                    dbHelper.close();

                } else {
                    Toast.makeText(MainActivity.this, "Introduzca un valor válido. La fecha y el año no coinciden", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    // Método para manejar la selección de elementos del menú de opciones
    private void handleMenuItemSelection(int itemId) {
        switch (itemId) {
            case R.id.menu_add_expense:
                // Acción para añadir un nuevo gasto
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;
            case R.id.menu_view_history:
                // Acción para ver el historial de gastos
                startActivity(new Intent(MainActivity.this, InfoGastosActivity.class));
                break;
        }
    }

    // Método para navegar a la actividad de información de gastos
    private void goToInfoGastosActivity() {
        startActivity(new Intent(MainActivity.this, InfoGastosActivity.class));
    }
}
