package telran.game.bulls_cows.exceprions;

import org.json.JSONObject;

import java.util.NoSuchElementException;

public class UserNotAuthorizedException extends NoSuchElementException
{
    public UserNotAuthorizedException()
    {
        super("The user is not logged in/not registered yet.");
    }
}
