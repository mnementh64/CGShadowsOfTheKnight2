import org.junit.Assert;
import org.junit.Test;

public class PlayerTest {

    @Test
    public void test1() throws Exception {
        int maxTurns = 80;
        Point batmanInitialPosition = new Point(1, 5);
        Problem problem = new Problem(5, 15, maxTurns);
        Referee referee = new Referee(4, 10, batmanInitialPosition.x, batmanInitialPosition.y, problem);
        Batman batman = new Batman();
        // initial position
        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y, DetectorAnswer.UNKNOWN.toString());

        ProblemSolver solver = new ProblemSolver(problem, batman);
        executeTest(referee, batman, solver);
    }

    @Test
    public void test2() throws Exception {
        int maxTurns = 45;
        Point batmanInitialPosition = new Point(17, 31);
        Problem problem = new Problem(18, 32, maxTurns);
        Referee referee = new Referee(2, 1, batmanInitialPosition.x, batmanInitialPosition.y, problem);
        Batman batman = new Batman();
        // initial position
        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y, DetectorAnswer.UNKNOWN.toString());

        ProblemSolver solver = new ProblemSolver(problem, batman);
        executeTest(referee, batman, solver);
    }

    @Test
    public void test3() throws Exception {
        int maxTurns = 11;
        Point batmanInitialPosition = new Point(0, 98);
        Problem problem = new Problem(1, 99, maxTurns);
        Referee referee = new Referee(0, 97, batmanInitialPosition.x, batmanInitialPosition.y, problem);
        Batman batman = new Batman();
        // initial position
        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y, DetectorAnswer.UNKNOWN.toString());

        ProblemSolver solver = new ProblemSolver(problem, batman);
        executeTest(referee, batman, solver);
    }

    @Test
    public void test4() throws Exception {
        int maxTurns = 12;
        Point batmanInitialPosition = new Point(3, 6);
        Problem problem = new Problem(15, 15, maxTurns);
        Referee referee = new Referee(0, 1, batmanInitialPosition.x, batmanInitialPosition.y, problem);
        Batman batman = new Batman();
        // initial position
        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y, DetectorAnswer.UNKNOWN.toString());

        ProblemSolver solver = new ProblemSolver(problem, batman);
        executeTest(referee, batman, solver);
    }

    private void executeTest(Referee referee, Batman batman, ProblemSolver solver) throws Exception {
        System.out.println("Position " + batman.lastPosition().toString() + " get an answer : UNKNOWN");

        // throw an exception in case of turnsout
        while (!referee.hasWon()) {
            // ask the solver to compute the next position
            Point nextPosition = solver.computeNextPosition();

            // submit to the referee and get its answer
            DetectorAnswer answer = referee.submitPosition(nextPosition);
            System.out.println("Position " + nextPosition.toString() + " get an answer : " + answer);

            // register this test with the result
            batman.addCheck(nextPosition.x, nextPosition.y, answer.toString());
        }

        Assert.assertEquals(1, 1);
    }
}

class Referee {
    private final Problem problem;
    Point bombPosition;
    Point lastSubmittedPosition;
    int turn = 1;

    public Referee(int bombPositionX, int bombPositionY, int batmanInitialX, int batmanInitialY, Problem problem) {
        this.problem = problem;
        this.bombPosition = new Point(bombPositionX, bombPositionY);
        this.lastSubmittedPosition = new Point(batmanInitialX, batmanInitialY);
    }


    public boolean hasWon() {
        return lastSubmittedPosition != null && lastSubmittedPosition.equals(bombPosition) && turn <= problem.maxTurns;
    }

    public DetectorAnswer submitPosition(Point nextPosition) throws Exception {
        checkMaxTurns();

        checkPosition(nextPosition);

        // the distance between the last submitted position and the bomb
        int lastDistance = bombPosition.distance2(lastSubmittedPosition);

        // the distance between the new submitted position and the bomb
        int newDistance = bombPosition.distance2(nextPosition);
        System.out.println("           last distance : " + lastDistance + " / new distance : " + newDistance);

        // register the next position as previous for next turn
        lastSubmittedPosition = nextPosition;

        // the answer comparing the two
        return newDistance > lastDistance ? DetectorAnswer.COLDER : newDistance < lastDistance ? DetectorAnswer.WARMER : DetectorAnswer.SAME;
    }

    private void checkPosition(Point nextPosition) throws Exception {
        if (nextPosition.x < 0 || nextPosition.x >= problem.building.width || nextPosition.y < 0 || nextPosition.y >= problem.building.height) {
            throw new Exception("Invalid position !!");
        }
    }

    private void checkMaxTurns() throws Exception {
        turn++;
        if (turn > problem.maxTurns) {
            throw new Exception("Too many turns !!");
        }
    }
}