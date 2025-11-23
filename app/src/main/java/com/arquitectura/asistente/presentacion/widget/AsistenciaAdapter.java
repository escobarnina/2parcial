package com.arquitectura.asistente.presentacion.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arquitectura.asistente.R;
import com.arquitectura.asistente.datos.Asistencia;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para mostrar la lista de asistencias en un RecyclerView
 * Capa de Presentación - Widget
 */
public class AsistenciaAdapter extends RecyclerView.Adapter<AsistenciaAdapter.AsistenciaViewHolder> {
    private List<Asistencia> asistencias;

    public AsistenciaAdapter() {
        this.asistencias = new ArrayList<>();
    }

    @NonNull
    @Override
    public AsistenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_asistencia, parent, false);
        return new AsistenciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AsistenciaViewHolder holder, int position) {
        Asistencia asistencia = asistencias.get(position);
        holder.bind(asistencia);
    }

    @Override
    public int getItemCount() {
        return asistencias.size();
    }

    public void actualizarAsistencias(List<Asistencia> nuevasAsistencias) {
        this.asistencias = nuevasAsistencias != null ? nuevasAsistencias : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class AsistenciaViewHolder extends RecyclerView.ViewHolder {
        private TextView txtId;
        private TextView txtAlumnoId;
        private TextView txtGrupoId;
        private TextView txtFecha;
        private TextView txtHora;
        private TextView txtEstado;

        public AsistenciaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtId);
            txtAlumnoId = itemView.findViewById(R.id.txtAlumnoId);
            txtGrupoId = itemView.findViewById(R.id.txtGrupoId);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtHora = itemView.findViewById(R.id.txtHora);
            txtEstado = itemView.findViewById(R.id.txtEstado);
        }

        public void bind(Asistencia asistencia) {
            txtId.setText("ID: " + (asistencia.getId() != null ? asistencia.getId() : "N/A"));
            txtAlumnoId.setText("Alumno: " + asistencia.getAlumnoId());
            txtGrupoId.setText("Grupo: " + asistencia.getGrupoId());
            txtFecha.setText("Fecha: " + (asistencia.getFecha() != null ? asistencia.getFecha() : "N/A"));
            txtHora.setText("Hora: " + (asistencia.getHoraMarcada() != null ? asistencia.getHoraMarcada() : "N/A"));
            txtEstado.setText("Estado: " + (asistencia.getEstado() != null ? asistencia.getEstado() : "N/A"));
        }
    }
}

