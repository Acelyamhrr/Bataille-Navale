package controller;

import model.contents.Content;
import model.contents.fleet.Boat;
import model.contents.traps.Tornado;
import model.contents.traps.Trap;
import model.contents.weapons.Weapon;
import model.contents.weapons.WeaponFactory;
import model.enums.ContentType;
import model.enums.*;
import model.game.Game;
import model.game.GameConfig;
import model.game.GamePlacement;
import model.grid.*;
import model.grid.Position;
import model.players.Player;
import view.EndView;
import view.GameView;

import java.awt.*;
import java.util.ArrayList;

public class GameController {
    private Game game;
    private GameView view;
    private GameConfig config;

    private int turnNumber = 1;
    private boolean playerTurn = true;

    public GameController(GameConfig config, GamePlacement placement) {
        this.config = config;

        this.game = new Game(config, placement);
        game.initialize();
    }

    public boolean isInIsland(int x, int y){
        Player player = this.game.getPlayer();
        Grid grid = player.getGrid();
        boolean inIsland = grid.squareIsInIsland(new Position(x, y));
        return hasIsland() && inIsland;
    }

    public void setView(GameView view) {
        this.view = view;

        registerObservers();        // enregistre la vue comme observer sur tous les boats et squares

        updateAllDisplays();
    }

    private void registerObservers() {
        for (Boat boat : game.getPlayer().getBoats()) {
            boat.addObserver(view);
        }

        for (Boat boat : game.getRobot().getBoats()) {
            boat.addObserver(view);
        }

        Grid playerGrid = game.getPlayer().getGrid();
        Grid robotGrid = game.getRobot().getGrid();

        for (int x = 0; x < config.getGridSize(); x++) {
            for (int y = 0; y < config.getGridSize(); y++) {
                Position pos = new Position(x, y);
                playerGrid.getSquare(pos).addObserver(view);
                robotGrid.getSquare(pos).addObserver(view);
            }
        }
    }

    private void updateAllDisplays() {
        view.setTurnNumber(turnNumber);
        updateWeaponsDisplay();
    }

    private void updateWeaponsDisplay() {
        Player player = game.getPlayer();
        Player robot = game.getRobot();

        // maj des armes du joueur
        view.updatePlayerWeapons(
                player.getWeaponCount(WeaponType.MISSILE),
                player.getWeaponCount(WeaponType.BOMB),
                player.getWeaponCount(WeaponType.SONAR)
        );

        // maj des armes du robot
        view.updateRobotWeapons(
                robot.getWeaponCount(WeaponType.MISSILE),
                robot.getWeaponCount(WeaponType.BOMB),
                robot.getWeaponCount(WeaponType.SONAR)
        );

        // activer/desactiver les btn
        view.setWeaponEnabled(WeaponType.BOMB, player.getWeaponCount(WeaponType.BOMB)>0);
        view.setWeaponEnabled(WeaponType.SONAR, player.getWeaponCount(WeaponType.SONAR)>0);
    }
    
    public void handleGridClick(int x, int y) {
        if (!playerTurn) {
            view.showError("Ce n'est pas votre tour !");
            return;
        }

        Position target = new Position(x,y);

        boolean actionSuccessful = false;

        if (view.isShovelSelected()) {
            actionSuccessful = handleIslandSearch(target);
        }
        else {
            WeaponType weapon = view.getSelectedWeapon();
            actionSuccessful = handleWeaponUse(weapon, target);
        }

        if (!actionSuccessful) {
            return;
        }
        // après le player, c'est au tour du robot
        playerTurn = false;
        playRobotTurn();

        if (game.checkGameOver()) {
            endGame();
        }
        else {
            turnNumber++;
            playerTurn = true;
            updateAllDisplays();
        }
    }

    private boolean handleWeaponUse(WeaponType weaponType, Position target) {
        Player player = game.getPlayer();
        Player robot = game.getRobot();

        // verifier que le joueur a l'arme
        if (player.getWeaponCount(weaponType) <= 0) {
            view.showError("Vous n'avez plus cette arme !");
            return false;
        }

        Square targetSquare = robot.getGrid().getSquare(target);
        if (targetSquare.wasAttacked()) {
            view.showError("Cette case a déjà été attaquée !");
            return false;
        }

        // appliquer la tornade si active
        Position finalTarget = target;
        if (robot.hasTornadoActive()) {
            finalTarget = robot.tornadoTrigger(target);
            view.showSuccess("⚠\uFE0F Tornade activée ! Votre tir a été détourné!");
            view.setPlayerAction("Tornade activée ! Votre tir a été détourné vers " + finalTarget.getX() + "," + finalTarget.getY());
        }

        // créer l'arme et obtenir les pos à attaquer
        WeaponFactory factory = new WeaponFactory();
        Weapon weapon = null;

        switch (weaponType) {
            case MISSILE:
                weapon = factory.createMissile();

                if (hasIsland() && robot.getGrid().squareIsInIsland(finalTarget)) {
                    view.showError("Le missile ne peut pas être utilisé sur l'île !");
                    return false;
                }
                break;
            case BOMB:
                weapon = factory.createBomb();

                // verif qu'aucune case de la bombe n'est dans l'ile
                if (hasIsland() && bombHitsIsland(finalTarget)) {
                    view.showError("La bombe ne peut pas toucher l'île !");
                    return false;
                }
                break;
            case SONAR:
                weapon = factory.createSonar();

                // verif que le sonar n'est pas sur l'île
                if (hasIsland() && robot.getGrid().squareIsInIsland(finalTarget)) {
                    view.showError("Le sonar ne peut pas être utilisé sur l'île !");
                    return false;
                }
                break;
        }

        if (weapon == null) return false;

        // utiliser l'arme (decremente le comp)
        player.useWeapon(weaponType);

        // obtenir les pos affectés
        ArrayList<Position> positions = weapon.use(finalTarget);

        // traiter selon le type d'arme
        if (weaponType == WeaponType.SONAR) {
            executeSonar(positions, robot, false);
        }
        else {
            // missile ou bombe c'est pareil, on attaque les positions de la liste
            executeAttack(positions, robot, false);
        }
        updateWeaponsDisplay();

        System.out.println("Target: " + target.getX() + " " + target.getY());

        return true;
    }

    private boolean bombHitsIsland(Position center) {
        Grid robotGrid = game.getRobot().getGrid();

        // verifier le centre et les 4 cases adjacentes
        if (robotGrid.squareIsInIsland(center)) {
            return true;

        }

        Position[] adjacent = {
                new Position(center.getX() - 1, center.getY()),     //haut
                new Position(center.getX() + 1, center.getY()),     // bas
                new Position(center.getX(), center.getY() - 1),     // gauche
                new Position(center.getX(), center.getY() + 1)      // droite
        };

        for (Position pos : adjacent) {
            if (robotGrid.squareIsInIsland(pos)) {
                return true;
            }
        }


        return false;
    }

    private void executeAttack(ArrayList<Position> positions, Player target, boolean isRobot) {
        System.out.println("\n--- DEBUT executeAttack ---");
        System.out.println("Nombre de positions: " + positions.size());
        System.out.println("Cible: " + (target.isRobot() ? "Robot" : "Joueur"));
        System.out.println("Attaquant est robot: " + isRobot);


        StringBuilder action = new StringBuilder();
        int hits = 0;
        int misses = 0;
        boolean sunkBoat = false;

        for (Position pos : positions) {

            System.out.println("  Attaque position: " + pos.getX() + "," + pos.getY());


            Square square = target.getGrid().getSquare(pos);
            System.out.println("    Square récupérée, isEmpty: " + square.isEmpty());


            // Vérifier si c'est un piège
            if (!square.isEmpty()) {
                ContentType contentType = square.getContent().getContentType();
                System.out.println("    Content type: " + contentType);

                if (contentType == ContentType.TRAP) {
                    Trap content = (Trap) square.getContent();

                    // Vérifier si c'est une tornade
                    if (content.getName() == TrapType.TORNADO) {
                        System.out.println("    -> TORNADE TOUCHEE! Activation...");
                        Tornado tornado = (Tornado) content;

                        // Activer la tornade
                        if (!tornado.isActive()) {
                            tornado.activate(config.getGridSize());
                            System.out.println("    -> Tornade activée pour 3 utilisations");

                            String message = "🌪️ TORNADE ACTIVÉE !\n\nLes 3 prochains tirs de " +
                                    (isRobot ? "vous" : "votre adversaire") +
                                    " seront détournés !";
                            if (isRobot) {
                                view.showSuccess(message);
                            }

                            action.append("Tornade touchée ! Les 3 prochains tirs de ").append(isRobot ? "votre adversaire" : "vous").append(" seront détournés !\n");
                        }

                        // Attaquer quand même la case
                        target.receiveAttack(pos);
                        continue;
                    }
                    // Sinon c'est un trou noir
                    else if (content.getName() == TrapType.BLACKHOLE) {
                        System.out.println("    -> TROU NOIR DETECTE!");
                        // Trou noir : l'attaque revient sur l'attaquant

                        // on désactive
                        square.setContent(null);


                        Player attacker = isRobot ? game.getRobot() : game.getPlayer();
                        attacker.receiveAttack(pos);

                        String message = "🕳️ TROU NOIR !\n\nL'attaque revient sur vous en position " +
                                pos.getX() + "," + pos.getY();
                        if (!isRobot) {
                            view.showSuccess(message);
                        }

                        action.append("Trou noir touché ! L'attaque revient sur vous en ").append(pos.getX()).append(",").append(pos.getY()).append("\n");
                        continue;
                    }
                }
            }

            System.out.println("    Attaque de la square...");
            // Attaque normale
            target.receiveAttack(pos);

            // Compter les hits et misses
            if (!square.isEmpty() && square.getContent().getContentType() == ContentType.BOAT) {
                System.out.println("    -> TOUCHE!");

                hits++;
                Boat boat = (Boat) square.getContent();
                if (boat.hasSunk()) {
                    System.out.println("    -> BATEAU COULE!");

                    sunkBoat = true;
                }
            } else {
                System.out.println("    -> A l'eau");
                misses++;
            }
        }

        if (sunkBoat) {
            action.append("Bateau coulé !");
        } else if (hits > 0) {
            action.append("Touché ! (").append(hits).append(" case(s))");
        } else {
            action.append("À l'eau !");
        }

        System.out.println("Résultat: " + action.toString());
        System.out.println("--- FIN executeAttack ---\n");



        if (isRobot) {
            view.setRobotAction(action.toString());
        } else {
            view.setPlayerAction(action.toString());
        }

        // Ajouter à l'historique
        view.appendHistory("Tour " + turnNumber + " - " +
                (isRobot ? "Robot" : game.getPlayer().getUsername()) + ": " +
                action.toString() + "\n");

    }

    private void executeSonar(ArrayList<Position> positions, Player target, boolean isRobot) {
        int boatCells = 0;

        for (Position pos : positions) {

            if (pos.getX() >= 0 && pos.getX() < config.getGridSize() && pos.getY() >= 0 && pos.getY() < config.getGridSize()) {

            Square square = target.getGrid().getSquare(pos);

            // Compter les cases avec contenu (boat ou blackhole)
            if (!square.isEmpty()) {
                ContentType type = square.getContent().getContentType();
                if (type == ContentType.BOAT) {
                    boatCells++;
                } else if (type == ContentType.TRAP) {
                    Trap trap =  (Trap) square.getContent();
                    if(trap.getName() == TrapType.BLACKHOLE) {
                        boatCells++;
                    }
                }
            }
        }
            }

        String action = "Sonar : " + boatCells + " case(s) occupée(s) détectée(s)";

        if (!isRobot) {
            view.showSuccess("📡 SONAR\n\n\nCases occupées: " + boatCells + "/9");
        }

        if (isRobot) {
            view.setRobotAction(action);
        } else {
            view.setPlayerAction(action);
        }

        view.appendHistory("Tour " + turnNumber + " - " +
                (isRobot ? "Robot" : game.getPlayer().getUsername()) + ": " +
                action + "\n");
    }


    private boolean handleIslandSearch(Position target) {
        Player robot = game.getRobot();

        // Vérifier que c'est bien sur l'île
        if (!robot.getGrid().squareIsInIsland(target)) {
            view.showError("Cette case n'est pas sur l'île !");
            return false;
        }

        Square square = robot.getGrid().getSquare(target);

        // Vérifier si déjà fouillée
        if (!square.isNotSearched()) {
            view.showError("Cette case a déjà été fouillée !");
            return false;
        }

        // Fouiller
        Content found = square.search();

        if (found != null && found.getContentType() == ContentType.WEAPON) {
            // Arme trouvée : l'ajouter au joueur
            Weapon foundWeapon = (Weapon) found;
            WeaponType weaponType = foundWeapon.getName();

            int currentCount = game.getPlayer().getWeaponCount(weaponType);
            game.getPlayer().setWeaponCount(weaponType, currentCount + 1);

            view.setPlayerAction("Arme trouvée sur l'île !");
            view.appendHistory("Tour " + turnNumber + " - " + game.getPlayer().getUsername() +
                    ": Arme trouvée en " + target.getX() + "," + target.getY() + "\n");
            updateWeaponsDisplay();
        } else {
            view.setPlayerAction("Case vide sur l'île");
            view.appendHistory("Tour " + turnNumber + " - " + game.getPlayer().getUsername() +
                    ": Case vide en " + target.getX() + "," + target.getY() + "\n");
        }

        return true;
    }

    private void playRobotTurn() {
        System.out.println("\n*** TOUR DU ROBOT ***");


        Player robot = game.getRobot();
        Player player = game.getPlayer();

        System.out.println("Player has tornado: " + (player.getTornado() != null));
        System.out.println("Player tornado active: " + player.hasTornadoActive());




        // tir aléatoire simple (niveau 1) - todo intelligent
        java.util.Random rand = new java.util.Random();
        Position target;
        Square square;

        // Trouver une case non encore attaquée
        do {
            int x = rand.nextInt(config.getGridSize());
            int y = rand.nextInt(config.getGridSize());
            target = new Position(x, y);
            square = player.getGrid().getSquare(target);
        } while (square.wasAttacked());

        System.out.println("Robot vise: " + target.getX() + "," + target.getY());


        // Appliquer la tornade du joueur si active
        if (player.hasTornadoActive()) {
            System.out.println("TORNADE DU JOUEUR ACTIVE!");

            target = player.tornadoTrigger(target);

        }

        // Le robot utilise toujours un missile (pour l'instant, a chager todo)
        WeaponFactory factory = new WeaponFactory();
        Weapon weapon = factory.createMissile();


        ArrayList<Position> positions = weapon.use(target);
        System.out.println("Robot attaque avec un missile");

        executeAttack(positions, player, true);
        System.out.println("*** FIN TOUR DU ROBOT ***\n");

    }

    private void endGame() {
        String winner;
        if (game.getPlayer().allBoatSunk()) {
            winner = "Robot";
        } else {
            winner = game.getPlayer().getUsername();
        }

        // recup les stats depuis les labels de la vue
        int playerBoatsIntact = Integer.parseInt(view.getPlayerBoatsIntactLabel().getText().substring(18));
        int playerBoatsTouched = Integer.parseInt(view.getPlayerBoatsTouchedLabel().getText().substring(18));
        int playerBoatsSunk = Integer.parseInt(view.getPlayerBoatsSunkLabel().getText().substring(17));
        int playerMissedShots = Integer.parseInt(view.getPlayerMissedShotsLabel().getText().substring(18));
        String[] playerHitRatioParts = view.getPlayerHitRatioLabel().getText().split("/");
        int playerHitCells = Integer.parseInt(playerHitRatioParts[0].substring(28));
        int playerTotalCells = Integer.parseInt(playerHitRatioParts[1]);

        int robotBoatsIntact = Integer.parseInt(view.getRobotBoatsIntactLabel().getText().substring(18));
        int robotBoatsTouched = Integer.parseInt(view.getRobotBoatsTouchedLabel().getText().substring(18));
        int robotBoatsSunk = Integer.parseInt(view.getRobotBoatsSunkLabel().getText().substring(17));
        int robotMissedShots = Integer.parseInt(view.getRobotMissedShotsLabel().getText().substring(18));
        String[] robotHitRatioParts = view.getRobotHitRatioLabel().getText().split("/");
        int robotHitCells = Integer.parseInt(robotHitRatioParts[0].substring(28));
        int robotTotalCells = Integer.parseInt(robotHitRatioParts[1]);

        EndView endView = new EndView( winner, turnNumber, playerBoatsIntact, playerBoatsTouched, playerBoatsSunk, playerMissedShots, playerHitCells, playerTotalCells, robotBoatsIntact, robotBoatsTouched, robotBoatsSunk, robotMissedShots, robotHitCells, robotTotalCells, game.getPlayer().getUsername() );

        endView.addQuitListener(e -> quit());
        endView.addRestartListener(e -> restart());

        endView.setVisible(true);
        view.dispose();
    }



    public void restart() {
        // TODO
    }

    public void quit() {
        System.exit(0);
    }

    public boolean hasIsland(){
        return this.config.getModeGame() == ModeGame.ISLAND;
    }

    public int getNumberBoats(){
        return this.config.getNumberBoatsTotal();
    }

    public int getNumberBoatSquares(){
        return this.config.getTotalBoatSquares();
    }

}
