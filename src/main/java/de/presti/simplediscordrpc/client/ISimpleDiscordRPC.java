package de.presti.simplediscordrpc.client;

import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.activity.Activity;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Simple Discord RPC interface.
 */
public interface ISimpleDiscordRPC {

    /**
     * Initializes the Discord RPC.
     *
     * @param applicationId The application ID.
     * @throws IOException If something goes wrong.
     */
    void initialize(long applicationId) throws IOException;

    /**
     * Updates the activity.
     *
     * @param activity The activity.
     * @param callback The callback.
     */
    void updateActivity(Activity activity, Consumer<Result> callback);

    /**
     * Adds an event handler.
     * NOTE: Add event handlers before initializing the Discord RPC.
     *
     * @param adapter The adapter.
     */
    void addEventHandler(DiscordEventAdapter adapter);

    /**
     * Shuts down the Discord RPC.
     */
    void shutdown();

}
