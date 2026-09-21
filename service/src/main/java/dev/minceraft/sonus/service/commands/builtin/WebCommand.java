package dev.minceraft.sonus.service.commands.builtin;

import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.commands.Command;
import dev.minceraft.sonus.service.commands.CommandExecutor;
import dev.minceraft.sonus.service.commands.LiteralCommandNode;
import dev.minceraft.sonus.service.participant.SonusPlayer;
import dev.minceraft.sonus.web.adapter.WebAdapter;
import dev.minceraft.sonus.web.adapter.config.WebConfig;
import org.jspecify.annotations.NullMarked;

import static dev.minceraft.sonus.service.commands.CommandNode.literal;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.ClickEvent.openUrl;

@NullMarked
public class WebCommand extends Command {

    public WebCommand() {
        super("web");
    }

    static void tryToRegister(SonusService service, SonusCommand rootCommand, LiteralCommandNode rootNode) {
        WebAdapter webAdapter = service.getAdapters().getAdapter(WebAdapter.class);
        if (webAdapter != null && webAdapter.getAdapterInfo().enabled()) {
            WebCommand webCommand = new WebCommand();
            rootNode.with(webCommand.construct());

            WebConfig webConfig = service.getConfig().getSubConfig(WebConfig.class);
            if (!webConfig.useRootCommand) {
                return;
            }

            CommandExecutor rootExecutor = rootNode.getExecutor();
            if (rootExecutor == null) {
                return;
            }

            // Inject into root command, run web command if root command don't handle it
            rootNode.executes(ctx -> rootCommand.execute((SonusPlayer) ctx.sender()) ||
                    webCommand.run(ctx.service(), (SonusPlayer) ctx.sender())
            );
        }
    }

    @Override
    public LiteralCommandNode construct() {
        return literal(this.label)
                .requires(ctx -> ctx.sender().hasPermission("sonus.command.web")
                        && ctx.sender() instanceof SonusPlayer player && !player.isConnected())
                .executes(ctx -> this.run(ctx.service(), (SonusPlayer) ctx.sender()));
    }

    boolean run(SonusService service, SonusPlayer player) {
        WebAdapter adapter = service.getAdapters().getAdapter(WebAdapter.class);
        if (adapter == null || !adapter.getAdapterInfo().enabled()) {
            return false;
        }
        String linkPattern = service.getConfig().getSubConfig(WebConfig.class).linkPattern;
        String token = adapter.getSessions().generateToken(player);
        String url = String.format(linkPattern, token);

        player.sendMessage(translatable("sonus.command.sonus.web.token")
                .clickEvent(openUrl(url))
                .arguments(text(url)));
        return true;
    }
}
