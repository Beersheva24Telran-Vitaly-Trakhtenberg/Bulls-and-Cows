package telran.game.bulls_cows.exceptions;

import java.util.Map;

public class UserAlreadyExistsException extends BaseGameCowsException
{
    public UserAlreadyExistsException(String userName)
    {
        super("Username [%s] is already exists.", Map.of("userName", userName));
    }
}
