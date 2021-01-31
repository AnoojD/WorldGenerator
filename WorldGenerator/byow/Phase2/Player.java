package byow.Phase2;

import byow.Phase1.Position;

public class Player {
    private Position userPos;

    public Player(Position uP) {
        userPos = uP;
    }

    public void changePos(Position p) {
        userPos = p;
    }

    public Position getUserPos() {
        return userPos;
    }

}
