package controller;

import model.enums.*;
import model.game.GameConfig;
import model.game.GamePlacement;
import model.grid.Position;
import view.*;

import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * Contrôleur principal.
 * CONNAÎT les Vues et le Modèle.
 * Fait le lien entre les deux.
 */
public class GameController {
    // vues menu et configuration
    private MenuView menuView;
    private ConfigurationView configView;
    private GameConfig currentConfig;

    // vue placement
    private PlacementView placementView;
    private GamePlacement currentPlacement;
    private Map<BoatName, List<Position>> playerBoats = new HashMap<>();
    private Map<TrapType, Position> playerTraps = new HashMap<>();
    private List<BoatName> boatsToPlace = new ArrayList<>();
    private List<TrapType> trapsToPlace = new ArrayList<>();
    private int currentBoatIndex = 0;
    private int currentTrapIndex = 0;

    public GameController() {}

    // démarre le jeu
    public void start() {
        menuView = new MenuView();
        connectMenuView();
        menuView.setVisible(true);
    }

    private void connectMenuView() {
        menuView.addNewGameListener(e -> openConfiguration());
        menuView.addQuitListener(e -> quit());
    }

    // ouvre l'écran de config
    private void openConfiguration() {
        configView = new ConfigurationView();
        connectConfigView();
        configView.setVisible(true);
        menuView.setVisible(false);
    }

    // events de la config
    private void connectConfigView() {
        configView.addBackListener(e -> backToMenu());
        configView.addNextListener(e -> validateConfiguration());
    }

    // retour au menu
    private void backToMenu() {
        configView.dispose();
        menuView.setVisible(true);
    }

    /**
     Valide la configuration et passe à l'étape suivante.
     Appelé quand on clique "Suivant" dans la config.

     C'est ICI que le Controller fait le lien Vue → Modèle :
     1. Il LIT les données de la Vue (via les getters)
     2. Il CRÉE le Modèle (GameConfig)
     3. Il VALIDE avec la logique du Modèle
     4. Il RÉAGIT (afficher erreur ou continuer)
     */
    private void validateConfiguration() {
        // 1. Récupérer les données de la vue
        String username = configView.getUsername();
        int gridSize = configView.getGridSize();
        ModeGame mode = configView.isStandardMode() ? ModeGame.STANDARD : ModeGame.ISLAND;
        RobotMode robot = configView.isRandomRobot() ? RobotMode.RANDOM : RobotMode.SMART;

        String trapMode = configView.getTrapPlacementMode();
        TrapPlacement trap;

        switch (trapMode) {
            case "FIXED":
                trap = TrapPlacement.FIXED;
                break;
            case "RANDOM":
                trap = TrapPlacement.RANDOM;
                break;
            default:
                trap = TrapPlacement.MANUAL;
        }

        int[] boats = configView.isDefaultBoats() ? new int[]{1, 1, 1, 1, 1} : configView.getCustomBoatNumbers();

        // 2. Créer le Modèle
        currentConfig = new GameConfig(mode, gridSize, robot, boats, username, trap);

        // 3. Valider avec le Modèle
        if (!currentConfig.isValid()) {
            StringBuilder error = new StringBuilder("Configuration invalide !\n");
            if (username.isEmpty()) error.append("- Nom vide\n");
            if (currentConfig.getTotalBoatSquares() > 35) error.append("- Max 35 cases\n");
            configView.showError(error.toString());
            return;
        }

        // DEBUG
        configView.showSuccess("Config OK: " + currentConfig);

        openPlacement();
    }

    private void openPlacement() {
        placementView = new PlacementView(currentConfig.getGridSize(), currentConfig.getUsername());
        connectPlacementView();
        initBoatsToPlace();
        updateBoatSelector();
        updateGrid();
        placementView.setVisible(true);;
        configView.setVisible(false);
    }

    private void initTrapsPlacement(){
        initTrapsToPlace();
        switch(currentConfig.getTrapMode()){
            case RANDOM:
                applyRandomPlacementTraps();
                break;
            case FIXED:
                applyFixedPlacementTraps();
                break;
            case MANUAL:
                applyManualPlacementTraps();
                break;
        }
    }

    private void connectPlacementView() {
        placementView.addBackListener(e -> backToConfig());
        placementView.addValidateListener(e -> validatePlacement());
        placementView.addFixedModeListener(e -> applyFixedPlacement());
        placementView.addRandomModeListener(e -> applyRandomPlacement());
        placementView.addManualModeListener(e -> enableManualPlacement());
        placementView.setGridClickCallback((x, y) -> onGridClick(x, y));
        placementView.setGridHoverCallback((x, y) -> updateGrid());
    }

    private void initBoatsToPlace() {
        boatsToPlace.clear();
        playerBoats.clear();
        int[] counts = currentConfig.getNumberBoat();
        BoatName[] types = {BoatName.AIRCRAFT_CARRIER, BoatName.CRUISER,
                BoatName.DESTROYER, BoatName.SUBMARINE, BoatName.TORPEDO_BOAT};
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < counts[i]; j++) {
                boatsToPlace.add(types[i]);
            }
        }
        currentBoatIndex = 0;
    }

    private void initTrapsToPlace() {
        trapsToPlace.clear();
        playerTraps.clear();
        TrapType[] types = {TrapType.BLACKHOLE, TrapType.TORNADO};
        for (int i = 0; i < types.length; i++) {
            trapsToPlace.add(types[i]);
        }
        currentTrapIndex = 0;
    }

    private void updateBoatSelector() {
        List<String> options = new ArrayList<>();
        String[] names = {"Porte-avions (5)", "Croiseur (4)", "Destroyer (3)", "Sous-marin (3)", "Torpilleur (2)"};
        int[] counts = currentConfig.getNumberBoat();
        int[] placed = new int[5];

        for (BoatName b : playerBoats.keySet()) placed[b.ordinal()]++;

        for (int i = 0; i < 5; i++) {
            int remaining = counts[i] - placed[i];
            if (remaining > 0) options.add(names[i] + " - " + remaining + " restant(s)");
        }

        if (options.isEmpty()) options.add("Tous placés !");
        placementView.setBoatOptions(options.toArray(new String[0]));
    }

    private int getBoatSize(BoatName type) {
        switch (type) {
            case AIRCRAFT_CARRIER: return 5;
            case CRUISER: return 4;
            case DESTROYER: return 3;
            case SUBMARINE: return 3;
            case TORPEDO_BOAT: return 2;
            default: return 0;
        }
    }

    private void onGridClick(int x, int y) {
        if (currentBoatIndex >= boatsToPlace.size()) return;

        BoatName boat = boatsToPlace.get(currentBoatIndex);
        Orientation orient = placementView.isHorizontal() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

        if (canPlaceBoat(boat, x, y, orient)) {
            if(!playerBoats.containsKey(boat)) playerBoats.put(boat, new ArrayList<>());
            playerBoats.get(boat).add(new Position(x, y, orient));
            currentBoatIndex++;
            placementView.setInfoText("Bateau placé !");
            updateBoatSelector();
            updateGrid();

            if (currentBoatIndex >= boatsToPlace.size()) {
                placementView.setInfoText("Tous les bateaux sont placés !");
            }
        } else {
            placementView.setInfoText("Placement invalide !");
        }
    }

    private boolean canPlaceBoat(BoatName boat, int x, int y, Orientation orient) {
        int size = getBoatSize(boat);
        int gridSize = currentConfig.getGridSize();

        // Vérifier limites
        if (orient == Orientation.HORIZONTAL && x + size > gridSize) return false;
        if (orient == Orientation.VERTICAL && y + size > gridSize) return false;

        // Vérifier chevauchement
        for (int i = 0; i < size; i++) {
            int cx = orient == Orientation.HORIZONTAL ? x + i : x;
            int cy = orient == Orientation.VERTICAL ? y + i : y;
            if (isCellOccupied(cx, cy)) return false;
        }
        return true;
    }

    private boolean canPlaceTrap(TrapType type, int x, int y) {
        int gridSize = currentConfig.getGridSize();

        // Vérifier limites
        if (x > gridSize) return false;
        if (y > gridSize) return false;

        // Vérifier chevauchement
        return !isCellOccupied(x, y);
    }

    private boolean isCellOccupied(int x, int y) {
        // Check if cell is occupied by a boat
        for (Map.Entry<BoatName, List<Position>> entry : playerBoats.entrySet()) {
            for(Position pos : entry.getValue()) {
                int size = getBoatSize(entry.getKey());
                for (int i = 0; i < size; i++) {
                    int bx = pos.getOrientation() == Orientation.HORIZONTAL ? pos.getX() + i : pos.getX();
                    int by = pos.getOrientation() == Orientation.VERTICAL ? pos.getY() + i : pos.getY();
                    if (bx == x && by == y) return true;
                }
            }
        }

        //Check if cell is occupied by a trap
        for(Map.Entry<TrapType, Position> entry : playerTraps.entrySet()) {
            Position pos = entry.getValue();
            if(pos.getX() == x && pos.getY() == y) return true;
        }

        return false;
    }

    private void updateGrid() {
        int size = currentConfig.getGridSize();
        Color water = new Color(100, 150, 200);
        Color boat = new Color(80, 80, 80);
        Color previewOk = new Color(100, 200, 100);
        Color previewBad = new Color(200, 100, 100);

        // Reset
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                placementView.setCellColor(x, y, water);
            }
        }

        // Bateaux placés
        for (Map.Entry<BoatName, List<Position>> entry : playerBoats.entrySet()) {
            for(Position pos : entry.getValue()) {
                int boatSize = getBoatSize(entry.getKey());
                for (int i = 0; i < boatSize; i++) {
                    int bx = pos.getOrientation() == Orientation.HORIZONTAL ? pos.getX() + i : pos.getX();
                    int by = pos.getOrientation() == Orientation.VERTICAL ? pos.getY() + i : pos.getY();
                    placementView.setCellColor(bx, by, boat);
                }
            }
        }

        // Preview hover
        int hx = placementView.getHoverX();
        int hy = placementView.getHoverY();
        if (hx >= 0 && hy >= 0 && currentBoatIndex < boatsToPlace.size()) {
            BoatName currentBoat = boatsToPlace.get(currentBoatIndex);
            Orientation orient = placementView.isHorizontal() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            boolean canPlace = canPlaceBoat(currentBoat, hx, hy, orient);
            int boatSize = getBoatSize(currentBoat);

            for (int i = 0; i < boatSize; i++) {
                int px = orient == Orientation.HORIZONTAL ? hx + i : hx;
                int py = orient == Orientation.VERTICAL ? hy + i : hy;
                if (px < size && py < size && !isCellOccupied(px, py)) {
                    placementView.setCellColor(px, py, canPlace ? previewOk : previewBad);
                }
            }
        }
    }

    private void applyFixedPlacement() {
        playerBoats.clear();
        int y = 0;
        for (BoatName boat : boatsToPlace) {
            if(!playerBoats.containsKey(boat)) playerBoats.put(boat, new ArrayList<>());
            playerBoats.get(boat).add(new Position(0, y++, Orientation.HORIZONTAL));
        }
        currentBoatIndex = boatsToPlace.size();
        updateBoatSelector();
        updateGrid();
    }

    private void applyRandomPlacement() {
        playerBoats.clear();
        Random rand = new Random();
        int gridSize = currentConfig.getGridSize();

        for (BoatName boat : boatsToPlace) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(gridSize);
                int y = rand.nextInt(gridSize);
                Orientation o = rand.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
                if (canPlaceBoat(boat, x, y, o)) {
                    if(!playerBoats.containsKey(boat)) playerBoats.put(boat, new ArrayList<>());
                    playerBoats.get(boat).add(new Position(x, y, o));
                    placed = true;
                }
                attempts++;
            }
        }
        currentBoatIndex = boatsToPlace.size();
        updateBoatSelector();
        updateGrid();
    }

    private void enableManualPlacement() {
        playerBoats.clear();
        currentBoatIndex = 0;
        updateBoatSelector();
        updateGrid();
    }

    private void applyFixedPlacementTraps(){
        playerTraps.clear();
        int x = 3;
        int y = 4;

        for(TrapType trap : trapsToPlace){
            if(canPlaceTrap(trap, x+1, y+1)) {
                playerTraps.put(trap, new Position(x++, y++));
            }
            else{
                if(canPlaceTrap(trap, x+2, y+2)) {
                    playerTraps.put(trap, new Position(x += 2, y += 2));
                }
            }
        }
    }

    private void applyRandomPlacementTraps(){
        playerBoats.clear();
        Random rand = new Random();
        int gridSize = currentConfig.getGridSize();

        for(TrapType trap : trapsToPlace){
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(gridSize);
                int y = rand.nextInt(gridSize);
                if (canPlaceTrap(trap, x, y)) {
                    playerTraps.put(trap, new Position(x, y));
                    placed = true;
                }
                attempts++;
            }
        }
    }

    private void applyManualPlacementTraps(){

    }

    private void backToConfig() {
        placementView.dispose();
        configView.setVisible(true);
    }

    private void validatePlacement() {
        if (currentBoatIndex < boatsToPlace.size()) {
            placementView.showError("Placez tous les bateaux d'abord !");
            return;
        }
        placementView.showSuccess("Placement validé ! Prêt à jouer.");
        // TODO: Créer GamePlacement et passer à GameView

        currentPlacement = new GamePlacement();
    }




    // quit
    private void quit() {
        System.exit(0);
    }
}
