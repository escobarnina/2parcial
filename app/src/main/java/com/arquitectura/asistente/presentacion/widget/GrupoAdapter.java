package com.arquitectura.asistente.presentacion.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arquitectura.asistente.R;
import com.arquitectura.asistente.datos.Grupo;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para mostrar la lista de grupos inscritos en un RecyclerView
 * Capa de Presentación - Widget
 */
public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.GrupoViewHolder> {
    private List<Grupo> grupos;
    private OnGrupoClickListener listener;

    public interface OnGrupoClickListener {
        void onGrupoClick(Grupo grupo);
    }

    public GrupoAdapter(OnGrupoClickListener listener) {
        this.grupos = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public GrupoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grupo, parent, false);
        return new GrupoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoViewHolder holder, int position) {
        Grupo grupo = grupos.get(position);
        holder.bind(grupo);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGrupoClick(grupo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return grupos.size();
    }

    public void actualizarGrupos(List<Grupo> nuevosGrupos) {
        this.grupos = nuevosGrupos != null ? nuevosGrupos : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class GrupoViewHolder extends RecyclerView.ViewHolder {
        private TextView txtMateria;
        private TextView txtGrupo;
        private TextView txtDocente;
        private TextView txtSemestreGestion;

        public GrupoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMateria = itemView.findViewById(R.id.txtMateria);
            txtGrupo = itemView.findViewById(R.id.txtGrupo);
            txtDocente = itemView.findViewById(R.id.txtDocente);
            txtSemestreGestion = itemView.findViewById(R.id.txtSemestreGestion);
        }

        public void bind(Grupo grupo) {
            txtMateria.setText(grupo.getMateriaNombre() != null ? grupo.getMateriaNombre() : "N/A");
            txtGrupo.setText("Grupo: " + (grupo.getGrupo() != null ? grupo.getGrupo() : "N/A"));
            txtDocente.setText("Docente: " + (grupo.getDocenteNombre() != null ? grupo.getDocenteNombre() : "N/A"));
            txtSemestreGestion.setText("Semestre " + grupo.getSemestre() + " - " + grupo.getGestion());
        }
    }
}

