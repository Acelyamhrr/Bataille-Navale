package view;

import controller.EndController;
import model.game.GameStats;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Vue de fin de partie.
 * prend des objets GameStats au lieu de 15 paramètres.
 */
public class EndView extends JFrame {
    private JButton restartButton;
    private JButton quitButton;
    private EndController controller;

    public EndView(String winner, int turnNumber, GameStats playerStats, GameStats robotStats, String playerName, EndController controller) {
        this.controller = controller;

        setTitle("Fin de partie - Bataille Navale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents(winner, turnNumber, playerStats, robotStats, playerName);
    }

    private void initComponents(String winner, int turnNumber, GameStats playerStats, GameStats robotStats, String playerName) {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Panel du gagnant
        JPanel winnerPanel = new JPanel();
        winnerPanel.setBackground(winner.equals(playerName) ? new Color(54, 126, 24) : new Color(204, 54, 54));
        winnerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        JLabel winnerLabel = new JLabel("🏆 VICTOIRE DE : " + winner.toUpperCase() + " ! 🏆");
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        winnerLabel.setForeground(Color.WHITE);
        winnerPanel.add(winnerLabel);

        mainPanel.add(winnerPanel, BorderLayout.NORTH);

        // Panel des stats
        JPanel statsPanel = new JPanel(new BorderLayout(10, 10));
        statsPanel.setBackground(new Color(240, 240, 240));

        JLabel statsTitle = new JLabel("📊 Statistiques de la partie", SwingConstants.CENTER);
        statsTitle.setFont(new Font("Arial", Font.BOLD, 20));
        statsTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        statsPanel.add(statsTitle, BorderLayout.NORTH);

        JLabel turnsLabel = new JLabel("Nombre de tours joués : " + turnNumber, SwingConstants.CENTER);
        turnsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        turnsLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Panel de comparaison
        JPanel comparisonPanel = new JPanel(new GridLayout(0, 3, 15, 8));
        comparisonPanel.setBackground(new Color(240, 240, 240));
        comparisonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        addHeader(comparisonPanel, "");
        addHeader(comparisonPanel, playerName);
        addHeader(comparisonPanel, "Robot");

        // Ajouter les stats (proprement avec les objets)
        addStatRow(comparisonPanel, "Bateaux intacts",
                String.valueOf(playerStats.getBoatsIntact()),
                String.valueOf(robotStats.getBoatsIntact()));

        addStatRow(comparisonPanel, "Bateaux touchés",
                String.valueOf(playerStats.getBoatsTouched()),
                String.valueOf(robotStats.getBoatsTouched()));

        addStatRow(comparisonPanel, "Bateaux coulés",
                String.valueOf(playerStats.getBoatsSunk()),
                String.valueOf(robotStats.getBoatsSunk()));

        addStatRow(comparisonPanel, "Tirs dans l'eau",
                String.valueOf(playerStats.getMissedShots()),
                String.valueOf(robotStats.getMissedShots()));

        addStatRow(comparisonPanel, "Précision",
                calculatePrecision(playerStats.getHitCells(), playerStats.getTotalCells()),
                calculatePrecision(robotStats.getHitCells(), robotStats.getTotalCells()));

        addStatRow(comparisonPanel, "Cases touchées",
                playerStats.getHitRatio(),
                robotStats.getHitRatio());

        JPanel centerContent = new JPanel(new BorderLayout());
        centerContent.setBackground(new Color(240, 240, 240));
        centerContent.add(turnsLabel, BorderLayout.NORTH);
        centerContent.add(comparisonPanel, BorderLayout.CENTER);

        statsPanel.add(centerContent, BorderLayout.CENTER);
        mainPanel.add(statsPanel, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));

        restartButton = new JButton("🔄 Recommencer");
        restartButton.setFont(new Font("Arial", Font.BOLD, 16));
        restartButton.setPreferredSize(new Dimension(200, 50));
        restartButton.setBackground(new Color(100, 150, 255));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        restartButton.addActionListener(e->controller.handleRestart());

        quitButton = new JButton("❌ Quitter");
        quitButton.setFont(new Font("Arial", Font.BOLD, 16));
        quitButton.setPreferredSize(new Dimension(200, 50));
        quitButton.setBackground(new Color(255, 100, 100));
        quitButton.setForeground(Color.WHITE);
        quitButton.setFocusPainted(false);
        quitButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        quitButton.addActionListener(e->controller.handleQuit());

        buttonPanel.add(restartButton);
        buttonPanel.add(quitButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addHeader(JPanel panel, String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(50, 50, 50));
        panel.add(label);
    }

    private void addStatRow(JPanel panel, String statName, String playerValue, String robotValue) {
        JLabel nameLabel = new JLabel(statName);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel playerLabel = new JLabel(playerValue, SwingConstants.CENTER);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        playerLabel.setForeground(new Color(0, 100, 200));

        JLabel robotLabel = new JLabel(robotValue, SwingConstants.CENTER);
        robotLabel.setFont(new Font("Arial", Font.BOLD, 14));
        robotLabel.setForeground(new Color(200, 50, 50));

        panel.add(nameLabel);
        panel.add(playerLabel);
        panel.add(robotLabel);
    }

    private String calculatePrecision(int hitCells, int totalCells) {
        if (totalCells == 0) return "0%";
        double precision = (hitCells * 100.0) / totalCells;
        return String.format("%.1f%%", precision);
    }

    public int showRestartChoiceDialog() {
        Object[] options = {
                "Rejouer la même partie",
                "Nouveaux placements",
                "Nouvelle configuration"
        };

        return JOptionPane.showOptionDialog(
                this,
                "Comment voulez-vous recommencer ?",
                "Recommencer la partie",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );
    }

}