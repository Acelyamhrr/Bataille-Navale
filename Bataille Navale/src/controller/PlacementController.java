package controller;

import model.enums.*;
import model.game.GameConfig;
import model.game.GamePlacement;
import model.grid.Position;
import view.PlacementView;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Controller pour le placement des bateaux et pièges.
 * Gère le placement pour le joueur ET le robot.
 */
public class PlacementController {
    private PlacementView view;
    private final GameConfig config;

    // Données de placement joueur
    private Map<BoatName, List<Position>> playerBoats = new HashMap<>();
    private Map<TrapType, List<Position>> playerTraps = new HashMap<>();
    private Map<WeaponType, List<Position>> playerWeapons = new HashMap<>();
    private List<BoatName> boatsToPlace = new ArrayList<>();
    private List<TrapType> trapsToPlace = new ArrayList<>();
    private List<WeaponType> weaponsToPlace = new ArrayList<>();
    private int currentBoatIndex = 0;
    private int currentTrapIndex = 0;
    private int currentWeaponIndex = 0;
    private boolean currentPlacementBoat;

    // Données de placement robot
    private Map<BoatName, List<Position>> robotBoats = new HashMap<>();
    private Map<TrapType, List<Position>> robotTraps = new HashMap<>();
    private Map<WeaponType, List<Position>> robotWeapons = new HashMap<>();

    public PlacementController(GameConfig config) {
        this.config = config;
    }

    public void setView(PlacementView view){
        this.view = view;
    }

    /**
     * Initialise le placement selon le mode de piège configuré.
     */
    public void initializePlacement() {
        initBoatsToPlace();
        initTrapsToPlace();

        if(config.getModeGame() == ModeGame.ISLAND) {
            initWeaponsToPlace();
        }

        switch (config.getTrapMode()) {
            case RANDOM, MANUAL:
                currentPlacementBoat = true;
                updateBoatSelector();
                updateGrid();
                break;
            case FIXED:
                currentPlacementBoat = false;
                applyFixedPlacementTraps();
                updateGrid();
                currentPlacementBoat = true;
                updateBoatSelector();
                break;
        }
    }

    // clics

    public void onGridClick(int x, int y) {
        if (currentPlacementBoat) {
            handleBoatPlacement(x, y);
        } else {
            if(config.getModeGame() ==  ModeGame.ISLAND) {
                if(currentTrapIndex >= trapsToPlace.size()) {
                    handleWeaponPlacementIsland(x, y);
                }
                else {
                    handleTrapPlacementIsland(x, y);
                }
            }
            else {
                handleTrapPlacement(x, y);
            }
        }
    }

    private void handleBoatPlacement(int x, int y) {
        if (currentBoatIndex >= boatsToPlace.size()) return;

        BoatName boat = boatsToPlace.get(currentBoatIndex);
        Orientation orient = view.isHorizontal() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

        if (canPlaceBoat(boat, x, y, orient, playerBoats, playerTraps, playerWeapons)) {
            if (!playerBoats.containsKey(boat)) playerBoats.put(boat, new ArrayList<>());
            playerBoats.get(boat).add(new Position(x, y, orient));
            currentBoatIndex++;
            view.setInfoText("Bateau placé !");
            updateBoatSelector();
            updateGrid();

            if (currentBoatIndex >= boatsToPlace.size()) {
                view.setInfoText("Tous les bateaux sont placés !");
                currentPlacementBoat = false;
                view.enableBoatSelector(false);
                view.enableTrapWeaponSelector(true);

                if(config.getModeGame() == ModeGame.STANDARD){
                    if (config.getTrapMode() == TrapPlacement.RANDOM) {
                        applyRandomPlacementTraps();
                    } else if (config.getTrapMode() == TrapPlacement.MANUAL) {
                        view.setPhaseText("Phase: Placement des pièges");
                        view.showSuccess("Placez les pièges.");
                        updateTrapWeaponSelector();
                    }
                }
                else{
                    if (config.getTrapMode() == TrapPlacement.RANDOM) {
                        applyRandomIsland();
                    } else if (config.getTrapMode() == TrapPlacement.MANUAL) {
                        playerWeapons.clear();
                        currentWeaponIndex = 0;
                        playerTraps.clear();
                        currentTrapIndex = 0;
                        view.setPhaseText("Phase: Placement des pièges et des armes");
                        view.showSuccess("Placez les armes/pièges.");
                        updateTrapWeaponSelector();
                    }
                }
            }
        } else {
            view.setInfoText("Placement invalide !");
        }
    }

    private void handleTrapPlacement(int x, int y) {
        if (currentTrapIndex >= trapsToPlace.size()) return;
        TrapType type = trapsToPlace.get(currentTrapIndex);

        if (canPlaceTrap(type, x, y, playerBoats, playerTraps, playerWeapons)) {
            if(!playerTraps.containsKey(type)) {
                playerTraps.put(type, new ArrayList<>());
            }
            playerTraps.get(type).add(new Position(x, y));
            currentTrapIndex++;
            view.setInfoText("Piège placé !");
            updateTrapWeaponSelector();
            updateGrid();

            if (currentTrapIndex >= trapsToPlace.size()) {
                view.setInfoText("Tous les pièges sont placés !");
            }
        } else {
            view.setInfoText("Placement invalide !");
        }
    }

    private void handleTrapPlacementIsland(int x, int y) {
        if (currentTrapIndex >= trapsToPlace.size()) return;
        TrapType type = trapsToPlace.get(currentTrapIndex);

        if(canPlaceOnIsland(x, y, playerBoats, playerTraps, playerWeapons)) {
            if(!playerTraps.containsKey(type)) {
                playerTraps.put(type, new ArrayList<>());
            }
            playerTraps.get(type).add(new Position(x, y));
            currentTrapIndex++;
            view.setInfoText("Piège placé !");
            updateTrapWeaponSelector();
            updateGrid();

            if(currentTrapIndex >= trapsToPlace.size()) {
                view.setInfoText("Pièges placés, veuillez placer les armes !");
            }
        }
        else{
            view.setInfoText("Placement invalide !");
        }
    }

    private void handleWeaponPlacementIsland(int x, int y){
        if(currentWeaponIndex >=  weaponsToPlace.size()) return;
        WeaponType type = weaponsToPlace.get(currentWeaponIndex);

        if(canPlaceOnIsland(x, y, playerBoats, playerTraps, playerWeapons)) {
            if(!playerWeapons.containsKey(type)) {
                playerWeapons.put(type, new ArrayList<>());
            }
            playerWeapons.get(type).add(new Position(x, y));
            currentWeaponIndex++;
            view.setInfoText("Arme placé !");
            updateTrapWeaponSelector();
            updateGrid();

            if(currentWeaponIndex >=  weaponsToPlace.size()) {
                view.setInfoText("Toutes les armes sont placées");
            }
        }
        else{
            view.setInfoText("Placement invalide !");
        }
    }

    // modes
    private boolean placeBoat(BoatName boat, int x, int y, Orientation orientation,  Map<BoatName, List<Position>> boats,  Map<TrapType, List<Position>> traps, Map<WeaponType, List<Position>> weapons){
        if (canPlaceBoat(boat, x, y, Orientation.VERTICAL, boats, traps, weapons)) {
            if (!boats.containsKey(boat)) boats.put(boat, new ArrayList<>());
            boats.get(boat).add(new Position(x, y, orientation));
            return true;
        }

        return false;
    }

    private boolean placeTrap(TrapType trap, int x, int y, Map<BoatName, List<Position>> boats,  Map<TrapType, List<Position>> traps, Map<WeaponType, List<Position>> weapons){
        if (canPlaceTrap(trap, x, y, boats, traps, weapons)) {
            if(!traps.containsKey(trap)) {
                traps.put(trap, new ArrayList<>());
            }
            traps.get(trap).add(new Position(x, y));
            return true;
        }
        return false;
    }

    private boolean placeTrapOnIsland(TrapType trap, int x, int y, Map<BoatName, List<Position>> boats,  Map<TrapType, List<Position>> traps, Map<WeaponType, List<Position>> weapons){
        if(canPlaceOnIsland(x, y, boats, traps, weapons)){
            if(!traps.containsKey(trap)) {
                traps.put(trap, new ArrayList<>());
            }
            traps.get(trap).add(new Position(x, y));
            return true;
        }
        return false;
    }

    private boolean placeWeaponOnIsland(WeaponType weapon, int x, int y, Map<BoatName, List<Position>> boats,  Map<TrapType, List<Position>> traps, Map<WeaponType, List<Position>> weapons){
        if(canPlaceOnIsland(x, y, boats, traps, weapons)){
            if(!weapons.containsKey(weapon)){
                weapons.put(weapon, new  ArrayList<>());
            }
            weapons.get(weapon).add(new Position(x, y));
            return true;
        }
        return false;
    }

    public void applyFixedPlacement() {
        view.setPhaseText("Phase: Placement des bateaux");

        // Réinitialisation pièges / armes
        if (config.getTrapMode() == TrapPlacement.MANUAL || config.getTrapMode() == TrapPlacement.RANDOM) {
            playerTraps.clear();
            currentTrapIndex = 0;
            playerWeapons.clear();
            currentWeaponIndex = 0;
            currentPlacementBoat = true;
        }

        // Placement des bateaux selon présence d'île
        if (config.getModeGame() == ModeGame.ISLAND) {
            applyFixedPlacementWithIsland();
        } else {
            applyFixedPlacementWithoutIsland();
        }

        currentBoatIndex = boatsToPlace.size();

        view.enableBoatSelector(false);
        view.enableTrapWeaponSelector(true);

        // Phase pièges / armes
        if (config.getTrapMode() == TrapPlacement.MANUAL) {
            currentPlacementBoat = false;
            if (config.getModeGame() == ModeGame.STANDARD) {
                view.setPhaseText("Phase: Placement des pièges");
                view.showSuccess("Placez les pièges.");
            } else {
                view.setPhaseText("Phase: Placement des pièges et des armes");
                view.showSuccess("Placez les pièges/armes sur l'île.");
            }
        }

        updateBoatSelector();
        updateTrapWeaponSelector();
        updateGrid();
    }

    private void applyFixedPlacementWithIsland() {
        playerBoats.clear();

        int gridSize = config.getGridSize();
        int islandSize = 4;
        int margin = (gridSize - islandSize) / 2;

        for (BoatName boat : boatsToPlace) {
            boolean placed = false;

            // Lignes du haut (horizontal)
            for (int y = 0; y < margin && !placed; y++) {
                for (int x = 0; x < gridSize && !placed; x++) {
                    if(placeBoat(boat, x, y, Orientation.HORIZONTAL, playerBoats, playerTraps, playerWeapons)){
                        placed = true;
                    }
                }
            }

            // Lignes du bas (horizontal)
            for (int y = gridSize - margin; y < gridSize && !placed; y++) {
                for (int x = 0; x < gridSize && !placed; x++) {
                    if(placeBoat(boat, x, y, Orientation.HORIZONTAL, playerBoats, playerTraps, playerWeapons)){
                        placed = true;
                    }
                }
            }

            // Colonnes de gauche (vertical)
            for (int x = 0; x < margin && !placed; x++) {
                for (int y = 0; y < gridSize && !placed; y++) {
                    if(placeBoat(boat, x, y, Orientation.VERTICAL, playerBoats, playerTraps, playerWeapons)) {
                        placed = true;
                    }
                }
            }

            // Colonnes de droite (vertical)
            for (int x = gridSize - margin; x < gridSize && !placed; x++) {
                for (int y = 0; y < gridSize && !placed; y++) {
                    if(placeBoat(boat, x, y, Orientation.VERTICAL, playerBoats, playerTraps, playerWeapons)) {
                        placed = true;
                    }
                }
            }

            if (!placed) {
                view.showError("Impossible de placer le bateau : " + boat);
            }
        }
    }

    private void applyFixedPlacementWithoutIsland() {
        playerBoats.clear();

        int gridSize = config.getGridSize();
        List<Position> startPositions = getBalancedStartPositionsPlayer(gridSize); // même que pour le robot
        int startIndex = 0;

        for (BoatName boat : boatsToPlace) {
            boolean placed = false;
            int boatSize = getBoatSize(boat);

            // Nouvelle zone pour chaque bateau
            Position start = startPositions.get(startIndex % startPositions.size());
            startIndex++;

            // HORIZONTAL
            int maxX = gridSize - boatSize;
            int maxY = gridSize - 1;
            for (int dy = 0; dy <= maxY && !placed; dy++) {
                for (int dx = 0; dx <= maxX && !placed; dx++) {
                    int x = (start.getX() + dx) % gridSize;
                    int y = (start.getY() + dy) % gridSize;

                    if(placeBoat(boat, x, y, Orientation.HORIZONTAL, playerBoats, playerTraps, playerWeapons)) {
                        placed = true;
                        break;
                    }
                }
            }

            // VERTICAL
            if (!placed) {
                maxX = gridSize - 1;
                maxY = gridSize - boatSize;
                for (int dy = 0; dy <= maxY && !placed; dy++) {
                    for (int dx = 0; dx <= maxX && !placed; dx++) {
                        int x = (start.getX() + dx) % gridSize;
                        int y = (start.getY() + dy) % gridSize;

                        if(placeBoat(boat, x, y, Orientation.VERTICAL, playerBoats, playerTraps, playerWeapons)) {
                            placed = true;
                            break;
                        }
                    }
                }
            }

            if (!placed) {
                view.showError("Impossible de placer le bateau : " + boat);
            }
        }
    }

    private List<Position> getBalancedStartPositionsPlayer(int gridSize) {
        int mid = gridSize / 2;
        List<Position> starts = new ArrayList<>();
        starts.add(new Position(gridSize - 1, gridSize - 1, Orientation.VERTICAL)); // bas-droite en premier
        starts.add(new Position(0, 0, Orientation.HORIZONTAL));                     // haut-gauche ensuite
        starts.add(new Position(0, gridSize - 1, Orientation.HORIZONTAL));          // bas-gauche
        starts.add(new Position(gridSize - 1, 0, Orientation.VERTICAL));            // haut-droite
        starts.add(new Position(mid, mid, Orientation.HORIZONTAL));                 // centre
        return starts;
    }

    public void applyRandomPlacement() {
        view.setPhaseText("Phase: Placement des bateaux");
        if (config.getTrapMode() == TrapPlacement.MANUAL || config.getTrapMode() == TrapPlacement.RANDOM) {
            playerTraps.clear();
            currentTrapIndex = 0;
            playerWeapons.clear();
            currentWeaponIndex = 0;
            currentPlacementBoat = true;
        }

        playerBoats.clear();
        Random rand = new Random();
        int gridSize = config.getGridSize();

        for (BoatName boat : boatsToPlace) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(gridSize);
                int y = rand.nextInt(gridSize);
                Orientation o = rand.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

                if(placeBoat(boat, x, y, o, playerBoats, playerTraps, playerWeapons)) {
                    placed = true;
                }
                attempts++;
            }
        }
        currentBoatIndex = boatsToPlace.size();

        view.enableBoatSelector(false);
        view.enableTrapWeaponSelector(true);


        if (config.getTrapMode() == TrapPlacement.MANUAL) {
            currentPlacementBoat = false;
            if (config.getModeGame() == ModeGame.STANDARD) {
                view.setPhaseText("Phase: Placement des pièges");
                view.showSuccess("Placez les pièges.");
            } else {
                view.setPhaseText("Phase: Placement des pièges et des armes");
                view.showSuccess("Placez les pièges/armes sur l'île.");
            }
        }

        updateBoatSelector();
        updateTrapWeaponSelector();
        updateGrid();
    }

    public void enableManualPlacement() {
        playerBoats.clear();
        currentBoatIndex = 0;
        currentPlacementBoat = true;

        view.enableBoatSelector(true);
        view.enableTrapWeaponSelector(false);

        if (config.getTrapMode() == TrapPlacement.MANUAL || config.getTrapMode() == TrapPlacement.RANDOM) {
            playerTraps.clear();
            currentTrapIndex = 0;
            playerWeapons.clear();
            currentWeaponIndex = 0;
        }
        updateBoatSelector();
        updateTrapWeaponSelector();
        updateGrid();
    }

    // pièges

    private void applyFixedPlacementTraps() {
        playerTraps.clear();
        int x = 3;
        int y = 3;

        for (TrapType trap : trapsToPlace) {
            if(placeTrap(trap, x+1, y+1, playerBoats, playerTraps, playerWeapons)) {
                x++;
                y++;
            }else {
                if(placeTrap(trap, x+2, y+2, playerBoats, playerTraps, playerWeapons)) {
                    x+=2;
                    y+=2;
                }
            }
        }

        currentTrapIndex = trapsToPlace.size();
        updateGrid();
    }

    private void applyRandomPlacementTraps() {
        playerTraps.clear();
        Random rand = new Random();
        int gridSize = config.getGridSize();

        for (TrapType trap : trapsToPlace) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(gridSize);
                int y = rand.nextInt(gridSize);

                if(placeTrap(trap, x, y, playerBoats, playerTraps, playerWeapons)) {
                    placed = true;
                }
                attempts++;
            }
        }

        currentTrapIndex = trapsToPlace.size();
        view.setTrapWeaponOptions(new String[]{"Tous placés !"});
        updateGrid();
    }

    private void applyRandomIsland(){
        playerTraps.clear();
        Random rand = new Random();
        int ix = this.config.getGridSize() / 2 - 2;
        int iy = this.config.getGridSize() / 2 - 2;

        for(TrapType trap : trapsToPlace){
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(ix, ix+4);
                int y = rand.nextInt(iy, iy+4);

                if(placeTrapOnIsland(trap, x, y, playerBoats, playerTraps, playerWeapons)) {
                    placed = true;
                }
                attempts++;
            }
        }
        currentTrapIndex = trapsToPlace.size();

        playerWeapons.clear();
        for(WeaponType weapon : weaponsToPlace){
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(ix, ix+4);
                int y = rand.nextInt(iy, iy+4);

                if(placeWeaponOnIsland(weapon,  x, y, playerBoats, playerTraps, playerWeapons)) {
                    placed = true;
                }
                attempts++;
            }
        }

        currentWeaponIndex = weaponsToPlace.size();
        view.setTrapWeaponOptions(new String[]{"Tous placés !"});
        updateGrid();
    }

    // placements robot.
    private List<Position> getBalancedStartPositionsRobot(int gridSize) {
        int mid = gridSize / 2;
        List<Position> starts = new ArrayList<>();
        starts.add(new Position(0, 0, Orientation.HORIZONTAL));
        starts.add(new Position(gridSize - 1, gridSize - 1, Orientation.VERTICAL));
        starts.add(new Position(gridSize - 1, 0, Orientation.VERTICAL));
        starts.add(new Position(0, gridSize - 1, Orientation.HORIZONTAL));
        starts.add(new Position(mid, mid, Orientation.HORIZONTAL));
        return starts;
    }



    private void applyFixedBoatsRobot() {
        if (config.getModeGame() == ModeGame.ISLAND) {
            applyFixedBoatsWithIsland();
        } else {
            applyFixedBoatsWithoutIsland();
        }
    }

    private void applyFixedBoatsWithoutIsland() {
        robotBoats.clear();

        int gridSize = config.getGridSize();
        List<Position> startPositions = getBalancedStartPositionsRobot(gridSize);
        int startIndex = 0;

        for (BoatName boat : boatsToPlace) {
            boolean placed = false;
            int boatSize = getBoatSize(boat);

            // On change de zone à chaque bateau
            Position start = startPositions.get(startIndex % startPositions.size());
            startIndex++;

            // Balayage à partir du point de départ choisi
            // Test horizontal et vertical séparément pour gérer maxX/maxY
            // HORIZONTAL
            int maxX = gridSize - boatSize;
            int maxY = gridSize - 1;
            for (int dy = 0; dy <= maxY && !placed; dy++) {
                for (int dx = 0; dx <= maxX && !placed; dx++) {
                    int x = (start.getX() + dx) % gridSize;
                    int y = (start.getY() + dy) % gridSize;

                    if(placeBoat(boat, x, y, Orientation.HORIZONTAL, robotBoats, robotTraps, robotWeapons)) {
                        placed = true;
                        break;
                    }
                }
            }

            // VERTICAL
            if (!placed) {
                maxX = gridSize - 1;
                maxY = gridSize - boatSize;
                for (int dy = 0; dy <= maxY && !placed; dy++) {
                    for (int dx = 0; dx <= maxX && !placed; dx++) {
                        int x = (start.getX() + dx) % gridSize;
                        int y = (start.getY() + dy) % gridSize;

                        if(placeBoat(boat, x, y, Orientation.VERTICAL, robotBoats, robotTraps, robotWeapons)) {
                            placed = true;
                            break;
                        }
                    }
                }
            }

            if (!placed) {
                view.showError("Impossible de placer le bateau : " + boat);
            }
        }
    }



    private void applyFixedBoatsWithIsland() {
        robotBoats.clear();

        int gridSize = config.getGridSize();
        int islandSize = 4;
        int margin = (gridSize - islandSize) / 2;

        for (BoatName boat : boatsToPlace) {
            boolean placed = false;

            // Lignes du haut (horizontal)
            for (int y = 0; y < margin && !placed; y++) {
                for (int x = 0; x < gridSize && !placed; x++) {

                    if(placeBoat(boat, x, y, Orientation.HORIZONTAL, robotBoats, robotTraps, robotWeapons)){
                        placed = true;
                    }
                }
            }

            // Lignes du bas (horizontal)
            for (int y = gridSize - margin; y < gridSize && !placed; y++) {
                for (int x = 0; x < gridSize && !placed; x++) {
                    if(placeBoat(boat, x, y, Orientation.HORIZONTAL, robotBoats, robotTraps, robotWeapons)){
                        placed = true;
                    }
                }
            }

            // Colonnes de gauche (vertical)
            for (int x = 0; x < margin && !placed; x++) {
                for (int y = 0; y < gridSize && !placed; y++) {
                    if(placeBoat(boat, x, y, Orientation.VERTICAL, robotBoats, robotTraps, robotWeapons)){
                        placed = true;
                    }
                }
            }

            // Colonnes de droite (vertical)
            for (int x = gridSize - margin; x < gridSize && !placed; x++) {
                for (int y = 0; y < gridSize && !placed; y++) {
                    if(placeBoat(boat, x, y, Orientation.VERTICAL, robotBoats, robotTraps, robotWeapons)){
                        placed = true;
                    }
                }
            }

            if (!placed) {
                view.showError("Impossible de placer le bateau : " + boat);
            }
        }
    }


    private void applyRandomBoatsRobot() {
        robotBoats.clear();
        Random rand = new Random();
        int gridSize = config.getGridSize();

        for (BoatName boat : boatsToPlace) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(gridSize);
                int y = rand.nextInt(gridSize);
                Orientation o = rand.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

                if(placeBoat(boat, x, y, o, robotBoats, robotTraps, robotWeapons)){
                    placed = true;
                }
                attempts++;
            }
        }
    }

    private void applyFixedTrapsRobot() {
        robotTraps.clear();
        int x = 3;
        int y = 3;

        for (TrapType trap : trapsToPlace) {
            if(placeTrap(trap, x+1, y+1, robotBoats, robotTraps, robotWeapons)){
                x++;
                y++;
            }else {
                if(placeTrap(trap, x+2, y+2, robotBoats, robotTraps, robotWeapons)){
                    x+=2;
                    y+=2;
                }
            }
        }
    }

    private void applyRandomTrapsRobot() {
        robotTraps.clear();
        Random rand = new Random();
        int gridSize = config.getGridSize();

        for (TrapType trap : trapsToPlace) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(gridSize);
                int y = rand.nextInt(gridSize);

                if(placeTrap(trap, x, y, robotBoats, robotTraps, robotWeapons)){
                    placed = true;
                }
                attempts++;
            }
        }
    }

    private void applyRandomIslandRobot(){
        robotTraps.clear();
        Random rand = new Random();
        int ix = this.config.getGridSize() / 2 - 2;
        int iy = this.config.getGridSize() / 2 - 2;

        for(TrapType trap : trapsToPlace){
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(ix, ix+4);
                int y = rand.nextInt(iy, iy+4);

                if(placeTrapOnIsland(trap, x, y, robotBoats,  robotTraps, robotWeapons)){
                    placed = true;
                }
                attempts++;
            }
        }

        robotWeapons.clear();
        for(WeaponType weapon : weaponsToPlace){
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = rand.nextInt(ix, ix+4);
                int y = rand.nextInt(iy, iy+4);

                if(placeWeaponOnIsland(weapon,  x, y, robotBoats, robotTraps, robotWeapons)) {
                    placed = true;
                }
                attempts++;
            }
        }
    }

    // valider et créer

    /**
     * Valide le placement et crée le GamePlacement.
     * @return GamePlacement si valide, null sinon (avec affichage d'erreur)
     */
    public GamePlacement validateAndCreatePlacement() {
        if (currentBoatIndex < boatsToPlace.size()) {
            view.showError("Placez tous les bateaux d'abord !");
            return null;
        } else if (currentTrapIndex < trapsToPlace.size()) {
            view.showError("Placez tous les pièges d'abord !");
            return null;
        }

        if(config.getModeGame() == ModeGame.ISLAND && currentWeaponIndex < weaponsToPlace.size()) {
            view.showError("Placez toutes les armes d'abord !");
            return null;
        }

        // Créer placement robot
        String modeBoat = view.getModeBoat();
        if (config.getTrapMode() == TrapPlacement.FIXED) {
            applyFixedTrapsRobot();
            if (modeBoat.equals("Random")) {
                applyRandomBoatsRobot();
            } else {
                applyFixedBoatsRobot();
            }
        } else {
            if (modeBoat.equals("Random")) {
                applyRandomBoatsRobot();
            } else {
                applyFixedBoatsRobot();
            }
            if(config.getModeGame() == ModeGame.STANDARD){
                applyRandomTrapsRobot();
            }
        }

        if(config.getModeGame() == ModeGame.ISLAND){
            applyRandomIslandRobot();
        }

        // Créer GamePlacement
        GamePlacement placement = new GamePlacement();

        for (Map.Entry<BoatName, List<Position>> entry : playerBoats.entrySet()) {
            for (Position pos : entry.getValue()) {
                placement.setBoatPlacementPlayer(entry.getKey(), pos);
            }
        }

        for (Map.Entry<TrapType, List<Position>> entry : playerTraps.entrySet()) {
            for(Position pos : entry.getValue()) {
                placement.setTrapPlacementPlayer(entry.getKey(), pos);
            }
        }

        for(Map.Entry<WeaponType, List<Position>> entry : playerWeapons.entrySet()) {
            for(Position pos : entry.getValue()) {
                placement.setWeaponPlacementPlayer(entry.getKey(), pos);
            }
        }

        for (Map.Entry<BoatName, List<Position>> entry : robotBoats.entrySet()) {
            for (Position pos : entry.getValue()) {
                placement.setBoatPlacementRobot(entry.getKey(), pos);
            }
        }

        for (Map.Entry<TrapType, List<Position>> entry : robotTraps.entrySet()) {
            for(Position pos : entry.getValue()) {
                placement.setTrapPlacementRobot(entry.getKey(), pos);
            }
        }

        for(Map.Entry<WeaponType, List<Position>> entry : robotWeapons.entrySet()) {
            for(Position pos : entry.getValue()) {
                placement.setWeaponPlacementRobot(entry.getKey(), pos);
            }
        }

        return placement;
    }

    // mettre à jour l'affichage

    public void updateGrid() {
        int size = config.getGridSize();

        // Reset
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                view.setCellColor(x, y, "water");
            }
        }

        // Squares island
        if(this.config.getModeGame() == ModeGame.ISLAND) {
            int ix = this.config.getGridSize() / 2 - 2;
            int iy = this.config.getGridSize() / 2 - 2;
            for (int i = ix; i < ix + 4; i++) {
                for (int j = iy; j < iy + 4; j++) {
                    view.setCellColor(i, j, "island");
                    view.setCellText(i, j, "");
                }
            }
        }

        // Bateaux placés
        for (Map.Entry<BoatName, List<Position>> entry : playerBoats.entrySet()) {
            for (Position pos : entry.getValue()) {
                int boatSize = getBoatSize(entry.getKey());
                for (int i = 0; i < boatSize; i++) {
                    int bx = pos.getOrientation() == Orientation.HORIZONTAL ? pos.getX() + i : pos.getX();
                    int by = pos.getOrientation() == Orientation.VERTICAL ? pos.getY() + i : pos.getY();
                    view.setCellColor(bx, by, "boat");
                }
            }
        }

        // Pièges placés
        for (Map.Entry<TrapType, List<Position>> entry : playerTraps.entrySet()) {
            for(Position pos : entry.getValue()) {
                view.setCellColor(pos.getX(), pos.getY(), "trap");

                String text;
                if(entry.getKey() == TrapType.BLACKHOLE){
                    text = "Trou noir";
                }
                else{
                    text = "Tornade";
                }
                view.setCellText(pos.getX(), pos.getY(), text);
            }
        }

        //Armes placées
        for (Map.Entry<WeaponType, List<Position>> entry : playerWeapons.entrySet()) {
            for(Position pos : entry.getValue()) {
                view.setCellColor(pos.getX(), pos.getY(), "weapon");

                String text;
                if(entry.getKey() == WeaponType.BOMB){
                    text = "Bombe";
                }
                else{
                    text = "Sonar";
                }
                view.setCellText(pos.getX(), pos.getY(), text);
            }
        }

        // Preview hover
        if (currentPlacementBoat) {
            previewBoatHover(size);
        } else {
            if(currentTrapIndex < trapsToPlace.size()) {
                previewTrapHover(config.getModeGame() == ModeGame.ISLAND);
            }
            else{
                previewWeaponHover();
            }
        }

        if (currentBoatIndex >= boatsToPlace.size() && currentTrapIndex == 0 && config.getTrapMode() == TrapPlacement.RANDOM) {
            if(config.getModeGame() == ModeGame.ISLAND) {
                applyRandomIsland();
            }
            else {
                applyRandomPlacementTraps();
            }
        }
    }

    private void previewBoatHover(int size) {
        int hx = view.getHoverX();
        int hy = view.getHoverY();
        if (hx >= 0 && hy >= 0 && currentBoatIndex < boatsToPlace.size()) {
            BoatName currentBoat = boatsToPlace.get(currentBoatIndex);
            Orientation orient = view.isHorizontal() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            boolean canPlace = canPlaceBoat(currentBoat, hx, hy, orient, playerBoats, playerTraps, playerWeapons);
            int boatSize = getBoatSize(currentBoat);

            for (int i = 0; i < boatSize; i++) {
                int px = orient == Orientation.HORIZONTAL ? hx + i : hx;
                int py = orient == Orientation.VERTICAL ? hy + i : hy;
                if (px < size && py < size && !isCellOccupied(px, py, playerBoats, playerTraps, playerWeapons)) {
                    view.setCellColor(px, py, canPlace ? "previewOk" : "previewBad");
                }
            }
        }
    }

    private void previewTrapHover(boolean island) {
        int hx = view.getHoverX();
        int hy = view.getHoverY();
        if (hx >= 0 && hy >= 0 && currentTrapIndex < trapsToPlace.size()) {
            TrapType currentTrap = trapsToPlace.get(currentTrapIndex);
            boolean canPlace;
            if(island) {
                canPlace = canPlaceOnIsland(hx, hy, playerBoats, playerTraps, playerWeapons);
            }
            else {
                canPlace = canPlaceTrap(currentTrap, hx, hy, playerBoats, playerTraps, playerWeapons);
            }
            view.setCellColor(hx, hy, canPlace ? "previewOk" : "previewBad");
        }
    }

    private void previewWeaponHover() {
        int hx = view.getHoverX();
        int hy = view.getHoverY();

        if (hx >= 0 && hy >= 0 && currentWeaponIndex < weaponsToPlace.size()) {
            WeaponType currentWeapon = weaponsToPlace.get(currentWeaponIndex);
            boolean canPlace = canPlaceOnIsland(hx, hy, playerBoats, playerTraps, playerWeapons);

            view.setCellColor(hx, hy, canPlace ? "previewOk" : "previewBad");
        }
    }

    private void updateBoatSelector() {
        List<String> options = new ArrayList<>();
        String[] names = {"Porte-avions (5)", "Croiseur (4)", "Destroyer (3)", "Sous-marin (3)", "Torpilleur (2)"};
        int[] counts = config.getNumberBoat();
        int[] placed = new int[5];

        for (BoatName b : playerBoats.keySet()) placed[b.ordinal()]++;

        for (int i = 0; i < 5; i++) {
            int remaining = counts[i] - placed[i];
            if (remaining > 0) options.add(names[i] + " - " + remaining + " restant(s)");
        }

        if (options.isEmpty()) options.add("Tous placés !");
        view.setBoatOptions(options.toArray(new String[0]));
    }

    private void updateTrapWeaponSelector() {
        List<String> options = new ArrayList<>();

        if(config.getModeGame() == ModeGame.ISLAND) {
            // Mode île : pièges puis armes
            String[] trapNames = {"Trou noir", "Tornade"};
            int[] trapPlaced = new int[2];
            for (TrapType t : playerTraps.keySet()) {
                trapPlaced[t.ordinal()] += playerTraps.get(t).size();
            }

            for (int i = 0; i < trapsToPlace.size(); i++) {
                TrapType trap = trapsToPlace.get(i);
                if (i >= currentTrapIndex) {
                    options.add(trapNames[trap.ordinal()] + " (Piège)");
                }
            }

            // Armes
            if(currentTrapIndex >= trapsToPlace.size()) {
                String[] weaponNames = {"Missile", "Bombe", "Sonar"};
                for (int i = 0; i < weaponsToPlace.size(); i++) {
                    WeaponType weapon = weaponsToPlace.get(i);
                    if (i >= currentWeaponIndex) {
                        options.add(weaponNames[weapon.ordinal()] + " (Arme)");
                    }
                }
            }
        } else {
            // Mode standard : seulement les pièges
            String[] trapNames = {"Trou noir", "Tornade"};
            for (int i = 0; i < trapsToPlace.size(); i++) {
                TrapType trap = trapsToPlace.get(i);
                if (i >= currentTrapIndex) {
                    options.add(trapNames[trap.ordinal()] + " (Piège)");
                }
            }
        }

        if (options.isEmpty()) options.add("Tous placés !");
        view.setTrapWeaponOptions(options.toArray(new String[0]));
    }

    // init

    private void initBoatsToPlace() {
        boatsToPlace.clear();
        playerBoats.clear();
        int[] counts = config.getNumberBoat();
        BoatName[] types = {BoatName.AIRCRAFT_CARRIER, BoatName.CRUISER,
                BoatName.DESTROYER, BoatName.SUBMARINE, BoatName.TORPEDO_BOAT};
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < counts[i]; j++) {
                boatsToPlace.add(types[i]);
            }
        }
        currentBoatIndex = 0;
    }

    private void initTrapsToPlace() {
        trapsToPlace.clear();
        playerTraps.clear();
        TrapType[] types = {TrapType.BLACKHOLE, TrapType.TORNADO};
        for (int i = 0; i < types.length; i++) {
            trapsToPlace.add(types[i]);
        }
        currentTrapIndex = 0;
    }

    private void initWeaponsToPlace(){
        weaponsToPlace.clear();
        playerWeapons.clear();
        WeaponType[] types = {WeaponType.BOMB, WeaponType.SONAR};
        for (int i = 0; i < types.length; i++) {
            weaponsToPlace.add(types[i]);
        }
        currentWeaponIndex = 0;
    }

    // valider placements

    private boolean canPlaceBoat(BoatName boat, int x, int y, Orientation orient, Map<BoatName, List<Position>> boats, Map<TrapType, List<Position>> traps, Map<WeaponType, List<Position>> weapons) {
        int size = getBoatSize(boat);
        int gridSize = config.getGridSize();

        if (orient == Orientation.HORIZONTAL && x + size > gridSize) return false;
        if (orient == Orientation.VERTICAL && y + size > gridSize) return false;

        for (int i = 0; i < size; i++) {
            int cx = orient == Orientation.HORIZONTAL ? x + i : x;
            int cy = orient == Orientation.VERTICAL ? y + i : y;

            if (squareInIsland(cx, cy)) return false;

            if (isCellOccupied(cx, cy, boats, traps, weapons)) return false;
        }
        return true;
    }

    public boolean squareInIsland(int cx, int cy) {
        if(this.config.getModeGame() == ModeGame.ISLAND) {
            int ix = this.config.getGridSize() / 2 - 2;
            int iy = this.config.getGridSize() / 2 - 2;

            return (cx >= ix && cx < ix + 4) && (cy >= iy && cy < iy + 4);
        }
        return false;
    }

    private boolean canPlaceTrap(TrapType type, int x, int y, Map<BoatName, List<Position>> boats, Map<TrapType, List<Position>> traps, Map<WeaponType, List<Position>> weapons) {
        int gridSize = config.getGridSize();

        if (x >= gridSize || y >= gridSize) return false;

        if (squareInIsland(x, y)) return false;

        return !isCellOccupied(x, y, boats, traps, weapons);
    }

    private boolean canPlaceOnIsland(int x, int y, Map<BoatName, List<Position>> boats, Map<TrapType, List<Position>> traps, Map<WeaponType, List<Position>> weapons) {
        return squareInIsland(x, y) && !isCellOccupied(x, y, boats, traps, weapons);
    }


    private boolean isBoatOccupyingCell(BoatName boat, Position pos, int x, int y) {
        int size = getBoatSize(boat);

        for (int i = 0; i < size; i++) {
            int bx = pos.getOrientation() == Orientation.HORIZONTAL ? pos.getX() + i : pos.getX();
            int by = pos.getOrientation() == Orientation.VERTICAL ? pos.getY() + i : pos.getY();

            if (bx == x && by == y) {
                return true;
            }
        }
        return false;
    }

    private boolean isSimplePositionOccupyingCell(List<Position> positions, int x, int y) {
        for (Position pos : positions) {
            if (pos.getX() == x && pos.getY() == y) {
                return true;
            }
        }
        return false;
    }


    private boolean isCellOccupied(int x, int y, Map<BoatName, List<Position>> boats, Map<TrapType, List<Position>> traps, Map<WeaponType, List<Position>> weapons) {
        // Boats
        for (Map.Entry<BoatName, List<Position>> entry : boats.entrySet()) {
            for (Position pos : entry.getValue()) {
                if (isBoatOccupyingCell(entry.getKey(), pos, x, y)) {
                    return true;
                }
            }
        }

        // Traps
        for (List<Position> positions : traps.values()) {
            if (isSimplePositionOccupyingCell(positions, x, y)) {
                return true;
            }
        }

        // Weapons
        for (List<Position> positions : weapons.values()) {
            if (isSimplePositionOccupyingCell(positions, x, y)) {
                return true;
            }
        }

        return false;
    }


    private int getBoatSize(BoatName type) {
        switch (type) {
            case AIRCRAFT_CARRIER:
                return 5;
            case CRUISER:
                return 4;
            case DESTROYER:
                return 3;
            case SUBMARINE:
                return 3;
            case TORPEDO_BOAT:
                return 2;
            default:
                return 0;
        }
    }
}