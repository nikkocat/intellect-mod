package nikkocat.intellect;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Decoration;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Intellect implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Intellect");

    @Override
    public void onInitialize() {
        try {
            LOGGER.info("This is Intellect. Key words: open, ai, gpt");
            CommandRegistrationCallback.EVENT.register(Commands::registerCommands);
            ServerMessageEvents.CHAT_MESSAGE.register(this::onChatMessage);
            ServerMessageEvents.GAME_MESSAGE.register(this::onGameMessage);
            Config.createConfigDir();
            Config.createConfigFile();
            Config.load();
            Database.createDatabaseIfNotExist();
            Database.connection();
            Database.createTableIfNotExist();
            OpenAI.initJson();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void onGameMessage(MinecraftServer minecraftServer, Text text, boolean b) {

    }

    private void onChatMessage(SignedMessage signedMessage, ServerPlayerEntity serverPlayerEntity, MessageType.Parameters parameters) {
        try {
            String content = signedMessage.getContent().getString();
            String sender = serverPlayerEntity.getName().getString();
            Database.addMessage(sender, content);
            OpenAI.addMessage(sender, content);
            // check if message contains "@gpt"
            if (content.contains("@gpt")) {
                String response = OpenAI.getResponse();
                MinecraftServer server = serverPlayerEntity.getServer();
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    player.sendMessageToClient(Texts.parse(server.getCommandSource(), Text.translatable(response), player, 0), false);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
