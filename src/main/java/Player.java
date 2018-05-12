import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

        Problem problem = new Problem(W, H, N);
        Batman batman = new Batman();
        ProblemSolver solver = new ProblemSolver(problem, batman);

        int X0 = in.nextInt();
        int Y0 = in.nextInt();

        int x = X0, y = Y0;

        // game loop
        while (true) {
            String bombDir = in.next(); // Current distance to the bomb compared to previous distance (COLDER, WARMER, SAME or UNKNOWN)

            // register this test with the result
            batman.addCheck(x, y, bombDir);

            // ask the solver to compute the next position
            Point nextPosition = solver.computeNextPosition();

            // output the position
            System.err.println("debug info ...");
            System.out.println(nextPosition.toString());
        }
    }
}

class Problem {
    Building building;
    int maxTurns;

    public Problem(int width, int height, int maxTurns) {
        this.building = new Building(width, height);
        this.maxTurns = maxTurns;
    }
}

class Building {
    int width;
    int height;

    public Building(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Point computeSymmetricalPosition(Point point) {
        int semiWidth = width / 2;
        int xs = point.x < semiWidth ? semiWidth + (semiWidth - point.x) : semiWidth - (point.x - semiWidth);
        int semiHeight = height / 2;
        int ys = point.y < semiHeight ? semiHeight + (semiHeight - point.y) : semiHeight - (point.y - semiHeight);
        Point symmetrical = new Point(xs, ys);
        System.err.println("point " + point.toString() + " get symmetrical " + symmetrical.toString());
        return symmetrical;
    }
}

class Batman {
    List<BatmanCheck> checks = new ArrayList<>();

    void addCheck(int x, int y, String rawDetectorAnswer) {
        checks.add(new BatmanCheck(new Point(x, y), DetectorAnswer.valueOf(rawDetectorAnswer)));
    }

    Point lastPosition() {
        return checks.get(checks.size() - 1).position;
    }

    DetectorAnswer lastDetectionAnswer() {
        return checks.get(checks.size() - 1).detectorAnswer;
    }
}

class Point {
    int x;
    int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + " " + y;
    }

    public int distance2(Point p) {
        return (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
    }
}

enum DetectorAnswer {
    WARMER,
    COLDER,
    SAME,
    UNKNOWN
}

class BatmanCheck {
    Point position;
    DetectorAnswer detectorAnswer;

    public BatmanCheck(Point position, DetectorAnswer detectorAnswer) {
        this.position = position;
        this.detectorAnswer = detectorAnswer;
    }
}

class ProblemSolver {
    private final Problem problem;
    private final Batman batman;

    public ProblemSolver(Problem problem, Batman batman) {
        this.problem = problem;
        this.batman = batman;
    }

    public Point computeNextPosition() {
        // if the last check answered unknown, then compute a symetrical position according to the building center
        if (batman.lastDetectionAnswer().equals(DetectorAnswer.UNKNOWN)) {
            System.err.println("next = Symmetrical position");
            return problem.building.computeSymmetricalPosition(batman.lastPosition());
        }

        return batman.lastPosition();
    }
}