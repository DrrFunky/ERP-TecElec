package l2madevs.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dimension;
import l2madevs.controlador.Login;

public class VentanaLogin extends JFrame {
    //Componentes de la Vista
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnEntrar;
    private Login lg;

    //Colores
    private final Color COLOR_FONDO_OSCURO = new Color(10, 38, 40); //Azul verdoso oscuro
    private final Color COLOR_BOTON = new Color(26, 179, 200);      //Celeste
    private final Color COLOR_BLANCO = Color.WHITE;
    private final Color COLOR_TEXTO = Color.BLACK;

    public VentanaLogin() {
        //Configuración de la ventana
        setTitle("ADTE");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Panel de Fondo
        JPanel panelFondo = (JPanel) getContentPane();
        panelFondo.setBackground(COLOR_FONDO_OSCURO);
        panelFondo.setLayout(new GridBagLayout());

        //Panel de Login
        JPanel panelLogin = new JPanel();
        panelLogin.setBackground(COLOR_BLANCO);
        panelLogin.setLayout(new GridBagLayout());
        panelLogin.setBorder(new EmptyBorder(40, 50, 40, 50));
        
        //Creación y Estilo de Componentes
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel lblTitulo = new JLabel("Bienvenido");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 36));
        lblTitulo.setForeground(COLOR_TEXTO);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 30, 0);
        panelLogin.add(lblTitulo, gbc);
        gbc.insets = new Insets(5, 0, 5, 0);
        
        //Etiqueta "Usuario"
        JLabel lblUser = new JLabel("Usuario");
        lblUser.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.anchor = GridBagConstraints.WEST;
        panelLogin.add(lblUser, gbc);

        //Campo "txtUsuario"
        txtUsuario = new JTextField(20);
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 16));
        txtUsuario.setPreferredSize(new Dimension(300, 40));
        panelLogin.add(txtUsuario, gbc);
        
        //Etiqueta "Contraseña"
        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.insets = new Insets(15, 0, 5, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panelLogin.add(lblPass, gbc);

        //Campo "txtPassword"
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPassword.setPreferredSize(new Dimension(300, 40));
        gbc.insets = new Insets(5, 0, 5, 0);
        panelLogin.add(txtPassword, gbc);

        //Botón "Iniciar sesión"
        btnEntrar = new JButton("Iniciar sesión");
        btnEntrar.setBackground(COLOR_BOTON);
        btnEntrar.setForeground(COLOR_BLANCO);
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 16));
        btnEntrar.setBorderPainted(false);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setPreferredSize(new Dimension(300, 45));
        getRootPane().setDefaultButton(btnEntrar);
	
        gbc.insets = new Insets(30, 0, 0, 0);
        panelLogin.add(btnEntrar, gbc);

        this.lg = new Login();

        btnEntrar.addActionListener(e -> {
            lg.handleLogin(
                this,
                txtUsuario.getText(),
                txtPassword.getPassword()
            );
        });
        panelFondo.add(panelLogin);
    }
}
