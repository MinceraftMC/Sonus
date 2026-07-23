package dev.minceraft.sonus.service.commands.builtin;

import dev.minceraft.sonus.service.adapter.SonusAdapter;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.commands.Command;
import dev.minceraft.sonus.service.commands.LiteralCommandNode;
import dev.minceraft.sonus.service.player.SonusPlayer;
import org.jspecify.annotations.NullMarked;

import static dev.minceraft.sonus.service.commands.CommandNode.literal;
import static net.kyori.adventure.text.Component.translatable;

@NullMarked
public class SonusCommand extends Command {

    private final SonusService service;

    public SonusCommand(SonusService service) {
        super("sonus", "audio", "voice", "voicechat");
        this.service = service;
    }

    @Override
    public LiteralCommandNode construct() {
        LiteralCommandNode command = literal(this.getLabel())
                .requires(ctx -> ctx.sender().hasPermission("sonus.command", true)
                        && ctx.sender() instanceof SonusPlayer)
                .executes(ctx -> this.execute0((SonusPlayer) ctx.sender()));
        command.with(new GroupCommand().construct());
        WebCommand.tryToRegister(this.service, this, command);
        ModerateCommand.tryToRegister(this.service, command);

        return command;
    }

    private boolean execute0(SonusPlayer player) {
        if (!this.execute(player)) {
            player.sendMessage(translatable("sonus.command.sonus.not-connected"));
        }
        return true;
    }

    boolean execute(SonusPlayer player) {
        SonusAdapter adapter = player.getAdapter();
        if (adapter != null) {
            player.sendMessage(translatable("sonus.command.sonus.connected-info")
                    .arguments(translatable("sonus.adapter." + adapter.getAdapterInfo().id() + ".name")));
            return true; // already connecting/connected
        }
        return false;
    }
}
