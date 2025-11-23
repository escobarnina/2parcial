package com.arquitectura.asistente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.arquitectura.asistente.presentacion.AsistenciaActivity;

/**
 * MainActivity - Punto de entrada de la aplicación
 * Conecta con la capa de presentación siguiendo la arquitectura de 3 capas
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button btnIrAsistencia = findViewById(R.id.btnIrAsistencia);
        if (btnIrAsistencia != null) {
            btnIrAsistencia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AsistenciaActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}

