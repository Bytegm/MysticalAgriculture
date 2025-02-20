package com.blakebr0.mysticalagriculture.data.generator;

import com.blakebr0.mysticalagriculture.MysticalAgriculture;
import com.blakebr0.mysticalagriculture.registry.CropRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashMap;

public class BlockModelJsonGenerator extends BlockStateProvider {
    public BlockModelJsonGenerator(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        var stemModels = new HashMap<ResourceLocation, ModelFile[]>();

        CropRegistry.getInstance().getTypes().forEach(type -> {
            var models = new ModelFile[8];
            var stemModel = type.getStemModel();

            for (int i = 0; i <= 7; i++) {
                models[i] = new ModelFile.UncheckedModelFile(new ResourceLocation(stemModel.getNamespace(), stemModel.getPath() + "_" + i));
            }

            stemModels.put(type.getId(), models);
        });

        CropRegistry.getInstance().getCrops().forEach(crop -> {
            var block = crop.getCropBlock();
            var models = stemModels.get(crop.getType().getId());

            if (crop.shouldRegisterCropBlock()) {
                this.getVariantBuilder(block).forAllStates(state -> {
                    var age = state.getValue(CropBlock.AGE);
                    if (age == block.getMaxAge()) {
                        var model = this.models().getBuilder(crop.getNameWithSuffix("crop"))
                                .parent(models[7])
                                .texture("flower", crop.getTextures().getFlowerTexture());

                        return ConfiguredModel.builder().modelFile(model).build();
                    }

                    return ConfiguredModel.builder().modelFile(models[age]).build();
                });
            }
        });
    }

    @Override
    public String getName() {
        return MysticalAgriculture.NAME + " block model generator";
    }
}
