package view;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {

    private final int rows = 10;
    private final int cols = 10;
    private JPanel[][] playerGrid;
    private JPanel[][] enemyGrid;
    private JTextArea logArea;

    public GameView() {
        setTitle("Bataille Navale - Partie");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // title and buttons on the top
        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("PARTIE EN COURS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        top.add(title, BorderLayout.CENTER);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnReturn = new JButton("Abandonner");
        btnReturn.addActionListener(e -> retourMenu());
        topRight.add(btnReturn);
        top.add(topRight, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // in the middle, we have both grids, side by side
        JPanel center = new JPanel(new GridLayout(1, 2, 10, 0));
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // player's grid
        JPanel pnlPlayer = new JPanel(new BorderLayout());
        pnlPlayer.setBorder(BorderFactory.createTitledBorder("Votre grille"));
        JPanel containerPlayerGrid = new JPanel(new GridLayout(rows, cols));
        playerGrid = new JPanel[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JPanel cell = new JPanel();
                cell.setBackground(new Color(180, 200, 255));
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                playerGrid[r][c] = cell;
                containerPlayerGrid.add(cell);
            }
        }

        pnlPlayer.add(containerPlayerGrid, BorderLayout.CENTER);
        center.add(pnlPlayer);

        // adverse's grid
        JPanel pnlEnemy = new JPanel(new BorderLayout());
        pnlEnemy.setBorder(BorderFactory.createTitledBorder("Grille adversaire (cliquer pour attaquer)"));
        JPanel containerEnemyGrid = new JPanel(new GridLayout(rows, cols));
        enemyGrid = new JPanel[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JPanel cell = new JPanel();
                cell.setBackground(new Color(220, 220, 220));
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                /*TO DO : ajt mouse listener pour l'attaque*/
                enemyGrid[r][c] = cell;
                containerEnemyGrid.add(cell);
            }
        }

        pnlEnemy.add(containerEnemyGrid, BorderLayout.CENTER);
        center.add(pnlEnemy);

        add(center, BorderLayout.CENTER);

        // bottom
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // logs
        logArea = new JTextArea(6, 20);
        logArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Historique"));
        scroll.setPreferredSize(new Dimension(400, 150));
        bottom.add(scroll, BorderLayout.CENTER);

        // commands
        JPanel commands = new JPanel();
        commands.setLayout(new BoxLayout(commands, BoxLayout.Y_AXIS));
        commands.setPreferredSize(new Dimension(300, 150));

        JLabel lblWeapon = new JLabel("Arme :");
        commands.add(lblWeapon);

        JComboBox<String> cbWeapon = new JComboBox(); // TO DO : remplir avec les armes dispos PAR RAPPORT AUX TRUCS
        cbWeapon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        commands.add(cbWeapon);

        JButton btnEndTour = new JButton("Terminer le tour");
        btnEndTour.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEndTour.addActionListener(e -> log("Tour terminé."));
        commands.add(btnEndTour);

        commands.add(Box.createVerticalGlue());

        bottom.add(commands, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);
    }

    private void log(String texte) {
        logArea.append(texte + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());        // !! moves the text cursor to the end of the text area
        System.out.println(texte);
    }

    private void retourMenu() {
        int res = JOptionPane.showConfirmDialog(this, "Abandonner la partie et retourner au menu ?", "Confirmer", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            MenuView menu = new MenuView();
            menu.setVisible(true);
            this.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameView game = new GameView();
            game.setVisible(true);
        });
    }
}
