//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.bblz.chaseEnabler;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.io.IOException;
//import net.minecraft.server.command.ChaseListener;
//import com.bblz.chaseEnabler.ChaseBroadcaster;
//import com.bblz.chaseEnabler.ChaseListener;
import net.minecraft.server.command.ChaseBroadcaster;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class ChaseCommand {
    private static ChaseBroadcaster broadcaster;
    private static ChaseListener listener;

    public ChaseCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("chase").executes((commandContext) -> {
            return execute((ServerCommandSource)commandContext.getSource(), "localhost", 10000);
        })).then(((LiteralArgumentBuilder)CommandManager.literal("me").executes((commandContext) -> {
            return executeMe((ServerCommandSource)commandContext.getSource(), 10000);
        })).then(CommandManager.argument("port", IntegerArgumentType.integer()).executes((commandContext) -> {
            return executeMe((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "port"));
        })))).then(((RequiredArgumentBuilder)CommandManager.argument("host", StringArgumentType.string()).executes((commandContext) -> {
            return execute((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "host"), 10000);
        })).then(CommandManager.argument("port", IntegerArgumentType.integer()).executes((commandContext) -> {
            return execute((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "host"), IntegerArgumentType.getInteger(commandContext, "port"));
        })))).then(CommandManager.literal("stop").executes((commandContext) -> {
            return executeStop((ServerCommandSource)commandContext.getSource());
        })));
    }

    private static int executeStop(ServerCommandSource serverCommandSource) {
        if (listener != null) {
            listener.stop();
            serverCommandSource.sendFeedback(new LiteralText("You will now stop chasing"), false);
            listener = null;
        }

        if (broadcaster != null) {
            broadcaster.stop();
            serverCommandSource.sendFeedback(new LiteralText("You will now stop being chased"), false);
            broadcaster = null;
        }

        return 0;
    }

    private static int executeMe(ServerCommandSource serverCommandSource, int i) {
        if (broadcaster != null) {
            serverCommandSource.sendError(new LiteralText("Chase server is already running. Stop it using /chase stop"));
            return 0;
        } else {
            broadcaster = new ChaseBroadcaster(i, serverCommandSource.getServer().getPlayerManager(), 100);

            try {
                broadcaster.start();
                serverCommandSource.sendFeedback(new LiteralText("Chase server is now running on port " + i + ". Clients can follow you using /chase <ip> <port>"), false);
            } catch (IOException var3) {
                var3.printStackTrace();
                serverCommandSource.sendError(new LiteralText("Failed to start chase server on port " + i));
                broadcaster = null;
            }

            return 0;
        }
    }

    private static int execute(ServerCommandSource serverCommandSource, String string, int i) {
        if (listener != null) {
            serverCommandSource.sendError(new LiteralText("You are already chasing someone. Stop it using /chase stop"));
            return 0;
        } else {
            listener = new ChaseListener(string, i, serverCommandSource.getServer());
            listener.start();
            serverCommandSource.sendFeedback(new LiteralText("You are now chasing " + string + ":" + i + ". If that server does '/chase me' then you will automatically go to the same position. Use '/chase stop' to stop chasing."), false);
            return 0;
        }
    }
}
