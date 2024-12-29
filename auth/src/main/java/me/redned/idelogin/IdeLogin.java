package me.redned.idelogin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.lenni0451.commons.httpclient.HttpClient;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepCredentialsMsaCode;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;
import net.raphimc.minecraftauth.util.logging.ILogger;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class IdeLogin {
    private static final ILogger LOGGER = MinecraftAuth.LOGGER;

    public static CompletableFuture<AuthResult> login(boolean saveToken, @Nullable Credentials credentials) {
        HttpClient httpClient = MinecraftAuth.createHttpClient();
        AbstractStep<?, StepFullJavaSession.FullJavaSession> loginStep;
        if (credentials != null) {
            loginStep = MinecraftAuth.JAVA_CREDENTIALS_LOGIN;
        } else {
            loginStep = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN;
        }

        Path sessionPath = Paths.get("session.json");
        CompletableFuture<StepFullJavaSession.FullJavaSession> sessionFuture = CompletableFuture.supplyAsync(() -> {
            StepFullJavaSession.FullJavaSession session;
            if (saveToken && Files.exists(sessionPath)) {
                LOGGER.info("Loading login session from file...");
                try {
                    session = loginStep.fromJson(JsonParser.parseString(Files.readString(sessionPath)).getAsJsonObject());

                    if (session.isExpired()) {
                        LOGGER.info("Session is expired or outdated, refreshing.");
                        StepFullJavaSession.FullJavaSession refreshedSession = loginStep.refresh(httpClient, session);
                        if (refreshedSession != null) {
                            session = refreshedSession;
                        }
                    } else {
                        LOGGER.info("Session is still valid; loaded from file successfully!");
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to load session from file!");
                    throw new RuntimeException(e);
                }
            } else {
                LOGGER.info("No session found; starting new login...");
                if (credentials != null) {
                    try {
                        session = loginStep.getFromInput(httpClient, new StepCredentialsMsaCode.MsaCredentials(credentials.email(), credentials.password()));
                    } catch (Exception e) {
                        LOGGER.error("Failed to start new login session!");
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        session = loginStep.getFromInput(httpClient, new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
                            // Method to generate a verification URL and a code for the user to enter on that page
                            LOGGER.info("Go to " + msaDeviceCode.getDirectVerificationUri() + " and log into your Microsoft account.");
                            LOGGER.info("Upon logging in successfully, the Minecraft client will resume it's loading and log you in.");

                            // TODO: Open the URL automatically?
                        }));
                    } catch (Exception e) {
                        LOGGER.error("Failed to start new login session!");
                        throw new RuntimeException(e);
                    }
                }
            }

            if (saveToken) {
                JsonObject jsonObject = loginStep.toJson(session);
                try {
                    Files.write(sessionPath, jsonObject.toString().getBytes());
                } catch (IOException e) {
                    LOGGER.error("Failed to save session to file!");
                    throw new RuntimeException(e);
                }
            }

            return session;
        });


        return sessionFuture.thenApply(session -> {
            String accessToken = session.getMcProfile().getMcToken().getAccessToken();
            String[] split = accessToken.split("\\.");
            byte[] jsonBytes = Base64.getUrlDecoder().decode(split[1]);
            JsonObject json = JsonParser.parseString(new String(jsonBytes, StandardCharsets.UTF_8)).getAsJsonObject();

            return new AuthResult(
                    session.getMcProfile().getName(),
                    session.getMcProfile().getId(),
                    json.get("xuid").getAsString(),
                    accessToken
            );
        });
    }

    public static String[] removeArgs(String[] args, List<String> argNames) {
        List<String> argList = new ArrayList<>(List.of(args));
        int i = 0;

        // Iterate through the list and remove matching arguments
        while (i < argList.size()) {
            String currentArg = argList.get(i);

            if (argNames.contains(currentArg)) {
                // If currentArg is in argNames, remove the argument name and its value (if it exists)
                argList.remove(i); // Remove the argument name
                if (i < argList.size()) {
                    argList.remove(i); // Remove the argument value at the same index
                }
            } else {
                // Move to the next element
                i++;
            }
        }

        // Convert the list back to an array and return
        return argList.toArray(new String[0]);
    }
}
