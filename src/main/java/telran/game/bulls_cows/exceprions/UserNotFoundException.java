package telran.game.bulls_cows.exceprions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String userName) {
        super("Username [" + userName + "] is not found/not registered yet.");
    }
}
