/**
 * This is a basic class used to represent starting/ending points within our intraday stock information.
 * The class will also store the maximum profit within the given starting/ending points.
 */
public class Jump{
    int beginJumpPosition;
    int endJumpPosition;
    double profit;

    public Jump(){
    }

    public Jump(int beginJumpPosition, int endJumpPosition, double profit){
        this.beginJumpPosition = beginJumpPosition;
        this.endJumpPosition = endJumpPosition;
        this.profit = profit;
    }

}