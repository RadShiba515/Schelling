public class Segregation {

    /**
     * Made by Cole McCorkendale!
     * This is my take on Schelling's segregation simulation in Java using the "stdlib" library from Princeton.
     */

    static boolean satisfied;

    public static void main(String[] args) {
        // t is the percentage of similar neighbors that satisfies an agent!
        // Default value = 0.3, 30% of neighbors must be similar to satisfy!
        double t = 0.7;

        // redBlue is the ratio of red agents to blue agents that will be created!
        // Default value = 0.5, half red and half blue!
        double redBlue = 0.5;

        // empty is the amount of vacant agents that will be created!
        // Default value = 0.1, 10% of the agents will be vacant!
        double empty = 0.1;

        // size is the length of the grid. Since it's a square, this will be the height as well!
        // Default value = 50, the grid will be 50x50 agents!
        int size = 256;
        // Initial setup! (I added the satisfied boolean so I can stop the main thread when everything is satisfied!)
        satisfied = false;
        int[][] agents = new int[size][size];
        int[][] empties = new int[(int)(size * size * empty)][2];
        initAgents(size, redBlue, empty, agents, empties);

        // Setting up StdDraw and drawing the initial grid!
        StdDraw.setCanvasSize(896, 896);
        StdDraw.enableDoubleBuffering();
        StdDraw.setScale(-0.5, size - 0.5);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.002);
        drawGrid(size, agents);

        // Main thread!
        while(true) {
            if(satisfied) {
                StdOut.println("All agents satisfied! Main thread closed!");
                break;
            }
            updateGrid(size, t, agents, empties);
        }
    }

    // updateGrid will check each agent and, if they are dissatisfied, will move them to a new spot.
    public static void updateGrid(int size, double t, int[][] agents, int[][] empties) {
        satisfied = true;
        for(int row = 0; row < size; row++) {
            for(int col = 0; col < size; col++) {
                // If a spot is not empty and it is dissatisfied, move it!
                if(agents[row][col] != 0 && percentSimilarNeighbors(row, col, agents) < t) {
                    // StdOut.println("Agent replaced! Percentage = " + percentSimilarNeighbors(row, col, agents) + "!");
                    replace(row, col, agents, empties);
                    satisfied = false;
                }
            }
        }
        // Update the grid visually after each replacement!
        drawGrid(size, agents);
    }

    // Replace will find a new empty location for any dissatisfied agent!
    public static void replace(int r, int c, int[][] agents, int[][] empties) {
        // We pick a random index from our list of empty spots, then we grab the row and column and save those as vars
        // for readability's sake!
        int emptyIndex = StdRandom.uniform(0, empties.length);
        int newRow = empties[emptyIndex][0];
        int newCol = empties[emptyIndex][1];

        // Move the dissatisfied agent to the empty agent selected at random, then set the old one to empty and save its
        // index in the empties list!
        agents[newRow][newCol] = agents[r][c];
        agents[r][c] = 0;
        empties[emptyIndex][0] = r;
        empties[emptyIndex][1] = c;
    }

    // percentSimilarNeighbors looks at all the neighboring squares of agents[r][c] and finds the percentage of filled
    // neighbor squares that are the same color.
    public static double percentSimilarNeighbors(int r, int c, int[][] agents) {
        // numRed and numBlue will be used to count the number of each color surrounding the subject agent!
        int numRed = 0;
        int numBlue = 0;
        double percent;

        // Starting values for the loop!
        int startRow = r - 1;
        int startCol = c - 1;
        int endRow = r + 2;
        int endCol = c + 2;

        // If the subject is at 0 on either axis, set the start values to 0 to avoid an index out of bounds exception!
        if (r == 0) startRow = 0;
        if (c == 0) startCol = 0;
        if (r == agents.length - 1) endRow = agents.length;
        if (c == agents[0].length - 1) endCol = agents[0].length;

        // Looping through both axes of the array, starting on the starting values we've established.
        for (int loopRow = startRow; loopRow < endRow; loopRow++) {
            for (int loopCol = startCol; loopCol < endCol; loopCol++) {
                // StdOut.println("Testing agents[" + loopRow + "][" + loopCol + "]!");
                if (agents[loopRow][loopCol] == 1) {
                    numRed++;
                    // StdOut.println("agents[" + loopRow + "][" + loopCol + "] == 1, numRed now equals " + numRed + "!");
                }
                if (agents[loopRow][loopCol] == 2) {
                    numBlue++;
                    // StdOut.println("agents[" + loopRow + "][" + loopCol + "] == 2, numBlue now equals " + numBlue + "!");
                }
            }
        }

        // Calculating the final percent and returning the result! Remove 1 from the values based on what color the
        // subject agent is so we don't count it!
        if(agents[r][c] == 1) {
            numRed--;
            percent = (double)numRed / (numRed + numBlue);
        }
        else {
            numBlue--;
            percent = (double)numBlue / (numRed + numBlue);
        }

        // This deals with divide-by-zero issues. If it's dividing by zero, it's satisfied so we just set the value to
        // 1.0! If it's 0/0, it's also satisfied since it has no neighbors!
        if(percent == Double.POSITIVE_INFINITY) percent = 1.0;
        if(numRed == 0 && numBlue == 0) percent = 1.0;
        return percent;
    }

    // initAgents will initialize the agents array, placing them randomly.
    public static void initAgents(int size, double redBlue, double empty, int[][] agents, int[][] empties) {
        // Establishing the actual integer values of how many of each type of agent we will need!
        int numEmpty = (int) ((size * size) * empty);
        int numRed = (int) (((size * size) - numEmpty) * redBlue);
        int numBlue = (((size * size) - numEmpty) - numRed);

        // For reference:
        // 0 represents empty agents
        // 1 represents red agents
        // 2 represents blue agents

        // Iterating through the array at random spots. If they're open, fill them with reds!
        while(numRed > 0) {
            int row = StdRandom.uniform(0, size);
            int col = StdRandom.uniform(0, size);

            if(agents[row][col] == 0) {
                agents[row][col] = 1;
                numRed--;
            }
        }

        // Doing the same thing for blues!
        while(numBlue > 0) {
            int row = StdRandom.uniform(0, size);
            int col = StdRandom.uniform(0, size);

            if(agents[row][col] == 0) {
                agents[row][col] = 2;
                numBlue--;
            }
        }

        // Take note of every empty space's coordinates! The "empties" array will store ordered pairs so we can more
        // efficiently replace dissatisfied agents without guesswork! This will improve the program's efficiency
        // by a large factor, or at least I predict so.
        int emptyIndex = 0;
        for(int r = 0; r < agents.length; r++) {
            for(int c = 0; c < agents.length; c++) {
                if(agents[r][c] == 0) {
                    empties[emptyIndex][0] = r;
                    empties[emptyIndex][1] = c;
                    emptyIndex++;
                }
            }
        }
    }

    // drawGrid will draw the grid given the current values of agents. Aside from initial setup, this will also update
    // the grid whenever it changes.
    public static void drawGrid(int size, int[][] agents) {
        // Iterating through the array and drawing squares depending on their assigned color (or lack thereof!)
        for(int row = 0; row < size; row++) {
            for(int col = 0; col < size; col++) {
                // I said it elsewhere, but as a reminder, 0 is empty, 1 is red, and 2 is blue!
                // If you comment out the last setPenColor and square methods on these, you get a cool look where the
                // squares have no outlines. Solely aesthetic, but still cool!
                if(agents[row][col] == 0) StdDraw.setPenColor(StdDraw.WHITE);
                if(agents[row][col] == 1) StdDraw.setPenColor(StdDraw.RED);
                if(agents[row][col] == 2) StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.filledSquare(row, col, 0.5);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.square(row, col, 0.5);
            }
        }
        StdDraw.show();
    }
}