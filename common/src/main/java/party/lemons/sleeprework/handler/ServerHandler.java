package party.lemons.sleeprework.handler;

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
    private static int increaseTick = 0;
    private static int phantomTick = 0;
    private static boolean doPhantomTick = false;
    private static final ReworkedPhantomSpawner phantomSpawner = new ReworkedPhantomSpawner();

    public static void serverTick(MinecraftServer server)
    {
        if(SleepRework.CONFIG.serverConfig.respectInsomniaGameRule && !server.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA))
            return;

        doPhantomTick = false;

        increaseTick++;
        if(increaseTick >= INCREASE_TIME)
        {
            increaseTick = 0;
            increasePlayerTiredness(server);
        }

        phantomTick--;
        if(phantomTick <= 0) {
            doPhantomTick = true;
            phantomTick = (60 + RAND.nextInt(60)) * 20;
        }
    }

    public static void levelTick(ServerLevel level)
    {
        if(doPhantomTick) {
            if (SleepRework.CONFIG.serverConfig.respectInsomniaGameRule && !level.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA))
                return;

            phantomSpawner.tick(level);
        }
    }

    private static void increasePlayerTiredness(MinecraftServer server)
    {
        for(ServerPlayer player : server.getPlayerList().getPlayers())
        {
            if(isPlayerTiredImmune(player))
                continue;

            ((SleepDataHolder)player).getSleepData().increaseTiredness(player);
        }
    }

    public static boolean isPlayerTiredImmune(ServerPlayer player)
    {
        return player.isCreative() || player.isSpectator() || player.isSleeping();
    }

    public static boolean isPlayerPhantomImmune(ServerPlayer player)
    {
        return isPlayerTiredImmune(player);
    }

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

    public static void handleWakeUp(ServerPlayer player)
    {
        resetTiredness(player);

        for(MobEffectInstance instance : SleepRework.CONFIG.playerConfig.wakeUpEffects)
        {
            if(!player.hasEffect(instance.getEffect()))
            {
                player.addEffect(new MobEffectInstance(instance));
            }
        }
    }

    public static void syncJoin(ServerPlayer player)
    {
        ((SleepDataHolder)player).getSleepData().syncTo(player);
    }
}
