package dev.satherov.utilityvest.config;

import dev.satherov.utilityvest.config.annotation.Config;
import dev.satherov.utilityvest.config.annotation.ConfigVal;

import net.neoforged.fml.config.ModConfig;

@Config(ModConfig.Type.CLIENT)
public class UVConfig {
    
    @ConfigVal(name = "invert_radial_scroll", comment = "Invert the scroll direction in the radial menu")
    @ConfigVal.Boolean
    public static boolean InvertRadialScroll = false;
    
    @ConfigVal(name = "radial_tooltip", comment = "Defines when the tooltip in the radial menu should be displayed")
    @ConfigVal.Enum(ToolTipDisplay.class)
    public static ToolTipDisplay RadialTooltip = ToolTipDisplay.ALWAYS;
    
    public enum ToolTipDisplay implements ConfigEnum {
        ALWAYS("Always show the tooltip"),
        SHIFT("Only show the tooltip when holding shift"),
        NEVER("Never show the tooltip"),;
        
        ToolTipDisplay(String comment) {
            this.comment = comment;
        }
        
        public final String comment;
        
        @Override
        public String comment() {
            return this.comment;
        }
    }
}
