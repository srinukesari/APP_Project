package actors;

import org.apache.pekko.stream.Materializer;
import org.apache.pekko.util.ByteString;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * A utility class for converting HTTP response bodies to JSON objects.
 * This class contains methods to asynchronously convert an HTTP entity's body 
 * to a JsonNode using the Akka Streams materializer for data processing.
 * @author team
 */
public class ConvertData {
    /**
     * Converts the body of a Result object (which is an HTTP response) 
     * to a JsonNode. The conversion is done asynchronously and 
     * the result is returned wrapped in a CompletableFuture.
     * 
     * @param result The Result containing the HTTP entity with the response body.
     * @param materializer The Materializer used to handle the stream of the HTTP entity.
     * @return A CompletableFuture that will be completed with the parsed JsonNode.
     *         If an error occurs during parsing, it returns a JsonNode with an error message.
     * 
     * @throws Exception if there is an error while reading the response body or parsing the JSON.
     */
    public CompletableFuture<JsonNode> convertHttpEntityToJsonNode
            (Result result, Materializer materializer) {
        try {
            // Convert the HttpEntity's body to a byte array asynchronously
            ByteString byteString = result.body().consumeData(materializer).toCompletableFuture().join();

            // Convert the byte array into a String
            String jsonString = byteString.utf8String();

            // Parse the String into a JsonNode
            JsonNode jsonNode = Json.parse(jsonString);
            return CompletableFuture.completedFuture(jsonNode);
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(Json.newObject().put("error", "Failed to parse response"));
        }
    }
}