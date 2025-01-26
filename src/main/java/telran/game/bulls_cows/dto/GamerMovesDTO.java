package telran.game.bulls_cows.dto;

public class GamerMovesDTO
{
    private String sequence;
    private Integer resultBulls;
    private Integer resultCows;

    public GamerMovesDTO(String sequence, int resultBulls, int resultCows)
    {
        this.sequence = sequence;
        this.resultBulls = resultBulls;
        this.resultCows = resultCows;
    }

    public String getSequence() { return sequence; }
    public int getResultBulls() { return resultBulls; }
    public int getResultCows() { return resultCows; }

    public void setSequence(String sequence) { this.sequence = sequence; }
    public void setResultBulls(int resultBulls) { this.resultBulls = resultBulls; }
    public void setResultCows(int resultCows) { this.resultCows = resultCows; }
}
