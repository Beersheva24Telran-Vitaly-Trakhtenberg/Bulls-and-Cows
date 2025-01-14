package telran.game.bulls_cows;

import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceprions.GameNotFoundException;
import telran.game.bulls_cows.exceprions.UserAlreadyExistsException;
import telran.game.bulls_cows.exceprions.UserNotFoundException;

import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BullsCowsServiceImpl implements BullsCowsService {

    @Override
    public SessionToken logIn(String gamerName) throws UserNotFoundException {
        return null;
    }

    @Override
    public void logOut(SessionToken gamerToken) throws AuthenticationException {

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
        return 0L;
    }

    @Override
    public void joinGame(SessionToken gamerToken, Long game_id) throws AuthenticationException {

    }

    @Override
    public void startGame(SessionToken gamerToken, Long game_id) throws AuthenticationException {

    }

    @Override
    public void startGame(SessionToken gamerToken, Long game_id, String dateTimeStart) throws AuthenticationException {

    }

    @Override
    public List<Long> getAvailabledGamesForStarting(SessionToken gamerToken) throws AuthenticationException {
        return List.of();
    }

    @Override
    public List<Long> getAvailabledGamesForJoining(SessionToken gamerToken) throws AuthenticationException {
        return List.of();
    }

    @Override
    public List<Long> getGamerStartedGames(SessionToken gamerToken) throws AuthenticationException {
        return List.of();
    }

    @Override
    public List<Long> getGamerFinishedGames(SessionToken gamerToken) throws AuthenticationException {
        return List.of();
    }

    @Override
    public List<Moves> getGamerMoves(SessionToken gamerToken, Long game_id) throws GameNotFoundException, AuthenticationException {
        return List.of();
    }

    @Override
    public List<Moves> addGamerNewMove(String sequence, SessionToken gamerToken, Long game_id) throws GameNotFoundException, AuthenticationException {
        return List.of();
    }
}
