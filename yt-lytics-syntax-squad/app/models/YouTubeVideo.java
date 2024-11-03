package models;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import play.twirl.api.Html;

public class YouTubeVideo{
    private String title;
    private String description;
    private String channel;
    private String thumbnailUrl;

    public YouTubeVideo(String title, String channel, String description,String thumbnailUrl){
        this.title = title;
        this.channel = channel;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        
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

    public String getThumbnailUrl() {
        return thumbnailUrl;  
    }

    public Html getHtmlLinkforProfile(){
        try {
            String htmlLink = String.format("<a href=\"/ytlytics/profile?channel=%s\">%s</a>",
                    URLEncoder.encode(channel, StandardCharsets.UTF_8.toString()),
                    channel);
            return Html.apply(htmlLink);
        } catch (Exception e) {
            e.printStackTrace();
            return Html.apply("<p>"+channel+"</p>");
        }

    }
}