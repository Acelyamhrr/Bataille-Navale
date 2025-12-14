package model.game.Results;

import model.enums.*;
import model.grid.Position;

public class TurnResult {
    private boolean success;
    private String errorMessage;
    private TurnType type;
    private AttackResult attackResult;
    private WeaponType weaponUsed;
    private Position targetPosition;
    private boolean tornadoActivated;
    private Position redirectedTo;

    // pour les trucs trouvés sur l'île
    private WeaponType weaponFound;
    private TrapType trapFound;
    private TrapType trapPlaced;
    private Position trapPlacementPosition;

    // pour le robot
    private boolean isRobotAction;

    private TurnResult() {}

    // factory methods du joueur

    public static TurnResult success(AttackResult attackResult, boolean tornadoActivated, Position finalTarget) {
        TurnResult result = new TurnResult();
        result.success = true;
        result.type = TurnType.ATTACK;
        result.attackResult = attackResult;
        result.tornadoActivated = tornadoActivated;
        result.redirectedTo = finalTarget;
        result.isRobotAction = false;
        return result;
    }

    public static TurnResult emptyIslandSquare() {
        TurnResult result = new TurnResult();
        result.success = true;
        result.type = TurnType.ISLAND_SEARCH;
        result.isRobotAction = false;
        return result;
    }

    public static TurnResult weaponFound(WeaponType weapon) {
        TurnResult result = new TurnResult();
        result.success = true;
        result.type = TurnType.ISLAND_SEARCH;
        result.weaponFound = weapon;
        result.isRobotAction = false;
        return result;
    }

    public static TurnResult trapFound(TrapType trap) {
        TurnResult result = new TurnResult();
        result.success = true;
        result.type = TurnType.ISLAND_SEARCH;
        result.trapFound = trap;
        result.isRobotAction = false;
        return result;
    }

    public static TurnResult trapPlaced(TrapType trap, Position position) {
        TurnResult result = new TurnResult();
        result.success = true;
        result.type = TurnType.TRAP_PLACEMENT;
        result.trapPlaced = trap;
        result.trapPlacementPosition = position;
        result.isRobotAction = false;
        return result;
    }

    public static TurnResult error(String message) {
        TurnResult result = new TurnResult();
        result.success = false;
        result.errorMessage = message;
        return result;
    }


    // factory methods du robot
    public static TurnResult robotAttack(WeaponType weapon, AttackResult attackResult, boolean tornadoActivated, Position finalTarget) {
        TurnResult result = new TurnResult();
        result.success = true;
        result.type = TurnType.ATTACK;
        result.weaponUsed = weapon;
        result.attackResult = attackResult;
        result.tornadoActivated = tornadoActivated;
        result.redirectedTo = finalTarget;
        result.isRobotAction = true;
        return result;
    }

    public static TurnResult robotEmptyIsland() {
        TurnResult result = new TurnResult();
        result.success = true;
        result.type = TurnType.ISLAND_SEARCH;
        result.isRobotAction = true;
        return result;
    }

    public static TurnResult robotWeaponFound(WeaponType weapon) {
        TurnResult result = new TurnResult();
        result.success = true;
        result.type = TurnType.ISLAND_SEARCH;
        result.weaponFound = weapon;
        result.isRobotAction = true;
        return result;
    }

    public static TurnResult robotTrapFound(TrapType trap, Position placement) {
        TurnResult result = new TurnResult();
        result.success = true;
        result.type = TurnType.ISLAND_SEARCH;
        result.trapFound = trap;
        result.trapPlacementPosition = placement;
        result.isRobotAction = true;
        return result;
    }

    public static TurnResult robotTrapFoundButNoSpace(TrapType trap) {
        TurnResult result = new TurnResult();
        result.success = true;
        result.type = TurnType.ISLAND_SEARCH;
        result.trapFound = trap;
        result.isRobotAction = true;
        return result;
    }


    // GETTERS

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public TurnType getType() {
        return type;
    }

    public AttackResult getAttackResult() {
        return attackResult;
    }

    public WeaponType getWeaponUsed() {
        return weaponUsed;
    }

    public boolean wasTornadoActivated() {
        return tornadoActivated;
    }

    public Position getRedirectedTo() {
        return redirectedTo;
    }

    public WeaponType getWeaponFound() {
        return weaponFound;
    }

    public TrapType getTrapFound() {
        return trapFound;
    }

    public TrapType getTrapPlaced() {
        return trapPlaced;
    }

    public Position getTrapPlacementPosition() {
        return trapPlacementPosition;
    }

    public boolean isRobotAction() {
        return isRobotAction;
    }

    public String getActionDescription() {
        if (!success) {
            return errorMessage;
        }

        String actor = isRobotAction ? "Robot" : "Joueur";

        switch (type) {
            case ATTACK:
                if (attackResult.isSonar()) {
                    return actor + " utilise un Sonar : " + attackResult.getOccupiedCells() + " case(s) occupée(s)";
                }
                if (attackResult.hadSunk()) {
                    return actor + " a coulé un bateau !";
                }
                if (attackResult.hadHit()) {
                    return actor + " a touché ! (" + attackResult.getHits() + " case(s))";
                }
                return actor + " a raté (à l'eau)";

            case ISLAND_SEARCH:
                if (weaponFound != null) {
                    return actor + " a trouvé une arme : " + weaponFound;
                }
                if (trapFound != null) {
                    String msg = actor + " a trouvé un piège : " + trapFound;
                    if (trapPlacementPosition != null && isRobotAction) {
                        msg += " (placé en " + trapPlacementPosition.getX() + "," + trapPlacementPosition.getY() + ")";
                    }
                    return msg;
                }
                return actor + " a fouillé une case vide";

            case TRAP_PLACEMENT:
                return "Piège placé en " + trapPlacementPosition.getX() + "," + trapPlacementPosition.getY();

            default:
                return "Action inconnue";
        }
    }


}
