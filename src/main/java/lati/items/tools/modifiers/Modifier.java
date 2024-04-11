package lati.items.tools.modifiers;

import lati.items.tools.ToolIDs;
import net.minecraft.ChatFormatting;

import java.util.Locale;

public class Modifier {
    private final String displayName;
    private final String codeName;
    private final ChatFormatting color;

    public Modifier(String displayName, ChatFormatting color) {
        this.displayName = displayName;
        this.codeName = ToolIDs.getNbtRef(displayName.toLowerCase(Locale.ROOT).replaceAll(" ", "_"));
        this.color = color;
    }

    public static Modifier SPEED_MODIFIER = new Modifier("Speed", ChatFormatting.RED);
    public static Modifier SHARPNESS_MODIFIER = new Modifier("Sharpness", ChatFormatting.BOLD);
}
