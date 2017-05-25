package player;
public class MonteCoord implements Comparable{
    int row;
    int col;
    int odds;
    char state;//n for nothing, h for active hit, s for sunk, m for miss
    public MonteCoord(int x, int y)
    {
        this.col = x;
        this.row = y;
        this.odds = 0;
        this.state = 'n';
    }
    public MonteCoord(int x, int y, int odds)
    {
        this.col = x;
        this.row = y;
        this.odds = odds;
        this.state = 'n';
    }
    public int compareTo(MonteCoord other)
    {
        return this.odds - other.odds;
    }
}