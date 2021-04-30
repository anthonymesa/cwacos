package com.cwacos;
/**
 * Last updated: 30-APR-2021
 *
 * Purpose: To store hex values of colors to be used in the UI.
 *
 * Contributing Authors:
 *      Anthony Mesa
 */

public class UIColors {

    /* UI Colors */
    private static final String PRIMARY_COLOR = "#1D1D1D";
    private static final String SECONDARY_COLOR = "#4E4E4E";
    private static final String FONT_COLOR = "#FFFFFF";
    private static final String ACCENT_COLOR = "#BB86FC";

    //================= GETTERS ===============

    public static String getPrimaryColor() {
        return UIColors.PRIMARY_COLOR;
    }

    public static String getSecondaryColor() {
        return UIColors.SECONDARY_COLOR;
    }

    public static String getFontColor() {
        return UIColors.FONT_COLOR;
    }

    public static String getAccentColor() {
        return UIColors.ACCENT_COLOR;
    }
}