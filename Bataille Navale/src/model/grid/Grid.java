package model.grid;

import model.contents.fleet.Boat;
import model.contents.traps.Trap;
import model.contents.weapons.Weapon;
import model.enums.*;
import model.placement.Placement;

import javax.sound.midi.SysexMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public  class Grid {
    private int gridSize;
    private Map<Position, Square> squares;
    private ModeGame mode;
    private Island island;
    private boolean robot;
    private Map<BoatName, Integer> boatsCount;
    private List<Boat> allBoats = new ArrayList<>();

    public Grid(int size, ModeGame mode, boolean robot) {
        this.gridSize = size;
        this.mode = mode;
        this.squares = new HashMap<>();
        this.robot = robot;
        this.boatsCount = new HashMap<>();

        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                Position pos = new Position(i, j);
                this.squares.put(pos, new Square(pos, robot));
            }
        }

        if(mode == ModeGame.ISLAND){
            int x = size/2 -2;
            int y = size/2 -2;
            this.island = new Island(new Position(x, y));

            for(int i=x; i<x+4; i++){
                for(int j=y; j<y+4; j++){
                    Position pos = new Position(i, j);
                    this.squares.get(pos).setIsland();
                }
            }
        }
    }

    /**
     * Vérifie si un bateau peut être placé à une position donnée
     */
    public boolean canPlaceBoat(int size, int x, int y, Orientation orientation){
        // Vérifier que le bateau ne dépasse pas de la grille
        if (orientation == Orientation.HORIZONTAL && x + size > gridSize) return false;
        if (orientation == Orientation.VERTICAL && y + size > gridSize) return false;

        // Vérifier chaque cellule du bateau
        for (int i = 0; i < size; i++) {
            int cx = orientation == Orientation.HORIZONTAL ? x + i : x;
            int cy = orientation == Orientation.VERTICAL ? y + i : y;
            Position pos2 = new Position(cx, cy);
            // Un bateau ne peut pas être sur l'île
            if(this.mode == ModeGame.ISLAND){
                if (island.contains(pos2)) return false;
            }

            // La cellule ne doit pas être déjà occupée
            if (!this.squares.get(pos2).isEmpty()) return false;
        }

        return true;
    }

    /**
     * Vérifie si un piège/arme peut être placé à une position donnée
     */
    public boolean canPlaceTrapWeapon(int x, int y){
        // Vérifier les limites
        if (x >= gridSize || y >= gridSize) return false;

        Position pos = new Position(x, y);

        // La cellule ne doit pas être occupée
        if(!this.squares.get(pos).isEmpty()) return false;

        if(mode == ModeGame.ISLAND){
            if(island.contains(pos)) return true;
            return false;
        }

        return true;
    }

    public void placeBoat(Boat b, int x, int y, Orientation orientation) {
        // Incrémenter le compteur
        Integer count = (this.boatsCount.getOrDefault(b.getName(), 0)) +1;
        this.boatsCount.put(b.getName(), count);
        this.allBoats.add(b);

        b.setPosition(x, y, orientation);
        b.belongsTo(robot);

        //Placement de l'instance dans les cases occupées
        for (int i = 0; i < b.getSize(); i++) {
            int cx = orientation == Orientation.HORIZONTAL ? x + i : x;
            int cy = orientation == Orientation.VERTICAL ? y + i : y;
            Position pos = new Position(cx, cy);

            this.squares.get(pos).setContent(b);
        }
    }

    public void placeTrap(Trap trap, int x, int y) {
        Position pos = new Position(x, y);
        trap.setPosition(x, y, Orientation.NONE);
        this.squares.get(pos).setContent(trap);
    }

    public void placeWeapon(Weapon weapon, int x, int y) {
        Position pos = new Position(x, y);
        weapon.setPosition(x, y, Orientation.NONE);
        this.squares.get(pos).setContent(weapon);
    }

    public void attack(Position position){
        this.squares.get(position).attack();
    }

    public void reset(){
        Map<WeaponType, List<Position>> weapons = getPositionsWeapons();
        Map<TrapType, List<Position>> traps = getPositionsTraps();
        List<Boat> boats = new ArrayList<>(this.getBoats());

        this.squares.clear();
        clear();

        for(int i = 0; i< gridSize; i++){
            for(int j = 0; j< gridSize; j++){
                Position pos = new Position(i, j);
                this.squares.put(pos, new Square(pos, this.robot));
            }
        }

        if(mode == ModeGame.ISLAND){
            int x = gridSize/2 -2;
            int y = gridSize/2 -2;
            this.island = new Island(new Position(x, y));

            for(int i=x; i<x+4; i++){
                for(int j=y; j<y+4; j++){
                    Position pos = new Position(i, j);
                    this.squares.get(pos).setIsland();
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
        for(Map.Entry<TrapType, List<Position>> entry : traps.entrySet()){
            for(Position pos : entry.getValue()){
                Trap t = Placement.createTrap(entry.getKey());
                placeTrap(t, pos.getX(), pos.getY());
            }
        }

        //Recréer les weapons
        for(Map.Entry<WeaponType, List<Position>> entry : weapons.entrySet()){
            for(Position pos : entry.getValue()){
                Weapon w = Placement.createWeapon(entry.getKey());
                placeWeapon(w, pos.getX(), pos.getY());
            }
        }
    }

    public int getSize(){
        return this.gridSize;
    }

    public ContentType getContentTypeSquare(Position pos){
        return this.squares.get(pos).getContentType();
    }

    public boolean squareIsInIsland(Position pos){
        return this.squares.get(pos).isInIsland();
    }

    public Square getSquare(Position pos) {
        return this.squares.get(pos);
    }

    public boolean hasIsland(){
        return this.mode == ModeGame.ISLAND;
    }

    public Position getPositionIsland(){
        return this.island.getPosition();
    }

    public int getSizeIsland(){
        return this.island.getSize();
    }

    /**
     * Cherche tous les boats et les supprime de la grille
     */
    public void clearBoats(){
        for(Map.Entry<Position, Square> entry : this.squares.entrySet()){
            if(entry.getValue().getContentType() == ContentType.BOAT){
                this.squares.get(entry.getKey()).setContent(null);
            }
        }

        allBoats.clear();
        boatsCount.clear();
    }

    /**
     * Cherche tous les traps et les supprime de la grille
     */
    public void clearTraps(){
        for(Map.Entry<Position, Square> entry : this.squares.entrySet()){
            if(entry.getValue().getContentType() == ContentType.TRAP){
                this.squares.get(entry.getKey()).setContent(null);
            }
        }
    }

    /**
     * Cherche tous les weapons et les supprime de la grille
     */
    public void clearWeapons(){
        for(Map.Entry<Position, Square> entry : this.squares.entrySet()){
            if (entry.getValue().getContentType() == ContentType.WEAPON){
                this.squares.get(entry.getKey()).setContent(null);
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
        return !this.squares.get(new Position(x, y)).isEmpty();
    }

    public int getBoatCount(BoatName boat){
        return this.boatsCount.getOrDefault(boat, 0);
    }

    /**
     * Retourne la liste des positions où il y a un bateau
     */
    public List<Position> getPositionsBoats(){
        List<Position> list = new ArrayList<>();
        for(Map.Entry<Position, Square> entry : this.squares.entrySet()){
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
        for(Map.Entry<Position, Square> entry : this.squares.entrySet()){
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
        for(Map.Entry<Position, Square> entry : this.squares.entrySet()){
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
        return this.allBoats;
    }

}