package models;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import play.twirl.api.Html;
import java.util.*;

public class YouTubeVideo{
    private String videoId;
    private String title;
    private String description;
    private String channel;
    private String thumbnailUrl;
    private List<String> tags;

    public YouTubeVideo(String videoId, String title, String channel, String description,
                        String thumbnailUrl,List<String> tags){
        this.videoId = videoId;
        this.title = title;
        this.channel = channel;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.tags = (tags != null) ? tags : new ArrayList<>();
    }

    public String getVideoId(){ return  videoId;}

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

    public List<String> getTags(){ return tags;}

    public Html getHtmlLinkforTitle(){
        try {
            String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
            String htmlLink = String.format("<a href=\"%s\" target=\"_blank\">%s</a>", videoUrl, title);
            return Html.apply(htmlLink);
        } catch (Exception e) {
            e.printStackTrace();
            return Html.apply("<p>"+title+"</p>");
        }
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

    public Html getHtmlLinkforVideoTags(){
        try {
            String htmlLink = String.format("<a href=\"/ytlytics/tags?videoId=%s\">Tags</a>",
                    URLEncoder.encode(videoId, StandardCharsets.UTF_8.toString()));
            return Html.apply(htmlLink);
        } catch (Exception e) {
            e.printStackTrace();
            return Html.apply("<p>Tags</p>");
        }
    }

    public Html getHtmlLinkforTagSearch(String tag){
        try{
            String htmlLink = String.format("<a href=\"/ytlytics/tags?hashTag=%s\">%s</a>",
                    URLEncoder.encode(tag, StandardCharsets.UTF_8.toString()),
                    tag);
            return Html.apply(htmlLink);
        }catch (Exception e){
            e.printStackTrace();
            return Html.apply("<p>"+tag+"</p>");
        }
    }
}