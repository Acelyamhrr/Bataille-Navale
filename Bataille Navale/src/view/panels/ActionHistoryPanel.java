package view.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Panel affichant l'historique des actions et les dernières actions des joueurs.
 * Contient 3 zones de texte : dernière action du joueur, dernière action du robot,
 * et historique complet de la partie.
 */
public class ActionHistoryPanel extends JPanel {

    /** Zone de texte pour la dernière action du joueur */
    private JTextArea _txtPlayerAction;

    /** Zone de texte pour la dernière action du robot */
    private JTextArea _txtRobotAction;

    private JPanel _actionsPanel;
    private JScrollPane _historyPanel;

    /** Zone de texte pour l'historique complet */
    private JTextArea _txtHistory;

    /**
     * Constructeur du panel d'historique.
     *
     * @param playerName Le nom du joueur humain
     */
    public ActionHistoryPanel(String playerName) {
        // Actions joueur / robot
        _txtPlayerAction = new JTextArea(3, 30);
        _txtPlayerAction.setEditable(false);
        _txtPlayerAction.setLineWrap(true);
        _txtPlayerAction.setWrapStyleWord(true);
        _txtPlayerAction.setBorder(
                BorderFactory.createTitledBorder("Dernière action - " + playerName)
        );

        _txtRobotAction = new JTextArea(3, 30);
        _txtRobotAction.setEditable(false);
        _txtRobotAction.setLineWrap(true);
        _txtRobotAction.setWrapStyleWord(true);
        _txtRobotAction.setBorder(
                BorderFactory.createTitledBorder("Dernière action - Robot")
        );

        _actionsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        _actionsPanel.add(new JScrollPane(_txtPlayerAction));
        _actionsPanel.add(new JScrollPane(_txtRobotAction));

        // Historique
        _txtHistory = new JTextArea(8, 40);
        _txtHistory.setEditable(false);
        _txtHistory.setLineWrap(true);
        _txtHistory.setWrapStyleWord(true);

        _historyPanel = new JScrollPane(_txtHistory);
        _historyPanel.setBorder(
                BorderFactory.createTitledBorder("Historique")
        );
    }

    public JComponent getActionsPanel() {
        return _actionsPanel;
    }

    public JComponent getHistoryPanel() {
        return _historyPanel;
    }

    //MÉTHODES MÉTIER

    public void setPlayerAction(String action) {
        _txtPlayerAction.setText(action);
    }

    public void setRobotAction(String action) {
        _txtRobotAction.setText(action);
    }

    public void appendHistory(String text) {
        _txtHistory.append(text);
        _txtHistory.setCaretPosition(_txtHistory.getDocument().getLength());
    }

    public void clearHistory() {
        _txtHistory.setText("");
    }

}