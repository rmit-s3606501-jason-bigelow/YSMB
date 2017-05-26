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
    public MonteCoord lockedTarget = null;
    public boolean lockChanged;//TODO: consider moving to a method
    public ArrayList<MonteCoord> strikes;
    public MonteCoord[][] seekGrid;
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
        lockChanged = false;
    } // end of initialisePlayer()

    public void populateTargetGrid()
    {
        int x, y, simpleSpace, currentOddsX = 0, currentOddsY = 0;
        //Populating the central regions
        for(x = 0; x < numCol; x++)
        {
            simpleSpace = Math.min(x, numCol - x - 1);
            switch(simpleSpace)
            {
                case 0:
                    currentOddsX = 5;
                    break;
                case 1:
                    currentOddsX = 10;
                    break;
                case 2:
                    currentOddsX = 14;
                    break;
                case 3:
                    currentOddsX = 16;
                    break;
                default:
                    currentOddsX = 17;
            }
            for(y =0; y < numRow; y++)
            {
                simpleSpace = Math.min(y, numRow - y - 1);
                switch(simpleSpace)
                {
                    case 0:
                        currentOddsY = 5;
                        break;
                    case 1:
                        currentOddsY = 10;
                        break;
                    case 2:
                        currentOddsY = 14;
                        break;
                    case 3:
                        currentOddsY = 16;
                        break;
                    default:
                        currentOddsY = 17;
                }
                seekGrid[x][y] = new MonteCoord(x, y, currentOddsX + currentOddsY);
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
        MonteCoord chosenTarget;
        if(lockedTarget == null || lockedTarget.state != 'h')
        {//Seeking target
            chosenTarget = fetchBestSeek();
        }
        else
        {//Eliminating target
            chosenTarget = fetchBestKill();
        }
        if(chosenTarget.state != 'n')
        {
            System.out.println("ERROR: USED SPACE FIRED UPON");
        }
        chosenTarget.odds = 0;
        chosenTarget.killerOdds = 0;
        Guess target = new Guess();
        target.column = chosenTarget.col;
        target.row = chosenTarget.row;
        return target;
    } // end of makeGuess()

    public MonteCoord fetchBestSeek()
    {
        int x, y, best = 0;
        ArrayList<MonteCoord> bestSquares = new ArrayList<MonteCoord>();
        for(x = 0; x < numCol; x++)
        {
            for(y = 0; y < numRow; y ++)
            {
                if(seekGrid[x][y].state == 'n')
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
        }
        int randIndex = this.random.nextInt(bestSquares.size());
        return bestSquares.get(randIndex);
    }
    
    public MonteCoord fetchBestKill()
    {
        int x, y, best = 0;
        ArrayList<MonteCoord> bestSquares = new ArrayList<MonteCoord>();
        for(x = 0; x < numCol; x++)
        {
            for(y = 0; y < numRow; y ++)
            {
                if(seekGrid[x][y].killerOdds > best)
                {
                    bestSquares.clear();
                    best = seekGrid[x][y].killerOdds;
                    bestSquares.add(seekGrid[x][y]);
                }
                else
                {
                    if(seekGrid[x][y].killerOdds == best)
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
            strikes.add(seekGrid[guess.column][guess.row]);
            if(answer.shipSunk == null)//Hit without sinking
            {
                seekGrid[guess.column][guess.row].state = 'h';
                seekGrid[guess.column][guess.row].odds = 0;
                lockedTarget = seekGrid[guess.column][guess.row];
                lockChanged = true;
                updateKillerOdds(lockedTarget);
            }
            else//Hit, sunk
            {
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
                clearStrikes(targetsLeft[sunkIndex], guess.column, guess.row);
                targetsLeft[sunkIndex] = 0;
                int i = targetsLeft.length - 1;
                while(i >= 0 && targetsLeft[i] == 0)
                {
                    i--;
                }
                if(i >= 0)
                {
                    longestTarget = targetsLeft[i];
                    oddsUpdate(targetsLeft);
                    recalcAllKillerOdds();
                }
                else
                {
                    longestTarget = 0;
                }
                
            }
        }
        else//Missed
        {
            seekGrid[guess.column][guess.row].state = 'm';
            seekGrid[guess.column][guess.row].odds = 0;
            oddsUpdate(guess);
            lockChanged = false;
            if(lockedTarget != null)
            {
                recalcAllKillerOdds();
            }
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
            if(x + i < numCol)
            {
                if(seekGrid[x+i][y].state == 'n')
                {
                    cellUpdate(x + i, y);
                }
            }
            if(x - i >= 0)
            {
                if(seekGrid[x-i][y].state == 'n')
                {
                    cellUpdate(x - i, y);
                }
            }
            if(y + i < numRow)
            {
                if(seekGrid[x][y+i].state == 'n')
                {
                    cellUpdate(x, y + i);
                }
            }
            if(y - i >= 0)
            {
                if(seekGrid[x][y-i].state == 'n')
                {
                    cellUpdate(x, y - i);
                }
            }
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
                if(seekGrid[x][y].state == 'n')
                {
                    cellUpdate(x,y);
                }
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
    
    public void updateKillerOdds(MonteCoord givenLock)
    {
        int i, ships, spacesAbove,spacesBelow,spacesRight,spacesLeft, maxVal;
        boolean stillClear;
        for(ships = 0; ships < targetsLeft.length; ships++)
        {
            i = 1;
            spacesAbove = 0;
            spacesBelow = 0;
            spacesRight = 0;
            spacesLeft  = 0;
            maxVal = 0;
            stillClear = true;
            //Working through each direction, finding how far it can go in that direction
            while( i < targetsLeft[ships] && stillClear && givenLock.col - i >= 0)
            {
                if(seekGrid[givenLock.col - i][givenLock.row].state == 'h' || seekGrid[givenLock.col - i][givenLock.row].state == 'n')
                {
                    i++;
                    spacesLeft++;
                }
                else
                {
                    stillClear = false;
                }
            }
            i = 1;
            stillClear = true;
            while( i < targetsLeft[ships] && stillClear && givenLock.col + i < this.numCol)
            {
                if(seekGrid[givenLock.col + i][givenLock.row].state == 'h' || seekGrid[givenLock.col + i][givenLock.row].state == 'n')
                {
                    i++;
                    spacesRight++;
                }
                else
                {
                    stillClear = false;
                }
            }
            i = 1;
            stillClear = true;
            while( i < targetsLeft[ships] && stillClear && givenLock.row + i < this.numRow)
            {
                if(seekGrid[givenLock.col][givenLock.row + i].state == 'h' || seekGrid[givenLock.col][givenLock.row + i].state == 'n')
                {
                    i++;
                    spacesAbove++;
                }
                else
                {
                    stillClear = false;
                }
            }
            i = 1;
            stillClear = true;
            while( i < targetsLeft[ships] && stillClear && givenLock.row - i >= 0)
            {
                if(seekGrid[givenLock.col][givenLock.row - i].state == 'h' || seekGrid[givenLock.col][givenLock.row - i].state == 'n')
                {
                    i++;
                    spacesBelow++;
                }
                else
                {
                    stillClear = false;
                }
            }
            //Iterating through each axis, adding up the expected
            if(spacesAbove + spacesBelow + 1 >= targetsLeft[ships])
            {
                maxVal = Math.min(targetsLeft[ships] - 1, spacesAbove + spacesBelow + 2 - targetsLeft[ships]);
                for(i = spacesBelow; i > 0; i--)
                {
                    if(seekGrid[givenLock.col][givenLock.row - i].state == 'n')
                    {
                        seekGrid[givenLock.col][givenLock.row - i].killerOdds += Math.min(maxVal, spacesBelow + 1 - i);
                    }
                }
                for(i = spacesAbove; i > 0; i--)
                {
                    if(seekGrid[givenLock.col][givenLock.row + i].state == 'n')
                    {
                        seekGrid[givenLock.col][givenLock.row + i].killerOdds += Math.min(maxVal, spacesAbove + 1 - i);
                    }
                }
            }
            if(spacesLeft + spacesRight + 1 >= targetsLeft[ships])
            {
                maxVal = Math.min(targetsLeft[ships] - 1, spacesLeft + spacesRight + 2 - targetsLeft[ships]);
                for(i = spacesLeft; i > 0; i--)
                {
                    if(seekGrid[givenLock.col - i][givenLock.row].state == 'n')
                    {
                        seekGrid[givenLock.col - i][givenLock.row].killerOdds += Math.min(maxVal, spacesLeft + 1 - i);
                    }
                }
                for(i = spacesRight; i > 0; i--)
                {
                    if(seekGrid[givenLock.col + i][givenLock.row].state == 'n')
                    {
                        seekGrid[givenLock.col + i][givenLock.row].killerOdds += Math.min(maxVal, spacesRight + 1 - i);
                    }
                }
            }
        }
    }//End of updateKillerOdds
    
    //Method for finding how many spaces are free to the left and right of a target
    public int targetWidth(int col, int row)
    {
        if(col < 0 || col >= numCol || row < 0 || row >= numRow)
        {
            return 0;
        }
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
    
    //Cleans out the list of recorded strikes, and checks if we need to keep pursing
    //any partially eliminated contacts
    public void clearStrikes(int killLength, int x, int y)
    {
        MonteCoord currentClearing = seekGrid[x][y];
        currentClearing.state = 'h';
        if(strikes.size() == killLength)//All hits are sunk, so we clear everything
        {
            
            for(int i = 0; i < killLength; i++)
            {
                currentClearing = strikes.get(i);
                currentClearing.state = 's';
                currentClearing.odds = 0;
                currentClearing.killerOdds = 0;
            }
            lockChanged = false;
            lockedTarget = null;
            strikes.clear();
        }
        else//some hits remain, so we eliminate the killed ship, and keep hunting
        {
            //Finding the spaces in the killed ship TODO: Redo all of this
            /*int xShift = x - lockedTarget.col;
            int yShift = y - lockedTarget.row;
            int i;
            for(i = 0; i < killLength; i++)
            {
                currentClearing = seekGrid[x + xShift * i][y + yShift * i];
                if(currentClearing.state == 'h')
                {
                    currentClearing.state = 's';
                    currentClearing.odds = 0;
                    currentClearing.killerOdds = 0;
                    strikes.remove(currentClearing);
                }
                else
                {
                    System.out.println("ERROR: Wrong cell found in clearing strikes.");
                }
            }*/
            
            //Checking each direction
            char dir;//N,S,E,W
            int length = 0, i;
            while(x + length + 1 < numCol - 1 && seekGrid[x + length + 1][y].state == 'h' && length < killLength)
            {
                length++;
            }
            if(length == killLength)
            {
                dir = 'E';
            }
            length = 0;
            while(x - length - 1 >= 0 && seekGrid[x - length - 1][y].state == 'h' && length < killLength)
            {
                length++;
            }
            if(length == killLength)
            {
                dir = 'W';
            }
            length = 0;
            while(y + length + 1 < numRow - 1 && seekGrid[x][y + length + 1].state == 'h' && length < killLength)
            {
                length++;
            }
            if(length == killLength)
            {
                dir = 'N';
            }
            
            while(y - length - 1 >=0 && seekGrid[x][y - length - 1].state == 'h' && length < killLength)
            {
                length++;
            }
            if(length == killLength)
            {
                dir = 'S';
            }
            else
            {
                dir = 'B';
                System.out.println("ERROR: COULD NOT FIND SHIP DIRECTION");
            }
            for(i = 0; i < killLength; i++)
            {
                switch(dir)
                {
                    case 'N':
                        currentClearing = seekGrid[x][y + i];
                        break;
                    case 'S':
                        currentClearing = seekGrid[x][y - i];
                        break;
                    case 'E': 
                        currentClearing = seekGrid[x + i][y];
                        break;
                    case 'D':
                        currentClearing = seekGrid[x - i][y];
                        break;
                    default: System.out.println("ERROR: BAD DIRECTION");
                }
                currentClearing.killerOdds = 0;
                currentClearing.odds = 0;
                currentClearing.state = 's';
                strikes.remove(currentClearing);
            }
            //redoing the odds
            recalcAllKillerOdds();
            //Locking a new target from the strikes array
            lockedTarget = strikes.get(random.nextInt(strikes.size()));
            lockChanged = true;
        }
    }
    
    public void recalcAllKillerOdds()
    {
        int x, y, i;
        for(x = 0; x < numCol; x++)//Cleaning out the old
        {
            for(y = 0; y < numRow; y++)
            {
                seekGrid[x][y].killerOdds = 0;
            }
        }
        for(i = 0; i < strikes.size(); i++)//And repopulating
        {
            updateKillerOdds(strikes.get(i));
        }
    }
    
    @Override
    public boolean noRemainingShips() {
        return super.noRemainingShips();
    } // end of noRemainingShips()

} // end of class MonteCarloGuessPlayer
