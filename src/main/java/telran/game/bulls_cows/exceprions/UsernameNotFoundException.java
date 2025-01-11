package telran.game.bulls_cows.exceprions;

public class UsernameNotFoundException extends Exception {
    public UsernameNotFoundException(String userName) {
        super("Username [" + userName + "] is not found/not registered yet.");
    }
}
