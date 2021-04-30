package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * 
 * Purpose: Jump is a basic class used to represent starting/ending points within our intraday stock information.
 *      The class will also store the maximum profit within the given starting/ending points.
 * 
 * Contributing Authors:
 *      Michael Leonard
 */

public class Jump{
    private int beginJumpPosition;
    private int endJumpPosition;
    private double profit;

    public Jump(){
        this( 0, 0, 0.0);
    }

    public Jump(int beginJumpPosition, int endJumpPosition, double profit){
        this.beginJumpPosition = beginJumpPosition;
        this.endJumpPosition = endJumpPosition;
        this.profit = profit;
    }

    //================= GETTERS ===============

    public int getBeginJumpPosition() {
        return this.beginJumpPosition;
    }

    public int getEndJumpPosition() {
        return this.endJumpPosition;
    }

    public double getProfit() {
        return this.profit;
    }

    //================= SETTERS ===============

    public void setBeginJumpPosition(int _begin) {
        this.beginJumpPosition = _begin;
    }

    public void setEndJumpPosition(int _end) {
        this.endJumpPosition = _end;
    }

    public void setProfit(double _profit) {
        this.profit = _profit;
    }
}