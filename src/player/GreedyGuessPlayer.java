package player;

import java.util.Scanner;
import world.World;

import java.util.Random;
import java.util.ArrayList;
import java.util.PriorityQueue;

import java.util.Comparator;

import world.TopLeftCoordinate;

/**
 * Greedy guess player (task B).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class GreedyGuessPlayer extends RandomGuessPlayer implements Player {

    // Some Fields are inherited from RandomGuessPlayer
    boolean targetingMode = false;
    private PriorityQueue<World.Coordinate> possibleCoords;
    private World enemyWorld;
    private Comparator<World.Coordinate> comparator;
    
    @Override
    public void initialisePlayer(World world) {
        this.numRow = world.numRow;
        this.numCol = world.numColumn;
        this.world = world;
        this.random = new Random();
        this.enemyWorld = new World();
        this.comparator = new TopLeftCoordinate();
        this.possibleCoords = new
            PriorityQueue<World.Coordinate>(comparator);
        /* Construct a list of all unguessed cells viz. all cells, which we 
           will later make guesses as a random index of. */
        this.unguessed = new ArrayList<Guess>();
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
        System.out.println("Random player init: unguessed grid=");
        System.out.println(unguessed);
    }

    /** Inherit from RandomGuessPlayer*/
    public Answer getAnswer(Guess guess) {
        return super.getAnswer(guess);
    }


    @Override
    public Guess makeGuess() {
        if (targetingMode) {
            World.Coordinate wcGuess = possibleCoords.poll();
            if (wcGuess == null) {
                break;
            }
            Guess g = new Guess();
            g.row = wcGuess.row;
            g.column = wcGuess.column;
            return g;
        }
        /*else*/  {
            // Grab a random guess, but avoid those we have already shot at in
            // targeting mode. Targeting mode does not remove the guesses, so we
            // need to check that they are new before making them.
            while (true) {
                Guess randomGuess = super.makeGuess();
                World.Coordinate coord = world.new Coordinate();
                coord.row = randomGuess.row;
                coord.column = randomGuess.column;
                if (world.shots.contains(coord)) {
                    unguessed.remove(randomGuess); 
                    continue;
                }
                return randomGuess;
            }
        }
    }


    @Override
    /** Update our tactic according to the response:
      * If we hit and didn't destroy, keep targeting;
      * else we keep randomly guessing from our guess set.
      */
    public void update(Guess guess, Answer answer) {
        // If we hit but haven't destroyed, targeting mode
        if (answer.isHit && answer.shipSunk == null) {
            /* Enqueue all adjacent cells that haven't already been fired at */

            /* Generate -1, 0, +1 for both directions */

            /* Rows */
            for (int i = -1; i < 2; i += 2) {
                // Construct the coordinate
                World.Coordinate coord = enemyWorld.new Coordinate();
                coord.row = guess.row + i;
                coord.column = guess.column;
                // Do not insert out of range coordinate
                if (coord.row < 0 || coord.row > (numRow-1)) {
                    continue;
                }

                // Do not insert a cell we have already fired at
                if (!world.shots.contains(coord)) {
                    possibleCoords.add(coord);
                }
            }
            for (int i = -1; i < 2; i += 2) {
                World.Coordinate coord = enemyWorld.new Coordinate();
                coord.column = guess.column + i;
                coord.row = guess.row;
                // Do not insert out of range coordinate
                if (coord.column < 0 || coord.column > (numCol)-1) {
                    continue;
                }

                // Do not insert a cel we have already fired at
                if (!world.shots.contains(coord)) {
                    possibleCoords.add(coord);
                }
            }
            targetingMode = true;
        }
        // Otherwise keep guessing randomly
        else {
            targetingMode = false;
        }
    }


    /** Does the same thing as RandomGuessPlayer */
    public boolean noRemainingShips() {
        return super.noRemainingShips();
    }

}
