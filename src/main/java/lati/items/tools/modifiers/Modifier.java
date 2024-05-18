package lati.items.tools.modifiers;

import lati.items.tools.ToolIDs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Modifier {
    static private final int MAX_SPEED_LEVEL = 3;
    static private final int SPEED_INCREMENT = 1;

    static private final int MAX_SHARPNESS_LEVEL = 5;
    static private final int SHARPNESS_INCREMENT = 1;

    private final String displayName;
    private final String codeName;
    private final ChatFormatting color;
    private final int maxLevel;
    private final int increment;

    public Modifier(String displayName, ChatFormatting color, int maxLevel, int increment) {
        this.displayName = displayName;
        this.codeName = ToolIDs.getNbtRef(displayName.toLowerCase(Locale.ROOT).replaceAll(" ", "_"));
        this.color = color;
        this.maxLevel = maxLevel;
        this.increment = increment;
    }

    public String getCodeName() {
        return codeName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getIncrement() {
        return increment;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatFormatting getColor() {
        return color;
    }

    public static Modifier SPEED_MODIFIER = new Modifier("Speed", ChatFormatting.RED, MAX_SPEED_LEVEL, SPEED_INCREMENT);
    public static Modifier SHARPNESS_MODIFIER = new Modifier("Sharpness", ChatFormatting.BOLD, MAX_SHARPNESS_LEVEL, SHARPNESS_INCREMENT);

    public static Component getCustomDisplay(Modifier mod, int level) {
        if(mod==null) {
            return Component.literal("null?"); //I'm getting a lil worn out today, probably gonna do some more work on this tmw, but I wanna just leave this
        }

        if(mod.equals(SPEED_MODIFIER)) {
            if(level == 1) {
                return Component.literal("Fast").withStyle(mod.getColor());
            }else if(level == 2) {
                return Component.literal("Faster").withStyle(mod.getColor());
            }else if(level == 3) {
                return Component.literal("Fastest").withStyle(mod.getColor());
            }
        }

        if(mod.equals(SHARPNESS_MODIFIER)) {
            if(level == 1) {
                return Component.literal("Sharp").withStyle(mod.getColor());
            }else if(level == 2) {
                return Component.literal("Sharper").withStyle(mod.getColor());
            }else if(level == 3) {
                return Component.literal("Sharpest").withStyle(mod.getColor());
            }if(level == 4) {
                return Component.literal("Sharpester").withStyle(mod.getColor());
            }if(level == 5) {
                return Component.literal("Sharpestest").withStyle(mod.getColor());
            }
        }

        return Component.literal(mod.getDisplayName() + ": " + level);
    }

    static Map<String, Modifier> key2mod = Map.of(SPEED_MODIFIER.codeName, SPEED_MODIFIER,
            SHARPNESS_MODIFIER.codeName, SHARPNESS_MODIFIER);

    public static Modifier keyToModifier(String key) {
        return key2mod.get(key.toLowerCase());
    }
}
