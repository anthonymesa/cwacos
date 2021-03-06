package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * <p>
 * Purpose: LocalStorageTranslator
 * <p>
 * Contributing Authors:
 * Hyoungjin Choi
 * Anthony Mesa
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;


class LocalStorageTranslator implements StorageAdapter {

    public LocalStorageTranslator() {

    }

    /**
     * Loads the settings from the settings file.
     *
     * @param _params Arraylist of Strings containing the path of the settings file.
     * @return Hashmap of settings.
     */
    public Map<String, String> loadSettings(ArrayList<String> _params) {
        String fileName = getFileName(_params);
        Path filePath = getPath(fileName);

        Map<String, String> settings = new HashMap<String, String>();

        if (exists(filePath)) {

            File inputFile = new File(fileName);
            Scanner read = null;

            try {
                read = new Scanner(inputFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            while (read.hasNextLine()) {
                String[] line = read.nextLine().split("=", 0);
                if (line.length == 2) {
                    settings.put(line[0], line[1]);
                }
            }
        }
        return settings;
    }

    /**
     * Saves settings to the settings file.
     *
     * @param _settings Hashmap of settings to be saved.
     * @param _params   ArrayList of Strings containing the path of the settings file.
     */
    public void saveSettings(Map<String, String> _settings, ArrayList<String> _params) {
        String fileName = getFileName(_params);
        File inputFile = new File(fileName);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, String> entry : _settings.entrySet()) {
            entry.getKey();

            writer.println(entry.getKey() + "=" + entry.getValue());
        }

        // flush and check for error
        if (writer.checkError()) {
            throw new PrintWriterException();
        } else {
            writer.close();
        }
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
     * Stores the entries to a file and returns a response for whether it saved or did not save.
     *
     * @param _params Array List of String containing a single element that is a Path/URL of the text file.
     * @param _input  Array List of String containing the data from API output to be stored.
     * @return Response message that informs whether if the file has been saved or not.
     */
    public Response store(ArrayList<String> _params, ArrayList<String> _input) {
        String fileName = getFileName(_params);
        Path filePath = getPath(fileName);

        if (exists(filePath)) {
            System.out.println(fileName + " exists and the data in it will be overwritten.");
        }

        try {
            File file = new File(fileName);
            PrintWriter writer = new PrintWriter(file);

            writer.println(_params.get(1));
            writer.println(_params.get(2));

            // loop through _input and write to file
            for (int i = 0; i < _input.size(); i++) {

                // ensure no trailing whitespace or it WILL cause an error when reading back
                if (i == _input.size() - 1) {
                    writer.print(_input.get(i));
                } else {
                    writer.println(_input.get(i));
                }
            }

            // flush and check for error
            if (writer.checkError()) {
                writer.close();
                throw new PrintWriterException();
            } else {
                writer.close();
            }
        } catch (IOException | PrintWriterException ex) {
            return new Response("Failed to write data to " + fileName + "...", false);
        }

        return new Response("saved successfully to " + fileName + "...", true);
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
     * Load the entries from a file.
     *
     * @param _storageInfo Array List of String containing a single element that is a Path/URL of the file.
     * @return LoadData object which contains the information for entries.
     */
    public LoadData load(ArrayList<String> _storageInfo) throws Exception {
        ArrayList<Object> data = new ArrayList<>();
        String fileName = getFileName(_storageInfo);

        SimpleDateFormat format = CwacosDateFormat.getDateFormat();

        try {
            File inputFile = new File(fileName);
            Scanner read = new Scanner(inputFile);

            String symbol = read.nextLine();
            int type = Integer.parseInt(read.nextLine());

            // read space separated Strings back into usable data
            while (read.hasNextLine()) {
                double open = read.nextDouble();
                double close = read.nextDouble();
                double low = read.nextDouble();
                double high = read.nextDouble();
                int volume = read.nextInt();
                Date date = format.parse(read.nextLine());

                // The usable data is retrieved as instances of Entries and are stored in the ArrayList of Objects
                Entry entry = new Entry(open, close, low, high, volume, date);
                data.add(entry);
            }

            LoadData returnable = new LoadData(data, symbol, type);
            read.close();

            return returnable;

        } catch (FileNotFoundException e) {
            throw new Exception(fileName + " can not be found.");
        } catch (NoSuchElementException e) {
            throw new Exception("No trailing whitespace is allowed!");
        } catch (Exception e) {
            throw new Exception("A fatal error occurred while loading");
        }
    }

    /**
     * Gets the file name from a given ArrayList of String.
     *
     * @param _storageInfo Array List of String containing a single element that is a Path/URL of the file.
     * @return String name of the file.
     */
    private static String getFileName(ArrayList<String> _storageInfo) {
        if (_storageInfo.isEmpty()) {
            throw new IllegalArgumentException("Empty arraylist!");
        } else {
            return _storageInfo.get(0);
        }
    }

    /**
     * Gets the file path of a given file from its name.
     *
     * @param _fileName String name of the file.
     * @return Path of the given file.
     */
    private static Path getPath(String _fileName) {
        try {
            return Paths.get(_fileName);
        } catch (InvalidPathException ex) {
            // exception thrown if the path string cannot be converted to a Path
            System.out.println("The file name is not valid!");
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if the file exists, given the path of the file.
     *
     * @param _path Path of the file.
     * @return True if the file exists, False if the file does not exist.
     */
    private static boolean exists(Path _path) {
        return Files.exists(_path);
    }
}