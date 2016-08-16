package main.download_manager;

import main.settings.UserSettings;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import static main.data_holder.BaseWritableObject.readSerializableObjects;

/**
 * DownloadModelParser read all the model files from sdcard and store them in
 * ArrayList object.
 */
public final class DownloadModelParser {

    /**
     * Get an arrayList of all the incomplete/running download models.
     */
    public static ArrayList<DownloadModel> getIncompleteDownloadModels() {
        return parseModels(DownloadModel.FILE_FORMAT);
    }

    /**
     * Get an arrayList of all the complete download models.
     */
    public static ArrayList<DownloadModel> getCompleteDownloadModels() {
        return parseModels(DownloadModel.COMPLETE_MODEL);
    }

    /**
     * This function parsed all the file with the given file format.. and read the
     * files.
     *
     * @param fileFormat the format name of model files that will be used for searching the matching files.
     * @return arrayList of all the download models.
     */
    private static ArrayList<DownloadModel> parseModels(String fileFormat) {
        ArrayList<DownloadModel> modelList = new ArrayList<>();

        if (isDirExists(getDownloadCacheDir())) {
            File[] modelFileList;
            modelFileList = getDownloadModelFileList(fileFormat, getDownloadCacheDir());

            for (File modelFile : modelFileList) {
                DownloadModel model = (DownloadModel) readSerializableObjects(modelFile);
                if (model != null) {
                    model.extraText = "";
                    model.networkSpeed = "";
                    modelList.add(model);
                }
            }
        }
        return modelList;
    }

    /**
     * Get the download cache directory of this app.
     *
     * @return Download Cache File
     */
    private static File getDownloadCacheDir() {
        return new File(UserSettings.downloadCachePath);
    }

    private static File[] getDownloadModelFileList(final String fileFormat, File downloadDir) {
        return downloadDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(fileFormat);
            }
        });
    }

    /**
     * Check if either the directory exists or not.
     *
     * @param file the directory to be checked.
     */
    private static boolean isDirExists(File file) {
        return file.exists();
    }
}
