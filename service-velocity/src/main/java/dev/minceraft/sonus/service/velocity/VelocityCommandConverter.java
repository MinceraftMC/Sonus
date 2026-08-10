package dev.minceraft.sonus.service.velocity;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.commands.ArgumentCommandNode;
import dev.minceraft.sonus.service.commands.CommandContext;
import dev.minceraft.sonus.service.commands.CommandException;
import dev.minceraft.sonus.service.commands.CommandNode;
import dev.minceraft.sonus.service.commands.CommandSender;
import dev.minceraft.sonus.service.commands.LiteralCommandNode;
import dev.minceraft.sonus.service.commands.arguments.EnumArgument;
import dev.minceraft.sonus.service.player.SonusPlayer;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.velocitypowered.api.command.BrigadierCommand.literalArgumentBuilder;
import static com.velocitypowered.api.command.BrigadierCommand.requiredArgumentBuilder;

@NullMarked
public final class VelocityCommandConverter {

    private final SonusService service;
    private final Object plugin;
    private final CommandManager commands;

    public VelocityCommandConverter(SonusService service, Object plugin, CommandManager commands) {
        this.service = service;
        this.plugin = plugin;
        this.commands = commands;
    }

    public void registerCommand(LiteralCommandNode rootNode) {
        CommandMeta meta = this.commands.metaBuilder(rootNode.getName()).plugin(this.plugin).build();
        this.commands.register(meta, this.convertRootNode(rootNode));
    }

    public BrigadierCommand convertRootNode(LiteralCommandNode node) {
        LiteralArgumentBuilder<CommandSource> builder = literalArgumentBuilder(node.getName());
        this.convertNode(new LinkedList<>(List.of(node)), new LinkedList<>(), builder);
        return new BrigadierCommand(builder);
    }

    private void convertChild(
            List<CommandNode> nodes,
            List<Map.Entry<ArgumentCommandNode<?>, String>> enumValues,
            CommandNode node, ArgumentBuilder<CommandSource, ?> brigNode
    ) {
        if (node instanceof LiteralCommandNode literal) {
            brigNode.then(this.convertNode(nodes, enumValues, literalArgumentBuilder(literal.getName())));
            return;
        }
        if (!(node instanceof ArgumentCommandNode<?> arg)) {
            throw new UnsupportedOperationException(String.valueOf(node));
        }

        if (arg.getType() instanceof EnumArgument<?> argType) {
            // special argument type, create literals for all values
            for (Enum<?> value : argType.getValues()) {
                String name = value.name().toLowerCase(Locale.ROOT);
                enumValues.addLast(Map.entry(arg, name));
                brigNode.then(this.convertNode(nodes, enumValues, literalArgumentBuilder(name)));
                enumValues.removeLast();
            }
            return;
        }

        ArgumentType<?> brigArgType = switch (arg.getType().getType()) {
            case WORD -> StringArgumentType.word();
            case GREEDY -> StringArgumentType.greedyString();
            case STRING -> StringArgumentType.string();
            default -> throw new UnsupportedOperationException();
        };
        RequiredArgumentBuilder<CommandSource, ?> builder = requiredArgumentBuilder(arg.getName(), brigArgType);
        brigNode.then(this.convertNode(nodes, enumValues, builder));
    }

    private void parseAncestor(
            CommandNode ancestor, CommandContext sctx,
            Map<ArgumentCommandNode<?>, String> enumValues,
            com.mojang.brigadier.context.CommandContext<CommandSource> ctx
    ) {
        if (!(ancestor instanceof ArgumentCommandNode<?> argAnc)) {
            return; // nothing to parse
        }
        String argVal = argAnc.getType() instanceof EnumArgument<?>
                ? enumValues.get(ancestor)
                : ctx.getArgument(ancestor.getName(), String.class);
        ancestor.parse(sctx, argVal);
    }

    private ArgumentBuilder<CommandSource, ?> convertNode(
            List<CommandNode> nodes,
            List<Map.Entry<ArgumentCommandNode<?>, String>> enumValues,
            ArgumentBuilder<CommandSource, ?> builder
    ) {
        CommandNode node = nodes.getLast();
        if (node.hasRequirement()) {
            builder.requires(source -> node.checkRequirement(this.createContext(source)));
        }
        if (node.hasExecutor()) {
            List<CommandNode> nodesCopy = List.copyOf(nodes);
            @SuppressWarnings("unchecked") // generic array creation
            Map<ArgumentCommandNode<?>, String> enumValueMap = Map.ofEntries(enumValues.toArray(new Map.Entry[0]));

            builder.executes(ctx -> {
                try {
                    CommandContext sctx = this.createContext(ctx.getSource());
                    // iterate through ancestor nodes and extract all arguments
                    for (CommandNode ancestor : nodesCopy) {
                        this.parseAncestor(ancestor, sctx, enumValueMap, ctx);
                    }

                    // execute node with all the provided context
                    return node.execute(sctx) ? Command.SINGLE_SUCCESS : 0;
                } catch (CommandException exception) {
                    if (exception.getComponent() == null) {
                        throw exception; // rethrow as is
                    }
                    Message message = VelocityBrigadierMessage.tooltip(exception.getComponent());
                    throw new CommandSyntaxException(
                            new SimpleCommandExceptionType(message),
                            message
                    );
                }
            });
        }
        // recursively convert all children
        for (CommandNode child : node.getChildren()) {
            nodes.addLast(child);
            this.convertChild(nodes, enumValues, child, builder);
            nodes.removeLast();
        }
        return builder;
    }

    private CommandContext createContext(CommandSource source) {
        return new CommandContext(this.service, this.convertCommandSource(source));
    }

    private CommandSender convertCommandSource(CommandSource source) {
        if (source instanceof Player player) {
            SonusPlayer sonusPlayer = this.service.getPlayerManager().getPlayer(player.getUniqueId());
            if (sonusPlayer == null) {
                // players are dynamically created
                throw new IllegalStateException("Can't get sonus player instance for " + player);
            }
            return sonusPlayer;
        } else if (source instanceof ConsoleCommandSource console) {
            return new ConsoleCommandSender(console);
        }
        throw new IllegalArgumentException("Unsupported CommandSource type: " + source.getClass().getName());
    }

    private record ConsoleCommandSender(ConsoleCommandSource source) implements CommandSender {

        @Override
        public boolean hasPermission(String permission, boolean defaultValue) {
            Tristate permissionValue = this.source.getPermissionValue(permission);
            if (permissionValue != Tristate.UNDEFINED) {
                return permissionValue.asBoolean();
            }
            return defaultValue;
        }

        @Override
        public void sendMessage(Component component) {
            this.source.sendMessage(component);
        }

        @Override
        public String getNameFor(ISonusPlayer target) {
            return target.getName();
        }

        @Override
        public UUID getUniqueIdFor(ISonusPlayer target) {
            return target.getUniqueId();
        }
    }
}
