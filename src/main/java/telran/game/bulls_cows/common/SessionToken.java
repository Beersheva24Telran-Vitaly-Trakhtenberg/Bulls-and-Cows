package telran.game.bulls_cows.common;

public class SessionToken
{
    private String token;

    public SessionToken(String user)
    {
        generateToken(user);
    }

    public String getToken() {
        return token;
    }

    private void generateToken(String user)
    {
        this.token = user + "@" + System.currentTimeMillis();
    }

    public String getUsername()
    {
        return token.split("@")[0];
    }

    public String toString() {
        return token;
    }
}
