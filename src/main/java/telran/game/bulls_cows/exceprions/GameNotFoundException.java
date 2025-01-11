package telran.game.bulls_cows.exceprions;

public class GameNotFoundException extends Exception {
    public GameNotFoundException(int gameId) {
        super("The game [id=" + gameId + "] is not found.");
    }
}
