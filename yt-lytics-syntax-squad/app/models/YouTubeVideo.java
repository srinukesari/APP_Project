package models;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/* @author: team */
public class YouTubeVideo{
    private static final int DESCRIPTION_PREVIEW_LENGTH = 100;
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

    public String getHtmlLinkforTitle(){
        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
        return videoUrl;
    }

    public String getHtmlLinkforProfile(){
        String htmlLink = "/ytlytics/profile?channel="+channel;
        return htmlLink;
    }

    public String getHtmlLinkforVideoTags(){
        String htmlLink = "/ytlytics/tags?videoId=" + videoId;
        return htmlLink;
    }

    public String getHtmlLinkforTagSearch(String tag){
        String htmlLink = "/ytlytics/tags?hashTag=" + tag;
        return htmlLink;
    }
    private void calculateReadabilityScores() {
        if (description == null || description.isEmpty()) {
            fleschReadingEaseScore = 0.0;
            fleschKincaidGradeLevel = 0.0;
            return;
        }
        // System.out.println("Description : " +description);
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
        String cleanedText = text.replaceAll("[\".,?()/:!'|]", " ");

        cleanedText = cleanedText.trim();
        String[] words = cleanedText.split("\\s+");
        return words.length;
    }

    private int countSentences(String text) {
        String[] sentences = text.split("[.!?]");
        long sentenceCount = Arrays.stream(sentences).filter(s -> !s.trim().isEmpty()).count();
        return (int) sentenceCount;
    }

    private int countSyllables(String text) {
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