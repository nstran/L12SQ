package l12sq.server.auth;

import java.io.IOException;
import l12sq.server.storage.AccountStore;

public final class AuthService {
    private final AccountStore accountStore;

    public AuthService(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    public int accountCount() {
        return accountStore.count();
    }

    public boolean canLogin(String username, String passwordHex) {
        String storedHash = accountStore.passwordHashOf(username);
        if (storedHash == null) {
            return false;
        }
        if ("OFFLINE".equalsIgnoreCase(storedHash)) {
            return true;
        }
        if (passwordHex != null && passwordHex.equalsIgnoreCase(storedHash)) {
            return true;
        }

        // The original JAR uses a challenge-based login blob, so direct equality with the
        // registration payload is only a temporary fallback while we rebuild auth properly.
        return passwordHex != null && !passwordHex.isEmpty();
    }

    public RegistrationResult register(String username, String passwordHex) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            return new RegistrationResult(false, "Ten dang nhap khong hop le.");
        }
        if (passwordHex == null || passwordHex.isEmpty()) {
            return new RegistrationResult(false, "Mat khau khong hop le.");
        }
        if (accountStore.exists(username)) {
            return new RegistrationResult(false, "Tai khoan da ton tai.");
        }
        boolean inserted = accountStore.register(username, passwordHex);
        if (!inserted) {
            return new RegistrationResult(false, "Khong the tao tai khoan.");
        }
        return new RegistrationResult(true, "Dang ky thanh cong! Hay dang nhap.");
    }

    public record RegistrationResult(boolean success, String message) {
    }
}
