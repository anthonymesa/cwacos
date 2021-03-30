import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author Alan
 */
public class IO {

    /**
     * Basic method tat stores API output into a .txt file.
     * Note: This does not store data in a .txt file in an organized manner. It is still in development.
     *
     * @param _info ArrayList of Entry containing the output of the API call.
     */
    public static void storeData(ArrayList<Entry> _info) {
        String fileName = "res/data.txt";
        Path filePath = Paths.get(fileName);
        try {
            // Append to existing data file if the data file exists
            if (Files.exists(filePath)) {
                FileWriter fw = new FileWriter(fileName, true);
                // Using PrintWriter for the ease of use compared to FileWriter
                PrintWriter pw = new PrintWriter(fw);
                pw.println(_info);
                pw.flush();
                pw.close();
            } else {
                // Create a new data file if there is no data file
                File file = new File("res/data.txt");
                PrintWriter pw = new PrintWriter(file);
                pw.println(_info);
                pw.flush();
                pw.close();
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }

    }
}
