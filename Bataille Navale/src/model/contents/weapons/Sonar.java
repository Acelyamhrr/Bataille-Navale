package model.contents.weapons;

import model.enums.WeaponType;
import model.grid.Position;

import java.util.ArrayList;

public class Sonar extends Weapon{
    public Sonar(){
        super(WeaponType.SONAR);
    }

    @Override
    public ArrayList<Position> use(Position position) {
        ArrayList<Position> positions = new ArrayList<>();
        positions.add(position);

        Position gauche = new Position(position.getX(), position.getY()-1);
        Position droite = new Position(position.getX(), position.getY()+1);
        Position haut =  new Position(position.getX()-1, position.getY());
        Position bas = new Position(position.getX()+1, position.getY());

        positions.add(gauche);
        positions.add(droite);
        positions.add(haut);
        positions.add(bas);

        Position hautgauche = new Position(position.getX()-1, position.getY()-1);
        Position basgauche = new Position(position.getX()+1, position.getY()-1);
        positions.add(hautgauche);
        positions.add(basgauche);

        Position hautdroite = new Position(position.getX()-1, position.getY()+1);
        Position basdroite = new Position(position.getX()+1, position.getY()+1);
        positions.add(hautdroite);
        positions.add(basdroite);

        return positions;
    }
}