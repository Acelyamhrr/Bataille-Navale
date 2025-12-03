import controller.CentralController;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CentralController controller = new CentralController();
            controller.start();
        });
    }
}
