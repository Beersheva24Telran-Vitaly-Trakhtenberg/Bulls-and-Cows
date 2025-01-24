package telran.game.bulls_cows.common;

import telran.game.bulls_cows.common.settings.Settings;
import telran.game.bulls_cows.exceptions.IllegalSequenceException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

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

    public static boolean isValidSequence(String sequence)
    {
        boolean result = true;

        if (sequence.length() != Settings.LENGTH_OF_SEQUENCE) {
            result = false;
        } else {
            HashSet<Character> uniqueChars = new HashSet<>();
            for (char c : sequence.toCharArray()) {
                if (Settings.DICTIONARY.indexOf(c) == -1) {
                    result = false;
                    break;
                }
                if (!uniqueChars.add(c)) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    public static HashMap<String, String> calculateSequenceResults(String gameSequence, String gamerSequence) throws IllegalSequenceException {
        AtomicInteger numberBulls = new AtomicInteger(0);
        AtomicInteger numberCows = new AtomicInteger(0);
        if (isValidSequence(gamerSequence)) {
            AtomicInteger indSequence = new AtomicInteger(0);

            gamerSequence.chars().forEach(c -> {
                int currentInd = gameSequence.indexOf(c);
                if (currentInd == indSequence.get()) {
                    numberBulls.getAndIncrement();
                } else if (currentInd != indSequence.get() && currentInd != -1) {
                    numberCows.getAndIncrement();
                }
                indSequence.getAndIncrement();
            });
        } else {
            numberBulls.set(0);
            numberCows.set(0);
            throw new IllegalSequenceException(gamerSequence);
        }

        HashMap<String, String> result = new HashMap<>();
        result.put("numberBulls", String.valueOf(numberBulls.get()));
        result.put("numberCows", String.valueOf(numberCows.get()));

        return result;
    }

    public static String generatePartialSequence(String used_sequence)
    {
        int length_generated_sequence = Settings.LENGTH_OF_SEQUENCE - used_sequence.length();
        if (length_generated_sequence > Settings.DICTIONARY.length()) {
            throw new IllegalArgumentException("Length of sequence exceeds dictionary size.");
        }
        if (length_generated_sequence < 0) {
            throw new IllegalArgumentException("Length of given sequence greater than length of sequence to exceeds dictionary size.");
        }

        StringBuilder dictionary = new StringBuilder(Settings.DICTIONARY);
        char[] sequence = new char[length_generated_sequence];
        Set<Character> chars_to_remove = used_sequence.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toSet());

        String result = dictionary.chars()
                .mapToObj(c -> (char) c)
                .filter(c -> !chars_to_remove.contains(c))
                .map(String::valueOf)
                .collect(Collectors.joining());

        dictionary.setLength(0);
        dictionary.append(result);

        RandomGenerator generator = RandomGenerator.getDefault();

        for (int i = 0; i < length_generated_sequence; i++) {
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

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
    );

    public static LocalDateTime parse(String dateTimeString)
    {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                if (dateTimeString.length() == 5) { // HH:mm
                    LocalTime localTime = LocalTime.parse(dateTimeString, formatter);
                    return LocalDateTime.of(LocalDateTime.now().toLocalDate(), localTime);
                } else {
                    return LocalDateTime.parse(dateTimeString, formatter);
                }
            } catch (DateTimeParseException e) {

            }
        }
        throw new IllegalArgumentException("Invalid date/time format: " + dateTimeString);
    }
}
