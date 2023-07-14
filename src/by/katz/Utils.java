package by.katz;

public class Utils {
    public static final int MOVE_KOEFF = 1600;

    public static void log(String logString) {System.out.println(logString);}

    public static int getMovePoints(int value) {return value / MOVE_KOEFF;}
}
