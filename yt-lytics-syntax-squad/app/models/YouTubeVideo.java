package models;
public class YouTubeVideo{
    private String title;
    private String description;
    private String channel;

    public YouTubeVideo(String title, String channel, String description){
        this.title = title;
        this.channel = channel;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getChannel() {
        return channel;
    }

    public String getDescription() {
        return description;
    }
}