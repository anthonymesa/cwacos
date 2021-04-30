package com.cwacos;

/**
 * Last updated: 26-APR-2021
 * 
 * Purpose: MaxProfit
 * 
 * Contributing Authors:
 *      Michael Leonard
 */


import java.util.ArrayList;

public class MaxProfit {

    /**
     * This method is used to calculate the Max Profit as well as the: Overall high, Overall low, buying price and selling price
     * @param _stocks An ArrayList of Entry objects received from an API call
     * @return An array of strings containing the data described above.
     */
    public static String[] getMaxProfit(ArrayList<Entry> _stocks){
        double[] stocksArray = new double[_stocks.size()*2];
        double overallLow = Integer.MAX_VALUE, overallHigh = Integer.MIN_VALUE;

        //Store the Entry values into an array and store the overall low and high for the given data
        for(int i = 0, j = 0; i < stocksArray.length; i+=2, j++){
            stocksArray[i] = _stocks.get(j).getLow();
            stocksArray[i+1] = _stocks.get(j).getHigh();

            if(_stocks.get(j).getLow() < overallLow)
                overallLow = _stocks.get(j).getLow();

            if(_stocks.get(j).getHigh() > overallHigh)
                overallHigh = _stocks.get(j).getHigh();
        }


        //Create Array of the changes in price from each Entry to the next
        double[] priceChangeArray = createPriceChangeArray(stocksArray);

        //Calculate the profit and buy/sell points and save them in a jump object
        Jump maxProfit = findMaxProfit(priceChangeArray, 0, priceChangeArray.length-1);

        //Create String array to return from this method
        String[] results = new String[7];

        //Overall Low price of entry data
        results[0] = String.valueOf(overallLow);
        //Price at which you buy
        results[1] = String.valueOf(_stocks.get(maxProfit.getBeginJumpPosition()/2).getLow());
        //Date of buy
        results[2] = _stocks.get(maxProfit.getBeginJumpPosition()/2).getDateTimeString();
        //Overall High price of entry data
        results[3] = String.valueOf(overallHigh);
        //Price at which you sell
        results[4] = String.valueOf(_stocks.get(maxProfit.getEndJumpPosition()/2).getHigh());
        //Date of sell
        results[5] = _stocks.get(maxProfit.getEndJumpPosition()/2).getDateTimeString();
        //Value of profit
        results[6] = String.format("%.2f", maxProfit.getProfit());

        return results;
    }

    /**
     * This method will create an array holding the changes in price of the stock between each access to it.
     * @param _stockPrices Array containing a stock's prices over a period of time.
     * @return Array of stock price differences.
     */
    private static double[] createPriceChangeArray(double[] _stockPrices){
        double[] priceChangeArray = new double[_stockPrices.length-1];
        for(int i = 1; i < _stockPrices.length; i++){
            priceChangeArray[i-1] = _stockPrices[i] - _stockPrices[i-1];
        }
        return priceChangeArray;
    }


    /**
     * This method will recursively find the maximum profit achieved by buying and selling stock within the timeframe of the given data.
     * @param _priceChangeArray Array of stock price differences.
     * @param _firstPosition First position of the array or subarray being solved.
     * @param _lastPosition Last position of the array or subarray being solved.
     * @return A Jump object containing where the jump began, ended, and the maximum profit between those indices.
     */
    private static Jump findMaxProfit(double[] _priceChangeArray, int _firstPosition, int _lastPosition){
        //initial and floor mid variable.
        int middlePosition = (_firstPosition + _lastPosition) / 2;

        //Base Case
        if(_firstPosition == _lastPosition)
            return(new Jump(_firstPosition, _lastPosition, _priceChangeArray[_firstPosition]));
        else{
            //Recursively solve left side of the array
            Jump leftJump = findMaxProfit(_priceChangeArray, _firstPosition, middlePosition);
            //Recursively solve right side of the array
            Jump rightJump = findMaxProfit(_priceChangeArray, middlePosition + 1, _lastPosition);
            //Solve for jumps that cross the midpoint of the array
            Jump crossJump = findMaxCrossingProfit(_priceChangeArray, _firstPosition, middlePosition, _lastPosition);

            //Determine the maximum profit and return it.
            if(leftJump.getProfit() >= rightJump.getProfit() && leftJump.getProfit() >= crossJump.getProfit())
                return leftJump;
            else if(rightJump.getProfit() >= leftJump.getProfit() && rightJump.getProfit() >= crossJump.getProfit())
                return rightJump;
            else
                return crossJump;
        }



    }

    /**
     * This method will test for cases were the max profit could lie across the mid point of the array.
     * @param _priceChangeArray  Array of price differences.
     * @param _firstPosition First position of array or subarray being solved.
     * @param _middlePosition Midpoint between first and last positions (rounded down if not whole integer).
     * @param _lastPosition Last position of array or subarray being solved.
     * @return Jump object containing max profit across the midpoint.
     */
    private static Jump findMaxCrossingProfit(double[] _priceChangeArray, int _firstPosition, int _middlePosition, int _lastPosition){
        double leftProfit = Integer.MIN_VALUE;
        double rightProfit = Integer.MIN_VALUE;
        double totalProfit = 0;
        int startingPosition = 0;
        int endingPosition = 0;

        //Find max profit withing left subarray
        for(int i = _middlePosition; i >= _firstPosition; i--){
            totalProfit += _priceChangeArray[i];
            if(totalProfit > leftProfit){
                leftProfit = totalProfit;
                startingPosition = i;
            }
        }

        totalProfit = 0;

        //Find max profit within right subarray
        for(int i = _middlePosition + 1; i <= _lastPosition; i++){
            totalProfit += _priceChangeArray[i];
            if(totalProfit > rightProfit){
                rightProfit = totalProfit;
                endingPosition = i;
            }
        }
        //return max Profit across entire array.
        return(new Jump(startingPosition, endingPosition, leftProfit + rightProfit));
    }

}
