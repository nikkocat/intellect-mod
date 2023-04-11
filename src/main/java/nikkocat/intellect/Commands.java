package nikkocat.intellect;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.sql.SQLException;
import java.util.List;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class Commands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
                                        CommandRegistryAccess registryAccess,
                                        CommandManager.RegistrationEnvironment environment) {
        int listEntries = Config.getMaxListEntries();
        dispatcher.register(literal("intellect").requires(source -> source.hasPermissionLevel(Config.getPermissionLevel()))
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(Text.literal("Intellect v1.0.0"), false);
                    return 1;
                })
                .then(literal("apikey")
                        // get api key
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(Text.literal("API Key: " + Config.getApiKey()), true);
                            return 1;
                        })
                )
                .then(literal("chat") // depreciated
                        .then(argument("prompt", string())
                                .executes(ctx -> {
                                    ctx.getSource().sendFeedback(Text.literal("Sending prompt: " + getString(ctx, "prompt")), true);
                                    return 1;
                                })
                        )
                )
                .then(literal("list")
                        .executes(ctx -> {
                            try {
                                int entries = listEntries;
                                int pages = Database.countMessages() / entries;
                                List<ChatMessage> messages = Database.getMessages(entries);
                                ctx.getSource().sendFeedback(Text.literal("   Showing page: 0/" + pages), false);
                                for (ChatMessage message : messages) {
                                    ctx.getSource().sendFeedback(Text.literal(message.toStringLine()), false);
                                }
                            } catch (SQLException e) {
                                ctx.getSource().sendError(Text.literal(e.getMessage()));
                            }
                            return 1;
                        })
                        .then(argument("page", integer(0))
                                .executes(ctx -> {
                                    try {
                                        int page = getInteger(ctx, "page");
                                        int entries = listEntries;
                                        int pages = Database.countMessages() / entries;
                                        List<ChatMessage> messages = Database.getMessages(page * entries, entries);
                                        ctx.getSource().sendFeedback(Text.literal("   Showing page: " + getInteger(ctx, "page") + "/" + pages), false);
                                        for (ChatMessage message : messages) {
                                            ctx.getSource().sendFeedback(Text.literal(message.toStringLine()), false);
                                        }
                                    } catch (SQLException e) {
                                        ctx.getSource().sendError(Text.literal(e.getMessage()));
                                    }
                                    return 1;
                                })
                        )
                )
                .then(literal("purge")
                        .executes(ctx -> {
                            try {
                                Database.purgeMessages();
                                ctx.getSource().sendFeedback(Text.literal("Successfully purged ALL messages"), true);
                            } catch (SQLException e) {
                                ctx.getSource().sendError(Text.literal(e.getMessage()));
                            }
                            return 1;
                        })
                        .then(argument("count", integer(0))
                                .executes(ctx -> {
                                    int count = getInteger(ctx, "count");
                                    try {
                                        Database.purgeMessages(count);
                                        ctx.getSource().sendFeedback(Text.literal("Successfully purged " + count + " messages"), true);
                                    } catch (SQLException e) {
                                        ctx.getSource().sendError(Text.literal(e.getMessage()));
                                    }
                                    return 1;
                                })
                        )
                        .then(argument("player", player())
                                .executes(ctx -> {
                                    ServerPlayerEntity player = getPlayer(ctx, "player");
                                    String name = player.getEntityName();
                                    try {
                                        Database.purgeMessages(name);
                                        ctx.getSource().sendFeedback(Text.literal("Successfully purged ALL messages from player " + name), true);
                                    } catch (SQLException e) {
                                        ctx.getSource().sendError(Text.literal(e.getMessage()));
                                    }
                                    return 1;
                                })
                                .then(argument("count", integer(0))
                                        .executes(ctx -> {
                                            int count = getInteger(ctx, "count");
                                            ServerPlayerEntity player = getPlayer(ctx, "player");
                                            String name = player.getEntityName();
                                            try {
                                                Database.purgeMessages(name, count);
                                                ctx.getSource().sendFeedback(Text.literal("Successfully purged " + count + " messages from player " + name), true);
                                            } catch (SQLException e) {
                                                ctx.getSource().sendError(Text.literal(e.getMessage()));
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
