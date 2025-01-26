package telran.game.bulls_cows.common.settings;

public class Settings
{
    public static final int LENGTH_OF_SEQUENCE = 4;
    public static final int NUMBER_OF_GAMES = 10;
    public static final int NUMBER_OF_GAMERS = 45;

    public static final String DICTIONARY = "0123456789";
    public static final String GAMES_DATA_FILE_PATH = "games_data.csv"; // Note Remove with standalone CSV
    public static final String GAMERS_DATA_FILE_PATH = "gamers_data.csv"; // Note Remove with standalone CSV
    public static final String GAMERS2GAMES_DATA_FILE_PATH = "gamers2games_data.csv"; // Note Remove with standalone CSV
    public static final String MOVES_GAMERS_DATA_FILE_PATH = "moves_gamers_data.csv"; // Note Remove with standalone CSV

    public static final String POSTGRES_HOST = System.getenv("POSTGRES_HOST");
    public static final String POSTGRES_USERNAME = System.getenv("POSTGRES_USERNAME");
    public static final String POSTGRES_PASSWORD = System.getenv("POSTGRES_PASSWORD");
}
