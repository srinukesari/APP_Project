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
            return Html.apply("<p>"+tag+"</p>");
        }
    }
     private void calculateReadabilityScores() {
        int totalWords = countWords(description);
        int totalSentences = countSentences(description);
        int totalSyllables = countSyllables(description);

        // Calculate Flesch Reading Ease Score
        fleschReadingEaseScore = 206.835 - (1.015 * ((double) totalWords / totalSentences))
                                 - (84.6 * ((double) totalSyllables / totalWords));

        // Calculate Flesch-Kincaid Grade Level
        fleschKincaidGradeLevel = (0.39 * ((double) totalWords / totalSentences))
                                 + (11.8 * ((double) totalSyllables / totalWords)) - 15.59;
    }
    private int countWords(String text) {
        // String[] words = text.trim().split("\\s+");
        // return words.length;
        if (text == null || text.isEmpty()) {
            return 0;
        }
        String[] words = text.split("\\s+");
        return words.length;
    }

    private int countSentences(String text) {
        // String[] sentences = text.split("[.!?]");
        // return sentences.length;
        if (text == null || text.isEmpty()) {
            return 0;
        }
        String[] sentences = text.split("[.!?]");
        return sentences.length;
    }

    private int countSyllables(String text) {
        // int syllableCount = 0;
        // for (String word : text.split("\\s+")) {
        //     syllableCount += countSyllablesInWord(word);
        // }
        // return syllableCount;
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
    //     word = word.toLowerCase();
    //     int count = 0;
    //     boolean vowel = false;
    //     for (int i = 0; i < word.length(); i++) {
    //         if ("aeiouy".indexOf(word.charAt(i)) >= 0) {
    //             if (!vowel) {
    //                 count++;
    //                 vowel = true;
    //             }
    //         } else {
    //             vowel = false;
    //         }
    //     }
    //     if (word.endsWith("e")) {
    //         count--;
    //     }
    //     return Math.max(count, 1);
    // }
        word = word.toLowerCase();
        int syllableCount = 0;
        boolean inVowelGroup = false;

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (isVowel(ch)) {
                if (!inVowelGroup) {
                    syllableCount++;
                    inVowelGroup = true;
                }
            } else {
                inVowelGroup = false;
            }
        }

        // Special case: if the word ends in 'e' and has more than one syllable, we might not count the 'e'
        if (word.endsWith("e") && syllableCount > 1 && !isVowel(word.charAt(word.length() - 2))) {
            syllableCount--;
        }

        return syllableCount;
    }
    
    private boolean isVowel(char ch) {
        return "aeiou".indexOf(ch) != -1;
    }
}