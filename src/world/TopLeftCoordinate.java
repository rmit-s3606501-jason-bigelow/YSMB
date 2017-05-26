package world;

import java.util.Comparator;
import java.util.Collections;
import java.util.PriorityQueue;

public final class TopLeftCoordinate implements Comparator<World.Coordinate> {
    public TopLeftCoordinate() {};
    /** Counter-intuively, return lower values for the higher preferences. This
     * is because the priority queue has higher priority for lower comparison
     * values.
     */
    public int compare(World.Coordinate a, World.Coordinate b) {
        // The top rows are more preferred
        if (a.row < b.row) {
            return -1;
        }
        else if (a.row == b.row) {
            // Left columns are more preferred
            if (a.column < b.column) {
                return -1;
            }
            else if (a.column == b.column) {
                return 0;
            }
            // Right columns are less preferred
            else /*if (a.column > b.column)*/ {
                return 1;
            }
        }
        // Bottom rows are less preferred
        else /*(a.row > b.row)*/ {
            return 1;
        }
    }

    public void comparisons(World w) {
        System.out.println(this.compare(
                    w.shots.get(0),
                    w.shots.get(3)
        ));
        System.out.println(this.compare(
                    w.shots.get(0),
                    w.shots.get(1)
        ));
        System.out.println(this.compare(
                    w.shots.get(1),
                    w.shots.get(0)
        ));
        System.out.println(this.compare(
                    w.shots.get(0),
                    w.shots.get(0)
        ));
    }

    /** Used to test the correctness of the comparator */
    public static void main(String[] args) {
        TopLeftCoordinate tlc = new TopLeftCoordinate();
        World w = new World();

        w.shots.add(w.new Coordinate(1,1));
        w.shots.add(w.new Coordinate(1,2));
        w.shots.add(w.new Coordinate(2,1));
        w.shots.add(w.new Coordinate(2,2));

        tlc.comparisons(w);   
        System.out.println("===");
        Collections.shuffle(w.shots);
        tlc.comparisons(w);

        PriorityQueue<World.Coordinate> pq = 
            new PriorityQueue<World.Coordinate>(tlc);
        pq.addAll(w.shots);
        System.out.println(pq);

        System.out.print("[");
        for (World.Coordinate co : pq) {
            System.out.print(co+", ");
        }
        System.out.println("]");
    }
}

