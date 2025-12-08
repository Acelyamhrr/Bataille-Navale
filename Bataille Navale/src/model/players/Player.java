package model.players;

import model.contents.fleet.Boat;
import model.contents.traps.Tornado;
import model.enums.WeaponType;
import model.grid.Grid;
import model.grid.Position;

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

    public Player(String username, Boolean robot) {
        this.username = username;
        this.isRobot = robot;
        this.weapons = new HashMap<>();
        this.boats = new ArrayList<>();
        this.tornado = null;
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
        tornado = null;
        if (grid != null) {
            grid.reset();
        }
    }


    // GETTERS / SETTERS

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




}