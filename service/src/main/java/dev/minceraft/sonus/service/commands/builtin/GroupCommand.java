package dev.minceraft.sonus.service.commands.builtin;

import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.common.participant.builtin.IRoom;
import dev.minceraft.sonus.common.participant.builtin.RoomAudioType;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.commands.Command;
import dev.minceraft.sonus.service.commands.CommandSender;
import dev.minceraft.sonus.service.commands.LiteralCommandNode;
import dev.minceraft.sonus.service.commands.arguments.PlayerArgument;
import dev.minceraft.sonus.service.commands.arguments.RoomTypeArgument;
import dev.minceraft.sonus.service.commands.arguments.StringArgument;
import dev.minceraft.sonus.service.player.SonusPlayer;
import dev.minceraft.sonus.service.rooms.TransientStaticRoom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static dev.minceraft.sonus.common.SonusConstants.PERMISSION_GROUPS_BYPASS_PASSWORD;
import static dev.minceraft.sonus.common.SonusConstants.PERMISSION_GROUPS_USE;
import static dev.minceraft.sonus.service.commands.CommandNode.argument;
import static dev.minceraft.sonus.service.commands.CommandNode.literal;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@NullMarked
public class GroupCommand extends Command {

    public GroupCommand() {
        super("groups");
    }

    public static Component getGroupSizeHoverComponent(CommandSender sender, IRoom room) {
        List<Component> lines = new ArrayList<>(room.getMembers().size());
        for (ISonusPlayer member : room.getMembers()) {
            lines.add(translatable("sonus.command.hover.group-members.entry", text(sender.getNameFor(member))));
        }
        return translatable("sonus.command.hover.group-members.header").arguments(text(room.getName()), text(room.getMembers().size()))
                .append(Component.join(JoinConfiguration.commas(true), lines));
    }

    @Override
    public LiteralCommandNode construct() {
        return literal(this.label)
                .requires(ctx -> ctx.sender().hasPermission("sonus.command.groups")
                        && ctx.sender().hasPermission(PERMISSION_GROUPS_USE, true) // Check if player can use groups
                        && ctx.sender() instanceof SonusPlayer player && player.isConnected())
                .with(literal("list")
                        .requires(ctx -> ctx.sender().hasPermission("sonus.command.groups.list")
                                && ctx.sender() instanceof SonusPlayer player && player.getPrimaryRoom() == null)
                        .executes(ctx -> this.listGroups(ctx.service(), (SonusPlayer) ctx.sender())))
                .with(literal("create")
                        .requires(ctx -> ctx.sender().hasPermission("sonus.command.groups.create") &&
                                ((SonusPlayer) ctx.sender()).getPrimaryRoom() == null)
                        .with(argument("name", StringArgument.INSTANCE)
                                .requires(ctx -> ctx.sender().hasPermission("sonus.command.groups.create.passwordless"))
                                .with(argument("type", RoomTypeArgument.INSTANCE)
                                        .executes(ctx -> {
                                            SonusPlayer player = (SonusPlayer) ctx.sender();
                                            String name = ctx.get("name", StringArgument.INSTANCE);
                                            RoomAudioType type = ctx.get("type", RoomTypeArgument.INSTANCE);

                                            return this.createGroup(ctx.service(), player, name, type, null);
                                        })
                                        .with(argument("password", StringArgument.INSTANCE)
                                                .requires(ctx -> ctx.sender().hasPermission("sonus.command.groups.create.password"))
                                                .executes(ctx -> {
                                                    SonusPlayer player = (SonusPlayer) ctx.sender();
                                                    String name = ctx.get("name", StringArgument.INSTANCE);
                                                    String password = ctx.get("password", StringArgument.INSTANCE);
                                                    RoomAudioType type = ctx.get("type", RoomTypeArgument.INSTANCE);

                                                    return this.createGroup(ctx.service(), player, name, type, password);
                                                }))))
                ).with(literal("join")
                        .requires(ctx -> ctx.sender().hasPermission("sonus.command.groups.join") &&
                                ((SonusPlayer) ctx.sender()).getPrimaryRoom() == null)
                        .with(argument("name", StringArgument.INSTANCE)
                                .with(argument("password", StringArgument.INSTANCE)
                                        .executes(ctx -> {
                                                    SonusPlayer player = (SonusPlayer) ctx.sender();
                                                    String name = ctx.get("name", StringArgument.INSTANCE);
                                                    String password = ctx.get("password", StringArgument.INSTANCE);

                                                    return this.joinGroup(ctx.service(), player, name, password);
                                                }
                                        )
                                ).executes(ctx -> {
                                    SonusPlayer player = (SonusPlayer) ctx.sender();
                                    String name = ctx.get("name", StringArgument.INSTANCE);

                                    return this.joinGroup(ctx.service(), player, name, null);
                                })))
                .with(literal("invite")
                        .requires(ctx -> ctx.sender().hasPermission("sonus.command.groups.invite") &&
                                ((SonusPlayer) ctx.sender()).getPrimaryRoom() != null)
                        .with(argument("player", PlayerArgument.INSTANCE)
                                .executes(ctx -> invite(ctx.service(), (SonusPlayer) ctx.sender(),
                                        ctx.get("player", PlayerArgument.INSTANCE)))))
                .with(literal("leave")
                        .requires(ctx -> ctx.sender().hasPermission("sonus.command.groups.leave") &&
                                ((SonusPlayer) ctx.sender()).getPrimaryRoom() != null)
                        .executes(ctx -> this.leaveGroup((SonusPlayer) ctx.sender())));
    }

    private boolean listGroups(SonusService service, SonusPlayer player) {
        Collection<IRoom> rooms = service.getRoomManager().getRooms(TransientStaticRoom.class);
        if (rooms.isEmpty()) {
            player.sendMessage(translatable("sonus.command.groups.list.empty"));
        } else {
            ComponentBuilder<?, ?> message = translatable()
                    .key("sonus.command.groups.list.header")
                    .arguments(text(rooms.size()));

            List<Component> lines = new ArrayList<>(rooms.size());
            for (IRoom room : rooms) {
                if (room.getPassword() == null || player.hasPermission(PERMISSION_GROUPS_BYPASS_PASSWORD)) {
                    lines.add(translatable("sonus.command.groups.list.entry.no-password",
                            text(room.getName()), text(room.getMembers().size())
                                    .hoverEvent(HoverEvent.showText(getGroupSizeHoverComponent(player, room))))
                            .clickEvent(ClickEvent.callback(__ -> this.joinGroup(service, player, room.getUniqueId(), null)))
                    );
                } else {
                    lines.add(translatable("sonus.command.groups.list.entry.password",
                            text(room.getName()), text(room.getMembers().size())
                                    .hoverEvent(HoverEvent.showText(getGroupSizeHoverComponent(player, room)))));
                }
            }

            player.sendMessage(message.append(Component.join(JoinConfiguration.newlines(), lines)).build());
        }

        return true;
    }

    private boolean leaveGroup(SonusPlayer player) {
        player.setPrimaryRoom(null);
        player.sendMessage(translatable("sonus.command.groups.leave.success"));
        return true;
    }

    private boolean invite(SonusService service, SonusPlayer player, SonusPlayer target) {
        IRoom room = player.getPrimaryRoom();
        if (room == null) {
            throw new IllegalStateException("Player is not in a room"); // should not happen due to command requirement
        }
        if (room.isMember(target)) {
            player.sendMessage(translatable("sonus.command.groups.invite.already_member")
                    .arguments(text(target.getName(player))));
            return true;
        }
        player.sendMessage(translatable("sonus.command.groups.invite.success")
                .arguments(text(target.getName(player))));
        target.sendMessage(translatable("sonus.command.groups.invite.invitation")
                .arguments(text(player.getName(target)), text(room.getName()))
                .clickEvent(ClickEvent.callback(__ -> this.joinGroup(service, target, room.getUniqueId(), room.getPassword()))));
        return true;
    }

    private boolean createGroup(SonusService service, SonusPlayer player, String name, RoomAudioType type, @Nullable String password) {
        IRoom room = service.getRoomManager().createStaticRoom(name, password, type, false);
        this.joinGroup(service, player, room.getUniqueId(), password);
        player.sendMessage(translatable("sonus.command.groups.create.success", text(name)));
        return true;
    }

    private boolean joinGroup(SonusService service, SonusPlayer player, String name, @Nullable String password) {
        Set<IRoom> matched = new HashSet<>();
        for (IRoom room : service.getRoomManager().getRooms()) {
            if (room.getName().equalsIgnoreCase(name)) {
                matched.add(room);
            }
        }

        if (matched.isEmpty()) {
            player.sendMessage(translatable("sonus.command.groups.join.not_found", text(name)));
        } else if (matched.size() == 1) {
            IRoom room = matched.iterator().next();
            this.joinGroup(service, player, room.getUniqueId(), password);
        } else {
            ComponentBuilder<?, ?> message = translatable()
                    .key("sonus.command.groups.join.multiple")
                    .arguments(text(matched.size()));

            List<Component> lines = new ArrayList<>(matched.size());
            for (IRoom iRoom : matched) {
                lines.add(translatable("sonus.command.groups.join.multiple.entry")
                        .arguments(text(iRoom.getName()), text(iRoom.getMembers().size())
                                .hoverEvent(HoverEvent.showText(getGroupSizeHoverComponent(player, iRoom))))
                        .clickEvent(ClickEvent.callback(__ -> this.joinGroup(service, player, name, password)))
                );
            }
            player.sendMessage(message.append(Component.join(JoinConfiguration.newlines(), lines)).build());
        }

        return true;
    }

    private boolean joinGroup(SonusService service, SonusPlayer player, UUID roomId, @Nullable String password) {
        IRoom room = service.getRoomManager().getRoom(roomId);
        boolean success = room != null && player.canAccessRoom(room, password);
        if (success) {
            player.setPrimaryRoom(room);
            player.sendMessage(translatable("sonus.command.groups.join.success",
                    text(room.getName())));
        } else {
            player.sendMessage(translatable("sonus.command.groups.join.failure"));
        }
        return true;
    }
}
