package main.utilities;

import java.io.File;

/**
 * FileUtility is the class that has static functions to handle
 * many useful file operations.
 *
 * @author Shiba Prasad J.
 */
public class FileUtility {

    private static LogHelper logHelper = new LogHelper(FileUtility.class);

    public static boolean  makeDirectory(File folder) {
        try {
            return folder.mkdirs();
        } catch (Exception e) {
            logHelper.e(e.getMessage());
            return false;
        }
    }
}
