package com.cwacos;

/**
 * Last updated: 29-APR-2021
 *
 * Purpose: This is a testing class used to test the correctness of Stocks.get() and Crypto.get() by checking their
 *          return values against expected results.
 *
 * Contributing Authors:
 *      Michael Leonard
 */

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class BlackBoxTest {

    public static void main(String[] args){
        //Run Tests, This will take 3 full minutes to run because the API timeout must be waited on
        testGetStocks();
        testGetCrypto();

    }

    /**
     * This method will test a selection of Stocks.get() calls and assess their correctness.
     */
    public static void testGetStocks() {
        Stocks.init();

        try {
            //Test Symbol cases
            System.out.println("\n===================== Stocks getStocksData Test ======================");
            System.out.println("Testing Cases for Symbols: \n");

            System.out.print("Symbol ibm: Expected=Passed : Actual=");
            test(Stocks.get("ibm", 1, 0));
            System.out.print("Symbol IBM: Expected=Passed : Actual=");
            test(Stocks.get("IBM", 1, 0));
            System.out.print("Symbol I like dogs: Expected=Failed : Actual=");
            test(Stocks.get("I like dogs", 1, 0));
            System.out.print("Symbol 1234: Expected=Failed : Actual=");
            test(Stocks.get("1234", 1, 0));
            System.out.print("Symbol null: Expected=Failed : Actual=");
            test(Stocks.get(null, 1, 0));

            //Wait for 1 minute to let API timeout finish
            System.out.println("\nNow waiting one minute for the API timeout to reset...");
            TimeUnit.MINUTES.sleep(1);

            //Test Call Type and Call Interval
            System.out.println("\nTesting Cases for Call Type and Call Interval: \n");

            System.out.print("Intraday Call with no Interval: Expected=Failed : Actual=");
            test(Stocks.get("IBM", 0, 0));
            System.out.print("Intraday Call with 1 Hour Interval: Expected=Passed : Actual=");
            test(Stocks.get("IBM", 0, 5));
            System.out.print("Daily Call with no Interval: Expected=Passed : Actual=");
            test(Stocks.get("IBM", 1, 0));
            System.out.print("Daily Call with 1 Minute Interval: Expected=Failed : Actual=");
            test(Stocks.get("IBM", 1, 1));
            System.out.print("Monthly Call with no Interval: Expected=Passed : Actual=");
            test(Stocks.get("IBM", 3, 0));
        }
        catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    /**
     * This method will test a selection of Cryptos.get() calls and assess their correctness.
     */
    public static void testGetCrypto() {
        Cryptos.init();

        try{
            //Test Symbol cases
            System.out.println("\n===================== Crypto getCryptosData Test ======================");
            System.out.println("Now waiting one minute for the API timeout to reset...");
            TimeUnit.MINUTES.sleep(1);
            System.out.println("Testing Cases for Symbols: \n");

            System.out.print("Symbol btc: Expected=Passed : Actual=");
            test(Cryptos.get("btc", "USD", 1));
            System.out.print("Symbol BTC: Expected=Passed : Actual=");
            test(Cryptos.get("BTC", "USD", 1));
            System.out.print("Symbol I like dogs: Expected=Failed : Actual=");
            test(Cryptos.get("I like dogs", "USD", 1));
            System.out.print("Symbol 1234: Expected=Failed : Actual=");
            test(Cryptos.get("1234", "USD", 1));
            System.out.print("Symbol null: Expected=Failed : Actual=");
            test(Cryptos.get(null, "USD", 1));

            //Wait for 1 minute to let API timeout finish
            System.out.println("\nNow waiting one minute for the API timeout to reset...");
            TimeUnit.MINUTES.sleep(1);

            //Test Call Type and Call Interval
            System.out.println("\nTesting Cases for Call Type and Market: \n");

            System.out.print("Intraday Call with USD Market: Expected=Failed : Actual=");
            test(Cryptos.get("BTC", "USD", 0));
            System.out.print("Daily Call with USD Market: Expected=Passed : Actual=");
            test(Cryptos.get("BTC", "USD", 1));
            System.out.print("Weekly Call with EUR: Expected=Passed : Actual=");
            test(Cryptos.get("BTC", "EUR", 2));
            System.out.print("Monthly Call with JPY: Expected=Passed : Actual=");
            test(Cryptos.get("BTC", "JPY", 3));
            System.out.print("Daily Call with I like dogs Market: Expected=Failed : Actual=");
            test(Cryptos.get("BTC", "I like dogs", 1));
        }
        catch (Exception e) {
            System.out.println("Exception: " + e);
        }

    }

    /**
     * This method will test whether the result it is sent is NULL or not and print out Passed/Failed accordingly.
     * @param _result The ArrayList returned by a Stocks.get() or Cryptos.get() call
     */
    private static void test(ArrayList<Entry> _result){
            if(_result != null)
                System.out.println("Passed");
            else
                System.out.println("Failed");
    }

}