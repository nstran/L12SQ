package l12sq.server.runtime;

import java.util.LinkedHashSet;
import java.util.Set;

public final class GameSession {
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

    public GameSession(String channel) {
        this.channel = channel;
    }

    public String channel() {
        return channel;
    }

    public boolean authenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean awaitingCharacterCreation() {
        return awaitingCharacterCreation;
    }

    public void setAwaitingCharacterCreation(boolean awaitingCharacterCreation) {
        this.awaitingCharacterCreation = awaitingCharacterCreation;
    }

    public boolean createCharacterOptionsSent() {
        return createCharacterOptionsSent;
    }

    public void setCreateCharacterOptionsSent(boolean createCharacterOptionsSent) {
        this.createCharacterOptionsSent = createCharacterOptionsSent;
    }

    public String username() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Integer> pendingResourceAnnouncements() {
        return pendingResourceAnnouncements;
    }

    public Set<Integer> preloadedResources() {
        return preloadedResources;
    }

    public Integer activeInstallResourceId() {
        return activeInstallResourceId;
    }

    public void setActiveInstallResourceId(Integer activeInstallResourceId) {
        this.activeInstallResourceId = activeInstallResourceId;
    }

    public boolean installManifestSent() {
        return installManifestSent;
    }

    public void setInstallManifestSent(boolean installManifestSent) {
        this.installManifestSent = installManifestSent;
    }

    public String currentMapName() {
        return currentMapName;
    }

    public void setCurrentMapName(String currentMapName) {
        this.currentMapName = currentMapName;
    }

    public int currentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(int currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    public boolean sceneReady() {
        return sceneReady;
    }

    public void setSceneReady(boolean sceneReady) {
        this.sceneReady = sceneReady;
    }
}
