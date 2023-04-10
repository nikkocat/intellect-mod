package nikkocat.intellect;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.*;

import static nikkocat.intellect.Intellect.LOGGER;

public class Config {
    private static JsonObject json = new JsonObject();
    private static String apiKey = "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static String model = "gpt-3.5-turbo"; // gpt-4, gpt-4-0314, gpt-4-32k, gpt-4-32k-0314, gpt-3.5-turbo, gpt-3.5-turbo-0301
    private static int queryLimit = 300; // 300 is approximately 8K content, 1200 is approximately 32K content
    private static boolean timestamp = true;
    private static int permissionLevel = 4;
    private static int maxListEntries = 19;

    public static void load() {
        createConfigDir();
        createConfigFile();
        try (FileReader reader = new FileReader("config/intellect/config.json")) {
            // read json file
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonObject json = jsonElement.getAsJsonObject();
            // openai object
            JsonObject openai = json.getAsJsonObject("openai");
            apiKey = openai.get("apiKey").getAsString();
            queryLimit = openai.get("queryLimit").getAsInt();
            model = openai.get("model").getAsString();
            // database object
            JsonObject database = json.getAsJsonObject("database");
            timestamp = database.get("timestamp").getAsBoolean();
            // commands object
            JsonObject commands = json.getAsJsonObject("commands");
            permissionLevel = commands.get("permissionLevel").getAsInt();
            maxListEntries = commands.get("maxListEntries").getAsInt();
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            LOGGER.error("Failed to load: config/intellect/config.json");
            LOGGER.error(e.getMessage());
            LOGGER.warn("Attempting to create a new config file...");
            // delete old config file
            File config = new File("config/intellect/config.json");
            boolean deleted = config.delete();
            if (deleted) {
                LOGGER.info("Deleted: config/intellect/config.json");
            } else {
                LOGGER.error("Failed to delete: config/intellect/config.json");
            }
            createConfigFile();
        }
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

    public static void createConfigFile() {
        File config = new File("config/intellect/config.json");
        if (!config.exists()) {
            try {
                // openai object
                JsonObject openai = new JsonObject();
                openai.addProperty("apiKey", apiKey);
                openai.addProperty("queryLimit", queryLimit);
                openai.addProperty("model", model);
                json.add("openai", openai);
                // database object
                JsonObject database = new JsonObject();
                database.addProperty("timestamp", timestamp);
                json.add("database", database);
                // commands object
                JsonObject commands = new JsonObject();
                commands.addProperty("permissionLevel", permissionLevel);
                commands.addProperty("maxListEntries", maxListEntries);
                json.add("commands", commands);
                // write to file
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                FileWriter fileWriter = new FileWriter("config/intellect/config.json");
                gson.toJson(json, fileWriter);
                fileWriter.flush();

                LOGGER.info("Created: config/intellect/config.json");
            } catch (JsonIOException | IOException e) {
                LOGGER.error("Failed to create: config/intellect/config.json");
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static int getQueryLimit() {
        return queryLimit;
    }

    public static int getMaxListEntries() {
        return maxListEntries;
    }

    public static int getPermissionLevel() {
        return permissionLevel;
    }

    public static String getModel() {
        return model;
    }
}
