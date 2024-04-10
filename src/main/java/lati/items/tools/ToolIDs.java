package lati.items.tools;

import lati.LatisTools;

public class ToolIDs {
    //Level refs
    public static final String EXP_REF = getNbtRef("exp");
    public static final String LEVEL_UP_EXP_REF = getNbtRef("level_up_exp");
    public static final String LEVEL_REF = getNbtRef("level");
    public static final String LEVEL_DATA_REF = getNbtRef("level_data");

    //Modifier refs
    public static final String AVAILABLE_MODIFIERS_REF = getNbtRef("available_modifiers");
    public static final String MODIFIERS_REF = getNbtRef("modifiers");
    public static final String MODIFIERS_DATA_REF = getNbtRef("modifier_data");

    //Helper function
    //return the basic format that NBT will be in
    public static final String getNbtRef(String string) {
        return LatisTools.MODID + ":" + string;
    }
}
