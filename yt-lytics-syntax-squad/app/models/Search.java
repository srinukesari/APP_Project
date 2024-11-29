package models;

/**
 * The `Search` class represents a search query with a key field that holds the search term.
 * This class is typically used to encapsulate the search key in applications where searches are
 * performed based on user input or other parameters.
 *
 * @author team
 */
public class Search {
    private String key;

    /**
     * Gets the search key.
     * If the key is `null`, an empty string is returned to avoid returning a `null` value.
     *
     * @return The search key. Returns an empty string if the key is `null`.
     */
    public String getKey(){
        if(key == null) return "";
        return this.key;
    }

    /**
     * Sets the search key.
     *
     * @param key The search key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }
}