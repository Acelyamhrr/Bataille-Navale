package controller;

import model.enums.*;
import model.game.GameConfig;
import model.game.GamePlacement;
import model.grid.Position;
import view.PlacementView;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Controller pour le placement des bateaux et pièges.
 * Gère le placement pour le joueur ET le robot.
 */
public class PlacementController {
    private final PlacementView view;
    private final GameConfig config;

    // Données de placement joueur
    private Map<BoatName, List<Position>> playerBoats = new HashMap<>();
    private Map<TrapType, Position> playerTraps = new HashMap<>();
    private List<BoatName> boatsToPlace = new ArrayList<>();
    private List<TrapType> trapsToPlace = new ArrayList<>();
    private int currentBoatIndex = 0;
    private int currentTrapIndex = 0;
    private boolean currentPlacementBoat;

    // Données de placement robot
    private Map<BoatName, List<Position>> robotBoats = new HashMap<>();
    private Map<TrapType, Position> robotTraps = new HashMap<>();

    public PlacementController(PlacementView view, GameConfig config) {
        this.view = view;
        this.config = config;
    }

    /**
     * Initialise le placement selon le mode de piège configuré.
     */
    public void initializePlacement() {
        initBoatsToPlace();
        initTrapsToPlace();

        switch (config.getTrapMode()) {
            case RANDOM:
                currentPlacementBoat = true;
                updateBoatSelector();
                updateGrid();
                break;
            case FIXED:
                currentPlacementBoat = false;
                applyFixedPlacementTraps();
                updateGrid();
                currentPlacementBoat = true;
                updateBoatSelector();
                break;
            case MANUAL:
                currentPlacementBoat = true;
                updateBoatSelector();
                updateGrid();
                break;
        }
    }

    // clics

    public void onGridClick(int x, int y) {
        if (currentPlacementBoat) {
            handleBoatPlacement(x, y);
        } else {
            handleTrapPlacement(x, y);
        }
    }

    private void handleBoatPlacement(int x, int y) {
        if (currentBoatIndex >= boatsToPlace.size()) return;

        BoatName boat = boatsToPlace.get(currentBoatIndex);
        Orientation orient = view.isHorizontal() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

        if (canPlaceBoat(boat, x, y, orient, playerBoats, playerTraps)) {
            if (!playerBoats.containsKey(boat)) playerBoats.put(boat, new ArrayList<>());
            playerBoats.get(boat).add(new Position(x, y, orient));
            currentBoatIndex++;
            view.setInfoText("Bateau placé !");
            updateBoatSelector();
            updateGrid();

            if (currentBoatIndex >= boatsToPlace.size()) {
                view.setInfoText("Tous les bateaux sont placés !");
                currentPlacementBoat = false;
                if (config.getTrapMode() == TrapPlacement.RANDOM) {
                    applyRandomPlacementTraps();
                } else if (config.getTrapMode() == TrapPlacement.MANUAL) {
                    view.showSuccess("Placez les pièges.");
                }
            }
        } else {
            view.setInfoText("Placement invalide !");
        }
    }

    private void handleTrapPlacement(int x, int y) {
        if (currentTrapIndex >= trapsToPlace.size()) return;
        TrapType type = trapsToPlace.get(currentTrapIndex);

        if (canPlaceTrap(type, x, y, playerBoats, playerTraps)) {
            playerTraps.put(type, new Position(x, y));
            currentTrapIndex++;
            view.setInfoText("Piège placé !");
            updateGrid();

            if (currentTrapIndex >= trapsToPlace.size()) {
                view.setInfoText("Tous les pièges sont placés !");
            }
        } else {
            view.setInfoText("Placement invalide !");
        }
    }

    // modes

    public void applyFixedPlacement() {
        if (config.getTrapMode() == TrapPlacement.MANUAL || config.getTrapMode() == TrapPlacement.RANDOM) {
            playerTraps.clear();
            currentTrapIndex = 0;
            currentPlacementBoat = true;
        }

        playerBoats.clear();
        int y = 0;
        for (BoatName boat : boatsToPlace) {
            if (!playerBoats.containsKey(boat)) playerBoats.put(boat, new ArrayList<>());
            playerBoats.get(boat).add(new Position(0, y++, Orientation.HORIZONTAL));
        }
        currentBoatIndex = boatsToPlace.size();

        if (config.getTrapMode() == TrapPlacement.MANUAL) {
            currentPlacementBoat = false;
            view.showSuccess("Placez les pièges.");
        }

        updateBoatSelector();
        updateGrid();
    }

    public void applyRandomPlacement() {
        if (config.getTrapMode() == TrapPlacement.MANUAL || config.getTrapMode() == TrapPlacement.RANDOM) {
            playerTraps.clear();
            currentTrapIndex = 0;
            currentPlacementBoat = true;
        }

        playerBoats.clear();
        Random rand = new Random();
        int gridSize = config.getGridSize();

        for (BoatName boat : boatsToPlace) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(gridSize);
                int y = rand.nextInt(gridSize);
                Orientation o = rand.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
                if (canPlaceBoat(boat, x, y, o, playerBoats, playerTraps)) {
                    if (!playerBoats.containsKey(boat)) playerBoats.put(boat, new ArrayList<>());
                    playerBoats.get(boat).add(new Position(x, y, o));
                    placed = true;
                }
                attempts++;
            }
        }
        currentBoatIndex = boatsToPlace.size();

        if (config.getTrapMode() == TrapPlacement.MANUAL) {
            view.showSuccess("Placez les pièges.");
            currentPlacementBoat = false;
        }

        updateBoatSelector();
        updateGrid();
    }

    public void enableManualPlacement() {
        playerBoats.clear();
        currentBoatIndex = 0;
        currentPlacementBoat = true;

        if (config.getTrapMode() == TrapPlacement.MANUAL || config.getTrapMode() == TrapPlacement.RANDOM) {
            playerTraps.clear();
            currentTrapIndex = 0;
        }
        updateBoatSelector();
        updateGrid();
    }

    // pièges

    private void applyFixedPlacementTraps() {
        playerTraps.clear();
        int x = 3;
        int y = 3;

        for (TrapType trap : trapsToPlace) {
            if (canPlaceTrap(trap, x + 1, y + 1, playerBoats, playerTraps)) {
                playerTraps.put(trap, new Position(x++, y++));
            } else {
                if (canPlaceTrap(trap, x + 2, y + 2, playerBoats, playerTraps)) {
                    playerTraps.put(trap, new Position(x += 2, y += 2));
                }
            }
        }

        currentTrapIndex = trapsToPlace.size();
        updateGrid();
    }

    private void applyRandomPlacementTraps() {
        playerTraps.clear();
        Random rand = new Random();
        int gridSize = config.getGridSize();

        for (TrapType trap : trapsToPlace) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(gridSize);
                int y = rand.nextInt(gridSize);
                if (canPlaceTrap(trap, x, y, playerBoats, playerTraps)) {
                    playerTraps.put(trap, new Position(x, y));
                    placed = true;
                }
                attempts++;
            }
        }

        currentTrapIndex = trapsToPlace.size();
        updateGrid();
    }

    // placements robot.

    private void applyFixedBoatsRobot() {
        robotBoats.clear();
        int y = 0;
        for (BoatName boat : boatsToPlace) {
            if (canPlaceBoat(boat, 0, y + 1, Orientation.HORIZONTAL, robotBoats, robotTraps)) {
                if (!robotBoats.containsKey(boat)) robotBoats.put(boat, new ArrayList<>());
                robotBoats.get(boat).add(new Position(0, y++, Orientation.HORIZONTAL));
            } else {
                view.showError("Le bateau ne peut pas être placé là.");
            }
        }
    }

    private void applyRandomBoatsRobot() {
        robotBoats.clear();
        Random rand = new Random();
        int gridSize = config.getGridSize();

        for (BoatName boat : boatsToPlace) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(gridSize);
                int y = rand.nextInt(gridSize);
                Orientation o = rand.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
                if (canPlaceBoat(boat, x, y, o, robotBoats, robotTraps)) {
                    if (!robotBoats.containsKey(boat)) robotBoats.put(boat, new ArrayList<>());
                    robotBoats.get(boat).add(new Position(x, y, o));
                    placed = true;
                }
                attempts++;
            }
        }
    }

    private void applyFixedTrapsRobot() {
        robotTraps.clear();
        int x = 3;
        int y = 3;

        for (TrapType trap : trapsToPlace) {
            if (canPlaceTrap(trap, x + 1, y + 1, robotBoats, robotTraps)) {
                robotTraps.put(trap, new Position(x++, y++));
            } else {
                if (canPlaceTrap(trap, x + 2, y + 2, robotBoats, robotTraps)) {
                    robotTraps.put(trap, new Position(x += 2, y += 2));
                }
            }
        }
    }

    private void applyRandomTrapsRobot() {
        robotTraps.clear();
        Random rand = new Random();
        int gridSize = config.getGridSize();

        for (TrapType trap : trapsToPlace) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(gridSize);
                int y = rand.nextInt(gridSize);
                if (canPlaceTrap(trap, x, y, robotBoats, robotTraps)) {
                    robotTraps.put(trap, new Position(x, y));
                    placed = true;
                }
                attempts++;
            }
        }
    }

    // valider et créer

    /**
     * Valide le placement et crée le GamePlacement.
     * @return GamePlacement si valide, null sinon (avec affichage d'erreur)
     */
    public GamePlacement validateAndCreatePlacement() {
        if (currentBoatIndex < boatsToPlace.size()) {
            view.showError("Placez tous les bateaux d'abord !");
            return null;
        } else if (currentTrapIndex < trapsToPlace.size()) {
            view.showError("Placez tous les pièges d'abord !");
            return null;
        }

        // Créer placement robot
        String modeBoat = view.getModeBoat();
        if (config.getTrapMode() == TrapPlacement.FIXED) {
            applyFixedTrapsRobot();
            if (modeBoat.equals("Random")) {
                applyRandomBoatsRobot();
            } else {
                applyFixedBoatsRobot();
            }
        } else {
            if (modeBoat.equals("Random")) {
                applyRandomBoatsRobot();
            } else {
                applyFixedBoatsRobot();
            }
            applyRandomTrapsRobot();
        }

        // Créer GamePlacement
        GamePlacement placement = new GamePlacement();

        for (Map.Entry<BoatName, List<Position>> entry : playerBoats.entrySet()) {
            for (Position pos : entry.getValue()) {
                placement.setBoatPlacementPlayer(entry.getKey(), pos);
            }
        }

        for (Map.Entry<TrapType, Position> entry : playerTraps.entrySet()) {
            placement.setTrapPlacementPlayer(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<BoatName, List<Position>> entry : robotBoats.entrySet()) {
            for (Position pos : entry.getValue()) {
                placement.setBoatPlacementRobot(entry.getKey(), pos);
            }
        }

        for (Map.Entry<TrapType, Position> entry : robotTraps.entrySet()) {
            placement.setTrapPlacementRobot(entry.getKey(), entry.getValue());
        }

        return placement;
    }

    // mettre à jour l'affichage

    public void updateGrid() {
        int size = config.getGridSize();
        Color water = new Color(100, 150, 200);
        Color boat = new Color(80, 80, 80);
        Color trap = new Color(243, 88, 48);
        Color previewOk = new Color(100, 200, 100);
        Color previewBad = new Color(200, 100, 100);
        Color island = new Color(248, 193, 59);

        // Reset
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                view.setCellColor(x, y, water);
            }
        }

        // Squares island
        if(this.config.getModeGame() == ModeGame.ISLAND) {
            int ix = this.config.getGridSize() / 2 - 2;
            int iy = this.config.getGridSize() / 2 - 2;
            for (int i = ix; i < ix + 4; i++) {
                for (int j = iy; j < iy + 4; j++) {
                    view.setCellColor(i, j, island);
                }
            }
        }

        // Bateaux placés
        for (Map.Entry<BoatName, List<Position>> entry : playerBoats.entrySet()) {
            for (Position pos : entry.getValue()) {
                int boatSize = getBoatSize(entry.getKey());
                for (int i = 0; i < boatSize; i++) {
                    int bx = pos.getOrientation() == Orientation.HORIZONTAL ? pos.getX() + i : pos.getX();
                    int by = pos.getOrientation() == Orientation.VERTICAL ? pos.getY() + i : pos.getY();
                    view.setCellColor(bx, by, boat);
                }
            }
        }

        // Pièges placés
        for (Map.Entry<TrapType, Position> entry : playerTraps.entrySet()) {
            Position pos = entry.getValue();
            view.setCellColor(pos.getX(), pos.getY(), trap);
        }

        // Preview hover
        if (currentPlacementBoat) {
            previewBoatHover(size, previewOk, previewBad);
        } else {
            previewTrapHover(size, previewOk, previewBad);
        }

        if (currentBoatIndex >= boatsToPlace.size() && currentTrapIndex == 0 && config.getTrapMode() == TrapPlacement.RANDOM) {
            applyRandomPlacementTraps();
        }
    }

    private void previewBoatHover(int size, Color previewOk, Color previewBad) {
        int hx = view.getHoverX();
        int hy = view.getHoverY();
        if (hx >= 0 && hy >= 0 && currentBoatIndex < boatsToPlace.size()) {
            BoatName currentBoat = boatsToPlace.get(currentBoatIndex);
            Orientation orient = view.isHorizontal() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            boolean canPlace = canPlaceBoat(currentBoat, hx, hy, orient, playerBoats, playerTraps);
            int boatSize = getBoatSize(currentBoat);

            for (int i = 0; i < boatSize; i++) {
                int px = orient == Orientation.HORIZONTAL ? hx + i : hx;
                int py = orient == Orientation.VERTICAL ? hy + i : hy;
                if (px < size && py < size && !isCellOccupied(px, py, playerBoats, playerTraps)) {
                    view.setCellColor(px, py, canPlace ? previewOk : previewBad);
                }
            }
        }
    }

    private void previewTrapHover(int size, Color previewOk, Color previewBad) {
        int hx = view.getHoverX();
        int hy = view.getHoverY();
        if (hx >= 0 && hy >= 0 && currentTrapIndex < trapsToPlace.size()) {
            TrapType currentTrap = trapsToPlace.get(currentTrapIndex);
            boolean canPlace = canPlaceTrap(currentTrap, hx, hy, playerBoats, playerTraps);
            view.setCellColor(hx, hy, canPlace ? previewOk : previewBad);
        }
    }

    private void updateBoatSelector() {
        List<String> options = new ArrayList<>();
        String[] names = {"Porte-avions (5)", "Croiseur (4)", "Destroyer (3)", "Sous-marin (3)", "Torpilleur (2)"};
        int[] counts = config.getNumberBoat();
        int[] placed = new int[5];

        for (BoatName b : playerBoats.keySet()) placed[b.ordinal()]++;

        for (int i = 0; i < 5; i++) {
            int remaining = counts[i] - placed[i];
            if (remaining > 0) options.add(names[i] + " - " + remaining + " restant(s)");
        }

        if (options.isEmpty()) options.add("Tous placés !");
        view.setBoatOptions(options.toArray(new String[0]));
    }

    // init

    private void initBoatsToPlace() {
        boatsToPlace.clear();
        playerBoats.clear();
        int[] counts = config.getNumberBoat();
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

    // valider placements

    private boolean canPlaceBoat(BoatName boat, int x, int y, Orientation orient, Map<BoatName, List<Position>> boats, Map<TrapType, Position> traps) {
        int size = getBoatSize(boat);
        int gridSize = config.getGridSize();

        if (orient == Orientation.HORIZONTAL && x + size > gridSize) return false;
        if (orient == Orientation.VERTICAL && y + size > gridSize) return false;

        for (int i = 0; i < size; i++) {
            int cx = orient == Orientation.HORIZONTAL ? x + i : x;
            int cy = orient == Orientation.VERTICAL ? y + i : y;

            if (squareInIsland(cx, cy)) return false;

            if (isCellOccupied(cx, cy, boats, traps)) return false;
        }
        return true;
    }

    private boolean squareInIsland(int cx, int cy) {
        if(this.config.getModeGame() == ModeGame.ISLAND) {
            int ix = this.config.getGridSize() / 2 - 2;
            int iy = this.config.getGridSize() / 2 - 2;

            return (cx >= ix && cx < ix + 4) && (cy >= iy && cy < iy + 4);
        }
        return false;
    }

    private boolean canPlaceTrap(TrapType type, int x, int y, Map<BoatName, List<Position>> boats, Map<TrapType, Position> traps) {
        int gridSize = config.getGridSize();

        if (x >= gridSize || y >= gridSize) return false;

        if (squareInIsland(x, y)) return false;

        return !isCellOccupied(x, y, boats, traps);
    }

    private boolean isCellOccupied(int x, int y, Map<BoatName, List<Position>> boats, Map<TrapType, Position> traps) {
        // Check boats
        for (Map.Entry<BoatName, List<Position>> entry : boats.entrySet()) {
            for (Position pos : entry.getValue()) {
                int size = getBoatSize(entry.getKey());
                for (int i = 0; i < size; i++) {
                    int bx = pos.getOrientation() == Orientation.HORIZONTAL ? pos.getX() + i : pos.getX();
                    int by = pos.getOrientation() == Orientation.VERTICAL ? pos.getY() + i : pos.getY();
                    if (bx == x && by == y) return true;
                }
            }
        }

        // Check traps
        for (Map.Entry<TrapType, Position> entry : traps.entrySet()) {
            Position pos = entry.getValue();
            if (pos.getX() == x && pos.getY() == y) return true;
        }

        return false;
    }

    private int getBoatSize(BoatName type) {
        switch (type) {
            case AIRCRAFT_CARRIER:
                return 5;
            case CRUISER:
                return 4;
            case DESTROYER:
                return 3;
            case SUBMARINE:
                return 3;
            case TORPEDO_BOAT:
                return 2;
            default:
                return 0;
        }
    }
}
