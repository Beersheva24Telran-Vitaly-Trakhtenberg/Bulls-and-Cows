package telran.game.bulls_cows.exceptions;

import java.util.Map;

public class UserNotFoundException extends BaseGameCowsException
{
    public UserNotFoundException(String userName) {
        super("Username [%s] is not found/not registered yet.", Map.of("userName", userName));
    }
}
