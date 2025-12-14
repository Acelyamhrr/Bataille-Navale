package model.game;

import model.enums.*;
import model.grid.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePlacement {
    private Map<BoatName, List<Position>> boatPlacementPlayer;
    private Map<TrapType,List<Position>> trapPlacementPlayer;
    private Map<WeaponType, List<Position>> weaponPlacementPlayer;
    private Map<BoatName, List<Position>> boatPlacementRobot;
    private Map<TrapType, List<Position>> trapPlacementRobot;
    private Map<WeaponType, List<Position>> weaponPlacementRobot;

    public GamePlacement() {
        this.boatPlacementPlayer = new HashMap<>();
        this.trapPlacementPlayer = new HashMap<>();
        this.weaponPlacementPlayer = new HashMap<>();
        this.boatPlacementRobot = new HashMap<>();
        this.trapPlacementRobot = new HashMap<>();
        this.weaponPlacementRobot = new HashMap<>();
    }

    // Getters
    public Map<BoatName, List<Position>> getBoatPlacementsPlayer() {
        return new HashMap<>(boatPlacementPlayer);
    }

    public Map<BoatName, List<Position>> getBoatPlacementsRobot() {
        return new HashMap<>(boatPlacementRobot);
    }

    public Map<TrapType, List<Position>> getTrapPlacementsPlayer() {
        return new HashMap<>(trapPlacementPlayer);
    }

    public Map<TrapType, List<Position>> getTrapPlacementsRobot() {
        return new HashMap<>(trapPlacementRobot);
    }

    public Map<WeaponType, List<Position>> getWeaponPlacementPlayer(){ return new HashMap<>(weaponPlacementPlayer); }

    public Map<WeaponType, List<Position>> getWeaponPlacementRobot() { return new HashMap<>(weaponPlacementRobot); }

    // Setters
    public void setBoatsPlacementPlayer(Map<BoatName, List<Position>> boatPlacementPlayer) {
        this.boatPlacementPlayer = boatPlacementPlayer;
    }

    public void setBoatsPlacementRobot(Map<BoatName, List<Position>> boatPlacementRobot) {
        this.boatPlacementRobot = boatPlacementRobot;
    }

    public void setTrapsPlacementsPlayer(Map<TrapType, List<Position>> trapPlacementPlayer) {
        this.trapPlacementPlayer = trapPlacementPlayer;
    }

    public void setTrapsPlacementRobot(Map<TrapType, List<Position>> trapPlacementRobot) {
        this.trapPlacementRobot = trapPlacementRobot;
    }

    public void setWeaponsPlacementPlayer(Map<WeaponType, List<Position>> weaponPlacementPlayer){
        this.weaponPlacementPlayer = weaponPlacementPlayer;
    }

    public void setWeaponsPlacementRobot(Map<WeaponType, List<Position>> weaponPlacementRobot){
        this.weaponPlacementRobot = weaponPlacementRobot;
    }

}