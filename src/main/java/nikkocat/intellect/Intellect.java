package nikkocat.intellect;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Intellect implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Intellect");

    @Override
    public void onInitialize() {
        try {
            LOGGER.info("This is Intellect. Key words: open, ai, gpt");
            CommandRegistrationCallback.EVENT.register(Commands::registerCommands);
            ServerMessageEvents.CHAT_MESSAGE.register(this::onChatMessage);
            File folder = new File("config/intellect/");
            if (!folder.exists()) {
                boolean created = folder.mkdir();
                if (created) {
                    LOGGER.info("Created: config/intellect/");
                } else {
                    LOGGER.error("Failed to create: config/intellect/");
                }
            }
            Database.connection();
            Database.createTableIfNotExist();

            // String api_key = "";
            //
            // OkHttpClient client = new OkHttpClient().newBuilder().build();
            // MediaType mediaType = MediaType.parse("application/json");
            // RequestBody body = RequestBody.create(mediaType,
            //         "{\n" +
            //                 "  \"model\": \"gpt-3.5-turbo\",\n" +
            //                 "  \"messages\": [\n" +
            //                 "    {\n" +
            //                 "      \"role\": \"user\",\n" +
            //                 "      \"content\": \"Hello!\"\n" +
            //                 "    }\n" +
            //                 "  ]\n" +
            //                 "}");
            // Request request = new Request.Builder()
            //         .url("https://api.openai.com/v1/chat/completions")
            //         .method("POST", body)
            //         .addHeader("Content-Type", "application/json")
            //         .addHeader("Authorization", "Bearer " + api_key)
            //         .build();
            // Response response = client.newCall(request).execute();
            // System.out.println(response.body().string());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void onChatMessage(SignedMessage signedMessage, ServerPlayerEntity serverPlayerEntity, MessageType.Parameters parameters) {
        try {
            String content = signedMessage.getContent().getString();
            String sender = serverPlayerEntity.getName().getString();
            Database.addMessage(sender, content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
