package dev.minceraft.sonus.service.commands.builtin;

import dev.minceraft.sonus.service.data.ISonusPlayer;
import dev.minceraft.sonus.service.rooms.IRoom;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.commands.Command;
import dev.minceraft.sonus.service.commands.CommandSender;
import dev.minceraft.sonus.service.commands.LiteralCommandNode;
import dev.minceraft.sonus.service.commands.arguments.PlayerArgument;
import dev.minceraft.sonus.service.commands.arguments.StringArgument;
import dev.minceraft.sonus.service.player.SonusPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.util.TriState;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static dev.minceraft.sonus.service.SonusConstants.PERMISSION_CONNECT;
import static dev.minceraft.sonus.service.SonusConstants.PERMISSION_GROUPS_USE;
import static dev.minceraft.sonus.service.SonusConstants.PERMISSION_VOICE_SPEAK;
import static dev.minceraft.sonus.service.commands.CommandNode.argument;
import static dev.minceraft.sonus.service.commands.CommandNode.literal;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@NullMarked
public class ModerateCommand extends Command {

    public ModerateCommand() {
        super("moderate");
    }

    static void tryToRegister(SonusService service, LiteralCommandNode node) {
        if (service.getPlatform().isPermissionSettingSupported()) {
            node.with(new ModerateCommand().construct());
        }
    }

    @Override
    public LiteralCommandNode construct() {
        return literal(this.label)
                .requires(ctx -> ctx.sender().hasPermission("sonus.command.moderate"))
                .with(literal("mute")
                        .requires(ctx -> ctx.sender().hasPermission("sonus.command.moderate.mute"))
                        .with(argument("target", PlayerArgument.INSTANCE)
                                .executes(ctx -> this.mute(ctx.sender(), ctx.get("target", PlayerArgument.INSTANCE)))))
                .with(literal("unmute")
                        .requires(ctx -> ctx.sender().hasPermission("sonus.command.moderate.unmute"))
                        .with(argument("target", PlayerArgument.INSTANCE)
                                .executes(ctx -> this.unmute(ctx.sender(), ctx.get("target", PlayerArgument.INSTANCE)))))
                .with(literal("ban")
                        .requires(ctx -> ctx.sender().hasPermission("sonus.command.moderate.ban"))
                        .with(argument("target", PlayerArgument.INSTANCE)
                                .executes(ctx -> this.ban(ctx.service(), ctx.sender(), ctx.get("target", PlayerArgument.INSTANCE)))))
                .with(literal("unban")
                        .requires(ctx -> ctx.sender().hasPermission("sonus.command.moderate.unban"))
                        .with(argument("target", PlayerArgument.INSTANCE)
                                .executes(ctx -> this.unban(ctx.sender(), ctx.get("target", PlayerArgument.INSTANCE)))))
                .with(literal("groups")
                        .requires(ctx -> ctx.sender().hasPermission("sonus.command.moderate.groups"))
                        .with(literal("remove")
                                .requires(ctx -> ctx.sender().hasPermission("sonus.command.moderate.groups.remove"))
                                .with(argument("group", StringArgument.INSTANCE)
                                        .executes(ctx -> this.groupsRemove(ctx.service(), ctx.sender(), ctx.get("group", StringArgument.INSTANCE)))))
                        .with(literal("kick")
                                .requires(ctx -> ctx.sender().hasPermission("sonus.command.moderate.groups.kick"))
                                .with(argument("target", PlayerArgument.INSTANCE)
                                        .executes(ctx -> this.groupsKick(ctx.sender(), ctx.get("target", PlayerArgument.INSTANCE)))))
                        .with(literal("ban")
                                .requires(ctx -> ctx.sender().hasPermission("sonus.command.moderate.groups.ban"))
                                .with(argument("target", PlayerArgument.INSTANCE)
                                        .executes(ctx -> this.groupsBan(ctx.sender(), ctx.get("target", PlayerArgument.INSTANCE)))))
                        .with(literal("unban")
                                .requires(ctx -> ctx.sender().hasPermission("sonus.command.moderate.groups.unban"))
                                .with(argument("target", PlayerArgument.INSTANCE)
                                        .executes(ctx -> this.groupsUnban(ctx.sender(), ctx.get("target", PlayerArgument.INSTANCE))))));

    }

    private boolean mute(CommandSender sender, ISonusPlayer target) {
        boolean muted = !target.hasPermission(PERMISSION_VOICE_SPEAK, true);
        if (muted) {
            sender.sendMessage(translatable("sonus.command.moderate.mute.already-muted")
                    .arguments(text(sender.getNameFor(target))));
            return true;
        }
        target.setPermission(PERMISSION_VOICE_SPEAK, TriState.FALSE);
        target.setMuted(true, true);
        target.updateState();
        sender.sendMessage(translatable("sonus.command.moderate.mute.success")
                .arguments(text(sender.getNameFor(target))));
        target.sendMessage(translatable("sonus.command.moderate.mute.notified"));
        return true;
    }

    private boolean unmute(CommandSender sender, ISonusPlayer target) {
        boolean muted = !target.hasPermission(PERMISSION_VOICE_SPEAK, true);
        if (!muted) {
            sender.sendMessage(translatable("sonus.command.moderate.unmute.not-muted")
                    .arguments(text(sender.getNameFor(target))));
            return true;
        }
        target.setPermission(PERMISSION_VOICE_SPEAK, TriState.NOT_SET);
        target.updateState();
        sender.sendMessage(translatable("sonus.command.moderate.unmute.success")
                .arguments(text(sender.getNameFor(target))));
        target.sendMessage(translatable("sonus.command.moderate.unmute.notified"));
        return true;
    }

    private boolean ban(SonusService service, CommandSender sender, ISonusPlayer target) {
        boolean banned = !target.hasPermission(PERMISSION_CONNECT, true);
        if (banned) {
            sender.sendMessage(translatable("sonus.command.moderate.ban.already-banned")
                    .arguments(text(sender.getNameFor(target))));
            return true;
        }
        target.setPermission(PERMISSION_CONNECT, TriState.FALSE);
        target.setPermission(PERMISSION_VOICE_SPEAK, TriState.FALSE);
        service.getEventManager().onPlayerSwitchBackend(target.getUniqueId()); // Simulate server switch to disconnect
        sender.sendMessage(translatable("sonus.command.moderate.ban.success")
                .arguments(text(sender.getNameFor(target))));
        target.sendMessage(translatable("sonus.command.moderate.ban.notified"));
        return true;
    }

    private boolean unban(CommandSender sender, ISonusPlayer target) {
        boolean banned = !target.hasPermission(PERMISSION_CONNECT, true);
        if (!banned) {
            sender.sendMessage(translatable("sonus.command.moderate.unban.not-banned")
                    .arguments(text(sender.getNameFor(target))));
            return true;
        }
        target.setPermission(PERMISSION_CONNECT, TriState.NOT_SET);
        target.setPermission(PERMISSION_VOICE_SPEAK, TriState.NOT_SET);
        sender.sendMessage(translatable("sonus.command.moderate.unban.success")
                .arguments(text(sender.getNameFor(target))));
        target.sendMessage(translatable("sonus.command.moderate.unban.notified"));
        return true;
    }

    private boolean groupsRemove(SonusService service, CommandSender sender, String group) {
        Collection<IRoom> rooms = service.getRoomManager().getRooms();
        List<IRoom> matched = new ArrayList<>();
        for (IRoom room : rooms) {
            if (room.getName().equalsIgnoreCase(group)) {
                matched.add(room);
            }
        }
        if (matched.isEmpty()) {
            sender.sendMessage(translatable("sonus.command.moderate.groups.remove.not-found")
                    .arguments(text(group)));
        } else if (matched.size() == 1) {
            this.groupsRemove0(service, sender, matched.getFirst());
        } else {
            ComponentBuilder<?, ?> builder = translatable()
                    .key("sonus.command.moderate.groups.remove.multiple-found")
                    .arguments(text(group));
            List<Component> entries = new ArrayList<>();
            for (IRoom room : matched) {
                entries.add(translatable("sonus.command.moderate.groups.remove.multiple-found.entry")
                        .arguments(text(room.getName()), text(room.getMembers().size())
                                .hoverEvent(GroupCommand.getGroupSizeHoverComponent(sender, room)))
                        .clickEvent(ClickEvent.callback(__ -> this.groupsRemove0(service, sender, room))));
            }
            builder.append(Component.join(JoinConfiguration.newlines(), entries));
            sender.sendMessage(builder.build());
        }
        return true;
    }

    private void groupsRemove0(SonusService service, CommandSender sender, IRoom room) {
        for (ISonusPlayer member : room.getMembers()) {
            member.setPrimaryRoom(null);
            member.sendMessage(translatable("sonus.command.moderate.groups.remove.notified"));
        }
        service.getRoomManager().removeRoom(room);
        sender.sendMessage(translatable("sonus.command.moderate.groups.remove.success")
                .arguments(text(room.getName())));
    }

    private boolean groupsKick(CommandSender sender, ISonusPlayer target) {
        IRoom primaryRoom = this.kickFromGroup(target);
        if (primaryRoom == null) {
            sender.sendMessage(translatable("sonus.command.moderate.groups.kick.not-in-room")
                    .arguments(text(sender.getNameFor(target))));
            return true;
        }
        sender.sendMessage(translatable("sonus.command.moderate.groups.kick.success")
                .arguments(text(sender.getNameFor(target)), text(primaryRoom.getName())));
        target.sendMessage(translatable("sonus.command.moderate.groups.kick.notified"));
        return true;
    }

    private boolean groupsBan(CommandSender sender, ISonusPlayer target) {
        boolean banned = !target.hasPermission(PERMISSION_GROUPS_USE, true);
        if (banned) {
            sender.sendMessage(translatable("sonus.command.moderate.groups.ban.already-banned")
                    .arguments(text(sender.getNameFor(target))));
            return true;
        }
        this.kickFromGroup(target);

        target.setPermission(PERMISSION_GROUPS_USE, TriState.FALSE);
        sender.sendMessage(translatable("sonus.command.moderate.groups.ban.success")
                .arguments(text(sender.getNameFor(target))));
        target.sendMessage(translatable("sonus.command.moderate.groups.ban.notified"));
        return true;
    }

    private boolean groupsUnban(CommandSender sender, SonusPlayer player) {
        boolean banned = !player.hasPermission(PERMISSION_GROUPS_USE, true);
        if (!banned) {
            sender.sendMessage(translatable("sonus.command.moderate.groups.unban.not-banned")
                    .arguments(text(sender.getNameFor(player))));
            return true;
        }
        player.setPermission(PERMISSION_GROUPS_USE, TriState.NOT_SET);
        sender.sendMessage(translatable("sonus.command.moderate.groups.unban.success")
                .arguments(text(sender.getNameFor(player))));
        player.sendMessage(translatable("sonus.command.moderate.groups.unban.notified"));
        return true;
    }

    @Nullable
    private IRoom kickFromGroup(ISonusPlayer target) {
        IRoom primaryRoom = target.getPrimaryRoom();
        if (primaryRoom == null) {
            return null;
        }
        target.setPrimaryRoom(null);
        return primaryRoom;
    }
}
