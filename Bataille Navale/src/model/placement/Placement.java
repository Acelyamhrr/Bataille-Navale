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
    private final GameConfig _config;
    private Grid _playerGrid;
    private Grid _robotGrid;
    private PlacementStrategy _strategy;

    private List<BoatName> _boatsToPlace = new ArrayList<>();
    private List<TrapType> _trapsToPlace = new ArrayList<>();
    private List<WeaponType> _weaponsToPlace = new ArrayList<>();

    private int _boatIndex = 0;
    private int _trapIndex = 0;
    private int _weaponIndex = 0;
    private PlacementPhase _phase = PlacementPhase.BOATS;
    private List<PlacementObserver> _observers = new ArrayList<>();

    public Placement(GameConfig config) {
        this._config = config;

        this._playerGrid = new Grid(config.getGridSize(), config.getModeGame(), false);
        this._robotGrid = new Grid(config.getGridSize(), config.getModeGame(), true);
        this._strategy = new RandomPlacementStrategy();
    }

    // OBSERVER

    public void addObserver(PlacementObserver observer) {
        _observers.add(observer);
    }

    private void notifyGridChanged() {
        for(PlacementObserver observer : _observers){
            observer.onGridChanged(getGridState());
        }
    }

    private void notifyPhaseChanged() {
        for(PlacementObserver observer : _observers){
            observer.onPhaseChanged(this._phase);
        }
    }

    private void notifyMessage(String message, MessageType type) {
        for(PlacementObserver observer : _observers){
            observer.onMessage(message, type);
        }
    }

    private void notifySelectionChanged() {
        for(PlacementObserver observer : _observers){
            observer.onSelectionChanged(getSelectionState());
        }
    }

    // INITIALISATION

    public void initialize() {
        initElementsToPlace();

        switch (_config.getTrapMode()) {
            case RANDOM, MANUAL:
                this._phase = PlacementPhase.BOATS;
                break;
            case FIXED:
                placeTrapsFixed();
                this._phase = PlacementPhase.BOATS;
                break;
        }

        notifyPhaseChanged();
        notifySelectionChanged();
        notifyGridChanged();
    }

    private void initElementsToPlace() {
        // Bateaux
        _boatsToPlace.clear();
        int[] counts = _config.getNumberBoat();
        BoatName[] types = {BoatName.AIRCRAFT_CARRIER, BoatName.CRUISER,
                BoatName.DESTROYER, BoatName.SUBMARINE, BoatName.TORPEDO_BOAT};
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < counts[i]; j++) {
                _boatsToPlace.add(types[i]);
            }
        }

        // Pièges
        _trapsToPlace.clear();
        _trapsToPlace.add(TrapType.BLACKHOLE);
        _trapsToPlace.add(TrapType.TORNADO);

        // Armes
        _weaponsToPlace.clear();
        if (_config.getModeGame() == ModeGame.ISLAND) {
            _weaponsToPlace.add(WeaponType.BOMB);
            _weaponsToPlace.add(WeaponType.SONAR);
        }
    }

    // ACTIONS DE PLACEMENT

    public boolean tryPlaceBoat(int x, int y, Orientation orient) {
        if (this._boatIndex >= _boatsToPlace.size()) return false;

        BoatName boatName = _boatsToPlace.get(this._boatIndex);

        if (_playerGrid.canPlaceBoat(Boat.getBoatSize(boatName), x, y, orient)) {
            Boat boat = createBoat(boatName);
            _playerGrid.placeBoat(boat, x, y, orient);
            this._boatIndex++;

            if (this._boatIndex >= _boatsToPlace.size()) {
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

    public static Boat createBoat(BoatName boatName){
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

    public static Trap createTrap(TrapType trapType){
        TrapFactory trapFactory = new TrapFactory();
        switch (trapType) {
            case TORNADO: return trapFactory.createTornado();
            case BLACKHOLE: return trapFactory.createBlackHole();
            default: throw new IllegalArgumentException("Type de piège inconnu: " + trapType);
        }
    }

    public static Weapon createWeapon(WeaponType weaponType){
        WeaponFactory weaponFactory = new WeaponFactory();
        switch (weaponType) {
            case MISSILE: return weaponFactory.createMissile();
            case BOMB: return weaponFactory.createBomb();
            case SONAR: return weaponFactory.createSonar();
            default: throw new IllegalArgumentException("Type d'arme inconnu: " + weaponType);
        }
    }

    public boolean tryPlaceTrap(int x, int y) {
        if (this._trapIndex >= _trapsToPlace.size()) return false;

        TrapType trapType = _trapsToPlace.get(this._trapIndex);
        boolean canPlace = _playerGrid.canPlaceTrapWeapon(x, y);

        if (canPlace) {
            Trap trap = createTrap(trapType);
            _playerGrid.placeTrap(trap, x, y);
            this._trapIndex++;

            if (this._trapIndex >= _trapsToPlace.size()) {
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
        if (this._weaponIndex >= _weaponsToPlace.size()) return false;

        WeaponType weaponType = _weaponsToPlace.get(this._weaponIndex);

        if (_playerGrid.canPlaceTrapWeapon(x, y)) {
            Weapon weapon = createWeapon(weaponType);
            _playerGrid.placeWeapon(weapon, x, y);
            this._weaponIndex++;

            if (this._weaponIndex >= _weaponsToPlace.size()) {
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

        if (_config.getTrapMode() == TrapPlacement.RANDOM) {
            placeTrapsRandom();
            if (_config.getModeGame() == ModeGame.ISLAND) {
                placeWeaponsRandom();
            }
        } else {
            this._phase = PlacementPhase.TRAPS;
            notifyPhaseChanged();
        }
    }

    private void onTrapsComplete() {
        if (_config.getModeGame() == ModeGame.ISLAND) {
            this._phase = PlacementPhase.WEAPONS;
            notifyPhaseChanged();
            notifyMessage("Pièges placés, placez les armes !", MessageType.INFO);
        } else {
            notifyMessage("Tous les pièges sont placés !", MessageType.INFO);
        }
    }

    // PLACEMENTS AUTOMATIQUES

    public void applyFixedPlacement() {
        this._strategy = new FixedPlacementStrategy();

        // Nettoyer complètement avant placement
        if(_config.getTrapMode() != TrapPlacement.FIXED){
            _playerGrid.clear();
            this._trapIndex = 0;
            this._weaponIndex = 0;
        } else {
            _playerGrid.clearBoats();
        }

        this._boatIndex = 0;

        if (!_strategy.placeBoats(_playerGrid, _boatsToPlace)) {
            notifyMessage("Impossible de placer tous les bateaux !", MessageType.ERROR);
            return;
        }

        this._boatIndex = _boatsToPlace.size();

        if (_config.getTrapMode() == TrapPlacement.MANUAL) {
            this._phase = PlacementPhase.TRAPS;
            notifyPhaseChanged();
        }
        onBoatsComplete();

        notifySelectionChanged();
        notifyGridChanged();
    }

    public void applyRandomPlacement() {
        this._strategy = new RandomPlacementStrategy();
        _playerGrid.clearBoats();
        this._boatIndex = 0;

        if(_config.getTrapMode() != TrapPlacement.FIXED){
            _playerGrid.clear();
            this._trapIndex = 0;
            this._weaponIndex = 0;
        } else {
            _playerGrid.clearBoats();
        }

        if (!_strategy.placeBoats(_playerGrid, _boatsToPlace)) {
            notifyMessage("Impossible de placer tous les bateaux !", MessageType.ERROR);
        }

        this._boatIndex = _boatsToPlace.size();

        if (_config.getTrapMode() == TrapPlacement.MANUAL) {
            this._phase = PlacementPhase.TRAPS;
            notifyPhaseChanged();
        }
        onBoatsComplete();

        notifySelectionChanged();
        notifyGridChanged();
    }

    public void enableManualPlacement() {
        _playerGrid.clearBoats();
        _playerGrid.clearTraps();
        _playerGrid.clearWeapons();
        reset();

        notifyPhaseChanged();
        notifySelectionChanged();
        notifyGridChanged();
    }

    private void reset(){
        this._boatIndex = 0;
        this._trapIndex = 0;
        this._weaponIndex = 0;
        this._phase = PlacementPhase.BOATS;
    }

    private void placeTrapsFixed() {
        _strategy = new FixedPlacementStrategy();
        _strategy.placeTraps(_playerGrid, _trapsToPlace);
        this._trapIndex = _trapsToPlace.size();
    }

    private void placeTrapsRandom() {
        _strategy = new RandomPlacementStrategy();
        _strategy.placeTraps(_playerGrid, _trapsToPlace);
        this._trapIndex = _trapsToPlace.size();
    }

    private void placeWeaponsRandom() {
        _strategy = new RandomPlacementStrategy();
        _strategy.placeWeapons(_playerGrid, _weaponsToPlace);
        this._weaponIndex = _weaponsToPlace.size();
    }

    // VALIDATION

    public GamePlacement validateAndCreatePlacement(String robotPlacementMode) {
        if (this._boatIndex < _boatsToPlace.size()) {
            notifyMessage("Placez tous les bateaux d'abord !", MessageType.ERROR);
            return null;
        }
        if (this._trapIndex < _trapsToPlace.size()) {
            notifyMessage("Placez tous les pièges d'abord !", MessageType.ERROR);
            return null;
        }
        if (_config.getModeGame() == ModeGame.ISLAND && this._weaponIndex < _weaponsToPlace.size()) {
            notifyMessage("Placez toutes les armes d'abord !", MessageType.ERROR);
            return null;
        }

        placeRobot(robotPlacementMode);
        return new GamePlacement(_playerGrid, _robotGrid);
    }

    private void placeRobot(String mode) {
        _robotGrid.clear();

        if(mode.equals("Random")) {
            _strategy = new RandomPlacementStrategy();
        }
        else{
            _strategy = new FixedPlacementStrategy();
        }
        _strategy.placeBoats(_robotGrid, _boatsToPlace);

        if (_config.getModeGame() == ModeGame.ISLAND) {
            this._strategy.placeTraps(_robotGrid, _trapsToPlace);
            this._strategy.placeWeapons(_robotGrid, _weaponsToPlace);
        } else {
            this._strategy.placeTraps(_robotGrid, _trapsToPlace);
        }
    }

    // PREVIEW

    public PreviewInfo getPreviewInfo(int x, int y, Orientation orientation) {
        if (x < 0 || y < 0) return null;

        switch (this._phase) {
            case BOATS:
                if (this._boatIndex < _boatsToPlace.size()) {
                    return getBoatPreview(x, y, orientation);
                }
                break;
            case TRAPS:
                if (this._trapIndex < _trapsToPlace.size()) {
                    return getTrapPreview(x, y);
                }
                break;
            case WEAPONS:
                if (this._weaponIndex < _weaponsToPlace.size()) {
                    return getWeaponPreview(x, y);
                }
                break;
        }
        return null;
    }

    private PreviewInfo getBoatPreview(int x, int y, Orientation orient) {
        BoatName boat = _boatsToPlace.get(this._boatIndex);
        int size = Boat.getBoatSize(boat);
        boolean canPlace = _playerGrid.canPlaceBoat(size, x, y, orient);

        List<Position> cells = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int px = orient == Orientation.HORIZONTAL ? x + i : x;
            int py = orient == Orientation.VERTICAL ? y + i : y;
            if (px < _config.getGridSize() && py < _config.getGridSize() && !_playerGrid.isCellOccupied(px, py)) {
                cells.add(new Position(px, py));
            }
        }

        return new PreviewInfo(cells, canPlace);
    }

    private PreviewInfo getTrapPreview(int x, int y) {
        boolean canPlace = _playerGrid.canPlaceTrapWeapon(x, y);

        List<Position> cells = new ArrayList<>();
        cells.add(new Position(x, y));
        return new PreviewInfo(cells, canPlace);
    }

    private PreviewInfo getWeaponPreview(int x, int y) {
        boolean canPlace = _playerGrid.canPlaceTrapWeapon(x, y);

        List<Position> cells = new ArrayList<>();
        cells.add(new Position(x, y));
        return new PreviewInfo(cells, canPlace);
    }

    // ÉTAT POUR LA VUE

    public Grid getGridState() {
        return _playerGrid;
    }

    public SelectionState getSelectionState() {
        return new SelectionState(
                getBoatOptions(),
                getTrapWeaponOptions(),
                this._phase == PlacementPhase.BOATS
        );
    }

    private List<String> getBoatOptions() {
        List<String> options = new ArrayList<>();
        String[] names = {"Porte-avions (5)", "Croiseur (4)", "Destroyer (3)", "Sous-marin (3)", "Torpilleur (2)"};
        int[] counts = _config.getNumberBoat();

        for (int i = 0; i < 5; i++) {
            BoatName boat = BoatName.values()[i];
            int placed = _playerGrid.getBoatCount(boat);
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

        for (int i = this._trapIndex; i < _trapsToPlace.size(); i++) {
            TrapType trap = _trapsToPlace.get(i);
            String name = trap == TrapType.BLACKHOLE ? "Trou noir" : "Tornade";
            options.add(name + " (Piège)");
        }

        if (_config.getModeGame() == ModeGame.ISLAND && this._trapIndex >= _trapsToPlace.size()) {
            for (int i = this._weaponIndex; i < _weaponsToPlace.size(); i++) {
                WeaponType weapon = _weaponsToPlace.get(i);
                String name = weapon == WeaponType.BOMB ? "Bombe" : "Sonar";
                options.add(name + " (Arme)");
            }
        }

        if (options.isEmpty()) options.add("Tous placés !");
        return options;
    }

    public PlacementPhase getCurrentPhase() {
        return this._phase;
    }

    public boolean squareInIsland(int x, int y) {
        return this._playerGrid.squareIsInIsland(new Position(x, y));
    }
}