package model.contents.fleet;

import model.enums.BoatName;

public class BoatFactory {
    public BoatFactory() {}

    public Boat createAircraftCarrier(){
        return new Boat(BoatName.AIRCRAFT_CARRIER, 5);
    }

    public Boat createCruiser(){
        return new Boat(BoatName.CRUISER, 4);
    }

    public Boat createDestroyer(){
        return new Boat(BoatName.DESTROYER, 3);
    }

    public Boat createSubmarine(){
        return new Boat(BoatName.SUBMARINE, 3);
    }

    public Boat createTorpedoBoat(){
        return new Boat(BoatName.TORPEDO_BOAT, 2);
    }
}
