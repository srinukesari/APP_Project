package models;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.UnsupportedEncodingException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The `YouTubeVideo` class represents a YouTube video with associated details such as 
 * video ID, title, channel, description, tags, and readability scores based on the description.
 * It provides methods to calculate readability scores (Flesch-Kincaid Grade Level and Flesch Reading Ease)
 * and to generate HTML links for the video, profile, and related tags.
 *
 * @author team
 */
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

    /**
     * Constructor to initialize a `YouTubeVideo` object with the given parameters.
     *
     * @param videoId The unique identifier for the YouTube video.
     * @param title The title of the YouTube video.
     * @param channel The name of the YouTube channel that uploaded the video.
     * @param description A brief description of the YouTube video.
     * @param thumbnailUrl URL of the video thumbnail image.
     * @param tags List of tags associated with the video.
     */
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

    /**
     * Gets the unique identifier for the YouTube video.
     *
     * @return The video ID.
     */
    @JsonProperty("videoId")
    public String getVideoId(){ return  videoId;}

    /**
     * Gets the title of the YouTube video.
     *
     * @return The title of the video.
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * Gets the name of the YouTube channel that uploaded the video.
     *
     * @return The channel name.
     */
    @JsonProperty("channel")
    public String getChannel() {
        return channel;
    }

    /**
     * Gets the description of the YouTube video.
     *
     * @return The video description.
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * Gets the URL of the thumbnail image for the YouTube video.
     *
     * @return The thumbnail URL.
     */
    @JsonProperty("thumbnailUrl")
    public String getThumbnailUrl() {
        return thumbnailUrl;  
    }

    /**
     * Gets the Flesch Reading Ease score for the description of the YouTube video.
     * This score indicates the readability of the description text.
     *
     * @return The Flesch Reading Ease score.
     */
    @JsonProperty("fleschReadingEaseScore")
    public double getFleschReadingEaseScore() {
        return fleschReadingEaseScore;
    }

   /**
     * Gets the Flesch-Kincaid Grade Level for the description of the YouTube video.
     * This score indicates the grade level required to understand the text.
     *
     * @return The Flesch-Kincaid Grade Level.
     */
    @JsonProperty("fleschKincaidGradeLevel")
    public double getFleschKincaidGradeLevel() {
        return fleschKincaidGradeLevel;
    }

    /**
     * Gets the list of tags associated with the YouTube video.
     *
     * @return The list of tags.
     */
    @JsonProperty("tags")
    public List<String> getTags(){ return tags;}

    /**
     * Generates an HTML link for the YouTube video based on its video ID.
     *
     * @return The HTML link for the video.
     */
    public String getHtmlLinkforTitle(){
        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
        return videoUrl;
    }

//    /**
//     * Generates an HTML link for the YouTube profile based on the channel name.
//     *
//     * @return The HTML link for the channel profile.
//     */
//    public String getHtmlLinkforProfile(){
//        try{
//            String encodedChannel = URLEncoder.encode(channel, StandardCharsets.UTF_8.toString());
//            String htmlLink = "/ytlytics/profile?channel="+encodedChannel;
//            return htmlLink;
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return "/ytlytics/profile?channel=" + channel;
//        }
//    }

    /**
     * Generates an HTML link for searching tags related to the YouTube video.
     *
     * @return The HTML link for searching video tags.
     */
    public String getHtmlLinkforVideoTags(){
        String htmlLink = "/ytlytics/tags?videoId=" + videoId;
        return htmlLink;
    }

//    /**
//     * Generates an HTML link for searching a specific tag across YouTube videos.
//     *
//     * @param tag The tag to search for.
//     * @return The HTML link for searching the tag.
//     */
//    public String getHtmlLinkforTagSearch(String tag){
//        try {
//            String encodedTag = URLEncoder.encode(tag, StandardCharsets.UTF_8.toString());
//            String htmlLink = "/ytlytics/tags?hashTag=" + encodedTag;
//            return htmlLink;
//        }catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return "/ytlytics/tags?hashTag=" + tag;
//        }
//    }
    /**
     * Calculates the readability scores (Flesch Reading Ease and Flesch-Kincaid Grade Level)
     * for the YouTube video's description.
     * The readability scores are based on the total number of words, sentences, and syllables.
     */
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
//        if (totalSentences == 0) {
//            fleschReadingEaseScore = 0.0;
//            fleschKincaidGradeLevel = 0.0;
//            return;
//        }
        fleschReadingEaseScore = (206.835 - (1.015 * ((double) totalWords / totalSentences))
                                    - (84.6 * ((double) totalSyllables / totalWords)));
        
        fleschReadingEaseScore = new BigDecimal(fleschReadingEaseScore).setScale(2, RoundingMode.HALF_UP).doubleValue();
//        if (fleschReadingEaseScore < 0) {
//            fleschReadingEaseScore = 0.0;
//        }
        fleschKincaidGradeLevel = ((0.39 * ((double) totalWords / totalSentences))
                                    + (11.8 * ((double) totalSyllables / totalWords)) - 15.59 );
        fleschKincaidGradeLevel = new BigDecimal(fleschKincaidGradeLevel).setScale(2, RoundingMode.HALF_UP).doubleValue();
        if (fleschKincaidGradeLevel < 0) {
            fleschKincaidGradeLevel = 0.0;
        }

    }

    /**
     * Counts the number of words in the provided text.
     *
     * @param text The text to count words in.
     * @return The total number of words.
     */
    private int countWords(String text) {
        String cleanedText = text.replaceAll("[\".,?()/:!'|]", " ");

        cleanedText = cleanedText.trim();
        String[] words = cleanedText.split("\\s+");
        return words.length;
    }

    /**
     * Counts the number of sentences in the provided text.
     *
     * @param text The text to count sentences in.
     * @return The total number of sentences.
     */
    private int countSentences(String text) {
        String[] sentences = text.split("[.!?]");
        long sentenceCount = Arrays.stream(sentences).filter(s -> !s.trim().isEmpty()).count();
        return (int) sentenceCount;
    }

    /**
     * Counts the number of syllables in the provided text.
     *
     * @param text The text to count syllables in.
     * @return The total number of syllables.
     */
    private int countSyllables(String text) {
        int syllableCount = 0;
        String[] words = text.split("\\s+");
        
        for (String word : words) {
            syllableCount += countSyllablesInWord(word);
        }
        return syllableCount;
    }

    /**
     * Counts the number of syllables in a single word.
     *
     * @param word The word to count syllables in.
     * @return The total number of syllables in the word.
     */
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