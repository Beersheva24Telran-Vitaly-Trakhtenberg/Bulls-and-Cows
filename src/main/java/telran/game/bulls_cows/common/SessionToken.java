package telran.game.bulls_cows.common;

public class SessionToken
{
    public static String getSessionToken(String gamerID)
    {
        return generateToken(gamerID);
    }

    private static String generateToken(String user)
    {
        return (user + "@" + System.currentTimeMillis());
    }

    public static boolean isTokenValid(String userToken)
    {
        return true;    // ToDo
    }

    public static String getUserIdFromToken(String userToken)
    {
        return userToken.split("@")[0];
    }
}
