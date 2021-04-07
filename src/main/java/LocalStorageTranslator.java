import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


class LocalStorageTranslator implements StorageAdapter {

    public LocalStorageTranslator() {

    }

    public Map<String, String> loadSettings(ArrayList<String> _params){
        String fileName = _params.get(0);
        Path filePath = Paths.get(fileName);

        Map<String, String> settings = new HashMap<String, String>();

        if (Files.exists(filePath)) {

            File inputFile = new File(fileName);
            Scanner read = null;

            try {
                read = new Scanner(inputFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            while (read.hasNextLine()) {
                String[] line = read.nextLine().split("=", 0);
                if(line.length == 2){
                    settings.put(line[0], line[1]);
                }
            }
        } else {
            File inputFile = new File(fileName);
            System.out.println("new settings file created");
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(inputFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            pw.close();
        }

        return settings;
    }

    public void saveSettings(Map<String, String> _settings, ArrayList<String> _params){
        String fileName = _params.get(0);
        File inputFile = new File(fileName);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, String> entry : _settings.entrySet()) {
            entry.getKey();

            pw.println(entry.getKey() + "=" + entry.getValue());
        }

        pw.flush();
        pw.close();
    }
    /*
     * It is expected that the params for creating and saving a file should
     * simpley be the url of the file. (so only one element in the
     * params array list).
     *
     * If a file exists already, it will be overwritten without alerting the
     * user (this should be let known to the user when they go to save.)
     * Maybe there is some way with exceptions we could alert them if they
     * try to save over an old file?
     *
     * This function should take the parameters and parse
     * them accordingly for setting up file saving. then the
     * input arraylist should be parsed and saved to the file
     */

    /**
     * @param _params Array List of String containing a single element that is a Path/URL of the text file.
     * @param _input  Array List of String containing the data from API output to be stored.
     */
    public void store(ArrayList<String> _params, ArrayList<String> _input) {
        String fileName = _params.get(0);
        Path filePath = Paths.get(fileName);

        if (Files.exists(filePath)) {
            System.out.println(fileName + " exists and the data in it will be overwritten.");
        }

        try {
            File file = new File(fileName);
            PrintWriter pw = new PrintWriter(file);

            // loop through _input and write to file
            for (int i = 0; i < _input.size(); i++) {

                // ensure no trailing whitespace
                if (i == _input.size() - 1) {
                    pw.print(_input.get(i));
                } else {
                    pw.println(_input.get(i));
                }
            }
//            pw.println(_input.toString());
            pw.flush();
            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /*
     * It is expected that the params for opening and reading a file should
     * simpley be the url of the file. (so only one element in the
     * params array list).
     *
     * The lines from the file are then parsed accordingly and
     * returned as an arraylist of Entry objects
     */

    /**
     *
     * @param _params Array List of String containing a single element that is a Path/URL of the file.
     * @return Array List of Objects containing the Entries read from a desired text file.
     */
    public ArrayList<Object> load(ArrayList<String> _params) {
        ArrayList<Object> data = new ArrayList<>();
        String fileName = _params.get(0);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH);;
        Date date;

        try {
            File inputFile = new File(fileName);
            Scanner read = new Scanner(inputFile);
            while (read.hasNextLine()) {
                // split by commas, space or whatever it is separated by
                double open = read.nextDouble();
                double close = read.nextDouble();
                double low = read.nextDouble();
                double high = read.nextDouble();
                int volume = read.nextInt();
                date = format.parse(read.nextLine());

                Entry entry = new Entry(open, close, low, high, volume, date);
                data.add(entry);
            }

        } catch (FileNotFoundException e) {
            System.out.println(fileName + " can not be found.");
        } catch (Exception e) {
            System.out.println("Something went wrong.");
            e.printStackTrace();
        }

        return data;
    }
}