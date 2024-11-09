package models;

/* @author: team */
public class Search {
    private String key;

    public String getKey(){
        if(key == null) return "";
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}