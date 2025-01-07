package telran.game.bulls_cows.common;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

public class Tools
{
    public static String generateSequence()
    {
        if (Settings.LENGTH_OF_SEQUENCE > Settings.DICTIONARY.length()) {
            throw new IllegalArgumentException("Length of sequence exceeds dictionary size.");
        }

        char[] sequence = new char[Settings.LENGTH_OF_SEQUENCE];
        StringBuilder dictionary = new StringBuilder(Settings.DICTIONARY);
        RandomGenerator generator = RandomGenerator.getDefault();

        for (int i = 0; i < Settings.LENGTH_OF_SEQUENCE; i++) {
            int dict_ind = generator.nextInt(dictionary.length());
            sequence[i] = dictionary.charAt(dict_ind);
            dictionary.deleteCharAt(dict_ind);
        }

        return new String(sequence);
    }

    public static LocalDate between(LocalDate start_date_included, LocalDate end_date_excluded)
    {
        long start_epoch_day = start_date_included.toEpochDay();
        long end_epoch_day = end_date_excluded.toEpochDay();
        long random_day = ThreadLocalRandom
                .current()
                .nextLong(start_epoch_day, end_epoch_day);

        return LocalDate.ofEpochDay(random_day);
    }
}
