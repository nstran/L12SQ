package l12sq.server.runtime;

import java.util.LinkedHashSet;
import java.util.Set;

final class GameSession {
    private final String channel;
    private boolean authenticated;
    private boolean awaitingCharacterCreation;
    private boolean createCharacterOptionsSent;
    private String username;
    private final Set<Integer> pendingResourceAnnouncements = new LinkedHashSet<>();
    private final Set<Integer> preloadedResources = new LinkedHashSet<>();
    private Integer activeInstallResourceId;
    private boolean installManifestSent;
    private String currentMapName;
    private int currentRoomId;
    private boolean sceneReady;

    GameSession(String channel) {
        this.channel = channel;
    }

    String channel() {
        return channel;
    }

    boolean authenticated() {
        return authenticated;
    }

    void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    boolean awaitingCharacterCreation() {
        return awaitingCharacterCreation;
    }

    void setAwaitingCharacterCreation(boolean awaitingCharacterCreation) {
        this.awaitingCharacterCreation = awaitingCharacterCreation;
    }

    boolean createCharacterOptionsSent() {
        return createCharacterOptionsSent;
    }

    void setCreateCharacterOptionsSent(boolean createCharacterOptionsSent) {
        this.createCharacterOptionsSent = createCharacterOptionsSent;
    }

    String username() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    Set<Integer> pendingResourceAnnouncements() {
        return pendingResourceAnnouncements;
    }

    Set<Integer> preloadedResources() {
        return preloadedResources;
    }

    Integer activeInstallResourceId() {
        return activeInstallResourceId;
    }

    void setActiveInstallResourceId(Integer activeInstallResourceId) {
        this.activeInstallResourceId = activeInstallResourceId;
    }

    boolean installManifestSent() {
        return installManifestSent;
    }

    void setInstallManifestSent(boolean installManifestSent) {
        this.installManifestSent = installManifestSent;
    }

    String currentMapName() {
        return currentMapName;
    }

    void setCurrentMapName(String currentMapName) {
        this.currentMapName = currentMapName;
    }

    int currentRoomId() {
        return currentRoomId;
    }

    void setCurrentRoomId(int currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    boolean sceneReady() {
        return sceneReady;
    }

    void setSceneReady(boolean sceneReady) {
        this.sceneReady = sceneReady;
    }
}
