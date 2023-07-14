package party.lemons.sleeprework;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import party.lemons.sleeprework.command.TiredCommand;
import party.lemons.sleeprework.config.SleepReworkConfig;
import party.lemons.sleeprework.effect.SleepReworkPotions;
import party.lemons.sleeprework.handler.PlayerSleepData;
import party.lemons.sleeprework.handler.ServerHandler;
import party.lemons.sleeprework.network.SleepReworkNetwork;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class SleepRework
{
    public static final String MODID = "sleeprework";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static SleepReworkConfig CONFIG;

    public static PlayerSleepData localSleepData = new PlayerSleepData();

    public static void init()
    {
        CONFIG = SleepReworkConfig.loadConfig();
        ServerHandler.init();

        CommandRegistrationEvent.EVENT.register(TiredCommand::register);

        SleepReworkPotions.init();
        SleepReworkNetwork.init();
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MODID, name);
    }
}
