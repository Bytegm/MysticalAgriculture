package com.blakebr0.mysticalagriculture.block;

import com.blakebr0.cucumber.util.Localizable;
import com.blakebr0.mysticalagriculture.api.crop.ICrop;
import com.blakebr0.mysticalagriculture.api.crop.ICropGetter;
import com.blakebr0.mysticalagriculture.config.ModConfigs;
import com.blakebr0.mysticalagriculture.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MysticalCropBlock extends CropsBlock implements ICropGetter {
    private static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
    private final ICrop crop;

    public MysticalCropBlock(ICrop crop) {
        super(Properties.from(Blocks.WHEAT));
        this.crop = crop;
    }

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.canGrow(world, pos))
            return;

        super.randomTick(state, world, pos, random);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public String getTranslationKey() {
        return Localizable.of("block.mysticalagriculture.mystical_crop").args(this.crop.getDisplayName()).buildString();
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        int age = state.get(AGE);

        int crop = 0;
        int seed = 1;
        int fertilizer = 0;

        if (age == this.getMaxAge()) {
            crop = 1;

            Vector3d vec = builder.get(LootParameters.field_237457_g_);
            if (vec != null) {
                ServerWorld world = builder.getWorld();
                BlockPos pos = new BlockPos(vec);
                Block below = world.getBlockState(pos.down()).getBlock();
                double chance = this.crop.getSecondaryChance(below);

                if (Math.random() < chance)
                    crop = 2;

                if (ModConfigs.SECONDARY_SEED_DROPS.get() && Math.random() < chance)
                    seed = 2;

                double fertilizerChance = ModConfigs.FERTILIZED_ESSENCE_DROP_CHANCE.get();
                if (Math.random() < fertilizerChance)
                    fertilizer = 1;
            }
        }

        List<ItemStack> drops = new ArrayList<>();
        if (crop > 0)
            drops.add(new ItemStack(this.getCropsItem(), crop));

        drops.add(new ItemStack(this.getSeedsItem(), seed));

        if (fertilizer > 0)
            drops.add(new ItemStack(ModItems.FERTILIZED_ESSENCE.get()));

        return drops;
    }

    @Override
    public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
        if (!this.canGrow(world, pos))
            return;

        super.grow(world, rand, pos, state);
    }

    @Override
    public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
        if (world instanceof World) {
            return this.canGrow((World) world, pos) && super.canGrow(world, pos, state, isClient);
        }

        return super.canGrow(world, pos, state, isClient);
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader world, BlockPos pos) {
        return state.getBlock() instanceof FarmlandBlock;
    }

    @Override
    protected IItemProvider getSeedsItem() {
        return this.crop.getSeeds();
    }

    @Override
    public ICrop getCrop() {
        return this.crop;
    }

    protected IItemProvider getCropsItem() {
        return this.crop.getEssence();
    }

    private boolean canGrow(World world, BlockPos pos) {
        Block crux = this.crop.getCrux();
        if (crux != null) {
            Block block = world.getBlockState(pos.down(2)).getBlock();
            if (block != crux)
                return false;
        }

        Set<ResourceLocation> biomes = this.crop.getRequiredBiomes();
        if (!biomes.isEmpty()) {
            Biome biome = world.getBiome(pos);
            return biomes.contains(biome.getRegistryName());
        }

        return true;
    }
}
