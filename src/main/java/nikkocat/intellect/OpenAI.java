package nikkocat.intellect;

import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;
import java.sql.SQLException;

import static nikkocat.intellect.Intellect.LOGGER;

public class OpenAI {
    private static String apiKey = Config.getApiKey();

    private static JsonObject json = new JsonObject();
    private static OkHttpClient client = new OkHttpClient().newBuilder().build();

    public static void initJson() throws SQLException {
        // probably not the best way to do this
        json.addProperty("model", Config.getModel());
        JsonArray messages = new JsonArray();
        // query the database and add all messages to the array
        for (ChatMessage message : Database.getMessages(Config.getQueryLimit())) {
            JsonObject messageJson = new JsonObject();
            messageJson.addProperty("role", message.getUser());
            messageJson.addProperty("content", message.getMessage());
            messages.add(messageJson);
        }
        json.add("messages", messages);
    }

    public String post() {
        String jsonString = json.toString();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonString);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body) // .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
        catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static void addMessage(String user, String message) {
        JsonObject messageJson = new JsonObject();
        messageJson.addProperty("role", user);
        messageJson.addProperty("content", message);
        json.getAsJsonArray("messages").add(messageJson);
        removeMessage();
    }

    private static void removeMessage() {
        json.getAsJsonArray("messages").remove(0);
    }
}
