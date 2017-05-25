package player;

import java.util.Scanner;
import world.World;

/**
 * Monte Carlo guess player (task C).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class MonteCarloGuessPlayer extends RandomGuessPlayer implements Player{

    boolean lockedTarget = false;
    public ArrayList<World.Coordinate> shotsFired = new ArrayList<>();
    public MonteCoord[][] targetGrid;
    public int[] targetsLeft;
    public int longestTarget;
    @Override
    public void initialisePlayer(World world) {
        // Pulled from Jason's greedy code; waste not, want not
        this.numRow = world.numRow;
        this.numCol = world.numColumn;
        this.world = world;
        this.random = new Random();
        /* Construct a list of all unguessed cells viz. all cells, which we 
           will later make guesses as a random index of. */
        this.unguessed = new ArrayList<Guess>();//TODO: maybe remove this?
        /* For every row */
        for (int row = 0; row < numRow; ++row) {
            /* Do one set of columns in the even rows,
               another in the odd rows */
            for (int col = row%2; col < numCol; col+= 2) {
                /* Form a guess for the row and column */
                Guess cellGuess = new Guess();
                cellGuess.row = row;
                cellGuess.column = col;
                /* Add it to the list */
                unguessed.add(cellGuess);
            }
        }
        targetGrid = new MonteCoord[numCol][numRow];
        populateTargetGrid();
        longestTarget = 5;
        targetsLeft = {2, 3, 3, 4, 5};
        longestTarget = 5;
        System.out.println("Monte Carlo player init: unguessed grid=");
        System.out.println(unguessed);
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
                targetGrid[x][y] = new MonteCoord(x, y, currentOdds);
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
        if(!lockedTarget)
        {//Seeking target
            MonteCoord chosenTarget = fetchBestSeek();
            Guess target = new Guess();
            target.col = chosenTarget.col;
            target.row = chosenTarget.row;
            return target;
        }
        else//TODO: Cleanup after killing a target?
        {//Eliminating target
            
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
                if(targetGrid[x][y].odds > best)
                {
                    bestSquares.clear()
                    best = targetGrid[x][y].odds;
                    bestSquares.add(targetGrid[x][y]);
                }
                else
                {
                    if(targetGrid[x][y].odds == best)
                    {
                        bestSquares.add(targetGrid[x,y]);
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
                targetGrid[guess.column][guess.row].state = 'h';
                targetGrid[guess.column][guess.row].odds = 0;
                lockedTarget = true;
            }
            else//Hit, sunk
            {
                targetGrid[guess.column][guess.row].state = 's';
                targetGrid[guess.column][guess.row].odds = 0;
                clearStrikes();//TODO: IMPLEMENT THIS
                lockedTarget = false;
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
                targetsLeft[sunkIndex] = 0;
                int i = targetsLeft.length - 1;
                while(targetsLeft[i] == 0)
                {
                    i--;
                }
                longestTarget = targetsLeft[i];
            }
        }
        else//Missed
        {
            targetGrid[guess.column][guess.row].state = 'm';
            targetGrid[guess.column][guess.row].odds = 0;
            oddsUpdate(guess);
        }
    } // end of update()
    
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
        targetGrid[x][y].odds = runningTotalOdds;
    }
    
    //Method for finding how many spaces are free to the left and right of a target
    public int targetWidth(int col, int row)
    {
        width = 1;
        int i = 1;
        boolean stillClear = true;
        while(i < longestTarget && col + i < numCol && stillClear)
        {
            if(this.targetGrid[col+i][row].state == 'n' || this.targetGrid[col+i][row] == 'h')
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
            if(this.targetGrid[col-i][row].state == 'n' || this.targetGrid[col-i][row] == 'h')
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
        height = 1;
        int i = 1;
        boolean stillClear = true;
        while(i < longestTarget && row + i < numRow && stillClear)
        {
            if(this.targetGrid[col][row+i].state == 'n' || this.targetGrid[col][row+i] == 'h')
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
            if(this.targetGrid[col][row-i].state == 'n' || this.targetGrid[col][row-i] == 'h')
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
        return Super.noRemainingShips();
    } // end of noRemainingShips()

} // end of class MonteCarloGuessPlayer
