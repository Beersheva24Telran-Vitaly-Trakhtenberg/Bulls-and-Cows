package telran.game.bulls_cows.exceprions;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String userName) {
        super("Username [" + userName + "] already exists.");
    }
}
