package player;

import java.util.Scanner;
import world.World;

import java.util.Random;
import java.util.ArrayList;

/**
 * Greedy guess player (task B).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class GreedyGuessPlayer extends RandomGuessPlayer implements Player {

    // Some Fields are inherited from RandomGuessPlayer
    boolean targetingMode = false;
    
    @Override
    public void initialisePlayer(World world) {
        this.numRow = world.numRow;
        this.numCol = world.numColumn;
        this.world = world;
        this.random = new Random();
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
            // NYI
            return super.makeGuess();
        }
        else return super.makeGuess();
    }


    @Override
    /** Update our tactic according to the response:
      * If we hit and didn't destroy, keep targeting;
      * else we keep randomly guessing from our guess set.
      */
    public void update(Guess guess, Answer answer) {
        // TODO: Detect which direction the ship is placed after two guesses
        // If we hit but haven't destroyed, targeting mode
        if (answer.isHit && answer.shipSunk == null) {
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
