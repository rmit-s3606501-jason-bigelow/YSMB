package player;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import world.World;

/**
 * Monte Carlo guess player (task C).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class MonteCarloGuessPlayer extends RandomGuessPlayer implements Player{

    MonteCoord lockedTarget = null;
    public ArrayList<MonteCoord> strikes;
    public ArrayList<World.Coordinate> shotsFired = new ArrayList<>();
    public MonteCoord[][] seekGrid;
    public ArrayList<MonteCoord> xTargetGrid;
    public ArrayList<MonteCoord> yTargetGrid;
    public int[] targetsLeft;
    public int longestTarget;
    @Override
    public void initialisePlayer(World world) {
        this.numRow = world.numRow;
        this.numCol = world.numColumn;
        this.world = world;
        this.random = new Random();
        seekGrid = new MonteCoord[numCol][numRow];
        populateTargetGrid();
        longestTarget = 5;
        targetsLeft = new int[5];
        targetsLeft[0] = 2;
        targetsLeft[1] = 3;
        targetsLeft[2] = 3;
        targetsLeft[3] = 4;
        targetsLeft[4] = 5;
        longestTarget = 5;
        strikes = new ArrayList<MonteCoord>();
    } // end of initialisePlayer()

    public void populateTargetGrid()
    {
        int x, y, simpleSpace, currentOdds = 0;
        //Populating the centeral regions
        for(x = 0; x < numCol; x++)
        {
            simpleSpace = Math.min(x, numCol - x - 1);
            switch(simpleSpace)
            {
                case 0:
                    currentOdds = 5;
                    break;
                case 1:
                    currentOdds = 10;
                    break;
                case 2:
                    currentOdds = 14;
                    break;
                case 3:
                    currentOdds = 16;
                    break;
                default:
                    currentOdds = 17;
            }
            for(y =0; y < numRow; y++)
            {
                simpleSpace = Math.min(y, numRow - y - 1);
                switch(simpleSpace)
                {
                    case 0:
                        currentOdds += 5;
                        break;
                    case 1:
                        currentOdds += 10;
                        break;
                    case 2:
                        currentOdds += 14;
                        break;
                    case 3:
                        currentOdds += 16;
                        break;
                    default:
                        currentOdds += 17;
                }
                seekGrid[x][y] = new MonteCoord(x, y, currentOdds);
            }
        }
    }
    
    @Override
    public Answer getAnswer(Guess guess) {
        return super.getAnswer(guess);
    } // end of getAnswer()


    @Override
    public Guess makeGuess() {
        // To be implemented.
        if(lockedTarget == null || lockedTarget.state != 'h')
        {//Seeking target
            MonteCoord chosenTarget = fetchBestSeek();
            Guess target = new Guess();
            target.column = chosenTarget.col;
            target.row = chosenTarget.row;
            return target;
        }
        else//TODO: Cleanup after killing a target?
        {//Eliminating target
            if(strikes.size() == 1)
            {
                //clean target seeking
                int width  = targetWidth(strikes.get(0).col,strikes.get(0).row);
                int height = targetHeight(strikes.get(0).col,strikes.get(0).row);
                int runningTotalOdds = 0;
                int i;
                /*for(i = 0; i < targetsLeft.length; i++)
                {
                    if(targetsLeft[i] != 0 && width - targetsLeft[i] > 0)
                    {
                        runningTotalOdds += Math.min(targetsLeft[i], width - targetsLeft[i] + 1 - strikes.size());
                    }
                    if(targetsLeft[i] != 0 && height - targetsLeft[i] > 0)
                    {
                        runningTotalOdds += Math.min(targetsLeft[i], height - targetsLeft[i] + 1 - strikes.size());
                    }
                    i++;
                }
                */
            }
            else
            {
                //multi-point seeking
                
            }
        }
        // dummy return
        return null;
    } // end of makeGuess()

    public MonteCoord fetchBestSeek()
    {
        int x, y, best = 0;
        ArrayList<MonteCoord> bestSquares = new ArrayList<MonteCoord>();
        for(x = 0; x < numCol; x++)
        {
            for(y = 0; y < numRow; y ++)
            {
                if(seekGrid[x][y].odds > best)
                {
                    bestSquares.clear();
                    best = seekGrid[x][y].odds;
                    bestSquares.add(seekGrid[x][y]);
                }
                else
                {
                    if(seekGrid[x][y].odds == best)
                    {
                        bestSquares.add(seekGrid[x][y]);
                    }
                }
            }
        }
        int randIndex = this.random.nextInt(bestSquares.size());
        return bestSquares.get(randIndex);
    }
    
    @Override
    public void update(Guess guess, Answer answer) {
        // To be implemented.
        if(answer.isHit)
        {
            if(answer.shipSunk == null)//Hit without sinking
            {
                seekGrid[guess.column][guess.row].state = 'h';
                seekGrid[guess.column][guess.row].odds = 0;
                lockedTarget = seekGrid[guess.column][guess.row];
                strikes.add(seekGrid[guess.column][guess.row]);
            }
            else//Hit, sunk
            {
                seekGrid[guess.column][guess.row].state = 's';
                seekGrid[guess.column][guess.row].odds = 0;
                clearStrikes();//TODO: IMPLEMENT THIS
                lockedTarget = null;
                int sunkIndex;
                switch(answer.shipSunk.name())
                {
                    case "AircraftCarrier":
                        sunkIndex = 4;
                        break;
                    case "Battleship":
                        sunkIndex = 3;
                        break;
                    case "Submarine":
                        sunkIndex = 2;
                        break;
                    case "Cruiser":
                        sunkIndex = 1;
                        break;
                    case "Destroyer":
                        sunkIndex = 0;
                        break;
                    default :
                        sunkIndex = -1;
                        System.out.println("Error: Bad sunk name");
                }
                //TODO Removing strikes here
                //strikes -= targetsLeft[sunkIndex];
                targetsLeft[sunkIndex] = 0;
                int i = targetsLeft.length - 1;
                while(targetsLeft[i] == 0)
                {
                    i--;
                }
                longestTarget = targetsLeft[i];
                oddsUpdate(targetsLeft);
            }
        }
        else//Missed
        {
            seekGrid[guess.column][guess.row].state = 'm';
            seekGrid[guess.column][guess.row].odds = 0;
            oddsUpdate(guess);
        }
    } // end of update()
    
    //Called after a shot is missed
    public void oddsUpdate(Guess guess)
    {
        int i, x, y;
        x = guess.column;
        y = guess.row;
        for(i = 0; i < longestTarget - 1; i++)
        {
            cellUpdate(x + i, y);
            cellUpdate(x - i, y);
            cellUpdate(x, y + i);
            cellUpdate(x, y - i);
        }
    }
    
    //Called after a ship is sunk
    public void oddsUpdate(int[] targetsLeft)
    {
        int x, y;
        for(x = 0; x < numCol; x++)
        {
            for(y = 0; y < numRow; y++)
            {
                cellUpdate(x,y);
            }
        }
    }
    
    //updates a single space
    public void cellUpdate(int x, int y)
    {
        int width = targetWidth(x, y);
        int height = targetHeight(x, y);
        int runningTotalOdds = 0;
        int i;
        for(i = 0; i < targetsLeft.length; i++)
        {
            if(targetsLeft[i] != 0 && width - targetsLeft[i] > 0)
            {
                runningTotalOdds += Math.min(targetsLeft[i], width - targetsLeft[i] + 1);
            }
            if(targetsLeft[i] != 0 && height - targetsLeft[i] > 0)
            {
                runningTotalOdds += Math.min(targetsLeft[i], height - targetsLeft[i] + 1);
            }
            i++;
        }
        seekGrid[x][y].odds = runningTotalOdds;
    }
    
    //Method for finding how many spaces are free to the left and right of a target
    public int targetWidth(int col, int row)
    {
        int width = 1;
        int i = 1;
        boolean stillClear = true;
        while(i < longestTarget && col + i < numCol && stillClear)
        {
            if(this.seekGrid[col+i][row].state == 'n' || this.seekGrid[col+i][row].state == 'h')
            {
                width++;
                i++;
            }
            else
            {
                stillClear = false;
            }
        }
        i = 1;
        stillClear = true;
        while(i < longestTarget && col-i >= 0 && stillClear)
        {
            if(this.seekGrid[col-i][row].state == 'n' || this.seekGrid[col-i][row].state == 'h')
            {
                width++;
                i++;
            }
            else
            {
                stillClear = false;
            }
        }
        return width;
    }
    
    //Method for finding how many spaces are free above and below a target
    public int targetHeight(int col, int row)
    {
        int height = 1;
        int i = 1;
        boolean stillClear = true;
        while(i < longestTarget && row + i < numRow && stillClear)
        {
            if(this.seekGrid[col][row+i].state == 'n' || this.seekGrid[col][row+i].state == 'h')
            {
                height++;
                i++;
            }
            else
            {
                stillClear = false;
            }
        }
        i = 1;
        stillClear = true;
        while(i < longestTarget && row-i >= 0 && stillClear)
        {
            if(this.seekGrid[col][row-i].state == 'n' || this.seekGrid[col][row-i].state == 'h')
            {
                height++;
                i++;
            }
            else
            {
                stillClear = false;
            }
        }
        return height;
    }
    
    public void clearStrikes()
    {
        //TODO
    }
    
    @Override
    public boolean noRemainingShips() {
        return super.noRemainingShips();
    } // end of noRemainingShips()

} // end of class MonteCarloGuessPlayer
