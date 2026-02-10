package dev.satherov.utilityvest.datagen.assets;

import dev.satherov.utilityvest.UtilityVest;
import dev.satherov.utilityvest.core.UVRegistry;

import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.data.PackOutput;

public class UVItemModelProvider extends ItemModelProvider {
    
    public UVItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, UtilityVest.MOD_ID, existingFileHelper);
    }
    
    @Override
    protected void registerModels() {
        UVRegistry.ITEMS.getEntries().stream().toList().forEach(item -> {
            singleTexture(item.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/" + item.getId().getPath()));
        });
    }
}
