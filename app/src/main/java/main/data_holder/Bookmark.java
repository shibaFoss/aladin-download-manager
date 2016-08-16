package main.data_holder;

import java.io.Serializable;

public class Bookmark implements Serializable {

    public String name;
    public String url;


    public Bookmark() {

    }

    public Bookmark(String name, String url) {
        this.name = name;
        this.url = url;
    }

}


