package com.cwacos;

/**
 * Last updated: 26-APR-2021
 * 
 * Purpose: Response provides a way to return an object from a function containing both the
 *      success/failure state of the function, and an error message that can be printed
 *      or shown in th edisplay.
 * 
 * Contributing Authors:
 *      Anthony Mesa
 */

public class Response {
    private String status;
    private boolean success;

    Response(String _status, boolean _success){
        this.status = _status;
        this.success = _success;
    }

    //==============================================================================
    // Getters
    //==============================================================================

    public String getStatus() {
        return this.status;
    }

    public boolean getSuccess() {
        return this.success;
    }

    //==============================================================================
    // Setters
    //==============================================================================

    public void setStatus(String _status) {
        this.status = _status;
    }

    public void setSuccess(boolean _success) {
        this.success = _success;
    }

}
