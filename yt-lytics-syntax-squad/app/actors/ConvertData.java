package actors;

import org.apache.pekko.stream.Materializer;
import org.apache.pekko.util.ByteString;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class ConvertData {
    public static CompletableFuture<JsonNode> convertHttpEntityToJsonNode
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