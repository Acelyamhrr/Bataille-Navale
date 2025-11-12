package view;

import javax.swing.*;
import java.awt.*;

public class PlacementView extends JFrame {

    private JPanel gridPnl;
    private final int rows = 10;
    private final int cols = 10;
    private JPanel[][] square;

    public PlacementView() {
        setTitle("Placement des bateaux");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // title
        JLabel title = new JLabel("PLACEMENT DES BATEAUX", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Panel central : grille + options
        JPanel center = new JPanel(new BorderLayout());
        add(center, BorderLayout.CENTER);

        // Grille
        gridPnl = new JPanel(new GridLayout(rows, cols));
        gridPnl.setBorder(BorderFactory.createTitledBorder("Grille (clique pour placer)"));
        square = new JPanel[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JPanel cell = new JPanel();
                cell.setBackground(new Color(200, 220, 255));
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                square[r][c] = cell;
                gridPnl.add(cell);
            }
        }

        center.add(gridPnl, BorderLayout.CENTER);

        // Options à droite
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        right.setPreferredSize(new Dimension(260, 0));

        JLabel lblBoat = new JLabel("Choisir bateau :");
        lblBoat.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(lblBoat);

        JButton btnPlay = new JButton("Terminer et Jouer");
        btnPlay.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnPlay.addActionListener(e -> openGameView());
        right.add(btnPlay);

        right.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnReturn = new JButton("Retour au menu");
        btnReturn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnReturn.addActionListener(e -> revenirMenu());
        right.add(btnReturn);

        center.add(right, BorderLayout.EAST);
    }

    private void openGameView() {
        GameView game = new GameView();
        game.setVisible(true);
        this.dispose();
    }

    private void revenirMenu() {
        MenuView menu = new MenuView();
        menu.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlacementView vp = new PlacementView();
            vp.setVisible(true);
        });
    }
}
