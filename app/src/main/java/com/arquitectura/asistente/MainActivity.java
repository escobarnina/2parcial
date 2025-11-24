package com.arquitectura.asistente;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

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
    
    // Header colapsable
    private View headerContainer;
    private LinearLayout headerContent;
    private ImageView ivIcono;
    private TextView tvTitulo;
    private Toolbar toolbarCollapsed;
    private ImageView ivIconoCollapsed;
    private TextView tvTituloCollapsed;
    private NestedScrollView nestedScrollView;
    
    // Valores para animación
    private static final int HEADER_EXPANDED_HEIGHT = 280; // dp convertido a px
    private static final int HEADER_COLLAPSED_HEIGHT = 80; // dp convertido a px
    private static final float ICON_EXPANDED_SIZE = 100f;
    private static final float ICON_COLLAPSED_SIZE = 32f;
    private static final float TITLE_EXPANDED_SIZE = 32f;
    private static final float TITLE_COLLAPSED_SIZE = 20f;
    
    private int headerExpandedHeightPx;
    private int headerCollapsedHeightPx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Convertir dp a px
        float density = getResources().getDisplayMetrics().density;
        headerExpandedHeightPx = (int) (HEADER_EXPANDED_HEIGHT * density);
        headerCollapsedHeightPx = (int) (HEADER_COLLAPSED_HEIGHT * density);
        
        inicializarVistas();
        configurarScrollListener();
        configurarEventos();
    }

    private void inicializarVistas() {
        btnModoEstudiante = findViewById(R.id.btnModoEstudiante);
        btnModoDocente = findViewById(R.id.btnModoDocente);
        cardEstudiante = findViewById(R.id.cardEstudiante);
        cardDocente = findViewById(R.id.cardDocente);
        
        // Header
        headerContainer = findViewById(R.id.headerContainer);
        headerContent = findViewById(R.id.headerContent);
        ivIcono = findViewById(R.id.ivIcono);
        tvTitulo = findViewById(R.id.tvTitulo);
        toolbarCollapsed = findViewById(R.id.toolbarCollapsed);
        ivIconoCollapsed = findViewById(R.id.ivIconoCollapsed);
        tvTituloCollapsed = findViewById(R.id.tvTituloCollapsed);
        nestedScrollView = findViewById(R.id.nestedScrollView);
    }

    private void configurarScrollListener() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int scrollRange = Math.max(0, headerExpandedHeightPx - headerCollapsedHeightPx);
                int scrolled = Math.min(scrollY, scrollRange);
                float progress = scrollRange > 0 ? (float) scrolled / scrollRange : 0f;
                progress = Math.min(1f, Math.max(0f, progress));
                
                animarHeader(progress);
            }
        });
    }

    private void animarHeader(float progress) {
        // Animar altura del header
        int currentHeight = (int) (headerExpandedHeightPx - (headerExpandedHeightPx - headerCollapsedHeightPx) * progress);
        headerContainer.getLayoutParams().height = currentHeight;
        headerContainer.requestLayout();
        
        // Animar tamaño del icono
        float iconSize = ICON_EXPANDED_SIZE - (ICON_EXPANDED_SIZE - ICON_COLLAPSED_SIZE) * progress;
        ivIcono.getLayoutParams().width = (int) (iconSize * getResources().getDisplayMetrics().density);
        ivIcono.getLayoutParams().height = (int) (iconSize * getResources().getDisplayMetrics().density);
        ivIcono.requestLayout();
        
        // Animar tamaño del título
        float titleSize = TITLE_EXPANDED_SIZE - (TITLE_EXPANDED_SIZE - TITLE_COLLAPSED_SIZE) * progress;
        tvTitulo.setTextSize(titleSize);
        
        // Animar opacidad del contenido expandido
        float alpha = 1f - progress;
        headerContent.setAlpha(alpha);
        headerContent.setVisibility(alpha > 0.1f ? View.VISIBLE : View.GONE);
        
        // Mostrar/ocultar toolbar colapsado
        if (progress > 0.5f) {
            toolbarCollapsed.setVisibility(View.VISIBLE);
            toolbarCollapsed.setAlpha((progress - 0.5f) * 2f);
        } else {
            toolbarCollapsed.setVisibility(View.GONE);
        }
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
