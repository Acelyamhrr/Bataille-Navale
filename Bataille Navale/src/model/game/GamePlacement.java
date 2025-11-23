package model.game;

import model.enums.*;
import model.grid.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePlacement {
    private Map<BoatName, List<Position>> boatPlacementPlayer;
    private Map<TrapType, List<Position>> trapPlacementPlayer;
    private Map<BoatName, List<Position>> boatPlacementRobot;
    private Map<TrapType, List<Position>> trapPlacementRobot;

    public GamePlacement() {
        this.boatPlacementPlayer = new HashMap<>();
        this.trapPlacementPlayer = new HashMap<>();
        this.boatPlacementRobot = new HashMap<>();
        this.trapPlacementRobot = new HashMap<>();
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

    // Setters
    public void setBoatPlacementPlayer(BoatName type, Position pos) {
        if (!boatPlacementPlayer.containsKey(type)) {
            boatPlacementPlayer.put(type, new ArrayList<>());
        }
        boatPlacementPlayer.get(type).add(pos);
    }

    public void setBoatPlacementRobot(BoatName type, Position pos) {
        if (!boatPlacementRobot.containsKey(type)) {
            boatPlacementRobot.put(type, new ArrayList<>());
        }
        boatPlacementRobot.get(type).add(pos);
    }

    public void setTrapPlacementPlayer(TrapType type, Position pos) {
        if (!trapPlacementPlayer.containsKey(type)) {
            trapPlacementPlayer.put(type, new ArrayList<>());
        }
        trapPlacementPlayer.get(type).add(pos);
    }

    public void setTrapPlacementRobot(TrapType type, Position pos) {
        if(!trapPlacementRobot.containsKey(type)){
            trapPlacementRobot.put(type, new ArrayList<>());
        }
        trapPlacementRobot.get(type).add(pos);
    }

}