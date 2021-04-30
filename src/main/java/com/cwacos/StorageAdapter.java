package com.cwacos;

/**
 * Last updated: 26-APR-2021
 * 
 * Purpose: StorageAdapter is an interface that defines the basic requirements for exporting or importing
 *      data from or to CwacosData respectively, no matter the method.
 * 
 * Contributing Authors:
 *      Anthony Mesa
 *      Hyoungjin Choi
 */

import java.util.ArrayList;
import java.util.Map;

interface StorageAdapter {
    Response store(ArrayList<String> _params, ArrayList<String> _input);

    LoadData load(ArrayList<String> _params) throws Exception;

    Map<String, String> loadSettings(ArrayList<String> _params);

    void saveSettings(Map<String, String> _settings, ArrayList<String> _params);
} 