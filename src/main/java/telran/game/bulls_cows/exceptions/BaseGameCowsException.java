package telran.game.bulls_cows.exceptions;

import org.json.JSONObject;

import java.util.Map;

public class BaseGameCowsException extends Exception
{
    protected final String additionalInfo;

    public BaseGameCowsException(String formatedMessage, Map<String, Object> exceptionInfo)
    {
        super(formatMessage(formatedMessage, exceptionInfo));
        this.additionalInfo = createAdditionalInfo(exceptionInfo);
    }

    private static String formatMessage(String message, Map<String, Object> parameters) {
        for (var value : parameters.values()) {
            message = message.replaceFirst("%s", value.toString());
        }
        return message;
    }

    private String createAdditionalInfo(Map<String, Object> exceptionInfo)
    {
        JSONObject json = new JSONObject();
        json.put("Exception", getClass().getName());
        for (Map.Entry<String, Object> entry : exceptionInfo.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json.toString();
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }
}
