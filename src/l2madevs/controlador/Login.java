package l2madevs.controlador;

import javax.swing.JOptionPane;
import l2madevs.modelo.Usuario;
import l2madevs.vista.VentanaLogin;
import l2madevs.dao.UsuarioDAO;
import l2madevs.vista.VentanaGestor;
import l2madevs.vista.VentanaTecnico;

public class Login {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void handleLogin(VentanaLogin frame, String txtUsuario, char[] txtPassword) {
	String password = new String(txtPassword);
	if (txtUsuario.isEmpty() || password.isEmpty()) {
	    JOptionPane.showMessageDialog(frame, "Por favor, ingrese un usuario y contraseña.",
		    "Error", 2);
	} else {
	    Usuario usuarioValidado = usuarioDAO.validarLogin(txtUsuario, password);
	    if (usuarioValidado == null) {
		JOptionPane.showMessageDialog(frame, "Usuario o Contraseña erroneos.",
			"Error", 0);
	    } else {
		//Cerramos la ventana de Login
		frame.dispose();
		//Decidimos qué panel abrir basado en el ROL
		if ("Gestor".equals(usuarioValidado.getRol().getNombreRol())) {
		    new VentanaGestor(usuarioValidado).setVisible(true);
		    
                } else if ("Tecnico".equals(usuarioValidado.getRol().getNombreRol())) {
		    new VentanaTecnico(usuarioValidado).setVisible(true);
                }
	    }
	}
    }
}
