package party.lemons.sleeprework.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import party.lemons.sleeprework.SleepRework;

public class ReworkedPhantomSpawner
{
    public void tick(ServerLevel level)
    {
        RandomSource rand = level.random;
        if (level.getSkyDarken() >= 5 || !level.dimensionType().hasSkyLight()) {
            for (ServerPlayer player : level.players()) {
                if (ServerHandler.playerSpawnsPhantoms(player)) {
                    BlockPos playerPosition = player.blockPosition();
                    if (!level.dimensionType().hasSkyLight() || playerPosition.getY() >= level.getSeaLevel() && level.canSeeSky(playerPosition)) {
                        DifficultyInstance localDifficulty = level.getCurrentDifficultyAt(playerPosition);
                        if (localDifficulty.isHarderThan(rand.nextFloat() * 3.0F)) {
                            float tiredness = ServerHandler.getPlayerTiredness(player);

                            if ((tiredness * rand.nextFloat()) * 100 >= (SleepRework.CONFIG.phantomConfig.phantomSpawnTiredness) * 100F) {
                                BlockPos spawnPosition = playerPosition.above(20 + rand.nextInt(15)).east(-10 + rand.nextInt(21)).south(-10 + rand.nextInt(21));
                                BlockState currentState = level.getBlockState(spawnPosition);
                                FluidState currentFluid = level.getFluidState(spawnPosition);
                                if (NaturalSpawner.isValidEmptySpawnBlock(level, spawnPosition, currentState, currentFluid, EntityType.PHANTOM)) {
                                    SpawnGroupData spawnGroupData = null;

                                    float tiredCheckLevel = tiredness + ((localDifficulty.getDifficulty().getId() + 1F) / 10);
                                    int phantomCount = getSpawnCount(tiredCheckLevel, rand);

                                    for (int i = 0; i < phantomCount; ++i)
                                    {
                                        Phantom phantom = EntityType.PHANTOM.create(level);
                                        if (phantom != null) {
                                            phantom.moveTo(spawnPosition, 0.0F, 0.0F);
                                            spawnGroupData = phantom.finalizeSpawn(level, localDifficulty, MobSpawnType.NATURAL, spawnGroupData, null);
                                            level.addFreshEntityWithPassengers(phantom);
                                            ++i;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public int getSpawnCount(float tiredness, RandomSource randomSource)
    {
        float p = 1F + ((tiredness * 100F) - SleepRework.CONFIG.phantomConfig.phantomSpawnTiredness);
        float amt = (p / 100F) + randomSource.nextInt(0, (int)(p/100F));

        return (int)amt;
    }
}
