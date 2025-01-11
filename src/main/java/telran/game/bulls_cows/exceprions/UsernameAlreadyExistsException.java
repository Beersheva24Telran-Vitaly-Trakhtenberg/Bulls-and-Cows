package telran.game.bulls_cows.exceprions;

import telran.game.bulls_cows.common.SessionToken;

public class UsernameAlreadyExistsException extends Exception {
    public UsernameAlreadyExistsException(String userName) {
        super("Username [" + userName + "] already exists.");
    }
}
