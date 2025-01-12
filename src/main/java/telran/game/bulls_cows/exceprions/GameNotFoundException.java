package telran.game.bulls_cows.exceprions;

import java.util.NoSuchElementException;

public class GameNotFoundException extends NoSuchElementException {
    public GameNotFoundException(int gameId) {
        super("The game [id=" + gameId + "] is not found.");
    }
}
