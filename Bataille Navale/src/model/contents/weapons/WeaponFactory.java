package model.contents.weapons;

public class WeaponFactory {
    public WeaponFactory(){}

    public Missile createMissile(){
        return new Missile();
    }

    public Bomb createBomb(){
        return new Bomb();
    }

    public Sonar createSonar(){
        return new Sonar();
    }

}
