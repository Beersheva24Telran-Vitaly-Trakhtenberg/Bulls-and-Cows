package telran.game.bulls_cows.exceprions;

import org.json.JSONObject;

import java.util.NoSuchElementException;

public class UserNotFoundException extends NoSuchElementException
{
    private final String additionalInfo;

    public UserNotFoundException(String userName) {
        super("Username [" + userName + "] is not found/not registered yet.");
        this.additionalInfo = createAdditionalInfo(userName);
    }

    private String createAdditionalInfo(String username)
    {
        JSONObject json = new JSONObject();
        json.put("Exception", getClass().getName());
        json.put("username", username);
        return json.toString();
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }
}
