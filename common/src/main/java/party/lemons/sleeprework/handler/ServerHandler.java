package party.lemons.sleeprework.handler;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.GameRules;
import party.lemons.sleeprework.SleepRework;

public class ServerHandler
{
    private static final RandomSource RAND = RandomSource.create();
    private static final int INCREASE_TIME = 60 * 20; //1 minute
    private static int increaseTick = 0;    //When increaseTick >= INCREASE_TIME, every player has their tiredness increased
    private static int phantomTick = 0;     //When phantomTick == 0, every world has their phantom spawner ticked
    private static boolean doPhantomTick = false;   //Tells phantom spawners to tick
    private static final ReworkedPhantomSpawner phantomSpawner = new ReworkedPhantomSpawner();

    public static void init()
    {
        TickEvent.SERVER_PRE.register(ServerHandler::serverTick);
        TickEvent.SERVER_LEVEL_POST.register(ServerHandler::levelTick);
        PlayerEvent.PLAYER_JOIN.register(ServerHandler::syncJoin);
    }

    public static void serverTick(MinecraftServer server)
    {
        /*
            Don't run anything if:
                - We're respecting the doInsomnia GameRule
                - doInsomnia = false
         */
        if(SleepRework.CONFIG.serverConfig.respectInsomniaGameRule && !server.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA))
            return;

        //reset phantom spawner tick, so they won't run again
        doPhantomTick = false;

        //Handle increase ticking
        increaseTick++;
        if(increaseTick >= INCREASE_TIME)
        {
            increaseTick = 0;
            increasePlayerTiredness(server);
        }

        //Handle phantom ticking
        phantomTick--;
        if(phantomTick <= 0) {
            doPhantomTick = true;
            phantomTick = (60 + RAND.nextInt(60)) * 20; // 1 - 2 mins
        }
    }

    /*
        Ticks each world separately
     */
    public static void levelTick(ServerLevel level)
    {
        if(doPhantomTick) { //If it's time to do a phantom tick, do it.

            if (SleepRework.CONFIG.serverConfig.respectInsomniaGameRule && !level.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA))
                return;

            phantomSpawner.tick(level);
        }

        //Loop through all players, checking to see if they've slept long enough to timeout
        for(ServerPlayer player : level.players())
        {
            if(player.isSleeping())
            {
                if(tickSleepTimeout(player))
                {
                    player.stopSleeping();
                    handleWakeUp(player);

                    player.displayClientMessage(Component.translatable("sleeprework.sleep.timeout"), true);
                }
            }
        }
    }

    private static void increasePlayerTiredness(MinecraftServer server)
    {
        //Loop through each player, increasing their tired level, unless they're immune
        for(ServerPlayer player : server.getPlayerList().getPlayers())
        {
            if(isPlayerTiredImmune(player))
                continue;

            ((SleepDataHolder)player).getSleepData().increaseTiredness(player);
        }
    }

    /*
        Returns if the player is immune to being tired.
        //TODO: Hook for other mods?
     */
    public static boolean isPlayerTiredImmune(ServerPlayer player)
    {
        return player.isCreative() || player.isSpectator() || player.isSleeping();
    }

    /*
        Returns if the player is immune to phantom spawning
        //TODO: Hook for other mods?
     */
    public static boolean isPlayerPhantomImmune(ServerPlayer player)
    {
        return isPlayerTiredImmune(player);
    }

    /*
        Returns if the player should have phantoms spawn on them
     */
    public static boolean playerSpawnsPhantoms(ServerPlayer player)
    {
        return !ServerHandler.isPlayerPhantomImmune(player) && ((SleepDataHolder)player).getSleepData().doesSpawnPhantoms();
    }

    public static float getPlayerTiredness(ServerPlayer player)
    {
        return ((SleepDataHolder)player).getSleepData().getTiredness();
    }

    public static boolean canPlayerSleep(ServerPlayer player)
    {
        return ((SleepDataHolder)player).getSleepData().canSleep();
    }

    public static void setPlayerTiredness(ServerPlayer player, float v)
    {
        ((SleepDataHolder)player).getSleepData().setTiredness(player, v);
    }

    public static void addPlayerTiredness(ServerPlayer player, float v)
    {
        ((SleepDataHolder)player).getSleepData().setTiredness(player, getPlayerTiredness(player) + v);
    }

    public static float getPlayerTirednessIncrease(ServerPlayer player)
    {
        return ((SleepDataHolder)player).getSleepData().getTirednessIncrease(player);
    }

    public static void resetTiredness(ServerPlayer player)
    {
        ((SleepDataHolder)player).getSleepData().resetTiredness(player);
    }

    public static void resetTimeout(ServerPlayer player)
    {
        ((SleepDataHolder)player).getSleepData().resetTimeout(player);
    }

    private static boolean tickSleepTimeout(ServerPlayer player)
    {
        return ((SleepDataHolder)player).getSleepData().tickTimeout(player);
    }

    public static void handleWakeUp(ServerPlayer player)
    {
        resetTiredness(player);
        resetTimeout(player);

        for(MobEffectInstance instance : SleepRework.CONFIG.playerConfig.wakeUpEffects)
        {
            if(!player.hasEffect(instance.getEffect()))
            {
                player.addEffect(new MobEffectInstance(instance));
            }
        }
    }

    public static void handleStopSleeping(ServerPlayer player)
    {
        resetTimeout(player);
    }

    public static void syncJoin(ServerPlayer player)
    {
        ((SleepDataHolder)player).getSleepData().syncTo(player);
    }
}
