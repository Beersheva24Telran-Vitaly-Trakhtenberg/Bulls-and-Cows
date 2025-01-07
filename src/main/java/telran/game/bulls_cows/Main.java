package telran.game.bulls_cows;

import com.opencsv.CSVWriter;
import telran.game.bulls_cows.common.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.random.*;

public class Main
{
    private static Set<Integer> games_ids = new HashSet<>();
    private static Game[] games = new Game[Settings.NUMBER_OF_GAMES];

    private static Set<String> gamers_names = new HashSet<>();
    private static Gamer[] gamers = new Gamer[Settings.NUMBER_OF_GAMERS];

    public static void main(String[] args)  throws IOException
    {
        try {
            generateGames();
            outputGeneratedGamesToCsv();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try {
            generateGamers();
            outputGeneratedGamersToCsv();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void generateGames() throws IOException
    {
        RandomGenerator generator = RandomGenerator.getDefault();

        for (int i = 0; i < Settings.NUMBER_OF_GAMES; i++) {
            int game_id;
            do {
                game_id = 1 + generator.nextInt(1000);
            } while (games_ids.contains(game_id));

            Game game = new Game(game_id);
            game.startGame();

            games_ids.add(game_id);
            games[i] = game;
        }
    }

    private static void outputGeneratedGamesToCsv() throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(Settings.GAMES_DATA_FILE_PATH))) {
            for (Game game : games) {
                writer.writeNext(new String[]{
                        Integer.toString(game.getGameID()),
                        game.getTimeStarting(),
                        Boolean.toString(game.isFinished()),
                        game.getSequence(),
                        game.isFinished() ? game.getTimeFinishing() : null
                });
            }
        }
    }
    private static void generateGamers() throws IOException
    {
        RandomGenerator generator = RandomGenerator.getDefault();
        String[] tmp_names = new String[]{"vasya", "benya", "haim", "shlomo"};

        for (int i = 0; i < Settings.NUMBER_OF_GAMERS; i++) {
            String gamer_name;
            do {
                gamer_name = tmp_names[generator.nextInt(tmp_names.length)] + "_" + String.valueOf(1 + generator.nextInt(1000));
            } while (games_ids.contains(gamer_name));

            Gamer gamer = new Gamer(gamer_name);

            gamers_names.add(gamer_name);
            gamers[i] = gamer;
        }
    }

    private static void outputGeneratedGamersToCsv() throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(Settings.GAMERS_DATA_FILE_PATH))) {
            for (Gamer gamer : gamers) {
                writer.writeNext(new String[]{
                        gamer.getGamerName(),
                        gamer.getBirthday().toString()
                });
            }
        }
    }
}
