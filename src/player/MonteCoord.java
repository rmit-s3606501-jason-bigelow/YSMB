package player;
public class MonteCoord{
    int row;
    int col;
    int odds;
    int killerOdds;//Used when finishing off a ship
    char state;//n for nothing, h for active hit, s for sunk, m for miss
    public MonteCoord(int x, int y)
    {
        this.col = x;
        this.row = y;
        this.odds = 0;
        this.killerOdds = 0;
        this.state = 'n';
    }
    public MonteCoord(int x, int y, int odds)
    {
        this.col = x;
        this.row = y;
        this.odds = odds;
        this.killerOdds = 0;
        this.state = 'n';
    }
}