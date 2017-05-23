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
public class GreedyGuessPlayer implements Player extends RandomGuessPlayer {

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

    @Override
    public Answer getAnswer(Guess guess) {
        // To be implemented.

        // dummy return
        return null;
    }


    @Override
    public Guess makeGuess() {
        // To be implemented.

        // dummy return
        return null;
    }


    @Override
    public void update(Guess guess, Answer answer) {
        // To be implemented.
        if (Answer.isHit) {
            // Keep targeting 
            if (Answer.shipSunk == null) {
            
            }
            // Resume random guessing
            else {
            
            }
        }
        else {
            targetingMode = false;
        }
    }


    /** Does the same thing as RandomGuessPlayer */
    public boolean noRemainingShips() {
        super.noRemainingShips();
    }

}
