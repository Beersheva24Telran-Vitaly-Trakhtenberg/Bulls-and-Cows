package telran.game.bulls_cows;

import jakarta.persistence.*;
import telran.game.bulls_cows.common.CsvConvertible;
import telran.game.bulls_cows.common.SessionToken;
import telran.game.bulls_cows.common.Tools;

import java.time.LocalDate;

@Entity
@Table(name = "gamers", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class Gamer implements CsvConvertible
{
    @Id
    @Column(name = "username")
    private String gamer_name;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Transient
    private SessionToken token;
    @Transient
    private LocalDate fromYearBirth = LocalDate.now().minusYears(65);
    @Transient
    private LocalDate uptoYearBirth = LocalDate.now().minusYears(18);

    public Gamer() {

    }
    public Gamer(String gamer_name) {
        this.gamer_name = gamer_name;
        createBirthday();
    }
    public Gamer(String gamer_name, LocalDate birthday) {
        this.gamer_name = gamer_name;
        this.birthday = birthday;
    }

    private SessionToken getToken() {
        return token;
    }

    private void setToken(SessionToken token) {
        this.token = token;
    }

    private void createBirthday()
    {
        this.birthday = Tools.between(fromYearBirth, uptoYearBirth);
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

    @Override
    public String[] toStringArray()
    {
        return new String[]{
                gamer_name,
                birthday.toString()};
    }
}
