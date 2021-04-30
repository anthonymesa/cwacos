package com.cwacos;

/**
 * Last updated: 30-APR-2021
 * 
 * Purpose: A custom PrintWriterException
 * 
 * Contributing Authors:
 *      Hyoungjin Choi
 */

public class PrintWriterException extends RuntimeException {
    public PrintWriterException() {super();}
    public PrintWriterException(String _s) {super(_s);}
}