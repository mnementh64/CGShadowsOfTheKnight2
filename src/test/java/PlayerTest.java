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
//        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y);

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
        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y);

        ProblemSolver solver = new ProblemSolver(problem, batman);
        executeTest(referee, batman, solver);
    }

    @Test
    public void test3() throws Exception {
        int maxTurns = 11;
        Point batmanInitialPosition = new Point(0, 98);
        Problem problem = new Problem(1, 99, maxTurns);
//        Referee referee = new Referee(0, 97, batmanInitialPosition.x, batmanInitialPosition.y, problem);
//        Referee referee = new Referee(0, 55, batmanInitialPosition.x, batmanInitialPosition.y, problem);
//        Referee referee = new Referee(0, 47, batmanInitialPosition.x, batmanInitialPosition.y, problem);
        Referee referee = new Referee(0, 0, batmanInitialPosition.x, batmanInitialPosition.y, problem);
        Batman batman = new Batman();
        // initial position
        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y);

        ProblemSolver solver = new ProblemSolver(problem, batman);
        executeTest(referee, batman, solver);
    }

    @Test
    public void test3_2() throws Exception {
        int maxTurns = 11;
        Point batmanInitialPosition = new Point(0, 1);
        Problem problem = new Problem(1, 99, maxTurns);
        Referee referee = new Referee(0, 99, batmanInitialPosition.x, batmanInitialPosition.y, problem);
        Batman batman = new Batman();
        // initial position
        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y);

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
        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y);

        ProblemSolver solver = new ProblemSolver(problem, batman);
        executeTest(referee, batman, solver);
    }

    @Test
    public void test5() throws Exception {
        int maxTurns = 12;
        Point batmanInitialPosition = new Point(2, 6);
        Problem problem = new Problem(15, 15, maxTurns);
        Referee referee = new Referee(0, 0, batmanInitialPosition.x, batmanInitialPosition.y, problem);
        Batman batman = new Batman();
        // initial position
        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y);

        ProblemSolver solver = new ProblemSolver(problem, batman);
        executeTest(referee, batman, solver);
    }

    @Test
    public void test6() throws Exception {
        int maxTurns = 16;
        Point batmanInitialPosition = new Point(17, 29);
        Problem problem = new Problem(50, 50, maxTurns);
        Referee referee = new Referee(48, 1, batmanInitialPosition.x, batmanInitialPosition.y, problem);
        Batman batman = new Batman();
        // initial position
        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y);

        ProblemSolver solver = new ProblemSolver(problem, batman);
        executeTest(referee, batman, solver);
    }

    @Test
    public void test7() throws Exception {
        int maxTurns = 16;
        Point batmanInitialPosition = new Point(17, 29);
        Problem problem = new Problem(50, 50, maxTurns);
        Referee referee = new Referee(47, 2, batmanInitialPosition.x, batmanInitialPosition.y, problem);
        Batman batman = new Batman();
        // initial position
        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y);

        ProblemSolver solver = new ProblemSolver(problem, batman);
        executeTest(referee, batman, solver);
    }

    @Test
    public void test7_2() throws Exception {
        int maxTurns = 16;
        Point batmanInitialPosition = new Point(17, 29);
        Problem problem = new Problem(50, 50, maxTurns);
        Referee referee = new Referee(15, 49, batmanInitialPosition.x, batmanInitialPosition.y, problem);
        Batman batman = new Batman();
        // initial position
        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y);

        ProblemSolver solver = new ProblemSolver(problem, batman);
        executeTest(referee, batman, solver);
    }

    @Test
    public void test8() throws Exception {
        int maxTurns = 27;
        Point batmanInitialPosition = new Point(501, 501);
        Problem problem = new Problem(1000, 1000, maxTurns);
        Referee referee = new Referee(98, 98, batmanInitialPosition.x, batmanInitialPosition.y, problem);
        Batman batman = new Batman();
        // initial position
        batman.addCheck(batmanInitialPosition.x, batmanInitialPosition.y);

        ProblemSolver solver = new ProblemSolver(problem, batman);
        executeTest(referee, batman, solver);
    }

    private void executeTest(Referee referee, Batman batman, ProblemSolver solver) throws Exception {
        int x = referee.lastSubmittedPosition.x;
        int y = referee.lastSubmittedPosition.y;

        // game loop
        String bombDir = DetectorAnswer.UNKNOWN.toString();
        while (!referee.hasWon()) {
            // register this test with the result
            batman.addCheck(x, y);
            solver.notifyDetectionAnswer(DetectorAnswer.valueOf(bombDir)); // tell the solver what is the detection for the last submitted position

            // ask the solver to compute the next position
            Point nextPosition = solver.computeNextPosition();

            // output the position
//            System.out.println(nextPosition.toString());
            bombDir = referee.submitPosition(nextPosition).toString();
            x = nextPosition.x;
            y = nextPosition.y;
        }
    }
}

class Referee {
    private final Problem problem;
    Point bombPosition;
    Point lastSubmittedPosition;
    int turn = 0;

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
        System.err.println("turn " + turn);
        if (turn >= problem.maxTurns) {
            throw new Exception("Too many turns !!");
        }
    }
}