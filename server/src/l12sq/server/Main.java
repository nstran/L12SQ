package l12sq.server;

import l12sq.server.config.ServerConfig;
import l12sq.server.runtime.GameServer;
import l12sq.server.storage.JsonAccountStore;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        ServerConfig config = ServerConfig.defaults();
        JsonAccountStore accountStore = new JsonAccountStore(config.accountsFile());
        GameServer server = new GameServer(config, accountStore);
        server.start();
    }
}
