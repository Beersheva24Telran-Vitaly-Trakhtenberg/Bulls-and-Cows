package telran.game.bulls_cows;

import jakarta.persistence.*;

@Entity
@Table(name = "moves")
public class GamerMoves {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "gamer2game_id")
    private Long keyGamerGameId;
    @Column(name = "sequence")
    private String sequence;
    @Column(name = "bulls_number")
    private int resultBulls;
    @Column(name = "cows_number")
    private int resultCows;

    public GamerMoves() {}
    public GamerMoves(Long keyGamerGameId, String sequence) {
        this.keyGamerGameId = keyGamerGameId;
        this.sequence = sequence;
    }
    public GamerMoves(Long keyGamerGameId, String sequence, int resultBulls, int resultCows) {
        this.keyGamerGameId = keyGamerGameId;
        this.sequence = sequence;
        this.resultBulls = resultBulls;
        this.resultCows = resultCows;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getKeyGamerGameId() { return keyGamerGameId; }
    public void setKeyGamerGameId(Long keyGamerGameId) { this.keyGamerGameId = keyGamerGameId; }
    public String getSequence() { return sequence; }
    public void setSequence(String sequence) { this.sequence = sequence; }
    public int getResultBulls() { return resultBulls; }
    public void setResultBulls(int resultBulls) { this.resultBulls = resultBulls; }
    public int getResultCows() { return resultCows; }
    public void setResultCows(int resultCows) { this.resultCows = resultCows; }
}

