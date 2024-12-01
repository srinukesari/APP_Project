//package tests;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import models.YouTubeVideo;
//import org.mockito.MockedStatic;
//
///* testcase not tested */
//public class YouTubeVideoTest {
//
//    @Mock
//    private YouTubeVideo mockYouTubeVideo;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//   @Test
//    public void testGetHtmlLinkforTagSearchHandleUnsupportedEncodingException() throws UnsupportedEncodingException {
//        String channel = "channel1";
//        YouTubeVideo video = new YouTubeVideo(" Id1", "Title1", channel, "Description", "ThumbnailUrl", List.of("tag1", "tag2"));
//        try (MockedStatic<URLEncoder> mockedURLEncoder = mockStatic(URLEncoder.class)) {
//            mockedURLEncoder.when(() -> URLEncoder.encode(channel, StandardCharsets.UTF_8.toString()))
//                            .thenThrow(new UnsupportedEncodingException("Encoding not supported"));
//            String result = video.getHtmlLinkforProfile();
//            System.out.println("Result : " + result);
//            assertEquals("/ytlytics/profile?channel=" + channel, result);
//            mockedURLEncoder.verify(() -> URLEncoder.encode(channel, StandardCharsets.UTF_8.toString()), times(1));
//        }
//    }
//}
