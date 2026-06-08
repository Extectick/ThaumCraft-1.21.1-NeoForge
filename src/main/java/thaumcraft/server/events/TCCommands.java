package thaumcraft.server.events;

import java.util.Locale;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.blockentities.AuraNodeBlockEntity;
import thaumcraft.common.lib.crafting.ObjectAspectRegistry;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.research.ResearchEntry;
import thaumcraft.common.research.ResearchManager;
import thaumcraft.common.research.ResearchRegistry;

public final class TCCommands {
    private TCCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        registerRoot(dispatcher, "thaumcraft");
        registerRoot(dispatcher, "thaum");
        registerRoot(dispatcher, "tc");
    }

    private static void registerRoot(CommandDispatcher<CommandSourceStack> dispatcher, String name) {
        dispatcher.register(Commands.literal(name)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("help")
                        .executes(TCCommands::help))
                .then(Commands.literal("aspects")
                        .then(Commands.literal("hand")
                                .executes(TCCommands::showHeldItemAspects)))
                .then(Commands.literal("node")
                        .then(Commands.literal("create")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                java.util.Arrays.stream(NodeType.values())
                                                        .map(type -> type.name().toLowerCase(Locale.ROOT)),
                                                builder))
                                        .executes(context -> createNode(context, "none"))
                                        .then(Commands.argument("modifier", StringArgumentType.word())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                        java.util.stream.Stream.concat(
                                                                java.util.stream.Stream.of("none"),
                                                                java.util.Arrays.stream(NodeModifier.values())
                                                                        .map(modifier -> modifier.name()
                                                                                .toLowerCase(Locale.ROOT))),
                                                        builder))
                                                .executes(context -> createNode(context,
                                                        StringArgumentType.getString(context, "modifier")))))))
                .then(Commands.literal("research")
                        .then(Commands.literal("list")
                                .executes(TCCommands::listResearch))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.literal("all")
                                        .executes(TCCommands::giveAllResearch))
                                .then(Commands.literal("reset")
                                        .executes(TCCommands::resetResearch))
                                .then(Commands.argument("research", StringArgumentType.word())
                                        .suggests(TCCommands::suggestResearch)
                                        .executes(TCCommands::giveResearch)))));
    }

    private static int help(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal("§3You can also use /thaum or /tc instead of /thaumcraft."), false);
        source.sendSuccess(() -> Component.literal("§3Use this to give research to a player."), false);
        source.sendSuccess(() -> Component.literal("  /thaumcraft research <list|player> <all|reset|<research>>"), false);
        source.sendSuccess(() -> Component.literal("  /thaumcraft aspects hand"), false);
        source.sendSuccess(() -> Component.literal("  /thaumcraft node create <type> [modifier]"), false);
        return 1;
    }

    private static int createNode(CommandContext<CommandSourceStack> context, String modifierName)
            throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String typeName = StringArgumentType.getString(context, "type").toUpperCase(Locale.ROOT);
        NodeType type;
        NodeModifier modifier = null;
        try {
            type = NodeType.valueOf(typeName);
            if (!modifierName.equalsIgnoreCase("none")) {
                modifier = NodeModifier.valueOf(modifierName.toUpperCase(Locale.ROOT));
            }
        } catch (IllegalArgumentException exception) {
            source.sendFailure(Component.literal("Unknown node type or modifier."));
            return 0;
        }

        ServerPlayer player = source.getPlayerOrException();
        BlockPos pos = BlockPos.containing(player.getEyePosition().add(player.getLookAngle().scale(3.0D)));
        if (!player.level().getBlockState(pos).canBeReplaced()) {
            source.sendFailure(Component.literal("Target position is occupied."));
            return 0;
        }

        player.level().setBlockAndUpdate(pos, TCBlocks.AURA_NODE.get().defaultBlockState());
        if (player.level().getBlockEntity(pos) instanceof AuraNodeBlockEntity node) {
            AspectList aspects = new AspectList();
            Aspect.getPrimalAspects().forEach(aspect -> aspects.add(aspect, 25));
            node.configure(type, modifier, aspects);
        }
        source.sendSuccess(() -> Component.literal("Created " + type.name().toLowerCase(Locale.ROOT)
                + " aura node at " + pos.toShortString() + "."), true);
        return 1;
    }

    private static int showHeldItemAspects(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            context.getSource().sendFailure(Component.literal("§cHold an item first."));
            return 0;
        }

        AspectList aspects = ObjectAspectRegistry.getObjectTagsWithBonus(stack);
        if (aspects.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("§7No aspects for " + stack.getHoverName().getString()
                    + ". Source: " + ObjectAspectRegistry.source(stack)
                    + ". Loaded entries: " + ObjectAspectRegistry.itemEntryCount() + " items, "
                    + ObjectAspectRegistry.tagEntryCount() + " tags, "
                    + ObjectAspectRegistry.generatedEntryCount() + " generated."), false);
            return 0;
        }

        StringBuilder message = new StringBuilder("§5");
        message.append(stack.getHoverName().getString())
                .append(" [").append(ObjectAspectRegistry.source(stack)).append("]: ");
        boolean first = true;
        for (Aspect aspect : aspects.getAspectsSorted()) {
            if (!first) {
                message.append(", ");
            }
            first = false;
            message.append(aspect.getTag()).append(" ").append(aspects.getAmount(aspect));
        }
        context.getSource().sendSuccess(() -> Component.literal(message.toString()), false);
        return aspects.visSize();
    }

    private static int listResearch(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        for (ResearchEntry entry : ResearchRegistry.entries()) {
            source.sendSuccess(() -> Component.literal("§5" + entry.key()), false);
        }
        return ResearchRegistry.entries().size();
    }

    private static int giveResearch(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String research = StringArgumentType.getString(context, "research").toUpperCase(Locale.ROOT);
        if (ResearchRegistry.get(research).isEmpty()) {
            source.sendFailure(Component.literal("§cResearch does not exist."));
            return 0;
        }
        ResearchManager.grantResearchTree(player, research);
        player.displayClientMessage(Component.literal("§5" + source.getTextName()
                + " gave you " + research + " research and its requisites."), false);
        source.sendSuccess(() -> Component.literal("§5Success!"), true);
        return 1;
    }

    private static int giveAllResearch(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int count = ResearchManager.grantAllResearch(player);
        player.displayClientMessage(Component.literal("§5" + source.getTextName()
                + " has given you all research."), false);
        source.sendSuccess(() -> Component.literal("§5Success!"), true);
        return count;
    }

    private static int resetResearch(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        ResearchManager.resetResearch(player);
        player.displayClientMessage(Component.literal("§5" + source.getTextName()
                + " has reset your research."), false);
        source.sendSuccess(() -> Component.literal("§5Success!"), true);
        return 1;
    }

    private static java.util.concurrent.CompletableFuture<Suggestions> suggestResearch(
            CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(
                ResearchRegistry.entries().stream().map(ResearchEntry::key), builder);
    }
}
