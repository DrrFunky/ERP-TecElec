package l2madevs.modelo;
import java.time.LocalDateTime;

public class Usuario {
    private int idUsuario;
    private Rol rol;
    private String nombre, aPaterno, aMaterno, correo, direccion, telefono, usuarioLogin ;
    private LocalDateTime fechaCreacionUsuario;

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getAPaterno() { return aPaterno; }
    public void setAPaterno(String aPaterno) { this.aPaterno = aPaterno; }
    public String getAMaterno() { return aMaterno; }
    public void setAMaterno(String aMaterno) { this.aMaterno = aMaterno; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public LocalDateTime getFechaCreacionUsuario() { return fechaCreacionUsuario; }
    public void setFechaCreacionUsuario(LocalDateTime fechaCreacionUsuario) { this.fechaCreacionUsuario = fechaCreacionUsuario; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getUsuarioLogin() { return usuarioLogin; }
    public void setUsuarioLogin(String usuarioLogin) { this.usuarioLogin = usuarioLogin; }
    
    public String getNombreCompleto() {
        return nombre + " " + aPaterno + " " + aMaterno;
    }
}
