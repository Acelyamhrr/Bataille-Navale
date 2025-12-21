package model.grid;

import model.contents.fleet.Boat;
import model.contents.traps.Trap;
import model.contents.weapons.Weapon;
import model.enums.*;
import model.placement.Placement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Grid {
    private int _gridSize;
    private Map<Position, Square> _squares;
    private ModeGame _mode;
    private Island _island;
    private boolean _robot;
    private Map<BoatName, Integer> _boatsCount;
    private List<Boat> _allBoats = new ArrayList<>();
    private Map<WeaponType, Position> _weapons = new HashMap<>();
    private Map<TrapType, Position> _traps = new HashMap<>();

    public Grid(int size, ModeGame mode, boolean robot) {
        this._gridSize = size;
        this._mode = mode;
        this._squares = new HashMap<>();
        this._robot = robot;
        this._boatsCount = new HashMap<>();

        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                Position pos = new Position(i, j);
                this._squares.put(pos, new Square(pos, robot));
            }
        }

        if(mode == ModeGame.ISLAND){
            int x = size/2 -2;
            int y = size/2 -2;
            this._island = new Island(new Position(x, y));

            for(int i=x; i<x+4; i++){
                for(int j=y; j<y+4; j++){
                    Position pos = new Position(i, j);
                    this._squares.get(pos).setIsland();
                }
            }
        }
    }

    /**
     * Vérifie si un bateau peut être placé à une position donnée
     */
    public boolean canPlaceBoat(int size, int x, int y, Orientation orientation){
        // Vérifier que le bateau ne dépasse pas de la grille
        if (orientation == Orientation.HORIZONTAL && x + size > _gridSize) return false;
        if (orientation == Orientation.VERTICAL && y + size > _gridSize) return false;

        // Vérifier chaque cellule du bateau
        for (int i = 0; i < size; i++) {
            int cx = orientation == Orientation.HORIZONTAL ? x + i : x;
            int cy = orientation == Orientation.VERTICAL ? y + i : y;
            Position pos2 = new Position(cx, cy);
            // Un bateau ne peut pas être sur l'île
            if(this._mode == ModeGame.ISLAND){
                if (_island.contains(pos2)) return false;
            }

            // La cellule ne doit pas être déjà occupée
            if (!this._squares.get(pos2).isEmpty()) return false;
        }

        return true;
    }

    /**
     * Vérifie si un piège/arme peut être placé à une position donnée
     */
    public boolean canPlaceTrapWeapon(int x, int y){
        // Vérifier les limites
        if (x >= _gridSize || y >= _gridSize) return false;

        Position pos = new Position(x, y);

        // La cellule ne doit pas être occupée
        if(!this._squares.get(pos).isEmpty()) return false;

        if(_mode == ModeGame.ISLAND){
            if(_island.contains(pos)) return true;
            return false;
        }

        return true;
    }

    public void placeBoat(Boat b, int x, int y, Orientation orientation) {
        // Incrémenter le compteur
        Integer count = (this._boatsCount.getOrDefault(b.getName(), 0)) +1;
        this._boatsCount.put(b.getName(), count);
        this._allBoats.add(b);

        b.setPosition(x, y, orientation);
        b.belongsTo(_robot);

        //Placement de l'instance dans les cases occupées
        for (int i = 0; i < b.getSize(); i++) {
            int cx = orientation == Orientation.HORIZONTAL ? x + i : x;
            int cy = orientation == Orientation.VERTICAL ? y + i : y;
            Position pos = new Position(cx, cy);

            this._squares.get(pos).setContent(b);
        }
    }

    public void placeTrap(Trap trap, int x, int y) {
        Position pos = new Position(x, y);
        trap.setPosition(x, y, Orientation.NONE);
        this._squares.get(pos).setContent(trap);
    }

    public void placeWeapon(Weapon weapon, int x, int y) {
        Position pos = new Position(x, y);
        weapon.setPosition(x, y, Orientation.NONE);
        this._squares.get(pos).setContent(weapon);
    }

    public void attack(Position position){
        this._squares.get(position).attack();
    }

    public void reset(){
        List<Boat> boats = new ArrayList<>(this.getBoats());

        this._squares.clear();
        clear();

        for(int i = 0; i< _gridSize; i++){
            for(int j = 0; j< _gridSize; j++){
                Position pos = new Position(i, j);
                this._squares.put(pos, new Square(pos, this._robot));
            }
        }

        if(_mode == ModeGame.ISLAND){
            int x = _gridSize /2 -2;
            int y = _gridSize /2 -2;
            this._island = new Island(new Position(x, y));

            for(int i=x; i<x+4; i++){
                for(int j=y; j<y+4; j++){
                    Position pos = new Position(i, j);
                    this._squares.get(pos).setIsland();
                }
            }
        }

        //Recréer les bateaux
        for(Boat boat : boats){
            Boat b = Placement.createBoat(boat.getName());
            Position oldPosition = boat.getPosition();
            placeBoat(b, oldPosition.getX(), oldPosition.getY(), oldPosition.getOrientation());
        }

        //Recréer les traps
        for(Map.Entry<TrapType, Position> entry : _traps.entrySet()){
            Trap t = Placement.createTrap(entry.getKey());
            placeTrap(t, entry.getValue().getX(), entry.getValue().getY());
        }

        //Recréer les weapons
        for(Map.Entry<WeaponType, Position> entry : _weapons.entrySet()){
            Weapon w = Placement.createWeapon(entry.getKey());
            placeWeapon(w, entry.getValue().getX(), entry.getValue().getY());

        }
    }

    public int getSize(){
        return this._gridSize;
    }

    public boolean squareIsInIsland(Position pos){
        return this._squares.get(pos).isInIsland();
    }

    public Square getSquare(Position pos) {
        return this._squares.get(pos);
    }

    public boolean hasIsland(){
        return this._mode == ModeGame.ISLAND;
    }

    public Position getPositionIsland(){
        if(_mode == ModeGame.ISLAND) return this._island.getPosition();
        else return new Position(0, 0);
    }

    public int getSizeIsland(){
        return this._island.getSize();
    }

    /**
     * Cherche tous les boats et les supprime de la grille
     */
    public void clearBoats(){
        for(Map.Entry<Position, Square> entry : this._squares.entrySet()){
            if(entry.getValue().getContentType() == ContentType.BOAT){
                this._squares.get(entry.getKey()).setContent(null);
            }
        }

        _allBoats.clear();
        _boatsCount.clear();
    }

    /**
     * Cherche tous les traps et les supprime de la grille
     */
    public void clearTraps(){
        for(Map.Entry<Position, Square> entry : this._squares.entrySet()){
            if(entry.getValue().getContentType() == ContentType.TRAP){
                this._squares.get(entry.getKey()).setContent(null);
            }
        }
    }

    /**
     * Cherche tous les weapons et les supprime de la grille
     */
    public void clearWeapons(){
        for(Map.Entry<Position, Square> entry : this._squares.entrySet()){
            if (entry.getValue().getContentType() == ContentType.WEAPON){
                this._squares.get(entry.getKey()).setContent(null);
            }
        }
    }

    /**
     * Supprime tous les éléments de la grille
     */
    public void clear(){
        clearBoats();
        clearTraps();
        clearWeapons();
    }

    public boolean isCellOccupied(int x, int y){
        return !this._squares.get(new Position(x, y)).isEmpty();
    }

    public int getBoatCount(BoatName boat){
        return this._boatsCount.getOrDefault(boat, 0);
    }

    /**
     * Retourne la liste des positions où il y a un bateau
     */
    public List<Position> getPositionsBoats(){
        List<Position> list = new ArrayList<>();
        for(Map.Entry<Position, Square> entry : this._squares.entrySet()){
            if(entry.getValue().getContentType() == ContentType.BOAT){
                list.add(entry.getKey());
            }
        }

        return list;
    }

    /**
     * Retourne la liste des positions où il y a un piège
     */
    public Map<TrapType, List<Position>> getPositionsTraps(){
        Map<TrapType,  List<Position>> map = new HashMap<>();
        for(Map.Entry<Position, Square> entry : this._squares.entrySet()){
            if(entry.getValue().getContentType() == ContentType.TRAP){
                Trap t = (Trap) entry.getValue().getContent();
                if(!map.containsKey(t.getName())) map.put(t.getName(), new ArrayList<>());
                map.get(t.getName()).add(entry.getKey());
            }
        }

        return map;
    }

    /**
     * Retourne la liste des positions où il y a une arme
     */
    public Map<WeaponType, List<Position>> getPositionsWeapons(){
        Map<WeaponType,  List<Position>> map = new HashMap<>();
        for(Map.Entry<Position, Square> entry : this._squares.entrySet()){
            if(entry.getValue().getContentType() == ContentType.WEAPON){
                Weapon w = (Weapon) entry.getValue().getContent();
                if(!map.containsKey(w.getName())) map.put(w.getName(), new ArrayList<>());
                map.get(w.getName()).add(entry.getKey());
            }
        }

        return map;
    }

    /**
     * Retourne tous les bateaux placés
     */
    public List<Boat> getBoats(){
        return this._allBoats;
    }

    public void addWeapon(WeaponType type, Position pos){
        _weapons.put(type, pos);
    }

    public void addTrap(TrapType type, Position pos){
        _traps.put(type, pos);
    }

}