import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CwacosTester {

    private static String symbol = "";
    private static String file_url = "";

    public static void begin(){
        boolean should_quit = false;

        while(!should_quit){
            Scanner kbinput = new Scanner(System.in);

            System.out.print("CwacosTester~ ");
            String command = kbinput.next();

            switch(command){
                case "symbol":
                    System.out.print("CwacosTester~ new symbol: ");
                    symbol = kbinput.next();
                    break;
                case "fileurl":
                    System.out.print("CwacosTester~ new file url: ");
                    file_url = kbinput.next();
                    break;
                case "addfavorite":
                    addFavoriteFunctionTest();
                    break;
                case "removefavorite":
                    removeFavoriteFunctionTest();
                    break;
                case "save":
                    saveFunctionTest();
                    break;
                case "load":
                    loadFunctionTest();
                    break;
                case "update":
                    updateFunctionTest();
                    break;
                case "updateall":
                    updateAllFunctionTest();
                    break;
                case "qfact":
                    quakkasFactsTest();
                    break;
                case "quit":
                    should_quit = true;
                    break;
                default:
                    break;
            }
        }
    }

    public static void addFavoriteFunctionTest(){
        //=============== ADD FAVORITE FUNCTION TEST ================
        // add a new favorite with symbol "IBM" and of datatype "stock"
        String add_favorite_error = CwacosData.AddFavorite(symbol, 0);

        if(add_favorite_error != null){
            System.out.println(add_favorite_error);
        }
    }

    public static void saveFunctionTest() {
        //=============== SAVE FUNCTION TEST ================

        // save data related to given symbol to file at given url
        String save_error = CwacosData.save(symbol);

        if (save_error != null) {
            System.out.println(save_error);
        }
    }

    public static void loadFunctionTest() {
        //=============== LOAD FUNCTION TEST ================
        // set the parameters for loading data
        ArrayList<String> load_parameters = new ArrayList<String>(
                Arrays.asList(
                        file_url
                )
        );

        ArrayList<Entry> loaded_data_for_table = CwacosData.load(symbol, load_parameters);

        if(loaded_data_for_table != null){
            StringBuilder sb = new StringBuilder();

            for(Entry each : loaded_data_for_table){
                sb.append(each.out() + "\n");
            }

            String load_test = sb.toString();
            System.out.println("Loaded data: " + load_test);
        } else {
            System.out.println("Can not load file data, symbol does not exist in favorites");
        }
    }

    public static void updateFunctionTest(){
        //=============== UPDATE FUNCTION TEST ================
        int call_type = 2;
        int call_interval = 10;

        // make api call and print returned data
        ArrayList<Entry> updated_data_for_table = CwacosData.update(symbol, call_type, call_interval);

        if(updated_data_for_table != null){

            StringBuilder builder = new StringBuilder();

            for(Entry each : updated_data_for_table){
                builder.append(each.out());
            }


            String update_test = builder.toString();
            System.out.println("Intraday Data: " + update_test);
        } else {
            System.out.println("Can not update data, symbol does not exist in favorites");
        }
    }

    public static void updateAllFunctionTest(){
        //=============== UPDATE ALL FUNCTION TEST ================
        CwacosData.updateAll();
    }

    /**
     * Use the call below to print a random fact about Quokkas.
     * CAREFUL! There only 5 calls/day on this API and I (Michael Leonard) get charged $.03 for each additional call.
    */
    public static void quakkasFactsTest(){
        System.out.println(RandomFactsAPITranslator.getQuokkasFact());
    }

    public static void removeFavoriteFunctionTest(){
        CwacosData.RemoveFavorite(symbol);
    }
}
