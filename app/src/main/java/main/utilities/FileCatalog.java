package main.utilities;

import net.fdm.R;

public class FileCatalog {

    public static final String[] ARCHIVE = {"zip", "rar", "cab", "iso", "tar", "arc", "arj", "7z"};
    public static final String[] PROGRAM = {"jar", "exe", "msi", "com", "sis", "sisx"};
    public static final String[] VIDEO = {"avi", "mp4", "wmv", "mkv", "mov", "vob", "3gp", "flv", "mpg", "mpeg"};
    public static final String[] MUSIC = {"mp3", "ogg", "wma", "wav", "aac", "ac3", "amr", "ape", "aif", "aiff", "aifc", "flac", "wave"};
    public static final String[] DOCUMENT = {"doc", "xls", "ppt", "pdf", "txt", "fb2", "chm", "docx", "xlsx", "djvu", "epub"};
    public static final String[] IMAGES = {"png", "jpg", "jpeg", "gif", "exif", "tiff", "raw", "bmp", "webp", "pam", "svg"};
    public static final String OTHER = "";

    public static final String[] ALl_DOWNLOADABLE_FORMAT = {
            "doc", "xls", "ppt", "pdf", "txt", "fb2", "chm", "docx", "xlsx", "djvu", "epub", //document
            "apk", "jar", "exe", "msi", "com", "sis", "sisx", //program
            "zip", "rar", "cab", "iso", "tar", "arc", "arj", "7z", //zip
            "avi", "mp4", "wmv", "mkv", "mov", "vob", "3gp", "flv", "mpg", "mpeg", //video
            "mp3", "ogg", "wma", "wav", "aac", "ac3", "amr", "ape", "aif", "aiff", "aifc", "flac", "wave", //audio
            "png", "jpg", "jpeg", "gif", "exif", "tiff", "raw", "bmp", "webp", "pam", "svg" //image
    };


    public static String getSubDirectoryBy(String fileName, String filePath) {
        String path = filePath;
        String name = fileName.toLowerCase();

        if (!path.endsWith("/")) path += "/";

        for (String format : FileCatalog.ARCHIVE)
            if (name.endsWith(format)) {
                path += "Archives";
                return path;
            }
        for (String format : FileCatalog.DOCUMENT)
            if (name.endsWith(format)) {
                path += "Documents";
                return path;
            }
        for (String format : FileCatalog.IMAGES)
            if (name.endsWith(format)) {
                path += "Images";
                return path;
            }
        for (String format : FileCatalog.MUSIC)
            if (name.endsWith(format)) {
                path += "Musics";
                return path;
            }
        for (String format : FileCatalog.PROGRAM)
            if (name.endsWith(format)) {
                path += "Programs";
                return path;
            }
        for (String format : FileCatalog.VIDEO)
            if (name.endsWith(format)) {
                path += "Videos";
                return path;
            }


        path += "Others";

        return path;
    }

    public static int getDownloadDrawableBy(String downloadFileName) {
        String fileName = downloadFileName.toLowerCase();

        for (String format : FileCatalog.VIDEO) {
            if (fileName.endsWith(format.toLowerCase()))
                return R.drawable.ic_download_video_catalog;
        }

        for (String format : FileCatalog.MUSIC) {
            if (fileName.endsWith(format.toLowerCase()))
                return R.drawable.ic_download_music_catalog;
        }

        for (String format : FileCatalog.ARCHIVE) {
            if (fileName.endsWith(format.toLowerCase()))
                return R.drawable.ic_download_zip_catalog;
        }

        for (String format : FileCatalog.PROGRAM) {
            if (fileName.endsWith(format.toLowerCase()))
                return R.drawable.ic_download_program_catalog;
        }

        for (String format : FileCatalog.IMAGES) {
            if (fileName.endsWith(format.toLowerCase()))
                return R.drawable.ic_download_image_catalog;
        }

        if (fileName.endsWith(".apk")) {
            return R.drawable.ic_download_apk_catalog;
        }

        return R.drawable.ic_download_other_catalog;
    }


}
