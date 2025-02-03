package me.redned.idelogin.mixin;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.redned.idelogin.AuthResult;
import me.redned.idelogin.Credentials;
import me.redned.idelogin.IdeLogin;
import net.minecraft.Util;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(Main.class)
public class MainMixin {

    @ModifyVariable(at = @At("LOAD"), method = "main([Ljava/lang/String;)V", argsOnly = true, remap = false)
    private static String[] main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();

        OptionSpec<Boolean> saveTokenSpec = parser.accepts("saveToken").withOptionalArg().ofType(Boolean.class).defaultsTo(true);
        OptionSpec<String> emailSpec = parser.accepts("email").withOptionalArg();
        OptionSpec<String> passwordSpec = parser.accepts("password").withOptionalArg();

        OptionSet optionSet = parser.parse(args);

        boolean saveToken = optionSet.valueOf(saveTokenSpec);

        String email = optionSet.valueOf(emailSpec);
        String password = optionSet.valueOf(passwordSpec);

        CompletableFuture<AuthResult> loginFuture = IdeLogin.login(saveToken, (email == null || password == null) ? null : new Credentials(email, password), Util.backgroundExecutor());
        try {
            AuthResult result = loginFuture.join();
            List<String> newArgs = new ArrayList<>(List.of(IdeLogin.removeArgs(args, List.of("--username", "--uuid", "--xuid", "--accessToken", "--userType", "--email", "--password"))));
            // Remove the args from below (and email/password args)

            newArgs.addAll(List.of(
                    "--username", result.username(),
                    "--uuid", result.uuid().toString(),
                    "--xuid", result.xuid(),
                    "--accessToken", result.accessToken(),
                    "--userType", "msa"
            ));
            return newArgs.toArray(String[]::new);
        } catch (Exception e) {
            System.err.println("An error occurred while logging in: " + e.getMessage());
            e.printStackTrace();
            return args;
        }
    }
}
