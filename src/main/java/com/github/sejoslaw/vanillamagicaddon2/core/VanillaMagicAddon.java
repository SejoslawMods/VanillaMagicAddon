//package com.github.sejoslaw.vanillamagicaddon2.core;
//
//import net.minecraftforge.fml.common.Mod;
//
///**
// * @author Sejoslaw - https://github.com/Sejoslaw
// */
//@Mod(VanillaMagicAddon.MODID)
//@Mod.EventBusSubscriber(modid = VanillaMagicAddon.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
//public final class VanillaMagicAddon {
//    public static final String MODID = "vanillamagicaddon2";
//
//    public VanillaMagicAddon() {
//        VanillaMagicAPI.call(this, api -> {
//            api.getRegistries().getMachineModuleRegistry().call(registry -> {
//                registry.registerDefaultModule(new RFEnergyModule());
//                registry.registerDefaultModule(new IC2EnergyModule());
//                registry.registerDefaultModule(new MekanismEnergyModule());
//                registry.registerDefaultModule(new TeslaEnergyModule());
//            });
//        });
//    }
//}
