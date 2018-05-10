import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int W = in.nextInt(); // width of the building.
        int H = in.nextInt(); // height of the building.
        int N = in.nextInt(); // maximum number of turns before game over.
        int X0 = in.nextInt();
        int Y0 = in.nextInt();

        // game loop
        while (true) {
            String bombDir = in.next(); // Current distance to the bomb compared to previous distance (COLDER, WARMER, SAME or UNKNOWN)

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println("0 0");
        }
    }
}