package telran.game.bulls_cows;

import com.opencsv.CSVWriter;
import telran.game.bulls_cows.common.*;
import telran.game.bulls_cows.common.settings.Settings;
import telran.game.bulls_cows.models.Game;
import telran.game.bulls_cows.models.Gamer;
import telran.game.bulls_cows.models.GamerGameRecord;
import telran.game.bulls_cows.models.GamerMovesRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.*;
import java.util.stream.Collectors;

public class Main
{
    private static Set<Integer> games_ids = new HashSet<>();
    private static Game[] games = new Game[Settings.NUMBER_OF_GAMES];

    private static Set<String> gamers_names = new HashSet<>();
    private static Gamer[] gamers = new Gamer[Settings.NUMBER_OF_GAMERS];
    
    private static Map<String, Game> gaming_map = new HashMap<>();
    private static Set<GamerGameRecord> gamers4game = new HashSet<>();
    private static Map<Integer, GamerMovesRecord> gamer_moves = new HashMap<>();

    public static void main(String[] args)  throws IOException
    {
        try {
            generateGames();
            outputToCsv(Set.of(games), Settings.GAMES_DATA_FILE_PATH);

            generateGamers();
            outputToCsv(Set.of(gamers), Settings.GAMERS_DATA_FILE_PATH);

            gamersStartPlayGames();
            outputGeneratedGamer2GamesToCsv();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        gamePlayProcess();
    }

    private static void generateGames() throws IOException
    {
        RandomGenerator generator = RandomGenerator.getDefault();

        for (int i = 0; i < Settings.NUMBER_OF_GAMES; i++) {
            int game_id;
            do {
                game_id = 1 + generator.nextInt(1000);
            } while (games_ids.contains(game_id));

            Game game = new Game();
            game.startGame();

            games_ids.add(game_id);
            games[i] = game;
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
            } while (gamers_names.contains(gamer_name));

            Gamer gamer = new Gamer(gamer_name);

            gamers_names.add(gamer_name);
            gamers[i] = gamer;
        }
    }

    private static void outputToCsv(@org.jetbrains.annotations.NotNull Set<? extends CsvConvertible> objects, String filePath) throws IOException
    {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END))
        {
            for (CsvConvertible obj : objects) {
                String[] line = obj.toStringArray();
                if (line != null && line.length > 0) {
                    writer.writeNext(line, false);
                }
            }
        }
    }

    private static void gamersStartPlayGames() throws IOException
    {
        for (Gamer gamer : gamers) {
            int game_id = -1;
            int attempt_count = 0;
            boolean is_finished;
            do {
                game_id = RandomGenerator.getDefault().nextInt(Settings.NUMBER_OF_GAMES);
                attempt_count++;
            } while (games[game_id].isFinished() && attempt_count < 100);
            if (attempt_count == 100) {
                throw new RuntimeException("The gamer '" + gamer.getGamerName() + "' didn't find any unfinished game");
            }
            Game game = games[game_id];
            gaming_map.put(gamer.getGamerName(), game);
        }
    }

    private static void outputGeneratedGamer2GamesToCsv() throws IOException 
    {
        try (CSVWriter writer = new CSVWriter(new FileWriter(Settings.GAMERS2GAMES_DATA_FILE_PATH))) {
            AtomicInteger gaming_id = new AtomicInteger(1);
            gaming_map.forEach((gamer_name, game) -> {
                writer.writeNext(new String[]{
                        String.valueOf(gaming_id.get()),
                        String.valueOf(game.getGameID()),
                        gamer_name,
                        String.valueOf(false)
                });
                gamers4game.add(new GamerGameRecord(gaming_id.get(), gamer_name, game.getGameID()));
                gaming_id.getAndIncrement();
            });
        }
    }
    
    private static void gamePlayProcess() throws IOException
    {
        try(CSVWriter writer = new CSVWriter(new FileWriter(Settings.MOVES_GAMERS_DATA_FILE_PATH),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END)) {

            for (int i = 0; i < 5; i++) {
                gaming_map.entrySet().stream()
                    .filter(entry -> !entry.getValue().isFinished())
                    .forEach(entry -> {
                        String gamer_name = entry.getKey();
                        Game game = entry.getValue();
                        Optional<Integer> gamer4game_id = gamers4game.stream()
                                .filter(gg -> gg.gamer_name().equals(gamer_name) && gg.game_id() == game.getGameID())
                                .map(GamerGameRecord::gamer4game_id)
                                .findFirst();

                        int move_id;
                        int attempt_number;
                        int gamer_game_id;
                        Set<String> attemps;
                        Moves move;

                        if (gamer4game_id.isPresent()) {
                            gamer_game_id = gamer4game_id.get();

                            attemps = gamer_moves.values().stream()
                                    .filter(gm -> gm.gamer4game_id() == gamer_game_id)
                                    .map(GamerMovesRecord::secuence)
                                    .collect(Collectors.toSet());

                            attempt_number = attemps.size() + 1;
                        } else {
                            gamer_game_id = createNewGamerGameId(gamer_name, game.getGameID());
                            attempt_number = 1;
                            attemps = new HashSet<>();
                        }
                        move = new Moves(game.getSequence(), attemps);
                        move.nextMove();
                        String gamer_sequence = move.getLastSequence();
                        Integer[] result = move.getLastResult();
                        boolean is_finished = move.isFinished();
                        move_id = gamer_moves.size() + 1;
                        gamer_moves.put(
                                move_id,
                                new GamerMovesRecord(
                                        move_id,
                                        gamer_game_id,
                                        gamer_sequence,
                                        result[0],
                                        result[1],
                                        attempt_number
                                )
                        );
                        attemps.add(gamer_sequence);

                        writer.writeNext(new String[]{
                                String.valueOf(move_id),
                                String.valueOf(gamer_game_id),
                                gamer_sequence,
                                String.valueOf(result[0]),
                                String.valueOf(result[1])/*,
                        String.valueOf(attempt_number),
                        String.valueOf(is_finished)*/
                        });

                        if (is_finished) {
                            game.finishGame();
                        }
                    });
            }
        }
    }

    private static int createNewGamerGameId(String gamer_name, Long game_id)
    {
        int new_gamer_game_id = gamers4game.size() + 1;
        gamers4game.add(new GamerGameRecord(new_gamer_game_id, gamer_name, game_id));
        return new_gamer_game_id;
    }
}
