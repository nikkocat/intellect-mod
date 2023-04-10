package nikkocat.intellect;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Intellect implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Intellect");

    @Override
    public void onInitialize() {
        try {
            LOGGER.info("This is Intellect. Key words: open, ai, gpt");
            CommandRegistrationCallback.EVENT.register(Commands::registerCommands);
            ServerMessageEvents.CHAT_MESSAGE.register(this::onChatMessage);
            Config.createConfigDir();
            Config.createConfigFile();
            Config.load();
            Database.createDatabaseIfNotExist();
            Database.connection();
            Database.createTableIfNotExist();
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
