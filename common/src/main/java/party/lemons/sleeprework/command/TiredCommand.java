package party.lemons.sleeprework.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import party.lemons.sleeprework.SleepRework;
import party.lemons.sleeprework.handler.ServerHandler;

import java.util.Collection;

public class TiredCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx, Commands.CommandSelection selection)
    {
        dispatcher.register(
                Commands.literal("tiredness").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(
                                Commands.argument("Target", EntityArgument.players())
                                        .then(
                                                Commands.argument("Value", IntegerArgumentType.integer(0, (int)(SleepRework.CONFIG.playerConfig().maxTiredness() * 100)))
                                                        .then(
                                                                Commands.literal("set").executes(TiredCommand::set)
                                                        )
                                                        .then(
                                                                Commands.literal("add").executes(TiredCommand::add)
                                                        )
                                        )
                                        .executes(TiredCommand::get)


                        )
        );
    }

    private static int get(CommandContext<CommandSourceStack> ctx)
    {
        try {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "Target");
            for(ServerPlayer player : players)
            {
                float level = ServerHandler.getPlayerTiredness(player);
                ctx.getSource().sendSystemMessage(Component.translatable("sleeprework.commands.tired.get", player.getDisplayName(), (int)(level * 100), ServerHandler.getPlayerTirednessIncrease(player)));
            }
            return 1;
        }
        catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private static int set(CommandContext<CommandSourceStack> ctx)
    {
        try {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "Target");
            int amount = IntegerArgumentType.getInteger(ctx, "Value");
            for(ServerPlayer player : players)
                ServerHandler.setPlayerTiredness(player, (float)amount / 100F);

            ctx.getSource().sendSuccess(
                    () -> Component.translatable("sleeprework.commands.tired.set.success", players.size()),
                    true
            );

            return 1;
        }
        catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private static int add(CommandContext<CommandSourceStack> ctx)
    {
        try {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "Target");
            int amount = IntegerArgumentType.getInteger(ctx, "Value");
            for(ServerPlayer player : players)
                ServerHandler.addPlayerTiredness(player, (float)amount / 100F);

            ctx.getSource().sendSuccess(
                    () -> Component.translatable("sleeprework.commands.tired.add.success", players.size()),
                    true
            );

            return 1;
        }
        catch (CommandSyntaxException e) {
            return 0;
        }
    }
}
