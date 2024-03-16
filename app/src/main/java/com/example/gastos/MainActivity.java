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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView input_fecha;
    private EditText input_gasto,input_ano,input_dinero;
    private Button boton_guardar,boton_sig;
    private ImageButton select_fecha;

    private DBHelper dbHelper;
    private DatePickerDialog dialog;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verificar si ya se tiene el permiso
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
                // Si el permiso no está otorgado, solicitarlo
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 11);
            }
        }

        // Crear NotificationManager y NotificationCompat.Builder
        createNotificationManagerAndBuilder();

        dbHelper=new DBHelper(this);
        intialize();
        select_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                dialog= new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        input_fecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },year, month, day);
                dialog.show();
            }
        });

        boton_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item=input_gasto.getText().toString();
                String year=input_ano.getText().toString();
                String price=input_dinero.getText().toString();
                String date=input_fecha.getText().toString();

                if(TextUtils.isEmpty(item))
                {
                    Toast.makeText(MainActivity.this,"Introduzca la descripción del gasto.",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(year))
                {
                    Toast.makeText(MainActivity.this,"Introduzca el año.",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(price))
                {
                    Toast.makeText(MainActivity.this,"Introduzca el dinero gastado.",Toast.LENGTH_SHORT).show();
                }else if(date.equals("Seleccione la fecha"))
                {
                    Toast.makeText(MainActivity.this,"Seleccione la fecha.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    double checkprice= Double.parseDouble(price);
                    if(checkprice<=0.0)
                    {
                        Toast.makeText(MainActivity.this,"Introduzca un valor válido mayor que 0.",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        int datelength = date.length();
                        String checkyear = date.substring(datelength - 4);
                        if (year.equals(checkyear)) {
                            dbHelper.open();
                            try {
                                long checkinsert=dbHelper.insertdata(item,Integer.parseInt(year),Double.parseDouble(price),date);
                                Toast.makeText(MainActivity.this, "Gasto guardado", Toast.LENGTH_SHORT).show();
                                notificationManager.notify(1, notificationBuilder.build());
                                input_gasto.setText("");input_dinero.setText("");input_ano.setText("");
                                input_fecha.setText("Seleccione la fecha");
                            }catch (Exception e)
                            {
                                Toast.makeText(MainActivity.this, "Intentelo de nuevo. Datos no guardados", Toast.LENGTH_SHORT).show();
                            }

                            dbHelper.close();

                        } else {
                            Toast.makeText(MainActivity.this, "Introduzca un valor válido. La fecha y el año no coinciden", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

            }
        });

        boton_sig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, InfoGastosActivity.class);
                startActivity(intent);

            }
        });
    }

    // Método para manejar la respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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

    private void intialize() {
        input_gasto=findViewById(R.id.Gasto);
        input_ano=findViewById(R.id.Ano);
        input_dinero=findViewById(R.id.Dinero);
        input_fecha=findViewById(R.id.Fecha);
        boton_guardar=findViewById(R.id.Boton_guardar);
        boton_sig=findViewById(R.id.Boton_siguiente);
        select_fecha=findViewById(R.id.Seleccion_fecha);
    }
}
