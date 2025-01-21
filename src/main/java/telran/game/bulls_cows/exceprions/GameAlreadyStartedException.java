package telran.game.bulls_cows.exceprions;

public class GameAlreadyStartedException extends Exception {
    public GameAlreadyStartedException(Long gameId) {
        super("Game [" + gameId + "] already started.");
    }
}
