package model.players;

import model.contents.fleet.Boat;
import model.contents.traps.Tornado;
import model.contents.traps.Trap;
import model.enums.ContentType;
import model.enums.TrapType;
import model.enums.WeaponType;
import model.game.RobotStrategy.RobotStrategy;
import model.grid.Grid;
import model.grid.Position;
import model.grid.Square;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private String username;
    private boolean isRobot;
    private Map<WeaponType, Integer> weapons;
    private Grid grid;
    private Tornado tornado;
    private List<Boat> boats;

    private RobotStrategy strategy;

    private Map<TrapType, Integer> trapInventory;

    public Player(String username, Boolean robot) {
        this.username = username;
        this.isRobot = robot;
        this.weapons = new HashMap<>();
        this.boats = new ArrayList<>();
        this.tornado = null;
        this.strategy = null;
        this.trapInventory = new HashMap<>();
    }

    public Boolean hasTornadoActive() {
        return tornado != null && tornado.isActive();
    }

    // déclenche la tornade et retourne sa posiiton.
    public Position tornadoTrigger(Position position) {
        if (hasTornadoActive()) {
            return tornado.getNewPosition(position);
        }
        return position;
    }

    // définit la tornade du joueur
    public void setTornado(Tornado tornado) {
        this.tornado = tornado;
    }

    // reçoit une attaque à la position donnée sur la grille du joueur
    public void receiveAttack(Position position) {
        if (grid != null) {
            grid.attack(position);
        }
    }

    // verifie si tous les bateaux du joueur sont coulés
    public boolean allBoatSunk() {
        for (Boat boat : boats) {
            if (!boat.hasSunk()) {
                return false;
            }
        }
        return true;
    }

    // utiliser une arme (décrémente le compteur d'arme)
    public void useWeapon(WeaponType weapon ){
        Integer count = weapons.get(weapon);
        if (count == null || count <= 0) {
            throw new IllegalStateException("Pas d'arme de type " + weapon);
        }
        if(weapon != WeaponType.MISSILE) {
            weapons.put(weapon, count - 1);
        }
    }

    public void reset() {
        weapons.clear();
        boats.clear();
        trapInventory.clear();
        tornado = null;
        if (grid != null) {
            grid.reset();
        }
    }

    public void addTrapToInventory(TrapType trapType) {
        trapInventory.put(trapType, trapInventory.getOrDefault(trapType, 0) + 1);
    }

    public int getTrapInventoryCount(TrapType trapType) {
        return trapInventory.getOrDefault(trapType, 0);
    }


    public void removeTrapFromInventory(TrapType trapType) {
        int count = getTrapInventoryCount(trapType);
        if (count>0) {
            trapInventory.put(trapType, count-1);
        }
    }

    public Map<TrapType, Integer> getTrapInventory(){
        return trapInventory;
    }

    public boolean placeTrapFromInventory(TrapType trapType, Position position, Trap trap) {
        System.out.println("\n🔧 DEBUG placeTrapFromInventory");
        System.out.println("TrapType: " + trapType);
        System.out.println("Position: " + position.getX() + "," + position.getY());
        System.out.println("Inventaire count: " + getTrapInventoryCount(trapType));


        if (getTrapInventoryCount(trapType) <= 0) {
            System.out.println("❌ Pas de piège dans l'inventaire");

            return false;
        }

        if (grid == null) {
            System.out.println("❌ Grid est null");

            return false;
        }

        Square square = grid.getSquare(position);
        System.out.println("Square isEmpty: " + square.isEmpty());


        // Vérifier que la case est vide
        if (!square.isEmpty()) {
            System.out.println("❌ Case occupée");

            return false;
        }

        // Placer le piège
        System.out.println("✅ Placement du piège...");

        grid.placeTrap(trap, position.getX(), position.getY());

        // Retirer de l'inventaire
        removeTrapFromInventory(trapType);
        System.out.println("✅ Piège retiré de l'inventaire");


        // Si c'est une tornade, la définir comme tornade active
        if (trapType == TrapType.TORNADO && trap.getName() == TrapType.TORNADO) {
            setTornado((Tornado) trap);
            System.out.println("✅ Tornade définie comme active");

        }
        System.out.println("✅ Placement réussi!");

        return true;
    }

    public String getUsername() {
        return username;
    }

    public boolean isRobot() {
        return isRobot;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public List<Boat> getBoats() {
        return boats;
    }

    public void addBoat(Boat boat) {
        this.boats.add(boat);
    }

    public Map<WeaponType, Integer> getWeapons() {
        return weapons;
    }

    public void setWeaponCount(WeaponType weapon, int count) {
        weapons.put(weapon, count);
    }

    public int getWeaponCount(WeaponType weapon) {
        return weapons.getOrDefault(weapon, 0);
    }

    public Tornado getTornado() {
        return tornado;
    }

    public String toString() {
        return "Player{" + "username='" + username + '\'' + ", isRobot=" + isRobot + ", boats=" + boats.size() + ", hasTornado=" + (tornado != null) + '}';
    }

    public void setStrategy(RobotStrategy strategy) {
        this.strategy = strategy;
    }

    public RobotStrategy getStrategy() {
        return strategy;
    }




}