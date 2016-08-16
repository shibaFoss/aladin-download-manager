package main.data_holder;

import java.io.Serializable;

public class SpeedDialModel implements Serializable {
    public String name;
    public String url;

    public SpeedDialModel() {

    }

    public SpeedDialModel(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
