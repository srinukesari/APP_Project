package models;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import play.twirl.api.Html;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class YouTubeVideo{
    private String videoId;
    private String title;
    private String description;
    private String channel;
    private String thumbnailUrl;
    private List<String> tags;
    private double fleschReadingEaseScore;
    private double fleschKincaidGradeLevel;

    public YouTubeVideo(String videoId, String title, String channel, String description,
                        String thumbnailUrl,List<String> tags){
        this.videoId = videoId;
        this.title = title;
        this.channel = channel;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.tags = (tags != null) ? tags : new ArrayList<>();
        calculateReadabilityScores();
        
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
    public double getFleschReadingEaseScore() {
        return fleschReadingEaseScore;
    }

    public double getFleschKincaidGradeLevel() {
        return fleschKincaidGradeLevel;
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
            System.out.println("check here ---> "+ e);
            return Html.apply("<p>"+tag+"</p>");
        }
    }
    private void calculateReadabilityScores() {
        if (description == null || description.isEmpty()) {
            fleschReadingEaseScore = 0.0;
            fleschKincaidGradeLevel = 0.0;
            return;
        }
        int totalWords = countWords(description);
        int totalSentences = countSentences(description);
        int totalSyllables = countSyllables(description);
        if (totalSentences == 0) {
            fleschReadingEaseScore = 0.0;
            fleschKincaidGradeLevel = 0.0;
            return;
        }
        fleschReadingEaseScore = (206.835 - (1.015 * ((double) totalWords / totalSentences))
                                    - (84.6 * ((double) totalSyllables / totalWords)));
        
        fleschReadingEaseScore = new BigDecimal(fleschReadingEaseScore).setScale(2, RoundingMode.HALF_UP).doubleValue();
        if (fleschReadingEaseScore < 0) {
            fleschReadingEaseScore = 0.0;
        }
        fleschKincaidGradeLevel = ((0.39 * ((double) totalWords / totalSentences))
                                    + (11.8 * ((double) totalSyllables / totalWords)) - 15.59 );
        fleschKincaidGradeLevel = new BigDecimal(fleschKincaidGradeLevel).setScale(2, RoundingMode.HALF_UP).doubleValue();
        if (fleschKincaidGradeLevel < 0) {
            fleschKincaidGradeLevel = 0.0;
        }

    }

    private int countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        String cleanedText = text.replaceAll("[\".,?()/:!'|]", " ");

        cleanedText = cleanedText.trim();
        String[] words = cleanedText.split("\\s+");
        return words.length;
    }

    private int countSentences(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
         String[] sentences = text.split("[.!?]");
        long sentenceCount = Arrays.stream(sentences).filter(s -> !s.trim().isEmpty()).count();
        return (int) sentenceCount;
    }

    private int countSyllables(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int syllableCount = 0;
        String[] words = text.split("\\s+");
        
        for (String word : words) {
            syllableCount += countSyllablesInWord(word);
        }
        return syllableCount;
    }

    private int countSyllablesInWord(String word) {
        word = word.toLowerCase();
        int count = 0;
        boolean vowel = false;

        for (int i = 0; i < word.length(); i++) {
            if ("aeiouy".indexOf(word.charAt(i)) >= 0) {
                if (!vowel) {
                    count++;
                    vowel = true;
                }
            } else {
                vowel = false;
            }
        }
        if (word.endsWith("e")) {
            count--;
        }
        int finalCount = Math.max(count, 1);
        return finalCount;
    }
}