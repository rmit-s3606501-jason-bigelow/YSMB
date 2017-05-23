package player;

import java.util.Scanner;
import world.World;

import java.util.Random;
import java.util.ArrayList;

/**
 * Random guess player (task A).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey, s3606501
 */
public class RandomGuessPlayer implements Player{

    protected int numRow;
    protected int numCol;
    protected World world;
    protected Random random;
    protected ArrayList<Guess> unguessed;

    @Override
    /** Initialise our random guessing playewr. */
    public void initialisePlayer(World world) {
        this.numRow = world.numRow;
        this.numCol = world.numColumn;
        this.world = world;
        this.random = new Random();
        /* Construct a list of all unguessed cells viz. all cells, which we 
           will later make guesses as a random index of. */
        this.unguessed = new ArrayList<Guess>(numRow*numCol);
        for (int row = 0; row < numRow; ++row) {
            for (int col = 0; col < numCol; ++col) {
                /* Form a guess for the row and column */
                Guess cellGuess = new Guess();
                cellGuess.row = row;
                cellGuess.column = col;
                /* Add it to the list */
                unguessed.add(cellGuess);
            }
        }
    }

    @Override
    /** Answer the other player about whether they hit anything and which type
      * (if any) of ship they sunk.
      */
    public Answer getAnswer(Guess guess) {
        Answer a = new Answer();
        ship.Ship hitShip = null;
        /* Check all of our ships */
        for (World.ShipLocation sl : world.shipLocations) {
            /* Check if this ship contains the guess */
            for (World.Coordinate c : sl.coordinates) {
                if (c.row == guess.row && c.column == guess.column) {
                    hitShip = sl.ship;
                }
            }
            /* This is not the ship you are looking for */
            if (hitShip == null) continue;

            /* Check how many cells were sunk in this ship */
            int sunkCells = 0;
            for (World.Coordinate c : sl.coordinates) {
                if (world.shots.contains(c)) {
                    ++sunkCells;
                }
            }
            /* Construct our answer */
            if (sunkCells > 0) {
                a.isHit = true;
                /* If we sunk all the cells, return the ship */
                if (sunkCells == sl.ship.len()) {a.shipSunk = sl.ship;}
                /* else return no ship sunk */
                else {a.shipSunk = null;}
                return a;
            }
        }
        /* If none of our ships were hit */
        a.isHit = false;
        a.shipSunk = null;
        return a;
    }


    @Override
    /** Pick a cell at random from the list of unchosen cells */
    public Guess makeGuess() {
        /* If we run out of guesses */
        if (unguessed.size() == 0) {return null;}

        /* Pick one of the columns that hasn't yet been guessed */
        int nextGuessIdx = random.nextInt(unguessed.size());
        Guess g = unguessed.get(nextGuessIdx);
        
        /* Don't guess here again */
        unguessed.remove(nextGuessIdx);
        
        return g;
    }


    @Override
    /** Update our own decision-making process according to whether we hit
      * anything or not. For this algorithm we don't care so it's a no-op.
      */
    public void update(Guess guess, Answer answer) {}

    @Override
    /** Check whether we have no remaining ships */
    public boolean noRemainingShips() {
        /* For each ship on our board */
        //System.out.println(world.shots);
        for (World.ShipLocation sl : world.shipLocations) {
            /*System.out.print("Checking ShipLocation:");
            System.out.println(
                "sl={name:"+sl.ship.name()+
                ",coords:{row:"+sl.coordinates.get(0).row
                +",col:"+sl.coordinates.get(0).column+"}"
            );/*
            /* For each cell of the ship */
            for (World.Coordinate c : sl.coordinates) {
                //System.out.println("c={row:"+c.row+",col:"+c.column+"}");
                if (!world.shots.contains(c)) {
                    return false;
                }
            }
        }
        // We found no ships with unsunk cells
        return true;
    }

}
