package model.history;

import model.Observer;
import model.enums.State;
import model.grid.Position;

import java.util.ArrayList;
import java.util.List;

public class History implements Observer {
    private final List<String> actions;

    public History() {
        this.actions = new ArrayList<String>();

    }

    public void reset() {
        this.actions.clear();
    }

    @Override
    public void boatAttacked(Position position) {
        String action = String.format("Bateau touché en position (%d, %d)", position.getX(), position.getY());
        actions.add(action);
    }

    @Override
    public void boatSunk(Position position, int size) {
        String action = String.format("Bateau de taille %d coulé en position (%d, %d)", size, position.getX(), position.getY());
        actions.add(action);

    }

    @Override
    public void squareAttacked(Position position) {
        String action = String.format("Case vide attaquée en position (%d, %d)", position.getX(), position.getY());
        actions.add(action);
    }

    @Override
    public void squareIsland(Position position, State state) {
        String action = String.format("île en position (%d, %d) - État: %s", position.getX(), position.getY(), state);
        actions.add(action);
    }

    public List<String> getActions() {
        return new ArrayList<>(actions);
    }

    public String getHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== HISTORIQUE DES ACTIONS ===\n");
        for (int i = 0; i < actions.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, actions.get(i)));
        }
        return sb.toString();
    }

    public String getLastAction() {
        if (actions.isEmpty()) {
            return null;
        }
        return actions.getLast();
    }

    public int getActionCount() {
        return actions.size();
    }


    public String toString() {
        return "History{" +
                "actions=" + actions.size() +
                '}';
    }

}