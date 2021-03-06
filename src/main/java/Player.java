import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

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
//        ProblemSolver solver = new ProblemSolver(problem, batman);
        ProblemSolverFinal solver = new ProblemSolverFinal(batman, problem);

        int X0 = in.nextInt();
        int Y0 = in.nextInt();

        int x = X0, y = Y0;

        // game loop
        while (true) {
            String bombDir = in.next(); // Current distance to the bomb compared to previous distance (COLDER, WARMER, SAME or UNKNOWN)

            // register this test with the result
            batman.addCheck(x, y);
            solver.notifyDetectionAnswer(DetectorAnswer.valueOf(bombDir)); // tell the solver what is the detection for the last submitted position

            // ask the solver to compute the next position
            Point nextPosition = solver.computeNextPosition();

            // output the position
            System.out.println(nextPosition.toString());
            x = nextPosition.x;
            y = nextPosition.y;
        }
    }
}

class Problem {
    Building building;
    int maxTurns;

    public Problem(int width, int height, int maxTurns) {
        this.building = new Building(width, height);
        this.maxTurns = maxTurns;
        System.err.println("Building w=" + width + ", h=" + height + " --> max turns=" + maxTurns);
    }
}

class Building {
    int width;
    int height;

    public Building(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean contains(Point pt) {
        return pt.x >= 0 && pt.x < width && pt.y >= 0 && pt.y < height;
    }

    public Point computeSymetricalOf(Point pt) {
        return new Point(width - pt.x, height - pt.y);
    }

    public List<StraightLine> getBoundingStraightLines() {
        List<StraightLine> bounds = new ArrayList<>();
        bounds.add(new StraightLine(0, 1, 0, StraightLine.REGION.POSITIVE));
        bounds.add(new StraightLine(0, 1, 1 - height, StraightLine.REGION.NEGATIVE));
        bounds.add(new StraightLine(1, 0, 0, StraightLine.REGION.POSITIVE));
        bounds.add(new StraightLine(1, 0, 1 - width, StraightLine.REGION.NEGATIVE));
        return bounds;
    }
}

class Batman {
    Point lastPosition;
    Point positionBefore;

    void addCheck(int x, int y) {
        if (lastPosition != null) {
            positionBefore = new Point(lastPosition);
        }

        lastPosition = new Point(x, y);
    }

    Point lastPosition() {
        return lastPosition;
    }

    Point positionBefore() {
        return positionBefore;
    }
}

class Point {
    int x;
    int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point lastPosition) {
        this.x = lastPosition.x;
        this.y = lastPosition.y;
    }

    @Override
    public String toString() {
        return x + " " + y;
    }

    public int distance2(Point p) {
        return (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        return y == point.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}

enum DetectorAnswer {
    WARMER,
    COLDER,
    SAME,
    UNKNOWN;
}

class ProblemSolver {
    private final Problem problem;
    private final Batman batman;
    private final PositionRange rangeX;
    private final PositionRange rangeY;
    private PositionFinder currentFinder;

    public ProblemSolver(Problem problem, Batman batman) {
        this.problem = problem;
        this.batman = batman;
        this.rangeX = new PositionRange(0, problem.building.width - 1);
        this.rangeY = new PositionRange(0, problem.building.height - 1);
        // start to look for X
        this.currentFinder = new PositionFinderX(rangeX);
    }

    public Point computeNextPosition() {
        Point nextPosition;
        // if current finder is X and works with a single point range, no need to ask it for a position
        int forceX = -1;
        if (currentFinder.lookForX() && currentFinder.range.length() == 1) {
            forceX = currentFinder.range.max; // same as min
        } else {
            nextPosition = currentFinder.computeNextPosition(batman.lastPosition(), true);
            System.err.println("       for range " + currentFinder.range + ", compute next point " + nextPosition);
            if (!currentFinder.samePosition(batman.lastPosition(), nextPosition)) {
                return nextPosition;
            }
        }

        // the position is the same for the finder ? So activate next finder.
        System.err.println("Switching to finder Y");
        currentFinder = new PositionFinderY(rangeY);
        nextPosition = currentFinder.computeNextPosition(batman.lastPosition(), true);
        System.err.println("       for range " + currentFinder.range + ", compute next point " + nextPosition);

        if (forceX >= 0) {
            System.err.println("Force X position to " + forceX);
            nextPosition.x = forceX;
        }
        return nextPosition;
    }

    public void notifyDetectionAnswer(DetectorAnswer answer) {
        if (answer.equals(DetectorAnswer.UNKNOWN)) {
            return;
        }

        // get the 2 last positions of Batman
        Point lastPosition = batman.lastPosition();
        Point positionBefore = batman.positionBefore();

        // adjust the search range
        currentFinder.adjustRange(positionBefore, lastPosition, answer);
    }
}

class ProblemSolverFinal {
    private final Batman batman;
    private final Problem problem;
    private PositionFinderMultilateration currentFinder;

    public ProblemSolverFinal(Batman batman, Problem problem) {
        this.batman = batman;
        this.problem = problem;
        this.currentFinder = new PositionFinderMultilateration(problem.building);
    }

    public Point computeNextPosition() {
        try {
            Point nextPosition = currentFinder.computeNextPosition(batman.lastPosition);
            System.err.println("       compute next point " + nextPosition);
            return nextPosition;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyDetectionAnswer(DetectorAnswer answer) {
        if (answer.equals(DetectorAnswer.UNKNOWN)) {
            return;
        }

        // get the 2 last positions of Batman
        Point lastPosition = batman.lastPosition();
        Point positionBefore = batman.positionBefore();

        // adjust the search range
        currentFinder.notifyDetectionAnswer(positionBefore, lastPosition, answer);
    }
}

class PositionRange {
    int min;
    int max;
    boolean roundToLower = true;

    public PositionRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public PositionRange(PositionRange range) {
        this.min = range.min;
        this.max = range.max;
    }

    @Override
    public String toString() {
        return "[" + min + "-" + max + "]";
    }

    public boolean contains(Point point, boolean abscisse) {
        if (abscisse) {
            return point.x >= min && point.x <= max;
        } else {
            return point.y >= min && point.y <= max;
        }
    }

    public int middle() {
        // change the rule for next time it must be applied
        if (((min + max) % 2) == 1) {
            roundToLower = !roundToLower;
            if (roundToLower) {
                return Math.floorDiv(min + max, 2);
            } else {
                return Math.floorDiv(min + max, 2) + 1;
            }
        }
        return (min + max) / 2;
    }

    public int length() {
        return max - min + 1;
    }
}

abstract class PositionFinder {
    PositionRange range;

    protected PositionFinder(PositionRange range) {
        this.range = range;
    }

    public abstract boolean lookForX();

    public abstract Point computeNextPosition(Point point, boolean canRecurse);

    public abstract boolean samePosition(Point p1, Point p2);

    public abstract void adjustRange(Point positionBefore, Point lastPosition, DetectorAnswer answer);
}

class PositionFinderX extends PositionFinder {

    public PositionFinderX(PositionRange rangeX) {
        super(rangeX);
    }

    @Override
    public boolean lookForX() {
        return true;
    }

    @Override
    public void adjustRange(Point positionBefore, Point lastPosition, DetectorAnswer answer) {
        // We expect the two points to be in the range
        // we split the points by their middle, keeping the range part according to the detector answer
        PositionRange rangeCopy = new PositionRange(range);
        int middle = (positionBefore.x + lastPosition.x) / 2;
        int deltaX = Math.abs(positionBefore.x - lastPosition.x);

        if (answer.equals(DetectorAnswer.SAME)) {
            range.min = middle;
            range.max = middle;
        } else {
            if (positionBefore.x < lastPosition.x) {
                if (deltaX > 1) {
                    if (answer.equals(DetectorAnswer.COLDER)) {
                        range.max = middle;
                    } else {
                        range.min = middle;
                    }
                } else {
                    if (answer.equals(DetectorAnswer.COLDER)) {
                        range.max = positionBefore.x;
                    } else {
                        range.min = lastPosition.x;
                    }
                }
            } else {
                if (deltaX > 1) {
                    if (answer.equals(DetectorAnswer.COLDER)) {
                        range.min = middle;
                    } else {
                        range.max = middle;
                    }
                } else {
                    if (answer.equals(DetectorAnswer.COLDER)) {
                        range.min = positionBefore.x;
                    } else {
                        range.max = lastPosition.x;
                    }
                }
            }
        }

        System.err.println("Points " + positionBefore + " and " + lastPosition + " with answer " + answer + " change range from " + rangeCopy + " to " + range);
    }

    @Override
    public Point computeNextPosition(Point lastPosition, boolean canRecurse) {
        int positionX;
        if (range.contains(lastPosition, lookForX())) {
            if (range.length() > 2) {
                positionX = range.middle();

                // if next position is too close accoording to the range size, then use the other algo
                if (range.length() >= 10 && Math.abs(lastPosition.x - positionX) < 0.8 * range.length() / 2) {
                    System.err.println("position too close --> use middle algo");
                    if (lastPosition.x > range.middle()) {
                        // |        x  | --> |    o     x  |
                        positionX = lastPosition.x / 2;
                    } else {
                        // |  x        | --> |  x    o     |
                        positionX = lastPosition.x - (lastPosition.x - range.max) / 2;
                    }
                }
            } else {
                // we expect the range length to be 2
                if (lastPosition.x == range.min) {
                    positionX = range.max;
                } else {
                    positionX = range.min;
                }
            }
        } else {
            // compute the middle of the range
            positionX = range.middle();
        }
        if (positionX == lastPosition.x && canRecurse) {
            // call it again to change the middle computation strategy
            return computeNextPosition(lastPosition, !canRecurse);
//            System.err.println("Force to max");
//            positionX = range.max;
        }

        return new Point(positionX, lastPosition.y);
    }

    @Override
    public boolean samePosition(Point p1, Point p2) {
        return p1.x == p2.x;
    }
}

class PositionFinderY extends PositionFinder {

    public PositionFinderY(PositionRange rangeY) {
        super(rangeY);
    }

    @Override
    public boolean lookForX() {
        return false;
    }

    @Override
    public void adjustRange(Point positionBefore, Point lastPosition, DetectorAnswer answer) {
        // We expect the two points to be in the range
        // we split the points by their middle, keeping the range part according to the detector answer
        PositionRange rangeCopy = new PositionRange(range);
        int middle = (positionBefore.y + lastPosition.y) / 2;
        int deltaY = Math.abs(positionBefore.y - lastPosition.y);

        if (answer.equals(DetectorAnswer.SAME)) {
            range.min = middle;
            range.max = middle;
        } else {
            if (positionBefore.y < lastPosition.y) {
                if (deltaY > 2) {
                    if (answer.equals(DetectorAnswer.COLDER)) {
                        range.max = middle;
                    } else {
                        range.min = middle;
                    }
                } else if (deltaY == 2) {
                    if (answer.equals(DetectorAnswer.COLDER)) {
                        range.max = lastPosition.y;
                    } else {
                        range.min = lastPosition.y;
                    }
                } else {
                    if (answer.equals(DetectorAnswer.COLDER)) {
                        range.max = positionBefore.y;
                    } else {
                        range.min = lastPosition.y;
                    }
                }
            } else {
                if (deltaY > 2) {
                    if (answer.equals(DetectorAnswer.COLDER)) {
                        range.min = middle;
                    } else {
                        range.max = middle;
                    }
                } else if (deltaY == 2) {
                    if (answer.equals(DetectorAnswer.COLDER)) {
                        range.min = lastPosition.y;
                    } else {
                        range.max = lastPosition.y;
                    }
                } else {
                    if (answer.equals(DetectorAnswer.COLDER)) {
                        range.min = positionBefore.y;
                    } else {
                        range.max = lastPosition.y;
                    }
                }
            }
        }

        System.err.println("Points " + positionBefore + " and " + lastPosition + " with answer " + answer + " change range from " + rangeCopy + " to " + range);
    }

    @Override
    public Point computeNextPosition(Point lastPosition, boolean canRecurse) {
        int positionY;
        if (range.contains(lastPosition, lookForX())) {
            if (range.length() > 2) {
                positionY = range.middle();

                // if next position is too close accoording to the range size, then use the other algo
                if (range.length() >= 10 && Math.abs(lastPosition.x - positionY) < 0.8 * range.length() / 2) {
                    System.err.println("position too close --> use middle algo");
                    if (lastPosition.y > range.middle()) {
                        // |        y  | --> |    o     x  |
                        positionY = range.min + (lastPosition.y - range.min) / 2;
                    } else {
                        // |  y        | --> |  x    o     |
                        positionY = lastPosition.y - (lastPosition.y - range.max) / 2;
                    }
                }
            } else {
                // we expect the range length to be 2
                if (lastPosition.y == range.min) {
                    positionY = range.max;
                } else {
                    positionY = range.min;
                }
            }
        } else {
            // compute the middle of the range
            positionY = range.middle();
        }

        if (positionY == lastPosition.y && canRecurse) {
            // call it again to change the middle computation strategy
            return computeNextPosition(lastPosition, !canRecurse);
//            System.err.println("Force to max");
//            positionY = range.max;
        }

        return new Point(lastPosition.x, positionY);
    }

    @Override
    public boolean samePosition(Point p1, Point p2) {
        return p1.y == p2.y;
    }
}

class PositionFinderMultilateration {
    private final Building building;
    private List<StraightLine> allLines = new ArrayList<>();

    PositionFinderMultilateration(Building building) {
        this.building = building;
        this.allLines.addAll(building.getBoundingStraightLines());
    }

    Point computeNextPosition(Point point) throws Exception {
        if (allLines.size() == 4) {
            return building.computeSymetricalOf(point);
        }
        // compute intersection points of all lines
        List<Point> intersections = computeIntersections(allLines, building);

        // compute iso barycenter of these points
        return computeIsoBarycenter(intersections);
    }

    void notifyDetectionAnswer(Point positionBefore, Point lastPosition, DetectorAnswer answer) {
        StraightLine newLine = StraightLine.asBissectionOf(positionBefore, lastPosition, answer);
        allLines.add(newLine);
    }

    private List<Point> computeIntersections(List<StraightLine> allLines, Building building) {
        List<Point> intersections = new ArrayList<>();

        allLines.forEach(refLine -> allLines.stream().filter(line -> !line.equals(refLine)).forEach(line -> {
            Point intersection = line.intersect(refLine);
            if (intersection != null) {
                System.err.println("Intersect " + refLine + " and " + line + " --> " + intersection.toString());
                if (building.contains(intersection) && validFor(intersection, allLines)) {
                    System.err.println("  keep this point");
                    intersections.add(intersection);
                }
            }
        }));

        intersections.forEach(pt -> System.err.println("Intersection " + pt));
        return intersections;
    }

    private boolean validFor(Point intersection, List<StraightLine> allLines) {
        return allLines.stream().allMatch(line -> line.validFor(intersection));
    }

    private Point computeIsoBarycenter(List<Point> intersections) throws Exception {
        double x = intersections.stream().mapToDouble(pt -> pt.x).reduce((x1, x2) -> x1 + x2).orElseThrow(Exception::new);
        double y = intersections.stream().mapToDouble(pt -> pt.y).reduce((y1, y2) -> y1 + y2).orElseThrow(Exception::new);
        return new Point((int) (x / intersections.size()), (int) (y / intersections.size()));
    }
}

class StraightLine {


    public enum REGION {POSITIVE, SAME, NEGATIVE;}

    private static AtomicInteger commonUniqueId = new AtomicInteger();
    private int uniqueId = commonUniqueId.getAndIncrement();

    private double a, b, c;
    private REGION region;

    StraightLine() {
    }

    StraightLine(double a, double b, double c, REGION region) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.region = region;
    }

    static StraightLine asBissectionOf(Point p1, Point p2, DetectorAnswer answer) {
        StraightLine line = new StraightLine();

        // Compute coef as y = mx + p
        double m = (p1.x - p2.x) / (p2.y - p1.y);
        double p = (p1.y + p2.y) / 2 - (p1.x - p2.x) * (p1.x + p2.x) / (2 * (p2.y - p1.y));

        // final form : ax + by + c = 0 <-> mx -y + p = 0
        line.a = m;
        line.b = -1;
        line.c = p;

        // define the region
        if (answer.equals(DetectorAnswer.SAME)) {
            line.region = REGION.SAME;
        } else {
            if (answer.equals(DetectorAnswer.WARMER)) {
                // p2 is warmer - keep its region
                if (line.value(p2) > 0) {
                    line.region = REGION.POSITIVE;
                } else {
                    line.region = REGION.NEGATIVE;
                }
            } else {
                // p2 is colder - keep p1 region
                if (line.value(p1) > 0) {
                    line.region = REGION.POSITIVE;
                } else {
                    line.region = REGION.NEGATIVE;
                }
            }
        }

        return line;
    }

    boolean validFor(Point pt) {
        double value = a * pt.x + b * pt.y + c;
        return (region.equals(REGION.POSITIVE) && value >= 0) || (region.equals(REGION.NEGATIVE) && value <= 0) || (region.equals(REGION.SAME) && value == 0);

    }

    private double value(Point p) {
        return a * p.x + b * p.y + c;
    }

    Point intersect(StraightLine line) {
        // are the lines parallels ?
        if ((line.b == 0 && this.b == 0) || line.getDirectorCoef() == this.getDirectorCoef()) {
            return null;
        }

        // vertical & horizontal lines
        if (this.a == 0 && line.b == 0) {
            return new Point(line, this.b);
        }

        double y = (this.a * line.c - line.a * this.c) / (line.a * this.b - this.a * line.b);
        double x = (this.c - this.b * y) / this.a;
        return new Point((int) x, (int) y);
    }

    private double getDirectorCoef() {
        return -a / b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StraightLine line = (StraightLine) o;

        return uniqueId == line.uniqueId;
    }

    @Override
    public int hashCode() {
        return uniqueId;
    }

    @Override
    public String toString() {
        return a + ".x + " + b + ".y + " + c + " = 0";
    }
}