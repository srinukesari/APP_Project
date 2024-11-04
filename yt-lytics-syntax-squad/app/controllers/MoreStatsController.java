// package controllers;

// import models.MoreStats;
// import models.YouTubeVideo;
// import play.mvc.Controller;
// import play.mvc.Result;

// import java.io.IOException;
// import java.util.List;
// import java.util.Map;

// public class MoreStatsController extends Controller {
//     public Result displayStats(String searchTerms) {
//         try{
//         List<YouTubeVideo> videos = YouTubeSearch.Search(searchTerms, "home"); 
//         MoreStats stats = new MoreStats(searchTerms, videos);
//         Map<String, Long> wordStats = stats.getWordStatistics();

//         return ok(views.html.wordstats.render(wordStats));
//         }catch (IOException e) {
//             e.printStackTrace(); 
//             return internalServerError("Error fetching video data");
//         }
//     }
// }
