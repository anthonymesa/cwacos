/*
Last updated:
Purpose of this class:
Contributing Authors:
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CwacosTester {

    private static String symbol = "";
    private static String file_url = "";
    private static int data_type = 0;
    private static int call_type = 1;
    private static int call_interval = 12;

    public static void begin() {
        boolean should_quit = false;

        CwacosData.loadState();
        System.out.println(CwacosData.getQuakkaFact());

        while (!should_quit) {
            Scanner kbinput = new Scanner(System.in);

            System.out.print("CwacosTester~ ");
            String command = kbinput.nextLine();

            String[] command_array = command.split(" ", 0);
            try {
                switch (command_array[0]) {
                    case "addfavorite":
                        symbol = command_array[1];
                        if ((Integer.parseInt(command_array[2])) < 3) {
                            data_type = Integer.parseInt(command_array[2]);
                            addFavoriteFunctionTest(symbol, data_type);
                        }
                        break;
                    case "removefavorite":
                        symbol = command_array[1];
                        removeFavoriteFunctionTest(symbol);
                        break;
                    case "save":
                        symbol = command_array[1];
                        saveFunctionTest(symbol);
                        break;
                    case "load":
                        symbol = command_array[1];
                        file_url = command_array[2];
                        loadFunctionTest(symbol, file_url);
                        break;
                    case "update":
                        symbol = command_array[1];
                        call_type = Integer.parseInt(command_array[2]);
                        call_interval = Integer.parseInt(command_array[3]);
                        updateFunctionTest(symbol, call_type, call_interval);
                        break;
                    case "updateall":
                        updateAllFunctionTest();
                        break;
                    case "quit":
                        should_quit = true;
                        break;
                    default:
                        System.out.println("command not recognised");
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        CwacosData.saveState();
    }

    public static void addFavoriteFunctionTest(String _symbol, int _data_type) {
        //=============== ADD FAVORITE FUNCTION TEST ================
        // add a new favorite with symbol "IBM" and of datatype "stock"
        String add_favorite_error = CwacosData.addFavorite(_symbol, _data_type);

        if (add_favorite_error != null) {
            System.out.println(add_favorite_error);
        }
    }

    public static void saveFunctionTest(String _symbol) {
        //=============== SAVE FUNCTION TEST ================

        // save data related to given symbol to file at given url
        String save_error = CwacosData.saveData(_symbol);

        if (save_error != null) {
            System.out.println(save_error);
        }
    }

    public static void loadFunctionTest(String _symbol, String _file_url) {
        //=============== LOAD FUNCTION TEST ================
        // set the parameters for loading data
        ArrayList<String> load_parameters = new ArrayList<String>(
                Arrays.asList(
                        _file_url
                )
        );

        ArrayList<Entry> loaded_data_for_table = CwacosData.loadData(_symbol, load_parameters);

        if (loaded_data_for_table != null) {
            StringBuilder sb = new StringBuilder();

            for (Entry each : loaded_data_for_table) {
                sb.append(each.toString() + "\n");
            }

            String load_test = sb.toString();
            System.out.println("Loaded data: " + load_test);
        } else {
            System.out.println("Can not load file data, symbol does not exist in favorites");
        }
    }

    public static void updateFunctionTest(String _symbol, int _call_type, int _call_interval) {
        //=============== UPDATE FUNCTION TEST ================

        // make api call and print returned data
        ArrayList<Entry> updated_data_for_table = CwacosData.update(_symbol, _call_type, _call_interval);

        if (updated_data_for_table != null) {

            // for each Entry

            StringBuilder builder = new StringBuilder();

            for (Entry each : updated_data_for_table) {
                builder.append(each.toString());
            }

            String update_test = builder.toString();
            System.out.println("Call Data: " + update_test);
        } else {
            System.out.println("Can not update data, symbol does not exist in favorites");
        }
    }

    public static void updateAllFunctionTest() {
        //=============== UPDATE ALL FUNCTION TEST ================
        CwacosData.updateAll();
    }

    public static void removeFavoriteFunctionTest(String _symbol) {
        CwacosData.removeFavorites(_symbol);
    }
}
