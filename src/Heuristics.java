import java.awt.geom.Point2D;
import java.util.List;

public class Heuristics {

    //Static weights heuristics function to determine stability
    private final static int[][] swhf = {
            {4, -3, 2, 2, 2, 2, -3, 4},
            {-3, -4, -1, -1, -1, -1, -4, -3},
            {2, -1, 1, 0, 0, 1, -1, 2},
            {2, -1, 0, 1, 1, 0, -1, 2},
            {2, -1, 0, 1, 1, 0, -1, 2},
            {2, -1, 1, 0, 0, 1, -1, 2},
            {-3, -4, -1, -1, -1, -1, -4, -3},
            {4, -3, 2, 2, 2, 2, -3, 4},
    };

    //Front counters function
    private final static int[] fcX = {-1, -1, 0, 1, 1, 1, 0, -1};
    private final static int[] fcY = {0, 1, 1, 1, 0, -1, -1, -1};

    //Pre-calculated weights
    private final static double S_WEIGHT = 10;
    private final static double P_WEIGHT = 10;
    private final static double F_WEIGHT = 74.396;
    private final static double C_WEIGHT = 801.724;
    private final static double L_WEIGHT = 382.026;
    private final static double M_WEIGHT = 78.922;

    //Initial eval values
    public static double MAX_EVAL = 999999.00;
    public static double MIN_EVAL = -999999.00;

    /*
     * The Dynamic Heuristic Evaluation Function
     * Returns the score of the current game state for a specific colour
     */
    public static double evaluate(Board board, String counterColour) {

        double s = 0; //(s)tability score
        double p;     //Counter (p)arity score
        double f;     //(f)ront counter score
        double c;     //(c)orner occupancy score
        double l;     //Corner c(l)oseness score
        double m;     //(m)obility score

        //Calculating number of counters
        int counters = board.getCount(counterColour);
        int oppCounters = board.getCount(board.altCounterColour(counterColour));

        int frontCounters = 0;
        int oppFrontCounters = 0;

        List<Point2D> coordinateList = board.getCoordinateList();

        for (Point2D point : coordinateList) {
            int coordX = (int) point.getX();
            int coordY = (int) point.getY();

            //Calculating stability of counters
            if (board.getCounterColour(coordX, coordY).equals(counterColour)) {
                s += swhf[coordX][coordY]; //Max
            } else {
                s -= swhf[coordX][coordY]; //Min
            }

            //Determining front counters
            for (int i = 0; i < 8; i++) {
                int x = coordX + fcX[i];
                int y = coordY + fcY[i];
                if (x >= 0 && x < 8 && y >= 0 && y < 8 && !board.hasCounter(x, y)) {
                    if (board.getCounterColour(coordX, coordY).equals(counterColour)) {
                        frontCounters++;
                    } else {
                        oppFrontCounters++;
                    }
                    break;
                }
            }
        }

        //Calculating counter parity
        if (counters > oppCounters) {
            p = (100 * counters) / (coordinateList.size());
        } else if (oppCounters > counters) {
            p = -(100 * oppCounters) / (coordinateList.size());
        } else {
            p = 0;
        }

        //Calculating front counters
        if (frontCounters > oppFrontCounters) {
            f = -(100 * frontCounters) / (frontCounters + oppFrontCounters);
        } else if (oppFrontCounters > frontCounters) {
            f = (100 * oppFrontCounters) / (frontCounters + oppFrontCounters);
        } else {
            f = 0;
        }

        //Calculating corner occupancy
        int[] cornerCoords = {0, 7};
        int cornerCounters = 0;
        int oppCornerCounters = 0;
        for (int i = 0; i < cornerCoords.length; i++) {
            for (int j = 0; j < cornerCoords.length; j++) {
                if (board.hasCounter(cornerCoords[i], cornerCoords[j])) {
                    if (board.getCounterColour(cornerCoords[i], cornerCoords[j]).equals(counterColour)) {
                        cornerCounters++;
                    } else {
                        oppCornerCounters++;
                    }
                }
            }
        }
        c = 25 * (cornerCounters - oppCornerCounters);

        //Calculating corner closeness
        int closeCounters = 0;
        int oppCloseCounters = 0;
        //Checking top left
        if (!board.hasCounter(0, 0)) {
            if (board.hasCounter(counterColour, 0, 1)) {
                closeCounters++;
            } else if (board.hasCounter(board.altCounterColour(counterColour), 0, 1)) {
                oppCloseCounters++;
            }
            if (board.hasCounter(counterColour, 1, 1)) {
                closeCounters++;
            } else if (board.hasCounter(board.altCounterColour(counterColour), 1, 1)) {
                oppCloseCounters++;
            }
            if (board.hasCounter(counterColour, 1, 0)) {
                closeCounters++;
            } else if (board.hasCounter(board.altCounterColour(counterColour), 1, 0)) {
                oppCloseCounters++;
            }
        }
        //Checking top right
        if (!board.hasCounter(0, 7)) {
            if (board.hasCounter(counterColour, 0, 6)) {
                closeCounters++;
            } else if (board.hasCounter(board.altCounterColour(counterColour), 0, 6)) {
                oppCloseCounters++;
            }
            if (board.hasCounter(counterColour, 1, 6)) {
                closeCounters++;
            } else if (board.hasCounter(board.altCounterColour(counterColour), 1, 6)) {
                oppCloseCounters++;
            }
            if (board.hasCounter(counterColour, 1, 7)) {
                closeCounters++;
            } else if (board.hasCounter(board.altCounterColour(counterColour), 1, 7)) {
                oppCloseCounters++;
            }
        }

        //Checking bottom left
        if (!board.hasCounter(7, 0)) {
            if (board.hasCounter(counterColour, 7, 1)) {
                closeCounters++;
            } else if (board.hasCounter(board.altCounterColour(counterColour), 7, 1)) {
                oppCloseCounters++;
            }
            if (board.hasCounter(counterColour, 6, 1)) {
                closeCounters++;
            } else if (board.hasCounter(board.altCounterColour(counterColour), 6, 1)) {
                oppCloseCounters++;
            }
            if (board.hasCounter(counterColour, 6, 0)) {
                closeCounters++;
            } else if (board.hasCounter(board.altCounterColour(counterColour), 6, 0)) {
                oppCloseCounters++;
            }
        }

        //Checking bottom right
        if (!board.hasCounter(7, 7)) {
            if (board.hasCounter(counterColour, 6, 7)) {
                closeCounters++;
            } else if (board.hasCounter(board.altCounterColour(counterColour), 6, 7)) {
                oppCloseCounters++;
            }
            if (board.hasCounter(counterColour, 6, 6)) {
                closeCounters++;
            } else if (board.hasCounter(board.altCounterColour(counterColour), 6, 6)) {
                oppCloseCounters++;
            }
            if (board.hasCounter(counterColour, 7, 6)) {
                closeCounters++;
            } else if (board.hasCounter(board.altCounterColour(counterColour), 7, 6)) {
                oppCloseCounters++;
            }
        }
        l = -12.5 * (closeCounters - oppCloseCounters);

        //Calculating mobility
        int legalMovesCounter = board.legalMovesPossible();
        board.initLegalMoves(board.altCounterColour(counterColour));
        int oppLegalMovesCounter = board.legalMovesPossible();
        if (legalMovesCounter > oppLegalMovesCounter) {
            m = (100 * legalMovesCounter) / (legalMovesCounter + oppLegalMovesCounter);
        } else if (oppLegalMovesCounter > legalMovesCounter) {
            m = -(100 * oppLegalMovesCounter) / (legalMovesCounter + oppLegalMovesCounter);
        } else {
            m = 0;
        }

        //Final weighted score
        return (s * S_WEIGHT) + (p * P_WEIGHT) + (f * F_WEIGHT) + (c * C_WEIGHT) + (l * L_WEIGHT) + (m * M_WEIGHT);
    }   
}
