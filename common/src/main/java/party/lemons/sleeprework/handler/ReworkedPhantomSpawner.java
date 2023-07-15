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
        if (level.getSkyDarken() >= SleepRework.CONFIG.phantomConfig().requiredSkyDarken() || !level.dimensionType().hasSkyLight())  //If the dimension has sky light && if the sky is dark enough (TODO: config?)
        {
            for (ServerPlayer player : level.players())     //Loop each player in the world
            {
                if (!ServerHandler.playerSpawnsPhantoms(player)) //If the player can't spawn phantoms, skip
                    continue;

                BlockPos playerPosition = player.blockPosition();

                if (!level.dimensionType().hasSkyLight() || playerPosition.getY() >= level.getSeaLevel() && level.canSeeSky(playerPosition))    //If the player is above sea level and can see the sky
                {
                    DifficultyInstance difficulty = level.getCurrentDifficultyAt(playerPosition);

                    if (difficulty.isHarderThan(rand.nextFloat() * 3.0F))   //If we're hard enough to spawn
                    {
                        //If the player's tiredness is enough to spawn phantoms
                        float tiredness = ServerHandler.getPlayerTiredness(player);
                        if ((tiredness * rand.nextFloat()) * 100 >= (SleepRework.CONFIG.phantomConfig().phantomSpawnTiredness()) * 100F)
                        {
                            //Find valid position, if found, continue to spawn
                            BlockPos spawnPosition = playerPosition.above(20 + rand.nextInt(15)).east(-10 + rand.nextInt(21)).south(-10 + rand.nextInt(21));
                            BlockState currentState = level.getBlockState(spawnPosition);
                            FluidState currentFluid = level.getFluidState(spawnPosition);
                            if (NaturalSpawner.isValidEmptySpawnBlock(level, spawnPosition, currentState, currentFluid, EntityType.PHANTOM)) {
                                SpawnGroupData spawnGroupData = null;

                                //Adjust the tiredness with the difficulty to get more phantoms if harder
                                float tiredCheckLevel = tiredness + ((difficulty.getDifficulty().getId() + 1F) / 10);
                                int phantomCount = getSpawnCount(tiredCheckLevel, rand);    //Phantom spawn count

                                //Spawn phantoms
                                for (int i = 0; i < phantomCount; ++i)
                                {
                                    Phantom phantom = EntityType.PHANTOM.create(level);
                                    if (phantom != null) {
                                        phantom.moveTo(spawnPosition, 0.0F, 0.0F);
                                        spawnGroupData = phantom.finalizeSpawn(level, difficulty, MobSpawnType.NATURAL, spawnGroupData, null);
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

    /*
        Gets the amount of phantoms to spawn for the tiredness level
     */
    public int getSpawnCount(float tiredness, RandomSource randomSource)
    {
        float p = 1F + ((tiredness * 100F) - SleepRework.CONFIG.phantomConfig().phantomSpawnTiredness());
        float amt = (p / 100F) + randomSource.nextInt(0, (int)(p/100F));

        return (int)amt;
    }
}
