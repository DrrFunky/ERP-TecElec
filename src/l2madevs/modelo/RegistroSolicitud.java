package l2madevs.modelo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class RegistroSolicitud {
    private int idSolicitud;
    private Usuario cliente;
    private TipoSolicitud tipoSolicitud;
    private TipoPrioridad tipoPrioridad;
    private Estado estado;
    private String descripcionSolicitud, ubicacion;
    private LocalDateTime fechaCreacion, fechaFin, fechaRealizarTrabajo;
    private List<Usuario> tecnicosAsignados;

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public TipoSolicitud getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public TipoPrioridad getTipoPrioridad() {
        return tipoPrioridad;
    }

    public void setTipoPrioridad(TipoPrioridad tipoPrioridad) {
        this.tipoPrioridad = tipoPrioridad;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getDescripcionSolicitud() {
        return descripcionSolicitud;
    }

    public void setDescripcionSolicitud(String descripcionSolicitud) {
        this.descripcionSolicitud = descripcionSolicitud;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalDateTime getFechaRealizarTrabajo() {
        return fechaRealizarTrabajo;
    }

    public void setFechaRealizarTrabajo(LocalDateTime fechaRealizarTrabajo) {
        this.fechaRealizarTrabajo = fechaRealizarTrabajo;
    }

    public List<Usuario> getTecnicosAsignados() {
        return tecnicosAsignados;
    }

    public void setTecnicosAsignados(List<Usuario> tecnicosAsignados) {
        this.tecnicosAsignados = tecnicosAsignados;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return idSolicitud == ((RegistroSolicitud) o).idSolicitud;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSolicitud);
    }
}
