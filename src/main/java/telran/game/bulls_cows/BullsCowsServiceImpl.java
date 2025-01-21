package telran.game.bulls_cows;

import org.json.JSONObject;
import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceprions.GameNotFoundException;
import telran.game.bulls_cows.exceprions.UserAlreadyExistsException;
import telran.game.bulls_cows.exceprions.UserNotFoundException;
import telran.game.bulls_cows.repository.BullsCowsRepository;

import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BullsCowsServiceImpl implements BullsCowsService
{
    private final BullsCowsRepository repository;

    public BullsCowsServiceImpl(BullsCowsRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public SessionToken logIn(Map<String, Object> params) throws UserNotFoundException
    {
        if (!params.containsKey("username") || !(params.get("username") instanceof String)) {
            throw new IllegalArgumentException("Missing or invalid parameter: username");
        }
        String gamerName = (String) params.get("username");
        try {
            if (!repository.isUserExists(gamerName))
                throw new UserNotFoundException(gamerName);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(gamerName);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return new SessionToken(gamerName);
    }

    @Override
    public void logOut(SessionToken gamerToken) throws AuthenticationException {
        isSessionTokenValid(gamerToken);
    }

    @Override
    public void isSessionTokenValid(SessionToken gamerToken) throws AuthenticationException {
        if (gamerToken == null) {   // FixMe
            throw new AuthenticationException("Session token is null or incorrect");
        }
    }

    /**
     * Registers a new user.
     *
     * @param params A map containing the following keys:
     *               - "username" (String): The username of the gamer.
     *               - "birthdate" (String): The birthdate of the gamer (in any of predefined formats).
     * @return SessionToken for the newly registered user.
     * @throws UserAlreadyExistsException if the username already exists.
     * @throws IllegalArgumentException if any of the parameters are missing or invalid.
     */
    @Override
    public SessionToken signUp(Map<String, Object> params) throws UserAlreadyExistsException, IllegalArgumentException
    {
        if (!params.containsKey("username") || !(params.get("username") instanceof String)) {
            throw new IllegalArgumentException("Missing or invalid parameter: username");
        }
        String gamerName = (String) params.get("username");

        if (!params.containsKey("birthdate") || !(params.get("birthdate") instanceof String)) {
            throw new IllegalArgumentException("Missing or invalid parameter: birthdate");
        }
        String birthdateStr = (String) params.get("birthdate");

        if (repository.isUserExists(gamerName)) {
            throw new UserAlreadyExistsException(gamerName);
        }

        List<DateTimeFormatter> formatters = new ArrayList<>();
        formatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        LocalDate birthdate = null;
        for (DateTimeFormatter formatter : formatters) {
            try {
                birthdate = LocalDate.parse(birthdateStr, formatter);
                break;
            } catch (DateTimeParseException e) {}
        }

        if (birthdate == null) {
            throw new IllegalArgumentException("Invalid date format for birthdate. Supported formats: dd-MM-yyyy, dd.MM.yyyy, yyyy-MM-dd, yyyy.MM.dd");
        }

        repository.createGamer(gamerName, birthdate);
        return new SessionToken(gamerName);
    }

    public void ping() {}

    @Override
    public Long createGame(SessionToken gamerToken) throws AuthenticationException {
        isSessionTokenValid(gamerToken);
        return 0L;
    }

    @Override
    public void joinGame(SessionToken gamerToken, Long game_id) throws AuthenticationException {
        isSessionTokenValid(gamerToken);
    }

    @Override
    public void startGame(SessionToken gamerToken, Long game_id) throws AuthenticationException {
        isSessionTokenValid(gamerToken);
    }

    @Override
    public void startGame(SessionToken gamerToken, Long game_id, String dateTimeStart) throws AuthenticationException {
        isSessionTokenValid(gamerToken);
    }

    @Override
    public boolean isGameStarted(Long game_id) throws GameNotFoundException
    {
/*
        Game game = repository.findGameById(game_id);
        boolean result = false;
        if (game != null) {
            result = game.isStarted();
        }
        return result;
*/
        return repository.isGameStarted(game_id);
    }

    @Override
    public List<Long> getAvailabledGamesForStarting(SessionToken gamerToken) throws AuthenticationException {
        isSessionTokenValid(gamerToken);
        return List.of();
    }

    @Override
    public List<Long> getAvailabledGamesForJoining(SessionToken gamerToken) throws AuthenticationException {
        isSessionTokenValid(gamerToken);
        return List.of();
    }

    @Override
    public List<Long> getGamerStartedGames(SessionToken gamerToken) throws AuthenticationException {
        isSessionTokenValid(gamerToken);
        return List.of();
    }

    @Override
    public List<Long> getGamerFinishedGames(SessionToken gamerToken) throws AuthenticationException {
        isSessionTokenValid(gamerToken);
        return List.of();
    }

    @Override
    public List<Moves> getGamerMoves(SessionToken gamerToken, Long game_id) throws GameNotFoundException, AuthenticationException {
        isSessionTokenValid(gamerToken);
        return List.of();
    }

    @Override
    public List<Moves> addGamerNewMove(String sequence, SessionToken gamerToken, Long game_id) throws GameNotFoundException, AuthenticationException {
        isSessionTokenValid(gamerToken);
        return List.of();
    }

/*
    public List<Game> getAllGames() {
        return repository.findAllGames();
    }
*/
    private String extractInput(String key, String data) throws IllegalArgumentException {
        if (isJson(data)) {
            return parseJsonForKey(key, data);
        } else {
            return data;
        }
    }

    private boolean isJson(String input) {
        return input.trim().startsWith("{") && input.trim().endsWith("}");
    }

    private String parseJsonForKey(String key, String data) throws IllegalArgumentException {
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has(key)) {
                return jsonObject.getString(key);
            } else {
                throw new IllegalArgumentException("Missing '" + key + "' field in JSON input");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
