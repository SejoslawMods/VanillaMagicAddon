package seia.vanillamagicaddon.core;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import seia.vanillamagic.api.exception.MappingExistsException;
import seia.vanillamagic.api.tileentity.machine.QuarryUpgradeAPI;
import seia.vanillamagicaddon.config.VMAConfig;
import seia.vanillamagicaddon.tileentity.quarry.upgrade.power.AddTileQuarryBattery;
import seia.vanillamagicaddon.tileentity.quarry.upgrade.power.QuarryUpgradePower;
import seia.vanillamagicaddon.tileentity.quarry.upgrade.power.TileQuarryBattery;

@Mod(
		modid = VanillaMagicAddon.MODID, 
		version = VanillaMagicAddon.VERSION,
		name = VanillaMagicAddon.NAME
		)
public class VanillaMagicAddon
{
	public static final String MODID = "vanillamagicaddon";
	public static final String VERSION = "@VERSION@";
	public static final String NAME = "Vanilla Magic Addon";
	
	@Mod.Instance
	public static VanillaMagicAddon INSTANCE;
	
	@Mod.Metadata
	public static ModMetadata METADATA;
	
	public static Logger LOGGER;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		LOGGER = event.getModLog();
		VMAConfig.preInit(event);
		METADATA = VanillaMagicAddonMetadata.preInit(METADATA);
		
		MinecraftForge.EVENT_BUS.register(new AddTileQuarryBattery());
		GameRegistry.registerTileEntity(TileQuarryBattery.class, TileQuarryBattery.REGISTRY_NAME);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		try 
		{
			QuarryUpgradeAPI.addUpgrade(QuarryUpgradePower.class);
		} 
		catch (MappingExistsException e) 
		{
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
	}
}