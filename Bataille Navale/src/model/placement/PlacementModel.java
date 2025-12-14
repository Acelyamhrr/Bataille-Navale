package model.placement;

import model.contents.fleet.Boat;
import model.enums.*;
import model.game.GameConfig;
import model.game.GamePlacement;
import model.grid.Position;

import java.util.*;

/**
 * MODÈLE de placement (logique métier pure)
 * Ne connaît PAS la vue, notifie via Observer pattern
 */
public class PlacementModel {
    private final GameConfig config;
    private final PlacementGrid playerGrid;
    private final PlacementGrid robotGrid;
    private final PlacementValidator validator;
    private final PlacementStrategy randomStrategy;
    private final PlacementStrategy balancedStrategy;

    private List<BoatName> boatsToPlace = new ArrayList<>();
    private List<TrapType> trapsToPlace = new ArrayList<>();
    private List<WeaponType> weaponsToPlace = new ArrayList<>();

    private PlacementState state = new PlacementState();
    private List<PlacementObserver> observers = new ArrayList<>();

    public PlacementModel(GameConfig config) {
        this.config = config;
        boolean hasIsland = config.getModeGame() == ModeGame.ISLAND;

        this.playerGrid = new PlacementGrid(config.getGridSize(), hasIsland);
        this.robotGrid = new PlacementGrid(config.getGridSize(), hasIsland);
        this.validator = new PlacementValidator();
        this.randomStrategy = new RandomPlacementStrategy(validator, hasIsland);
        this.balancedStrategy = new BalancedPlacementStrategy(validator, hasIsland);
    }

    // OBSERVER

    public void addObserver(PlacementObserver observer) {
        observers.add(observer);
    }

    private void notifyGridChanged() {
        for(PlacementObserver observer : observers){
            observer.onGridChanged(getGridState());
        }
    }

    private void notifyPhaseChanged() {
        for(PlacementObserver observer : observers){
            observer.onPhaseChanged(state.getPhase());
        }
    }

    private void notifyMessage(String message, MessageType type) {
        for(PlacementObserver observer : observers){
            observer.onMessage(message, type);
        }
    }

    private void notifySelectionChanged() {
        for(PlacementObserver observer : observers){
            observer.onSelectionChanged(getSelectionState());
        }
    }

    // INITIALISATION

    public void initialize() {
        initElementsToPlace();

        switch (config.getTrapMode()) {
            case RANDOM, MANUAL:
                state.setPhase(PlacementPhase.BOATS);
                break;
            case FIXED:
                placeTrapsFixed();
                state.setPhase(PlacementPhase.BOATS);
                break;
        }

        notifyPhaseChanged();
        notifySelectionChanged();
        notifyGridChanged();
    }

    private void initElementsToPlace() {
        // Bateaux
        boatsToPlace.clear();
        int[] counts = config.getNumberBoat();
        BoatName[] types = {BoatName.AIRCRAFT_CARRIER, BoatName.CRUISER,
                BoatName.DESTROYER, BoatName.SUBMARINE, BoatName.TORPEDO_BOAT};
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < counts[i]; j++) {
                boatsToPlace.add(types[i]);
            }
        }

        // Pièges
        trapsToPlace.clear();
        trapsToPlace.add(TrapType.BLACKHOLE);
        trapsToPlace.add(TrapType.TORNADO);

        // Armes
        weaponsToPlace.clear();
        if (config.getModeGame() == ModeGame.ISLAND) {
            weaponsToPlace.add(WeaponType.BOMB);
            weaponsToPlace.add(WeaponType.SONAR);
        }
    }

    // ACTIONS DE PLACEMENT

    public boolean tryPlaceBoat(int x, int y, Orientation orient) {
        if (state.boatIndex >= boatsToPlace.size()) return false;

        BoatName boat = boatsToPlace.get(state.boatIndex);

        if (validator.canPlaceBoat(playerGrid, boat, x, y, orient, config.getGridSize())) {
            playerGrid.addBoat(boat, new Position(x, y, orient));
            state.boatIndex++;

            if (state.boatIndex >= boatsToPlace.size()) {
                onBoatsComplete();
            }

            notifySelectionChanged();
            notifyGridChanged();
            return true;
        } else {
            notifyMessage("Placement invalide !", MessageType.ERROR);
            return false;
        }
    }

    public boolean tryPlaceTrap(int x, int y) {
        if (state.trapIndex >= trapsToPlace.size()) return false;

        TrapType trap = trapsToPlace.get(state.trapIndex);
        boolean canPlace = config.getModeGame() == ModeGame.ISLAND
                ? validator.canPlaceOnIsland(playerGrid, x, y, config.getGridSize())
                : validator.canPlaceTrap(playerGrid, x, y, config.getGridSize());

        if (canPlace) {
            playerGrid.addTrap(trap, new Position(x, y));
            state.trapIndex++;

            if (state.trapIndex >= trapsToPlace.size()) {
                onTrapsComplete();
            }

            notifySelectionChanged();
            notifyGridChanged();
            return true;
        } else {
            notifyMessage("Placement invalide !", MessageType.ERROR);
            return false;
        }
    }

    public boolean tryPlaceWeapon(int x, int y) {
        if (state.weaponIndex >= weaponsToPlace.size()) return false;

        WeaponType weapon = weaponsToPlace.get(state.weaponIndex);

        if (validator.canPlaceOnIsland(playerGrid, x, y, config.getGridSize())) {
            playerGrid.addWeapon(weapon, new Position(x, y));
            state.weaponIndex++;

            if (state.weaponIndex >= weaponsToPlace.size()) {
                notifyMessage("Tous les éléments sont placés !", MessageType.INFO);
            }

            notifySelectionChanged();
            notifyGridChanged();
            return true;
        } else {
            notifyMessage("Placement invalide !", MessageType.ERROR);
            return false;
        }
    }

    // TRANSITIONS

    private void onBoatsComplete() {
        notifyMessage("Tous les bateaux sont placés !", MessageType.INFO);

        if (config.getTrapMode() == TrapPlacement.RANDOM) {
            placeTrapsRandom();
            if (config.getModeGame() == ModeGame.ISLAND) {
                placeWeaponsRandom();
            }
        } else {
            state.setPhase(PlacementPhase.TRAPS);
            notifyPhaseChanged();
        }
    }

    private void onTrapsComplete() {
        if (config.getModeGame() == ModeGame.ISLAND) {
            state.setPhase(PlacementPhase.WEAPONS);
            notifyPhaseChanged();
            notifyMessage("Pièges placés, placez les armes !", MessageType.INFO);
        } else {
            notifyMessage("Tous les pièges sont placés !", MessageType.INFO);
        }
    }

    // PLACEMENTS AUTOMATIQUES

    public void applyFixedPlacement() {
        playerGrid.clearBoats();
        state.boatIndex = 0;

        if(config.getTrapMode() != TrapPlacement.FIXED){
            playerGrid.clear();
            state.trapIndex = 0;
            state.weaponIndex = 0;
        }

        if (!balancedStrategy.placeBoats(playerGrid, boatsToPlace, config.getGridSize())) {
            notifyMessage("Impossible de placer tous les bateaux !", MessageType.ERROR);
        }

        state.boatIndex = boatsToPlace.size();

        if (config.getTrapMode() == TrapPlacement.MANUAL) {
            state.setPhase(PlacementPhase.TRAPS);
            notifyPhaseChanged();
        }
        onBoatsComplete();

        notifySelectionChanged();
        notifyGridChanged();
    }

    public void applyRandomPlacement() {
        playerGrid.clearBoats();
        state.boatIndex = 0;

        if(config.getTrapMode() != TrapPlacement.FIXED){
            playerGrid.clear();
            state.trapIndex = 0;
            state.weaponIndex = 0;
        }

        if (!randomStrategy.placeBoats(playerGrid, boatsToPlace, config.getGridSize())) {
            notifyMessage("Impossible de placer tous les bateaux !", MessageType.ERROR);
        }

        state.boatIndex = boatsToPlace.size();

        if (config.getTrapMode() == TrapPlacement.MANUAL) {
            state.setPhase(PlacementPhase.TRAPS);
            notifyPhaseChanged();
        }
        onBoatsComplete();

        notifySelectionChanged();
        notifyGridChanged();
    }

    public void enableManualPlacement() {
        playerGrid.clearBoats();
        playerGrid.clearTraps();
        playerGrid.clearWeapons();
        state.reset();

        notifyPhaseChanged();
        notifySelectionChanged();
        notifyGridChanged();
    }

    private void placeTrapsFixed() {
        balancedStrategy.placeTraps(playerGrid, trapsToPlace, config.getGridSize());
        state.trapIndex = trapsToPlace.size();
    }

    private void placeTrapsRandom() {
        randomStrategy.placeTraps(playerGrid, trapsToPlace, config.getGridSize());
        state.trapIndex = trapsToPlace.size();
    }

    private void placeWeaponsRandom() {
        randomStrategy.placeWeapons(playerGrid, weaponsToPlace, config.getGridSize());
        state.weaponIndex = weaponsToPlace.size();
    }

    // VALIDATION

    public GamePlacement validateAndCreatePlacement(String robotPlacementMode) {
        if (state.boatIndex < boatsToPlace.size()) {
            notifyMessage("Placez tous les bateaux d'abord !", MessageType.ERROR);
            return null;
        }
        if (state.trapIndex < trapsToPlace.size()) {
            notifyMessage("Placez tous les pièges d'abord !", MessageType.ERROR);
            return null;
        }
        if (config.getModeGame() == ModeGame.ISLAND && state.weaponIndex < weaponsToPlace.size()) {
            notifyMessage("Placez toutes les armes d'abord !", MessageType.ERROR);
            return null;
        }

        placeRobot(robotPlacementMode);
        return createGamePlacement();
    }

    private void placeRobot(String mode) {
        robotGrid.clear();

        PlacementStrategy strategy = mode.equals("Random") ? randomStrategy : balancedStrategy;
        strategy.placeBoats(robotGrid, boatsToPlace, config.getGridSize());

        if (config.getModeGame() == ModeGame.ISLAND) {
            randomStrategy.placeTraps(robotGrid, trapsToPlace, config.getGridSize());
            randomStrategy.placeWeapons(robotGrid, weaponsToPlace, config.getGridSize());
        } else {
            randomStrategy.placeTraps(robotGrid, trapsToPlace, config.getGridSize());
        }
    }

    private GamePlacement createGamePlacement() {
        GamePlacement placement = new GamePlacement();

        //Player
        placement.setBoatsPlacementPlayer(playerGrid.getBoats());
        placement.setTrapsPlacementsPlayer(playerGrid.getTraps());
        placement.setWeaponsPlacementPlayer(playerGrid.getWeapons());

        //Robot
        placement.setBoatsPlacementRobot(robotGrid.getBoats());
        placement.setTrapsPlacementRobot(robotGrid.getTraps());
        placement.setWeaponsPlacementRobot(robotGrid.getWeapons());

        return placement;
    }

    // PREVIEW

    public PreviewInfo getPreviewInfo(int x, int y, Orientation orientation) {
        if (x < 0 || y < 0) return null;

        switch (state.getPhase()) {
            case BOATS:
                if (state.boatIndex < boatsToPlace.size()) {
                    return getBoatPreview(x, y, orientation);
                }
                break;
            case TRAPS:
                if (state.trapIndex < trapsToPlace.size()) {
                    return getTrapPreview(x, y);
                }
                break;
            case WEAPONS:
                if (state.weaponIndex < weaponsToPlace.size()) {
                    return getWeaponPreview(x, y);
                }
                break;
        }
        return null;
    }

    private PreviewInfo getBoatPreview(int x, int y, Orientation orient) {
        BoatName boat = boatsToPlace.get(state.boatIndex);
        boolean canPlace = validator.canPlaceBoat(playerGrid, boat, x, y, orient, config.getGridSize());
        int size = Boat.getBoatSize(boat);

        List<Position> cells = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int px = orient == Orientation.HORIZONTAL ? x + i : x;
            int py = orient == Orientation.VERTICAL ? y + i : y;
            if (px < config.getGridSize() && py < config.getGridSize() && !playerGrid.isCellOccupied(px, py)) {
                cells.add(new Position(px, py));
            }
        }

        return new PreviewInfo(cells, canPlace);
    }

    private PreviewInfo getTrapPreview(int x, int y) {
        boolean canPlace = config.getModeGame() == ModeGame.ISLAND
                ? validator.canPlaceOnIsland(playerGrid, x, y, config.getGridSize())
                : validator.canPlaceTrap(playerGrid, x, y, config.getGridSize());

        List<Position> cells = new ArrayList<>();
        cells.add(new Position(x, y));
        return new PreviewInfo(cells, canPlace);
    }

    private PreviewInfo getWeaponPreview(int x, int y) {
        boolean canPlace = validator.canPlaceOnIsland(playerGrid, x, y, config.getGridSize());

        List<Position> cells = new ArrayList<>();
        cells.add(new Position(x, y));
        return new PreviewInfo(cells, canPlace);
    }

    // ÉTAT POUR LA VUE

    public PlacementGrid getGridState() {
        return playerGrid;
    }

    public SelectionState getSelectionState() {
        return new SelectionState(
                getBoatOptions(),
                getTrapWeaponOptions(),
                state.getPhase() == PlacementPhase.BOATS,
                state.getPhase() != PlacementPhase.BOATS
        );
    }

    private List<String> getBoatOptions() {
        List<String> options = new ArrayList<>();
        String[] names = {"Porte-avions (5)", "Croiseur (4)", "Destroyer (3)", "Sous-marin (3)", "Torpilleur (2)"};
        int[] counts = config.getNumberBoat();

        for (int i = 0; i < 5; i++) {
            BoatName boat = BoatName.values()[i];
            int placed = playerGrid.getBoatCount(boat);
            int remaining = counts[i] - placed;
            if (remaining > 0) {
                options.add(names[i] + " - " + remaining + " restant(s)");
            }
        }

        if (options.isEmpty()) options.add("Tous placés !");
        return options;
    }

    private List<String> getTrapWeaponOptions() {
        List<String> options = new ArrayList<>();

        for (int i = state.trapIndex; i < trapsToPlace.size(); i++) {
            TrapType trap = trapsToPlace.get(i);
            String name = trap == TrapType.BLACKHOLE ? "Trou noir" : "Tornade";
            options.add(name + " (Piège)");
        }

        if (config.getModeGame() == ModeGame.ISLAND && state.trapIndex >= trapsToPlace.size()) {
            for (int i = state.weaponIndex; i < weaponsToPlace.size(); i++) {
                WeaponType weapon = weaponsToPlace.get(i);
                String name = weapon == WeaponType.BOMB ? "Bombe" : "Sonar";
                options.add(name + " (Arme)");
            }
        }

        if (options.isEmpty()) options.add("Tous placés !");
        return options;
    }

    public PlacementPhase getCurrentPhase() {
        return state.getPhase();
    }

    public boolean squareInIsland(int x, int y) {
        return this.playerGrid.isInIsland(x, y);
    }
}