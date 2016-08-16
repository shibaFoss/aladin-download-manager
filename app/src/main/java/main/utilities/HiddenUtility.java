package main.utilities;

public class HiddenUtility {

    public static String getYoutubeVideoId(String youtubeVideoUrl) {
        String videoId;
        if (youtubeVideoUrl != null) {
            if (youtubeVideoUrl.startsWith("http") &&
                    youtubeVideoUrl.contains("youtube.com") && youtubeVideoUrl.contains("watch?")) {
                try {
                    String[] x = youtubeVideoUrl.split("v=");
                    String y = x[1];
                    x = y.split("&");
                    y = x[0];
                    videoId = y;

                    if (videoId != null)
                        return videoId;
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        }
        return null;
    }
}
