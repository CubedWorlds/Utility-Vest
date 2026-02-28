package dev.satherov.utilityvest.datagen;

import dev.satherov.utilityvest.UtilityVest;
import dev.satherov.utilityvest.datagen.assets.UVItemModelProvider;
import dev.satherov.utilityvest.datagen.assets.UVLanguageProvider;
import dev.satherov.utilityvest.datagen.data.UVItemTagsProvider;
import dev.satherov.utilityvest.datagen.data.UVRecipeProvider;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = UtilityVest.MOD_ID, bus = EventBusSubscriber.Bus.MOD)

public class UVDataGenerator {
    
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        
        UVDataProvider provider = new UVDataProvider();
        
        provider.addSubProvider(event.includeClient(), new UVLanguageProvider(packOutput));
        provider.addSubProvider(event.includeClient(), new UVItemModelProvider(packOutput, fileHelper));
        
        provider.addSubProvider(event.includeServer(), new UVItemTagsProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new UVRecipeProvider(packOutput, lookupProvider));
        
        generator.addProvider(true, provider);
    }
}
