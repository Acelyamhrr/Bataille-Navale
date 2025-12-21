package model.game.Results;

import model.enums.*;
import model.grid.Position;

public class TurnResult {
    private boolean _success;
    private String _errorMessage;
    private TurnType _type;
    private AttackResult _attackResult;
    private WeaponType _weaponUsed;
    private Position _targetPosition;
    private boolean _tornadoActivated;
    private Position _redirectedTo;

    // pour les trucs trouvés sur l'île
    private WeaponType _weaponFound;
    private TrapType _trapFound;
    private TrapType _trapPlaced;
    private Position _trapPlacementPosition;

    // pour le robot
    private boolean _isRobotAction;

    private TurnResult() {}

    // factory methods du joueur

    public static TurnResult success(AttackResult attackResult, boolean tornadoActivated, Position finalTarget) {
        TurnResult result = new TurnResult();
        result._success = true;
        result._type = TurnType.ATTACK;
        result._attackResult = attackResult;
        result._tornadoActivated = tornadoActivated;
        result._redirectedTo = finalTarget;
        result._isRobotAction = false;
        return result;
    }

    public static TurnResult emptyIslandSquare(boolean tornadoActivated, Position finalTarget) {
        TurnResult result = new TurnResult();
        result._success = true;
        result._type = TurnType.ISLAND_SEARCH;
        result._tornadoActivated = tornadoActivated;
        result._redirectedTo = finalTarget;
        result._isRobotAction = false;
        return result;
    }

    public static TurnResult weaponFound(WeaponType weapon, boolean tornadoActivated, Position finalTarget) {
        TurnResult result = new TurnResult();
        result._success = true;
        result._type = TurnType.ISLAND_SEARCH;
        result._weaponFound = weapon;
        result._tornadoActivated = tornadoActivated;
        result._redirectedTo = finalTarget;
        result._isRobotAction = false;
        return result;
    }

    public static TurnResult trapFound(TrapType trap, boolean tornadoActivated, Position finalTarget) {
        TurnResult result = new TurnResult();
        result._success = true;
        result._type = TurnType.ISLAND_SEARCH;
        result._tornadoActivated = tornadoActivated;
        result._redirectedTo = finalTarget;
        result._trapFound = trap;
        result._isRobotAction = false;
        return result;
    }

    public static TurnResult trapPlaced(TrapType trap, Position position) {
        TurnResult result = new TurnResult();
        result._success = true;
        result._type = TurnType.TRAP_PLACEMENT;
        result._trapPlaced = trap;
        result._trapPlacementPosition = position;
        result._isRobotAction = false;
        return result;
    }

    public static TurnResult error(String message) {
        TurnResult result = new TurnResult();
        result._success = false;
        result._errorMessage = message;
        return result;
    }


    // factory methods du robot
    public static TurnResult robotAttack(WeaponType weapon, AttackResult attackResult, boolean tornadoActivated, Position finalTarget) {
        TurnResult result = new TurnResult();
        result._success = true;
        result._type = TurnType.ATTACK;
        result._weaponUsed = weapon;
        result._attackResult = attackResult;
        result._tornadoActivated = tornadoActivated;
        result._redirectedTo = finalTarget;
        result._isRobotAction = true;
        return result;
    }

    public static TurnResult robotEmptyIsland(boolean tornadoActivated, Position finalTarget) {
        TurnResult result = new TurnResult();
        result._success = true;
        result._type = TurnType.ISLAND_SEARCH;
        result._tornadoActivated = tornadoActivated;
        result._redirectedTo = finalTarget;
        result._isRobotAction = true;
        return result;
    }

    public static TurnResult robotWeaponFound(WeaponType weapon, boolean tornadoActivated, Position finalTarget) {
        TurnResult result = new TurnResult();
        result._success = true;
        result._type = TurnType.ISLAND_SEARCH;
        result._weaponFound = weapon;
        result._tornadoActivated = tornadoActivated;
        result._redirectedTo = finalTarget;
        result._isRobotAction = true;
        return result;
    }

    public static TurnResult robotTrapFound(TrapType trap, Position placement, boolean tornadoActivated, Position finalTarget) {
        TurnResult result = new TurnResult();
        result._success = true;
        result._type = TurnType.ISLAND_SEARCH;
        result._trapFound = trap;
        result._trapPlacementPosition = placement;
        result._tornadoActivated = tornadoActivated;
        result._redirectedTo = finalTarget;
        result._isRobotAction = true;
        return result;
    }

    public static TurnResult robotTrapFoundButNoSpace(TrapType trap) {
        TurnResult result = new TurnResult();
        result._success = true;
        result._type = TurnType.ISLAND_SEARCH;
        result._trapFound = trap;
        result._isRobotAction = true;
        return result;
    }


    // GETTERS

    public boolean isSuccess() {
        return _success;
    }

    public String getErrorMessage() {
        return _errorMessage;
    }

    public TurnType getType() {
        return _type;
    }

    public AttackResult getAttackResult() {
        return _attackResult;
    }

    public WeaponType getWeaponUsed() {
        return _weaponUsed;
    }

    public boolean wasTornadoActivated() {
        return _tornadoActivated;
    }

    public Position getRedirectedTo() {
        return _redirectedTo;
    }

    public WeaponType getWeaponFound() {
        return _weaponFound;
    }

    public TrapType getTrapFound() {
        return _trapFound;
    }

    public TrapType getTrapPlaced() {
        return _trapPlaced;
    }

    public Position getTrapPlacementPosition() {
        return _trapPlacementPosition;
    }

    public boolean isRobotAction() {
        return _isRobotAction;
    }

    public String getActionDescription() {
        if (!_success) {
            return _errorMessage;
        }

        String actor = _isRobotAction ? "Robot" : "Joueur";

        switch (_type) {
            case ATTACK:
                if (_attackResult.isSonar()) {
                    return actor + " utilise un Sonar : " + _attackResult.getOccupiedCells() + " case(s) occupée(s)";
                }
                if (_attackResult.hadSunk()) {
                    return actor + " a coulé un bateau !";
                }
                if (_attackResult.hadHit()) {
                    return actor + " a touché ! (" + _attackResult.getHits() + " case(s))";
                }
                return actor + " a raté (à l'eau)";

            case ISLAND_SEARCH:
                if (_weaponFound != null) {
                    return actor + " a trouvé une arme : " + _weaponFound;
                }
                if (_trapFound != null) {
                    String msg = actor + " a trouvé un piège : " + _trapFound;
                    if (_trapPlacementPosition != null && _isRobotAction) {
                        msg += " (placé en " + _trapPlacementPosition.getX() + "," + _trapPlacementPosition.getY() + ")";
                    }
                    return msg;
                }
                return actor + " a fouillé une case vide";

            case TRAP_PLACEMENT:
                return "Piège placé en " + _trapPlacementPosition.getX() + "," + _trapPlacementPosition.getY();

            default:
                return "Action inconnue";
        }
    }


}
