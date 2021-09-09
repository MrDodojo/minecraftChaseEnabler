//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.bblz.chaseEnabler;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChaseListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int LISTEN_INTERVAL_SECONDS = 5;
    private final String host;
    private final int port;
    private final MinecraftServer server;
    private boolean active;
    private Socket socket;
    private Thread thread;

    public ChaseListener(String string, int i, MinecraftServer minecraftServer) {
        this.host = string;
        this.port = i;
        this.server = minecraftServer;
    }

    public void start() {
        LOGGER.info(java.lang.System.nanoTime() + "chaselistener start");
        if (this.thread != null && this.thread.isAlive()) {
            LOGGER.warn(java.lang.System.nanoTime() + "Remote control client was asked to start, but it is already running. Will ignore.");
        }

        this.active = true;
        this.thread = new Thread(this::listen);
        this.thread.start();
    }

    public void stop() {
        LOGGER.info(java.lang.System.nanoTime() + "chaselistener stop");
        this.active = false;
        if (this.socket != null && !this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException var2) {
                LOGGER.warn(java.lang.System.nanoTime() + "Failed to close socket to remote control server", var2);
            }
        }

        this.socket = null;
        this.thread = null;
    }

    public void listen() {
        LOGGER.info(java.lang.System.nanoTime() + "chaselistener listen");
        String string = this.host + ":" + this.port;

        while(this.active) {
            try {
                LOGGER.info(java.lang.System.nanoTime() + "Connecting to remote control server " + string);
                this.socket = new Socket(this.host, this.port);
                LOGGER.info(java.lang.System.nanoTime() + "Connected to remote control server! Will continuously execute the command broadcasted by that server.");

                try {
                    DataInputStream dataInputStream =    new DataInputStream(this.socket.getInputStream());
                    LOGGER.warn(dataInputStream);

                    while(this.active) {
                        String string2 = dataInputStream.readUTF();
                        this.runCommand(string2);
                    }
                } catch (IOException var5) {
                    LOGGER.warn(java.lang.System.nanoTime() + "Lost connection to remote control server " + string + ". Will retry in 5s. " + var5);
                }
            } catch (IOException var6) {
                LOGGER.warn(java.lang.System.nanoTime() + "Failed to connect to remote control server " + string + ". Will retry in 5s.");
            }

            if (this.active) {
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException var4) {
                }
            }
        }

    }

    private void runCommand(String string) {
        LOGGER.info(java.lang.System.nanoTime() + "chaselistener runcommand");
        List<ServerPlayerEntity> list = this.server.getPlayerManager().getPlayerList();
        if (!list.isEmpty()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)list.get(0);
            ServerWorld serverWorld = this.server.getOverworld();
            ServerCommandSource serverCommandSource = new ServerCommandSource(serverPlayerEntity, Vec3d.of(serverWorld.getSpawnPos()), Vec2f.ZERO, serverWorld, 4, "", LiteralText.EMPTY, this.server, serverPlayerEntity);
            CommandManager commandManager = this.server.getCommandManager();
            commandManager.execute(serverCommandSource, string);
        }
    }
}
