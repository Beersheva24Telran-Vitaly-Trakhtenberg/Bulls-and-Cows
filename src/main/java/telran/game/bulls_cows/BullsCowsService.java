package telran.game.bulls_cows;

import telran.game.bulls_cows.common.SessionToken;

import javax.naming.AuthenticationException;
import java.time.LocalDate;

public interface BullsCowsService
{
    SessionToken logIn(String gamer_name) throws AuthenticationException;
    void logOut();
    SessionToken signUp(String gamer_name, LocalDate birthday) throws IllegalArgumentException;


}
