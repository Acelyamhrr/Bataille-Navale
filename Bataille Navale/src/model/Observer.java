package model;

import model.enums.State;
import model.enums.TrapType;
import model.enums.WeaponType;
import model.grid.Position;

/**
 * Interface Observer pour le patron Observer.
 * Permet de notifier les observateurs des événements du jeu.
 */
public interface Observer {

    //Notifie qu'un bateau a été touché.
    void boatAttacked(Position position, boolean robot);

    //Notifie qu'un bateau a été touché pour la première fois
    void boatTouched(boolean robot);

     // Notifie qu'un bateau a été coulé
     void boatSunk(Position position, int size, boolean robot);

     //Notifie qu'une case a été attaquée (dans l'eau).
     void squareAttacked(Position position, boolean robot);


     //Notifie qu'une case de l'île a été explorée
     void squareIsland(Position position, State state, boolean robot);

}