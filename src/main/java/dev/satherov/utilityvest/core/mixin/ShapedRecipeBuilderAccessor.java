package dev.satherov.utilityvest.core.mixin;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ShapedRecipeBuilder.class)
public interface ShapedRecipeBuilderAccessor {
    
    @Accessor("category")
    RecipeCategory category();
    
    @Accessor("resultStack")
    ItemStack result();
    
    @Accessor("group")
    String group();
    
    @Accessor("showNotification")
    boolean showNotification();
    
    @Accessor("criteria")
    Map<String, Criterion<?>> criteria();
    
    @Invoker("ensureValid")
    ShapedRecipePattern onEnsureValid(ResourceLocation location);
}
