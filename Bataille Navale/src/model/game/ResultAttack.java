package model.game;

import model.enums.TypeResult;

public class ResultAttack {
    private String message;
    private TypeResult type;

    public ResultAttack(String message, TypeResult type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return this.message;
    }

    public TypeResult getType() {
        return this.type;
    }
}
