package lati.items.tools.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class BetterToolCapability {
    public static final Capability<BetterToolCapabilityInterface> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(BetterToolCapabilityInterface.class);
    }

    private BetterToolCapability() {}
}
