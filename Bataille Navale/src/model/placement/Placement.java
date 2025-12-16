package model.placement;

import model.contents.fleet.Boat;
import model.contents.fleet.BoatFactory;
import model.contents.traps.Trap;
import model.contents.traps.TrapFactory;
import model.contents.weapons.Weapon;
import model.contents.weapons.WeaponFactory;
import model.enums.*;
import model.game.GameConfig;
import model.game.GamePlacement;
import model.grid.Grid;
import model.grid.Position;

import java.util.*;

/**
 * MODÈLE de placement (logique métier pure)
 * Ne connaît PAS la vue, notifie via Observer pattern
 */
public class Placement {
    private final GameConfig config;
    private Grid playerGrid;
    private Grid robotGrid;
    private PlacementStrategy strategy;

    private List<BoatName> boatsToPlace = new ArrayList<>();
    private List<TrapType> trapsToPlace = new ArrayList<>();
    private List<WeaponType> weaponsToPlace = new ArrayList<>();

    private int boatIndex = 0;
    private int trapIndex = 0;
    private int weaponIndex = 0;
    private PlacementPhase phase = PlacementPhase.BOATS;
    private List<PlacementObserver> observers = new ArrayList<>();

    public Placement(GameConfig config) {
        this.config = config;

        this.playerGrid = new Grid(config.getGridSize(), config.getModeGame(), false);
        this.robotGrid = new Grid(config.getGridSize(), config.getModeGame(), true);
        this.strategy = new RandomPlacementStrategy();
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
            observer.onPhaseChanged(this.phase);
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
                this.phase = PlacementPhase.BOATS;
                break;
            case FIXED:
                placeTrapsFixed();
                this.phase = PlacementPhase.BOATS;
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
        if (this.boatIndex >= boatsToPlace.size()) return false;

        BoatName boatName = boatsToPlace.get(this.boatIndex);

        if (playerGrid.canPlaceBoat(Boat.getBoatSize(boatName), x, y, orient)) {
            Boat boat = createBoat(boatName);
            playerGrid.placeBoat(boat, x, y, orient);
            this.boatIndex++;

            if (this.boatIndex >= boatsToPlace.size()) {
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

    static Boat createBoat(BoatName boatName){
        BoatFactory boatFactory = new BoatFactory();
        switch (boatName) {
            case AIRCRAFT_CARRIER:
                return boatFactory.createAircraftCarrier();
            case CRUISER:
                return boatFactory.createCruiser();
            case DESTROYER:
                return boatFactory.createDestroyer();
            case SUBMARINE:
                return boatFactory.createSubmarine();
            case TORPEDO_BOAT:
                return boatFactory.createTorpedoBoat();
            default:
                throw new IllegalArgumentException("Type de bateau inconnu: " + boatName);
        }
    }

    static Trap createTrap(TrapType trapType){
        TrapFactory trapFactory = new TrapFactory();
        switch (trapType) {
            case TORNADO: return trapFactory.createTornado();
            case BLACKHOLE: return trapFactory.createBlackHole();
            default: throw new IllegalArgumentException("Type de piège inconnu: " + trapType);
        }
    }

    static Weapon createWeapon(WeaponType weaponType){
        WeaponFactory weaponFactory = new WeaponFactory();
        switch (weaponType) {
            case MISSILE: return weaponFactory.createMissile();
            case BOMB: return weaponFactory.createBomb();
            case SONAR: return weaponFactory.createSonar();
            default: throw new IllegalArgumentException("Type d'arme inconnu: " + weaponType);
        }
    }

    public boolean tryPlaceTrap(int x, int y) {
        if (this.trapIndex >= trapsToPlace.size()) return false;

        TrapType trapType = trapsToPlace.get(this.trapIndex);
        boolean canPlace = playerGrid.canPlaceTrapWeapon(x, y);

        if (canPlace) {
            Trap trap = createTrap(trapType);
            playerGrid.placeTrap(trap, x, y);
            this.trapIndex++;

            if (this.trapIndex >= trapsToPlace.size()) {
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
        if (this.weaponIndex >= weaponsToPlace.size()) return false;

        WeaponType weaponType = weaponsToPlace.get(this.weaponIndex);

        if (playerGrid.canPlaceTrapWeapon(x, y)) {
            Weapon weapon = createWeapon(weaponType);
            playerGrid.placeWeapon(weapon, x, y);
            this.weaponIndex++;

            if (this.weaponIndex >= weaponsToPlace.size()) {
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
            this.phase = PlacementPhase.TRAPS;
            notifyPhaseChanged();
        }
    }

    private void onTrapsComplete() {
        if (config.getModeGame() == ModeGame.ISLAND) {
            this.phase = PlacementPhase.WEAPONS;
            notifyPhaseChanged();
            notifyMessage("Pièges placés, placez les armes !", MessageType.INFO);
        } else {
            notifyMessage("Tous les pièges sont placés !", MessageType.INFO);
        }
    }

    // PLACEMENTS AUTOMATIQUES

    public void applyFixedPlacement() {
        this.strategy = new FixedPlacementStrategy();
        playerGrid.clearBoats();
        this.boatIndex = 0;

        if(config.getTrapMode() != TrapPlacement.FIXED){
            playerGrid.clear();
            this.trapIndex = 0;
            this.weaponIndex = 0;
        }

        if (!strategy.placeBoats(playerGrid, boatsToPlace)) {
            notifyMessage("Impossible de placer tous les bateaux !", MessageType.ERROR);
        }

        this.boatIndex = boatsToPlace.size();

        if (config.getTrapMode() == TrapPlacement.MANUAL) {
            this.phase = PlacementPhase.TRAPS;
            notifyPhaseChanged();
        }
        onBoatsComplete();

        notifySelectionChanged();
        notifyGridChanged();
    }

    public void applyRandomPlacement() {
        this.strategy = new RandomPlacementStrategy();
        playerGrid.clearBoats();
        this.boatIndex = 0;

        if(config.getTrapMode() != TrapPlacement.FIXED){
            playerGrid.clear();
            this.trapIndex = 0;
            this.weaponIndex = 0;
        }

        if (!strategy.placeBoats(playerGrid, boatsToPlace)) {
            notifyMessage("Impossible de placer tous les bateaux !", MessageType.ERROR);
        }

        this.boatIndex = boatsToPlace.size();

        if (config.getTrapMode() == TrapPlacement.MANUAL) {
            this.phase = PlacementPhase.TRAPS;
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
        reset();

        notifyPhaseChanged();
        notifySelectionChanged();
        notifyGridChanged();
    }

    private void reset(){
        this.boatIndex = 0;
        this.trapIndex = 0;
        this.weaponIndex = 0;
        this.phase = PlacementPhase.BOATS;
    }

    private void placeTrapsFixed() {
        strategy = new FixedPlacementStrategy();
        strategy.placeTraps(playerGrid, trapsToPlace);
        this.trapIndex = trapsToPlace.size();
    }

    private void placeTrapsRandom() {
        strategy = new RandomPlacementStrategy();
        strategy.placeTraps(playerGrid, trapsToPlace);
        this.trapIndex = trapsToPlace.size();
    }

    private void placeWeaponsRandom() {
        strategy = new RandomPlacementStrategy();
        strategy.placeWeapons(playerGrid, weaponsToPlace);
        this.weaponIndex = weaponsToPlace.size();
    }

    // VALIDATION

    public GamePlacement validateAndCreatePlacement(String robotPlacementMode) {
        if (this.boatIndex < boatsToPlace.size()) {
            notifyMessage("Placez tous les bateaux d'abord !", MessageType.ERROR);
            return null;
        }
        if (this.trapIndex < trapsToPlace.size()) {
            notifyMessage("Placez tous les pièges d'abord !", MessageType.ERROR);
            return null;
        }
        if (config.getModeGame() == ModeGame.ISLAND && this.weaponIndex < weaponsToPlace.size()) {
            notifyMessage("Placez toutes les armes d'abord !", MessageType.ERROR);
            return null;
        }

        placeRobot(robotPlacementMode);
        return new GamePlacement(playerGrid, robotGrid);
    }

    private void placeRobot(String mode) {
        robotGrid.clear();

        if(mode.equals("Random")) {
            strategy = new RandomPlacementStrategy();
        }
        else{
            strategy = new FixedPlacementStrategy();
        }
        strategy.placeBoats(robotGrid, boatsToPlace);

        if (config.getModeGame() == ModeGame.ISLAND) {
            this.strategy.placeTraps(robotGrid, trapsToPlace);
            this.strategy.placeWeapons(robotGrid, weaponsToPlace);
        } else {
            this.strategy.placeTraps(robotGrid, trapsToPlace);
        }
    }

    // PREVIEW

    public PreviewInfo getPreviewInfo(int x, int y, Orientation orientation) {
        if (x < 0 || y < 0) return null;

        switch (this.phase) {
            case BOATS:
                if (this.boatIndex < boatsToPlace.size()) {
                    return getBoatPreview(x, y, orientation);
                }
                break;
            case TRAPS:
                if (this.trapIndex < trapsToPlace.size()) {
                    return getTrapPreview(x, y);
                }
                break;
            case WEAPONS:
                if (this.weaponIndex < weaponsToPlace.size()) {
                    return getWeaponPreview(x, y);
                }
                break;
        }
        return null;
    }

    private PreviewInfo getBoatPreview(int x, int y, Orientation orient) {
        BoatName boat = boatsToPlace.get(this.boatIndex);
        int size = Boat.getBoatSize(boat);
        boolean canPlace = playerGrid.canPlaceBoat(size, x, y, orient);

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
        boolean canPlace = playerGrid.canPlaceTrapWeapon(x, y);

        List<Position> cells = new ArrayList<>();
        cells.add(new Position(x, y));
        return new PreviewInfo(cells, canPlace);
    }

    private PreviewInfo getWeaponPreview(int x, int y) {
        boolean canPlace = playerGrid.canPlaceTrapWeapon(x, y);

        List<Position> cells = new ArrayList<>();
        cells.add(new Position(x, y));
        return new PreviewInfo(cells, canPlace);
    }

    // ÉTAT POUR LA VUE

    public Grid getGridState() {
        return playerGrid;
    }

    public SelectionState getSelectionState() {
        return new SelectionState(
                getBoatOptions(),
                getTrapWeaponOptions(),
                this.phase == PlacementPhase.BOATS
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

        for (int i = this.trapIndex; i < trapsToPlace.size(); i++) {
            TrapType trap = trapsToPlace.get(i);
            String name = trap == TrapType.BLACKHOLE ? "Trou noir" : "Tornade";
            options.add(name + " (Piège)");
        }

        if (config.getModeGame() == ModeGame.ISLAND && this.trapIndex >= trapsToPlace.size()) {
            for (int i = this.weaponIndex; i < weaponsToPlace.size(); i++) {
                WeaponType weapon = weaponsToPlace.get(i);
                String name = weapon == WeaponType.BOMB ? "Bombe" : "Sonar";
                options.add(name + " (Arme)");
            }
        }

        if (options.isEmpty()) options.add("Tous placés !");
        return options;
    }

    public PlacementPhase getCurrentPhase() {
        return this.phase;
    }

    public boolean squareInIsland(int x, int y) {
        return this.playerGrid.squareIsInIsland(new Position(x, y));
    }
}