import java.util.ArrayList;

public class Cwacos {

    public static void main(String[] args) {
        //CwacosController.startUI(args);

        Stocks.init();
        ArrayList<Entry> test = Stocks.get("IBM", 1, 11);
        String[] testing = MaxProfit.getMaxProfit(test);

        for(int i = 0; i < 7; i++)
            System.out.println(testing[i]);
    }
}
