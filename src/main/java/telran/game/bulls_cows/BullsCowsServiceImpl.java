package telran.game.bulls_cows;

import org.json.JSONObject;
import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceprions.GameNotFoundException;
import telran.game.bulls_cows.exceprions.UserAlreadyExistsException;
import telran.game.bulls_cows.exceprions.UserNotFoundException;
import telran.net.*;

import javax.naming.AuthenticationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class BullsCowsServiceImpl implements BullsCowsService
{
    private final BullsCowsRepository repository;

    public BullsCowsServiceImpl(BullsCowsRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public SessionToken logIn(String gamerName) throws UserNotFoundException {
        String param = extractInput("username", gamerName);
        try {
            if (!repository.isUserExists(param))
                throw new UserNotFoundException(param);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(param);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return new SessionToken(param);
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
     * Registers new user
     *
     * @param gamerName
     * @param birthday
     * @return SessionToken
     * @throws UserAlreadyExistsException
     * @throws IllegalArgumentException
     */
    @Override
    public SessionToken signUp(String gamerName, String birthday) throws UserAlreadyExistsException, IllegalArgumentException
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthdate = LocalDate.parse(birthday, formatter);

        return new SessionToken(gamerName);
    }

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
        Game game = repository.findGameById(game_id);
        boolean result = false;
        if (game != null) {
            result = game.isStarted();
        }
        return result;
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
