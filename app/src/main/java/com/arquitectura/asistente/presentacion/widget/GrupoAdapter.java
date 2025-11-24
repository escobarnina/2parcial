package com.arquitectura.asistente.presentacion.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arquitectura.asistente.R;
import com.arquitectura.asistente.datos.Grupo;
import com.arquitectura.asistente.datos.Horario;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para mostrar la lista de grupos inscritos en un RecyclerView
 * Capa de Presentación - Widget
 */
public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.GrupoViewHolder> {
    private List<Grupo> grupos;
    private OnGrupoClickListener listener;
    private Context context;
    private HorarioProvider horarioProvider;

    public interface OnGrupoClickListener {
        void onGrupoClick(Grupo grupo);
    }

    public interface HorarioProvider {
        List<Horario> obtenerHorarios(Integer grupoId);
    }

    public GrupoAdapter(OnGrupoClickListener listener) {
        this.grupos = new ArrayList<>();
        this.listener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setHorarioProvider(HorarioProvider horarioProvider) {
        this.horarioProvider = horarioProvider;
    }

    @NonNull
    @Override
    public GrupoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
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

    class GrupoViewHolder extends RecyclerView.ViewHolder {
        private TextView txtMateria;
        private TextView txtGrupo;
        private TextView txtDocente;
        private TextView txtSemestreGestion;
        private TextView txtHorario;
        private TextView txtDias;

        public GrupoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMateria = itemView.findViewById(R.id.txtMateria);
            txtGrupo = itemView.findViewById(R.id.txtGrupo);
            txtDocente = itemView.findViewById(R.id.txtDocente);
            txtSemestreGestion = itemView.findViewById(R.id.txtSemestreGestion);
            txtHorario = itemView.findViewById(R.id.txtHorario);
            txtDias = itemView.findViewById(R.id.txtDias);
        }

        public void bind(Grupo grupo) {
            txtMateria.setText(grupo.getMateriaNombre() != null ? grupo.getMateriaNombre() : "N/A");
            txtGrupo.setText("Grupo: " + (grupo.getGrupo() != null ? grupo.getGrupo() : "N/A"));
            txtDocente.setText("Docente: " + (grupo.getDocenteNombre() != null ? grupo.getDocenteNombre() : "N/A"));
            txtSemestreGestion.setText("Semestre " + grupo.getSemestre() + " - " + grupo.getGestion());
            
            // Obtener y mostrar horarios
            if (grupo.getId() != null && horarioProvider != null) {
                try {
                    List<Horario> horarios = horarioProvider.obtenerHorarios(grupo.getId());
                    if (horarios != null && !horarios.isEmpty()) {
                        // Formatear horarios y días
                        StringBuilder horariosStr = new StringBuilder();
                        StringBuilder diasStr = new StringBuilder();
                        
                        for (int i = 0; i < horarios.size(); i++) {
                            Horario h = horarios.get(i);
                            if (i > 0) {
                                horariosStr.append(" | ");
                                diasStr.append(", ");
                            }
                            horariosStr.append(h.getHoraInicio()).append("-").append(h.getHoraFin());
                            // Abreviar días: tomar primeros 3 caracteres
                            String dia = h.getDia() != null ? h.getDia() : "";
                            if (dia.length() >= 3) {
                                diasStr.append(dia.substring(0, 3));
                            } else {
                                diasStr.append(dia);
                            }
                        }
                        
                        txtHorario.setText(horariosStr.toString());
                        txtDias.setText(diasStr.toString());
                    } else {
                        txtHorario.setText("Sin horario");
                        txtDias.setText("N/A");
                    }
                } catch (Exception e) {
                    txtHorario.setText("Sin horario");
                    txtDias.setText("N/A");
                }
            } else {
                txtHorario.setText("Sin horario");
                txtDias.setText("N/A");
            }
        }
    }
}

