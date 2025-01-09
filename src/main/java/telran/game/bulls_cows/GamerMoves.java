package telran.game.bulls_cows;

public record GamerMoves(
        int move_id,
        int gamer4game_id,
        String secuence,
        int number_bulls,
        int number_cows,
        int attempt_number) { }
