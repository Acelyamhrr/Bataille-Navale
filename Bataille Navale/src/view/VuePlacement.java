package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VuePlacement extends JFrame {

    private JPanel grillePanel;
    private JButton btnOrientation;
    private JComboBox<String> cbBateaux;
    private final int rows = 10;
    private final int cols = 10;
    private JPanel[][] cases;

    public VuePlacement() {
        setTitle("Placement des bateaux");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Titre
        JLabel titre = new JLabel("PLACEMENT DES BATEAUX", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 22));
        titre.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titre, BorderLayout.NORTH);

        // Panel central : grille + options
        JPanel center = new JPanel(new BorderLayout());
        add(center, BorderLayout.CENTER);

        // Grille
        grillePanel = new JPanel(new GridLayout(rows, cols));
        grillePanel.setBorder(BorderFactory.createTitledBorder("Grille (clique pour placer)"));
        cases = new JPanel[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JPanel cell = new JPanel();
                cell.setBackground(new Color(200, 220, 255));
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                cases[r][c] = cell;
                grillePanel.add(cell);
            }
        }

        center.add(grillePanel, BorderLayout.CENTER);

        // Options à droite
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        right.setPreferredSize(new Dimension(260, 0));

        JLabel lblBateau = new JLabel("Choisir bateau :");
        lblBateau.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(lblBateau);

        /*cbBateaux = new JComboBox<>(new String[]{"Porte-avion (5)", "Croiseur (4)", "Contre-torpilleur (3)", "Sous-marin (3)", "Torpilleur (2)"});
        cbBateaux.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        right.add(cbBateaux);

        right.add(Box.createRigidArea(new Dimension(0, 10)));

        btnOrientation = new JButton("Orientation : HORIZONTAL");
        btnOrientation.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnOrientation.addActionListener(e -> toggleOrientation());
        right.add(btnOrientation);

        right.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnPlacerAuto = new JButton("Placer automatiquement");
        btnPlacerAuto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnPlacerAuto.addActionListener(e -> placerAuto());
        right.add(btnPlacerAuto);

        right.add(Box.createVerticalGlue());
*/
        JButton btnJouer = new JButton("Terminer et Jouer");
        btnJouer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnJouer.addActionListener(e -> ouvrirVueJeu());
        right.add(btnJouer);

        right.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnRetour = new JButton("Retour au menu");
        btnRetour.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRetour.addActionListener(e -> revenirMenu());
        right.add(btnRetour);

        center.add(right, BorderLayout.EAST);
    }

    /*private void onCaseClicked(int r, int c) {
        // Placeholder UI feedback : toggle color pour simuler placement
        JPanel cell = cases[r][c];
        Color cur = cell.getBackground();
        if (cur.equals(new Color(200, 220, 255))) {
            cell.setBackground(new Color(100, 180, 120)); // placé
            System.out.println("Case placée: " + r + "," + c + " (bateau: " + cbBateaux.getSelectedItem() + ", orientation: " + btnOrientation.getText() + ")");
        } else {
            cell.setBackground(new Color(200, 220, 255)); // enleve
            System.out.println("Case enlevée: " + r + "," + c);
        }

        // Ici tu brancheras la logique (controleur) pour valider le placement réel
    }*/

    /*private void toggleOrientation() {
        if (btnOrientation.getText().contains("HORIZONTAL")) {
            btnOrientation.setText("Orientation : VERTICAL");
        } else {
            btnOrientation.setText("Orientation : HORIZONTAL");
        }
    }*/

    /*private void placerAuto() {
        // simple demo : colore quelques cases aléatoirement (remplace par algo réel)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cases[r][c].setBackground(new Color(200, 220, 255));
            }
        }
        // place 5 marques pour l'exemple
        cases[0][0].setBackground(new Color(100, 180, 120));
        cases[1][0].setBackground(new Color(100, 180, 120));
        cases[2][0].setBackground(new Color(100, 180, 120));
        cases[3][0].setBackground(new Color(100, 180, 120));
        cases[4][0].setBackground(new Color(100, 180, 120));
        System.out.println("Placement automatique (exemple).");
    }*/

    private void ouvrirVueJeu() {
        VueJeu jeu = new VueJeu();
        jeu.setVisible(true);
        this.dispose();
    }

    private void revenirMenu() {
        VueMenu menu = new VueMenu();
        menu.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VuePlacement vp = new VuePlacement();
            vp.setVisible(true);
        });
    }
}
