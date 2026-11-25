package l2madevs;
import l2madevs.vista.VentanaLogin;
import java.sql.SQLException;

public class L2MADevs {
    public static void main(String[] args) throws SQLException {
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new VentanaLogin().setVisible(true);
            }
    });
    }
}
