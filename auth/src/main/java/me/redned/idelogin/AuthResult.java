package me.redned.idelogin;

import java.util.UUID;

public record AuthResult(String username, UUID uuid, String xuid, String accessToken) {
}
