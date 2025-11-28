package view;

import model.enums.*;
import model.grid.Position;
import model.Observer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Vue principale du jeu.
 * Affiche les deux grilles et les informations de la partie.
 */
public class GameView extends JFrame implements Observer {
    private final int gridSize;
    private final String username;

    // Grilles
    private JButton[][] playerGrid;
    private JButton[][] robotGrid;

    // Informations
    private JLabel turnLabel;
    private JLabel playerInfoLabel;
    private JLabel robotInfoLabel;

    // Panneau central
    private JPanel mainPanel;

    public GameView(int gridSize, String username) {
        this.gridSize = gridSize;
        this.username = username;

        setTitle("Bataille Navale - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // En-tête avec le numéro de tour
        turnLabel = new JLabel("Tour 1", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(turnLabel, BorderLayout.NORTH);

        // Panneau central avec les deux grilles
        JPanel gridsPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        // Grille du joueur (ses bateaux)
        JPanel playerPanel = createGridPanel("Votre grille", true);
        gridsPanel.add(playerPanel);

        // Grille d'attaque (où attaquer le robot)
        JPanel robotPanel = createGridPanel("Grille d'attaque", false);
        gridsPanel.add(robotPanel);

        mainPanel.add(gridsPanel, BorderLayout.CENTER);

        // Panneau d'informations en bas
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        playerInfoLabel = new JLabel("<html>Joueur: " + username + "<br>Bateaux: 5/5</html>");
        playerInfoLabel.setVerticalAlignment(SwingConstants.TOP);
        infoPanel.add(playerInfoLabel);

        robotInfoLabel = new JLabel("<html>Robot<br>Bateaux: 5/5</html>");
        robotInfoLabel.setVerticalAlignment(SwingConstants.TOP);
        infoPanel.add(robotInfoLabel);

        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createGridPanel(String title, boolean isPlayerGrid) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Titre
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Grille
        JPanel gridPanel = new JPanel(new GridLayout(gridSize, gridSize, 2, 2));
        gridPanel.setBackground(Color.DARK_GRAY);

        JButton[][] grid = new JButton[gridSize][gridSize];

        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                JButton cell = new JButton();
                cell.setPreferredSize(new Dimension(50, 50));
                cell.setBackground(new Color(100, 150, 200)); // Eau
                cell.setFocusPainted(false);
                cell.setBorderPainted(true);

                // On stocke les coordonnées dans le bouton
                final int fx = x;
                final int fy = y;

                // Seule la grille d'attaque est cliquable
                if (!isPlayerGrid) {
                    cell.addActionListener(e -> onCellClick(fx, fy));
                }

                grid[y][x] = cell;
                gridPanel.add(cell);
            }
        }

        if (isPlayerGrid) {
            playerGrid = grid;
        } else {
            robotGrid = grid;
        }

        panel.add(gridPanel, BorderLayout.CENTER);

        return panel;
    }

    // Callback quand on clique sur une case de la grille d'attaque
    private void onCellClick(int x, int y) {
        System.out.println("Clic sur case: (" + x + ", " + y + ")");
        // TODO: Notifier le controller
    }

    // Méthodes pour mettre à jour l'affichage

    public void setTurnNumber(int turn) {
        turnLabel.setText("Tour " + turn);
    }

    public void setPlayerCellColor(int x, int y, Color color) {
        playerGrid[y][x].setBackground(color);
    }

    public void setRobotCellColor(int x, int y, Color color) {
        robotGrid[y][x].setBackground(color);
    }

    public void updatePlayerInfo(String info) {
        playerInfoLabel.setText("<html>" + info + "</html>");
    }

    public void updateRobotInfo(String info) {
        robotInfoLabel.setText("<html>" + info + "</html>");
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== MÉTHODES DE DEBUG (À RETIRER PLUS TARD) =====

    /**
     * Affiche les bateaux du robot (pour debug uniquement).
     * @param positions Liste des positions des bateaux du robot
     * @param sizes Tailles correspondantes des bateaux
     */
    public void debugShowRobotBoats(java.util.List<Position> positions, java.util.List<Integer> sizes) {
        Color robotBoat = new Color(200, 100, 100); // Rouge clair pour debug

        for (int i = 0; i < positions.size(); i++) {
            Position pos = positions.get(i);
            int size = sizes.get(i);

            for (int j = 0; j < size; j++) {
                int x = pos.getOrientation() == Orientation.HORIZONTAL ? pos.getX() + j : pos.getX();
                int y = pos.getOrientation() == Orientation.VERTICAL ? pos.getY() + j : pos.getY();

                if (x < gridSize && y < gridSize) {
                    playerGrid[y][x].setBackground(robotBoat);
                    playerGrid[y][x].setText("R"); // "R" pour Robot
                }
            }
        }
    }

    /**
     * Affiche les pièges du robot (pour debug uniquement).
     */
    public void debugShowRobotTraps(java.util.List<Position> trapPositions) {
        Color trapColor = new Color(255, 150, 0); // Orange pour les pièges

        for (Position pos : trapPositions) {
            if (pos.getX() < gridSize && pos.getY() < gridSize) {
                playerGrid[pos.getY()][pos.getX()].setBackground(trapColor);
                playerGrid[pos.getY()][pos.getX()].setText("T"); // "T" pour Trap
            }
        }
    }

    // ===== FIN DES MÉTHODES DE DEBUG =====


    // Méthodes Observer (pour l'historique)

    @Override
    public void boatAttacked(Position position) {
        System.out.println("Bateau touché en " + position.getX() + "," + position.getY());
    }

    @Override
    public void boatSunk(Position position, int size) {
        System.out.println("Bateau coulé en " + position.getX() + "," + position.getY());
    }

    @Override
    public void squareAttacked(Position position) {
        System.out.println("Case attaquée en " + position.getX() + "," + position.getY());
    }

    @Override
    public void squareIsland(Position position, State state) {
        System.out.println("Île en " + position.getX() + "," + position.getY() + " : " + state);
    }
}