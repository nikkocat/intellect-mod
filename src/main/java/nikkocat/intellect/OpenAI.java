package nikkocat.intellect;

import okhttp3.*;
import com.google.gson.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
            String user = message.getUser();
            // check if user is an assistant
            if (user.equals("gpt-model")) {
                messageJson.addProperty("role", "assistant");
            } else {
                messageJson.addProperty("role", "user");
            }
            messageJson.addProperty("content", user + ": " + message.getMessage());
            messages.add(messageJson);
        }
        json.add("messages", messages);
    }

    public static String getResponse() {
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
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static String debug() {
        // return json to string with nice formatting
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json).toString();
    }

    public static void addMessage(String user, String message) {
        JsonObject messageJson = new JsonObject();
        messageJson.addProperty("role", "user");
        messageJson.addProperty("content", user + ": " + message);
        json.getAsJsonArray("messages").add(messageJson);
        // if array is too big, remove the first element
        if (json.getAsJsonArray("messages").size() > Config.getQueryLimit()) {
            removeMessage();
        }
    }

    public static <T> Future<T> executeOnThread(Callable<T> callable) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(callable);
        executor.shutdown();
        return future;
    }

    private static void removeMessage() {
        json.getAsJsonArray("messages").remove(0);
    }
}
