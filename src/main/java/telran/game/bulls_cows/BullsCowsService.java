package telran.game.bulls_cows;

import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceprions.*;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Map;

/**
 * Service layer for Bulls and Cows game
 */
public interface BullsCowsService
{
    SessionToken logIn(Map<String, Object> params) throws UserNotFoundException;
    void logOut(SessionToken gamerToken) throws AuthenticationException;
    void isSessionTokenValid(SessionToken gamerToken) throws AuthenticationException;

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
    public SessionToken signUp(Map<String, Object> params) throws UserAlreadyExistsException, IllegalArgumentException;

    Long createGame(SessionToken gamerToken) throws AuthenticationException;
    void joinGame(SessionToken gamerToken, Long game_id) throws AuthenticationException;
    void startGame(SessionToken gamerToken, Long game_id) throws AuthenticationException;
    void startGame(SessionToken gamerToken, Long game_id, String dateTimeStart) throws AuthenticationException;
    boolean isGameStarted(Long game_id) throws GameNotFoundException;

    List<Long> getAvailabledGamesForStarting(SessionToken gamerToken) throws AuthenticationException;
    List<Long> getAvailabledGamesForJoining(SessionToken gamerToken) throws AuthenticationException;
    List<Long> getGamerStartedGames(SessionToken gamerToken) throws AuthenticationException;
    List<Long> getGamerFinishedGames(SessionToken gamerToken) throws AuthenticationException;
    List<Moves> getGamerMoves(SessionToken gamerToken, Long game_id) throws GameNotFoundException, AuthenticationException;
    List<Moves> addGamerNewMove(String sequence, SessionToken gamerToken, Long game_id) throws GameNotFoundException, AuthenticationException;
}
