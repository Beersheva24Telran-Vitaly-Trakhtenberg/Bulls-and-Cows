package telran.game.bulls_cows.exceptions;

import java.util.NoSuchElementException;

public class GameNotFoundException extends NoSuchElementException {
    public GameNotFoundException(Long gameId) {
        super("The game [id=" + gameId + "] is not found.");
    }
}
