package telran.game.bulls_cows;

import telran.game.bulls_cows.common.Settings;
import telran.game.bulls_cows.common.Tools;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Moves
{
    private final String game_sequence;
    private final Set<String> gamer_sequences;
    private boolean is_finished;
    private String sequence;
    private Integer[] result;

    public Moves(String game_sequence, Set<String> gamer_sequences)
    {
        this.game_sequence = game_sequence;
        this.gamer_sequences = gamer_sequences;
    }

    public void nextMove()
    {
        calculateResult();

        generateSequenceBasedPreviousResults();
        gamer_sequences.add(this.sequence);

        calculateResult();
    }

    public Integer[] getLastResult()
    {
        this.result = calculateResult();
        this.is_finished = this.result[0] == Settings.LENGTH_OF_SEQUENCE;

        return this.result;
    }

    public String getLastSequence()
    {
        return this.sequence;
    }

    private Integer[] calculateResult()
    {
        AtomicInteger number_bulls = new AtomicInteger(0);
        AtomicInteger number_cows = new AtomicInteger(0);
        if (this.sequence != null) {
            AtomicInteger ind_sequence = new AtomicInteger(0);

            sequence.chars().forEach(c -> {
                int current_ind = game_sequence.indexOf(c);
                if (current_ind == ind_sequence.get()) {
                    number_bulls.getAndIncrement();
                } else if (current_ind != ind_sequence.get() && current_ind != -1) {
                    number_cows.getAndIncrement();
                }
                ind_sequence.getAndIncrement();
            });
        } else {
            number_bulls.set(0);
            number_cows.set(0);
        }

        return new Integer[]{number_bulls.get(), number_cows.get()};
    }

    public boolean isFinished()
    {
        return this.is_finished;
    }

    private void generateSequenceBasedPreviousResults()
    {
        String sequence;
        if (this.result == null || (this.result[0] == 0 && this.result[1] == 0)) {
            do {
                sequence = Tools.generateSequence();
            } while (gamer_sequences.contains(sequence));
        } else {
            sequence = Tools.generatePartialSequence("41");
        }

        this.sequence = sequence;
    }
}
