package telran.game.bulls_cows;

import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.exceprions.*;

import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.util.List;

public interface BullsCowsService
{
    SessionToken logIn(String gamerName) throws UsernameNotFoundException;
    void logOut(SessionToken gamerToken) throws AuthenticationException;
    SessionToken signUp(String gamerName, LocalDate birthday) throws UsernameAlreadyExistsException,IllegalArgumentException;

    int createGame(SessionToken gamerToken) throws AuthenticationException;
    void joinGame(SessionToken gamerToken, int game_id) throws AuthenticationException;
    void startGame(SessionToken gamerToken, int game_id) throws AuthenticationException;

    List<Game> getAvailabledGames(SessionToken gamerToken) throws AuthenticationException;
    List<Game> getMyStartedGames(SessionToken gamerToken) throws AuthenticationException;
    List<Game> getMyFinishedGames(SessionToken gamerToken) throws AuthenticationException;
}
