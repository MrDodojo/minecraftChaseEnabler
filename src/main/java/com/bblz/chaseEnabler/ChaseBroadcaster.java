//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.bblz.chaseEnabler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.List;
import java.util.Locale;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChaseBroadcaster {
    private static final Logger LOGGER = LogManager.getLogger();
    private final int port;
    private final PlayerManager playerManager;
    private final int interval;
    private boolean active = true;
    private ServerSocket serverSocket;

    public ChaseBroadcaster(int i, PlayerManager playerManager, int j) {
        this.port = i;
        this.playerManager = playerManager;
        this.interval = j;
    }

    public void start() throws IOException {
        LOGGER.info(java.lang.System.nanoTime() + "chasebroadcaster start");
        if (this.serverSocket != null && !this.serverSocket.isClosed()) {
            LOGGER.warn(java.lang.System.nanoTime() + "Remote control server was asked to start, but it is already running. Will ignore.");
        } else {
            this.active = true;
            this.serverSocket = new ServerSocket(this.port);
            (new Thread(this::waitForClient)).start();
        }
    }

    public void stop() {
        LOGGER.info(java.lang.System.nanoTime() + "chasebroadcaster stop");
        this.active = false;
        if (this.serverSocket != null) {
            try {
                this.serverSocket.close();
            } catch (IOException var2) {
                LOGGER.error(java.lang.System.nanoTime() + "Failed to close remote control server socket", var2);
            }

            this.serverSocket = null;
        }

    }

    public void waitForClient() {
        LOGGER.info(java.lang.System.nanoTime() + "waitforclient start");
        while(true) {
            try {
                if (this.active) {
                    LOGGER.info(java.lang.System.nanoTime() + "Remote control server is listening for connections on port " + this.port);
                    Socket socket = this.serverSocket.accept();
                    LOGGER.info(java.lang.System.nanoTime() + "Remote control server received client connection on port " + socket.getPort());
                    (new Thread(() -> {
                        this.broadcast(socket);
                    })).start();
                    continue;
                }
            } catch (ClosedByInterruptException var12) {
                if (this.active) {
                    LOGGER.info(java.lang.System.nanoTime() + "Remote control server closed by interrupt");
                }
            } catch (IOException var13) {
                if (this.active) {
                    LOGGER.error(java.lang.System.nanoTime() + "Remote control server closed because of an IO exception", var13);
                }
            } finally {
                if (this.serverSocket != null && !this.serverSocket.isClosed()) {
                    try {
                        this.serverSocket.close();
                    } catch (IOException var11) {
                        LOGGER.warn(java.lang.System.nanoTime() + "Failed to close remote control server socket", var11);
                    }
                }

            }

            LOGGER.info(java.lang.System.nanoTime() + "Remote control server is now stopped");
            this.active = false;
            return;
        }
    }

    private void broadcast(Socket socket) {
        LOGGER.info(java.lang.System.nanoTime() + "chasebroadcaster broadcast");
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            while(this.active) {
                Thread.sleep((long)this.interval);
                this.write(dataOutputStream);
            }
        } catch (InterruptedException var13) {
            LOGGER.info(java.lang.System.nanoTime() + "Remote control client broadcast socket was interrupted and will be closed");
        } catch (IOException var14) {
            LOGGER.info(java.lang.System.nanoTime() + "Remote control client broadcast socket got an IO exception and will be closed", var14);
        } finally {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException var12) {
                LOGGER.warn(java.lang.System.nanoTime() + "Failed to close remote control client socket", var12);
            }

        }

        LOGGER.info(java.lang.System.nanoTime() + "Closed connection to remote control client");
    }

    private void write(DataOutputStream dataOutputStream) throws IOException {
        LOGGER.info(java.lang.System.nanoTime() + "chasebroadcaster write");
        List<ServerPlayerEntity> list = this.playerManager.getPlayerList();
        if (!list.isEmpty()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)list.get(0);
            String string = String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", serverPlayerEntity.world.getRegistryKey().getValue(), serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch());
            dataOutputStream.writeUTF(string);
        }
    }
}
