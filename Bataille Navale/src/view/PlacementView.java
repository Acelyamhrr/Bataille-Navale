package view;

import controller.PlacementController;
import model.enums.*;
import model.grid.Grid;
import model.grid.Position;
import model.placement.Placement;
import model.placement.PlacementObserver;
import model.placement.PreviewInfo;
import model.placement.SelectionState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import view.panels.PlacementGridPanel;
import view.panels.PlacementControlPanel;
import static view.utils.GameColors.*;

/**
 * Vue principale pour la phase de placement des éléments de jeu.
 * Affiche la grille, les contrôles de placement et gère l'interaction avec le joueur.
 * Implémente PlacementObserver pour être notifiée des changements dans le modèle.
 */
public class PlacementView extends JFrame implements PlacementObserver {

    /** Taille de la grille de jeu */
    private int _gridSize;

    /** Label affichant la phase actuelle (bateaux, pièges ou armes) */
    private JLabel _lblPhase;

    /** Bouton pour retourner à l'écran précédent */
    private JButton _btnBack;

    /** Bouton pour valider le placement et commencer la partie */
    private JButton _btnValidate;

    /** Contrôleur gérant la logique de placement */
    private PlacementController _controller;

    /** Modèle contenant l'état du placement */
    private Placement _model;

    /** Panel affichant la grille de placement */
    private PlacementGridPanel _pnlGrid;

    /** Panel contenant les contrôles de placement */
    private PlacementControlPanel _pnlControl;

    /**
     * Constructeur de la vue de placement.
     * Initialise la fenêtre avec tous les composants nécessaires.
     *
     * @param gridSize Taille de la grille (6 à 10)
     * @param username Nom du joueur affiché dans le titre
     * @param controller Contrôleur gérant la logique de placement
     * @param model Modèle contenant l'état du placement
     */
    public PlacementView(int gridSize, String username, PlacementController controller, Placement model) {
        this._gridSize = gridSize;
        this._controller = controller;
        this._model = model;

        setTitle("Placement - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    /**
     * Initialise tous les composants de la fenêtre.
     * Crée et organise le titre, la grille, les contrôles et les boutons.
     */
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(240, 245, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Label de phase
        _lblPhase = new JLabel("Phase: Placement des bateaux", SwingConstants.CENTER);
        _lblPhase.setFont(new Font("Arial", Font.BOLD, 24));
        _lblPhase.setForeground(new Color(30, 50, 100));

        // Panel de grille
        _pnlGrid = new PlacementGridPanel(_gridSize, _controller);

        // Panel de contrôle
        _pnlControl = new PlacementControlPanel(_controller);

        // Panel des boutons de navigation
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(240, 245, 250));
        _btnBack = createBtn("Retour", new Color(150, 150, 150));
        _btnValidate = createBtn("Valider et Commencer", new Color(70, 150, 70));
        btnPanel.add(_btnBack);
        btnPanel.add(_btnValidate);

        // Assemblage
        mainPanel.add(_lblPhase, BorderLayout.NORTH);
        mainPanel.add(_pnlGrid, BorderLayout.CENTER);
        mainPanel.add(_pnlControl, BorderLayout.EAST);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Crée un bouton stylisé avec texte et couleur personnalisés.
     *
     * @param text Texte du bouton
     * @param bg Couleur de fond
     * @return Bouton configuré
     */
    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(180, 40));
        return btn;
    }

    // ==================== Méthodes pour le contrôleur ====================

    /**
     * Ajoute un listener au bouton Retour.
     *
     * @param l Listener à ajouter
     */
    public void addBackListener(ActionListener l) {
        _btnBack.addActionListener(l);
    }

    /**
     * Ajoute un listener au bouton Valider.
     *
     * @param l Listener à ajouter
     */
    public void addValidateListener(ActionListener l) {
        _btnValidate.addActionListener(l);
    }

    /**
     * Indique si l'orientation actuelle est horizontale.
     *
     * @return true si horizontal, false si vertical
     */
    public boolean isHorizontal() {
        return _pnlControl.isHorizontal();
    }

    /**
     * Récupère la coordonnée X de la case survolée.
     *
     * @return Position X du survol, -1 si aucune
     */
    public int getHoverX() {
        return _pnlGrid.getHoverX();
    }

    /**
     * Récupère la coordonnée Y de la case survolée.
     *
     * @return Position Y du survol, -1 si aucune
     */
    public int getHoverY() {
        return _pnlGrid.getHoverY();
    }

    /**
     * Récupère le mode de placement des bateaux sélectionné.
     *
     * @return "Fixed", "Random" ou "Manual"
     */
    public String getModeBoat() {
        return _pnlControl.getModeBoat();
    }

    /**
     * Change la couleur d'une cellule selon un code de couleur.
     * Convertit le code string en couleur appropriée.
     *
     * @param x Position X de la cellule
     * @param y Position Y de la cellule
     * @param color Code de couleur ("island", "boat", "trap", "weapon", "previewOk", "previewBad", ou "water" par défaut)
     */
    public void setCellColor(int x, int y, String color) {
        switch(color){
            case "island":
                _pnlGrid.setCellColor(x, y, ISLAND_COLOR);
                break;
            case "boat":
                _pnlGrid.setCellColor(x, y, BOAT_COLOR);
                break;
            case "trap":
                _pnlGrid.setCellColor(x, y, TRAP_COLOR);
                break;
            case "weapon":
                _pnlGrid.setCellColor(x, y, WEAPON_COLOR);
                break;
            case "previewOk":
                _pnlGrid.setCellColor(x, y, PREVIEW_OK);
                break;
            case "previewBad":
                _pnlGrid.setCellColor(x, y, PREVIEW_BAD);
                break;
            default:
                _pnlGrid.setCellColor(x, y, WATER_COLOR);
        }
    }

    /**
     * Change le texte d'une cellule.
     *
     * @param x Position X de la cellule
     * @param y Position Y de la cellule
     * @param text Texte à afficher
     */
    public void setCellText(int x, int y, String text) {
        _pnlGrid.setCellText(x, y, text);
    }

    /**
     * Met à jour le label d'information dans le panel de contrôle.
     *
     * @param text Texte à afficher
     */
    private void setInfoText(String text) {
        _pnlControl.setInfoText(text);
    }

    /**
     * Met à jour le label de phase (bateaux, pièges ou armes).
     *
     * @param text Texte de la phase
     */
    private void setPhaseText(String text) {
        _lblPhase.setText(text);
    }

    /**
     * Affiche un message d'erreur dans une boîte de dialogue.
     *
     * @param msg Message d'erreur
     */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Affiche un message de succès dans une boîte de dialogue.
     *
     * @param msg Message de succès
     */
    public void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== Méthodes Observer ====================

    /**
     * Appelée lorsque la grille change.
     * Redessine complètement la grille avec tous les éléments placés et l'aperçu.
     *
     * @param grid Grille mise à jour
     */
    @Override
    public void onGridChanged(Grid grid) {
        // Reset complet de la grille
        _pnlGrid.resetAllCells();

        // Dessin de l'île si présente
        if (grid.hasIsland()) {
            drawIsland(grid.getSize());
        }

        // Dessin des éléments placés
        drawBoats(grid);
        drawTraps(grid);
        drawWeapons(grid);

        // Dessin de l'aperçu au survol
        drawPreview();
    }

    /**
     * Dessine l'île au centre de la grille (4x4 cases).
     *
     * @param gridSize Taille de la grille
     */
    private void drawIsland(int gridSize) {
        int ix = gridSize / 2 - 2;
        int iy = gridSize / 2 - 2;
        for (int i = ix; i < ix + 4; i++) {
            for (int j = iy; j < iy + 4; j++) {
                setCellColor(i, j, "island");
            }
        }
    }

    /**
     * Dessine tous les bateaux placés sur la grille.
     *
     * @param grid Grille contenant les bateaux
     */
    private void drawBoats(Grid grid) {
        for(Position position : grid.getPositionsBoats()){
            setCellColor(position.getX(), position.getY(), "boat");
        }
    }

    /**
     * Dessine tous les pièges placés sur la grille avec leur type.
     *
     * @param grid Grille contenant les pièges
     */
    private void drawTraps(Grid grid) {
        for(Map.Entry<TrapType, java.util.List<Position>> entry : grid.getPositionsTraps().entrySet()) {
            String text = (entry.getKey() == TrapType.BLACKHOLE ? "Trou noir" : "Tornade");
            for(Position pos : entry.getValue()) {
                setCellColor(pos.getX(), pos.getY(), "trap");
                setCellText(pos.getX(), pos.getY(), text);
            }
        }
    }

    /**
     * Dessine toutes les armes placées sur la grille avec leur type.
     *
     * @param grid Grille contenant les armes
     */
    private void drawWeapons(Grid grid) {
        for(Map.Entry<WeaponType, List<Position>> entry : grid.getPositionsWeapons().entrySet()) {
            String text = (entry.getKey() == WeaponType.BOMB ? "Bombe" : "Sonar");
            for(Position pos : entry.getValue()) {
                setCellColor(pos.getX(), pos.getY(), "weapon");
                setCellText(pos.getX(), pos.getY(), text);
            }
        }
    }

    /**
     * Dessine l'aperçu du placement au survol de la souris.
     * Affiche en vert si le placement est valide, en rouge sinon.
     */
    private void drawPreview() {
        int hx = _pnlGrid.getHoverX();
        int hy = _pnlGrid.getHoverY();
        if (hx < 0 || hy < 0) return;

        Orientation orient = _pnlControl.isHorizontal() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        PreviewInfo preview = _model.getPreviewInfo(hx, hy, orient);

        if (preview != null) {
            String color = preview.isValid() ? "previewOk" : "previewBad";
            for(Position pos : preview.getCells()){
                setCellColor(pos.getX(), pos.getY(), color);
            }
        }
    }

    /**
     * Appelée lorsque la phase de placement change.
     * Met à jour le label de phase (bateaux, pièges ou armes).
     *
     * @param phase Nouvelle phase de placement
     */
    @Override
    public void onPhaseChanged(PlacementPhase phase) {
        String phaseText;
        switch (phase) {
            case WEAPONS:
                phaseText = "Phase: Placement des armes";
                break;
            case TRAPS:
                phaseText = "Phase: Placement des pièges";
                break;
            default:
                phaseText = "Phase: Placement des bateaux";
        };
        setPhaseText(phaseText);
    }

    /**
     * Appelée lorsqu'un message doit être affiché à l'utilisateur.
     * Affiche le message selon son type (info, succès ou erreur).
     *
     * @param message Contenu du message
     * @param type Type de message (INFO, SUCCESS ou ERROR)
     */
    @Override
    public void onMessage(String message, MessageType type) {
        switch (type) {
            case INFO:
                setInfoText(message);
                break;
            case SUCCESS:
                setInfoText(message);
                showSuccess(message);
                break;
            case ERROR:
                setInfoText(message);
                showError(message);
                break;
        }
    }

    /**
     * Appelée lorsque l'état de sélection change.
     * Met à jour les options disponibles dans les sélecteurs et leur état activé/désactivé.
     *
     * @param state Nouvel état de sélection
     */
    @Override
    public void onSelectionChanged(SelectionState state) {
        _pnlControl.setBoatOptions(state.getBoatOptions().toArray(new String[0]));
        _pnlControl.setTrapWeaponOptions(state.getTrapWeaponOptions().toArray(new String[0]));
        _pnlControl.enableBoatSelector(state.isBoatSelectorEnabled());
        _pnlControl.enableTrapWeaponSelector(state.isTrapWeaponSelectorEnabled());
    }
}