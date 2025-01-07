package telran.game.bulls_cows;

import telran.game.bulls_cows.common.Tools;

import java.time.LocalDate;
import java.time.Month;

public class Gamer
{
    private final String gamer_name;
    private LocalDate start = LocalDate.now().minusYears(65);
    private LocalDate end = LocalDate.now().minusYears(18);
    private LocalDate birthday;

    public Gamer(String gamer_name) {
        this.gamer_name = gamer_name;
        createBirthday();
    }

    private void createBirthday()
    {
        this.birthday = Tools.between(start, end);
    }

    public String getGamerName() {
        return this.gamer_name;
    }

    public LocalDate getBirthday()
    {
        if (this.birthday == null) {
            createBirthday();
        }
        return this.birthday;
    }

    public String[] toStringArray()
    {
        return new String[]{
                gamer_name,
                birthday.toString()};
    }
}
