package nikkocat.intellect;

import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;

public class OpenAI {
    private static String apiKey = Config.getApiKey();
    private static Gson gson = new Gson();
    public void post() throws IOException {
        String jsonString = "{\n" +
                "  \"model\": \"gpt-3.5-turbo\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"Hello!\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonString);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                // .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }
}
