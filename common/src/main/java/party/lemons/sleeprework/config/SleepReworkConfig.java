package party.lemons.sleeprework.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import party.lemons.sleeprework.SleepRework;
import party.lemons.sleeprework.util.SleepReworkUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public record SleepReworkConfig(PlayerConfig playerConfig, PhantomConfig phantomConfig, PotionConfig potionConfig, ServerConfig serverConfig, ClientConfig clientConfig)
{
    public SleepReworkConfig()
    {
        this(new PlayerConfig(), new PhantomConfig(), new PotionConfig(), new ServerConfig(), new ClientConfig());
    }

    public static Codec<SleepReworkConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                PlayerConfig.CODEC.fieldOf("player").forGetter(c->c.playerConfig),
                PhantomConfig.CODEC.fieldOf("phantom").forGetter(c->c.phantomConfig),
                PotionConfig.CODEC.fieldOf("potion").forGetter(c->c.potionConfig),
                ServerConfig.CODEC.fieldOf("server").forGetter(c->c.serverConfig),
                ClientConfig.CODEC.fieldOf("client").forGetter(c->c.clientConfig)
            ).apply(instance, SleepReworkConfig::new)
    );

    public record PlayerConfig(float tirednessIncreasePerMinute, float maxTiredness, float minSleepLevel, int sleepTimeout, List<MobEffectInstance> wakeUpEffects)
    {
        public static Codec<PlayerConfig> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.FLOAT.fieldOf("tiredness_increase_per_minute").forGetter(c->c.tirednessIncreasePerMinute),
                        Codec.FLOAT.fieldOf("max_tiredness").forGetter(c->c.maxTiredness),
                        Codec.FLOAT.fieldOf("min_sleep_level").forGetter(c->c.minSleepLevel),
                        Codec.INT.fieldOf("sleep_timeout").forGetter(c->c.sleepTimeout),
                        SleepReworkCodecs.MOB_EFFECT_INSTANCE.listOf().fieldOf("wake_up_effects").forGetter(c->c.wakeUpEffects)
                ).apply(instance, PlayerConfig::new)
        );

        public PlayerConfig()
        {
            this(0.025F, 5.0F, 1.0F, 400, List.of(new MobEffectInstance(MobEffects.REGENERATION, 100, 1, true,true)));
        }
    }

    public record PhantomConfig(boolean tirednessPhantoms, float phantomSpawnTiredness, int requiredSkyDarken, boolean disableVanillaPhantomSpawner)
    {
        public static Codec<PhantomConfig> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.BOOL.fieldOf("tiredness_phantoms").forGetter(c->c.tirednessPhantoms),
                        Codec.FLOAT.fieldOf("phantom_spawn_tiredness").forGetter(c->c.phantomSpawnTiredness),
                        Codec.INT.fieldOf("spawn_required_sky_darken").forGetter(c->c.requiredSkyDarken),
                        Codec.BOOL.fieldOf("disable_vanilla_phantom_spawner").forGetter(c->c.disableVanillaPhantomSpawner)
                ).apply(instance, PhantomConfig::new)
        );

        public PhantomConfig()
        {
            this(true, 3.0F, 5, true);
        }
    }

    public record PotionConfig(float livelinessTirednessModifier, float drowsinessTirednessModifier, Item livelinessBrewingItem)
    {
        public static Codec<PotionConfig> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.FLOAT.fieldOf("liveliness_tiredness_modifier").forGetter(c->c.livelinessTirednessModifier),
                        Codec.FLOAT.fieldOf("drowsiness_tiredness_modifier").forGetter(c->c.drowsinessTirednessModifier),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("liveliness_brewing_item").forGetter(c->c.livelinessBrewingItem)
                ).apply(instance, PotionConfig::new)
        );

        public PotionConfig()
        {
            this(0.5F, 2.0F, Items.HONEYCOMB);
        }
    }

    public record ServerConfig(boolean respectInsomniaGameRule)
    {
        public static Codec<ServerConfig> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.BOOL.fieldOf("respect_insomnia_game_rule").forGetter(c->c.respectInsomniaGameRule)
                ).apply(instance, ServerConfig::new)
        );

        public ServerConfig()
        {
            this(true);
        }
    }

    public record ClientConfig(int iconX, int iconY, int color_1, int color_2, int color_3, int color_4, boolean doRecolor, AtomicBoolean showTutorial)
    {
        public static Codec<ClientConfig> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.INT.fieldOf("icon_x").forGetter(c->c.iconX),
                        Codec.INT.fieldOf("icon_y").forGetter(c->c.iconY),
                        Codec.INT.fieldOf("color_1").forGetter(c->c.color_1),
                        Codec.INT.fieldOf("color_2").forGetter(c->c.color_2),
                        Codec.INT.fieldOf("color_3").forGetter(c->c.color_3),
                        Codec.INT.fieldOf("color_4").forGetter(c->c.color_4),
                        Codec.BOOL.fieldOf("do_recolor").forGetter(c->c.doRecolor),
                        SleepReworkCodecs.ATOMIC_BOOL.fieldOf("show_tutorial").forGetter(c->c.showTutorial)
                ).apply(instance, ClientConfig::new)
        );

        public ClientConfig() {
            this(80, 10, 0xffffff, 0xffdd00, 0xeb7434, 0xeb2d2d, true, new AtomicBoolean(false));

        }
    }

    public static SleepReworkConfig loadConfig()
    {
        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
        File cfgFile = getConfigFile().toFile();

        if(cfgFile.exists())
        {
            try(FileReader reader = new FileReader(cfgFile))
            {
                JsonElement json = gson.fromJson(reader, JsonElement.class);
                SleepReworkConfig config = attemptLoad(gson, json, true);

                if(config != null)
                    return config;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        SleepRework.LOGGER.error("Unable to load Sleep Rework config, overwriting existing");
        SleepReworkConfig config = new SleepReworkConfig();
        writeConfig(config);

        return config;
    }

    public static SleepReworkConfig attemptLoad(Gson gson, JsonElement json, boolean allowRecursion)
    {
        DataResult<Pair<SleepReworkConfig, JsonElement>> configData = SleepReworkConfig.CODEC.decode(JsonOps.INSTANCE, json);
        if(configData.error().isEmpty()) {  //If the config data has successfully loaded, return it
            return configData.result().get().getFirst();
        }
        else {  //If it did no load successfully
            SleepRework.LOGGER.error(configData.error().get().message());

            //Create a known good config
            SleepReworkConfig newConfig = new SleepReworkConfig();
            DataResult<JsonElement> newConfigJson = SleepReworkConfig.CODEC.encodeStart(JsonOps.INSTANCE, newConfig);

            //If the known good config is correct, merge bad and good and return the result
            if(newConfigJson.error().isEmpty())
            {
                if(!allowRecursion)
                    return null;

                Map c1 = gson.fromJson(newConfigJson.get().left().get(), HashMap.class);
                Map c2 = gson.fromJson(json, HashMap.class);

                SleepReworkConfig fixedConfig = attemptLoad(gson, gson.toJsonTree(SleepReworkUtil.recursiveCollectionMerge(c1, c2)), false);
                if(fixedConfig != null)
                {
                    SleepRework.LOGGER.info("Sleep Rework Config data fixed");
                    writeConfig(fixedConfig);
                }
                return fixedConfig;
            }
            return null;    //Something has broken!!
        }
    }

    public static void writeConfig(SleepReworkConfig config)
    {
        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
        File cfgFile = getConfigFile().toFile();

        try(FileWriter writer = new FileWriter(cfgFile)){
            DataResult<JsonElement> element = SleepReworkConfig.CODEC.encodeStart(JsonOps.INSTANCE, config);
            if(element.error().isEmpty())
            {
                gson.toJson(element.result().get(), writer);
            }
            else {
                SleepRework.LOGGER.error(element.error().get().message());
            }

        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static Path getConfigFile()
    {
        return Platform.getConfigFolder().resolve(SleepRework.MODID + ".json");
    }
}
