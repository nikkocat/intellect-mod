package nikkocat.intellect;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.*;

import static nikkocat.intellect.Intellect.LOGGER;

public class Config {
    private static String apiKey = "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static int permissionLevel = 4;
    private static int maxListEntries = 19;
    public static void load() {

    }
    public static void createConfigDir() {
        File folder = new File("config/intellect/");
        if (!folder.exists()) {
            boolean created = folder.mkdir();
            if (created) {
                LOGGER.info("Created: config/intellect/");
            } else {
                LOGGER.error("Failed to create: config/intellect/");
            }
        }
    }

    public static void createConfigFile() throws IOException {
        File config = new File("config/intellect/config.json");
        if (!config.exists()) {
            try {
                // create json object
                JsonObject json = new JsonObject();
                // openai object
                JsonObject openai = new JsonObject();
                openai.addProperty("apiKey", "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                json.add("openai", openai);
                // database object
                JsonObject database = new JsonObject();
                json.add("database", database);
                // commands object
                JsonObject commands = new JsonObject();
                commands.addProperty("permissionLevel", 4);
                commands.addProperty("maxListEntries", 19);
                json.add("commands", commands);
                // write to file
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                FileWriter fileWriter = new FileWriter("config/intellect/config.json");
                gson.toJson(json, fileWriter);
                fileWriter.flush();

                LOGGER.info("Created: config/intellect/config.json");
            } catch (JsonIOException e) {
                LOGGER.error("Failed to create: config/intellect/config.json");
                LOGGER.error(e.getMessage());
            }
        }
    }
    public static String getApiKey() {
        return apiKey;
    }

    public static int getMaxListEntries() {
        return maxListEntries;
    }

    public static int getPermissionLevel() {
        return permissionLevel;
    }
}
