package de.presti.simplediscordrpc.client;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.activity.Activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimpleDiscordRPC {

    private static Core coreInstance;
    protected static Thread callbackThread;

    protected static List<DiscordEventAdapter> adapters = new ArrayList<>();

    public static void initialize(long applicationId) throws IOException {
        Core.initDownload();
        try (CreateParams createParams = new CreateParams()) {
            createParams.setClientID(applicationId);
            if (!adapters.isEmpty()) {
                adapters.forEach(createParams::registerEventHandler);
            }

            try {
                coreInstance = new Core(createParams);
                callbackThread = new Thread(() -> {
                    while (true) {
                        coreInstance.runCallbacks();
                        try {
                            Thread.sleep(16);
                        } catch (InterruptedException ignored) {
                        }
                    }
                });
                callbackThread.start();
            } catch (Exception e) {
                throw new IOException("Failed to create Discord RPC instance", e);
            }
        } catch (Exception e) {
            throw new IOException("Failed to create Discord RPC instance", e);
        }
    }

    public static void updateActivity(Activity activity, Consumer<Result> callback) {
        if (callback == null) {
            callback = result -> {};
        }

        coreInstance.activityManager().updateActivity(activity, callback);
    }

    public static void addEventHandler(DiscordEventAdapter adapter) {
        adapters.add(adapter);
    }

    public static void shutdown() {
        callbackThread.interrupt();
        coreInstance.close();
    }

}
