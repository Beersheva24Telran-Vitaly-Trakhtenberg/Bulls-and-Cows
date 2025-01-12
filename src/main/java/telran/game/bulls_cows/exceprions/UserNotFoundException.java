package telran.game.bulls_cows.exceprions;

import java.util.NoSuchElementException;

public class UserNotFoundException extends NoSuchElementException {
    public UserNotFoundException(String userName) {
        super("Username [" + userName + "] is not found/not registered yet.");
    }
}
