package telran.game.bulls_cows.exceptions;

import java.util.Map;

public class UserNotAuthorizedException extends BaseGameCowsException
{
    public UserNotAuthorizedException(String userName) {
        super("The user is not logged in/not registered yet.", Map.of());
    }

}
