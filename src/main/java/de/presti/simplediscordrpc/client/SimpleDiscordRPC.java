package de.presti.simplediscordrpc.client;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.activity.Activity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Some simple Discord RPC implementation.
 */
@Getter
@NoArgsConstructor
public class SimpleDiscordRPC implements ISimpleDiscordRPC {

    /**
     * The instance of the Core.
     */
    private static Core coreInstance;

    /**
     * The list of DiscordEventAdapters.
     */
    protected static List<DiscordEventAdapter> adapters = new ArrayList<>();

    /**
     * The executor.
     */
    protected static ScheduledExecutorService executor;

    /**
     * The callback future.
     */
    protected static ScheduledFuture<?> callbackFuture;

    /**
     * Initializes the Discord RPC.
     *
     * @param applicationId The application ID.
     * @throws IOException If something goes wrong.
     */
    @Override
    public void initialize(long applicationId) throws IOException {
        executor = Executors.newSingleThreadScheduledExecutor();

        Core.initDownload();

        try (CreateParams createParams = new CreateParams()) {
            createParams.setClientID(applicationId);

            if (!adapters.isEmpty()) {
                adapters.forEach(createParams::registerEventHandler);
            }

            try {
                coreInstance = new Core(createParams);
                callbackFuture = executor.scheduleAtFixedRate(coreInstance::runCallbacks, 0, 16, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new IOException("Failed to create Discord RPC instance", e);
            }
        } catch (Exception e) {
            throw new IOException("Failed to create Discord RPC instance", e);
        }
    }

    /**
     * Updates the activity.
     *
     * @param activity The activity.
     * @param callback The callback.
     */
    @Override
    public void updateActivity(Activity activity, Consumer<Result> callback) {
        if (callback == null) {
            callback = result -> {};
        }

        coreInstance.activityManager().updateActivity(activity, callback);
    }

    /**
     * Adds an event handler.
     * NOTE: Add event handlers before initializing the Discord RPC.
     *
     * @param adapter The adapter.
     */
    @Override
    public void addEventHandler(DiscordEventAdapter adapter) {
        adapters.add(adapter);
    }

    /**
     * Shuts down the Discord RPC.
     */
    @Override
    public void shutdown() {
        callbackFuture.cancel(true);
        executor.shutdownNow();
        coreInstance.close();
    }

}
