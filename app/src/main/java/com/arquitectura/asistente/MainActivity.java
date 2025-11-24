package com.arquitectura.asistente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.arquitectura.asistente.presentacion.EstudianteActivity;
import com.arquitectura.asistente.presentacion.DocenteActivity;

/**
 * MainActivity - Punto de entrada de la aplicación
 * Permite seleccionar entre modo Estudiante o Docente
 * Conecta con la capa de presentación siguiendo la arquitectura de 3 capas
 */
public class MainActivity extends AppCompatActivity {

    private MaterialButton btnModoEstudiante;
    private MaterialButton btnModoDocente;
    private MaterialCardView cardEstudiante;
    private MaterialCardView cardDocente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        inicializarVistas();
        configurarEventos();
    }

    private void inicializarVistas() {
        btnModoEstudiante = findViewById(R.id.btnModoEstudiante);
        btnModoDocente = findViewById(R.id.btnModoDocente);
        cardEstudiante = findViewById(R.id.cardEstudiante);
        cardDocente = findViewById(R.id.cardDocente);
    }

    private void configurarEventos() {
        // Botones
        btnModoEstudiante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirModoEstudiante();
            }
        });

        btnModoDocente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirModoDocente();
            }
        });

        // Cards también son clicables
        cardEstudiante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirModoEstudiante();
            }
        });

        cardDocente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirModoDocente();
            }
        });
    }

    private void abrirModoEstudiante() {
        Intent intent = new Intent(MainActivity.this, EstudianteActivity.class);
        startActivity(intent);
    }

    private void abrirModoDocente() {
        Intent intent = new Intent(MainActivity.this, DocenteActivity.class);
        startActivity(intent);
    }
}
