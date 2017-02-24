package seia.vanillamagicaddon.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class VMAConfig 
{
	private static Configuration _config;
	
	// Power
	private static final String _categoryPower = "Power";
	public static int ratioRF = 1;
	public static int ratioTesla = 1;
	public static int ratioIC2 = 1;
	public static int ratioMekanism = 1;
	
	// Quarry
	private static final String _categoryQuarry = "Quarry";
	public static int quarryOperations = 10;
	
	public static void preInit(FMLPreInitializationEvent event) 
	{
		File configFile = new File(event.getModConfigurationDirectory(), "VanillaMagicAddon.cfg");
		_config = new Configuration(configFile);
		processConfig();
	}
	
	public static void processConfig()
	{
		// Power
		_config.addCustomCategoryComment(_categoryPower, "Options connected with various Power APIs.");
		ratioRF = _config.getInt(
				"ratioRF", 
				_categoryPower, 
				ratioRF, 
				1, 
				Integer.MAX_VALUE, 
				"Ratio 1 VM tick to 1 RF. It is basically how many RF is one VM tick. If for instance ratioRF = 3, than 1 VM tick = 3 RF (use mainly in Quarry). Also this value is taken if used in ForgeCapabilityEnergy, ImmersiveEngineering, and MinecraftFlux.");
		if(ratioRF < 1)
		{
			ratioRF = 1;
		}
		ratioTesla = _config.getInt(
				"ratioTesla", 
				_categoryPower, 
				ratioTesla, 
				1, 
				Integer.MIN_VALUE, 
				"Works just like RF ratio. Set how many Tesla power 1 VM tick should be.");
		if(ratioTesla < 1)
		{
			ratioTesla = 1;
		}
		ratioIC2 = _config.getInt(
				"ratioIC2", 
				_categoryPower, 
				ratioIC2, 
				1, 
				Integer.MAX_VALUE, 
				"Works just like RF ratio. Used in IC2 and TechReborn.");
		if(ratioIC2 < 1)
		{
			ratioIC2 = 1;
		}
		ratioMekanism = _config.getInt(
				"ratioMekanism", 
				_categoryPower, 
				ratioMekanism, 
				1, 
				Integer.MAX_VALUE, 
				"Works just like RF ratio. Used in Mekanism.");
		if(ratioMekanism < 1)
		{
			ratioMekanism = 1;
		}
		
		// Quarry
		_config.addCustomCategoryComment(_categoryQuarry, "Options connected with Quarry.");
		quarryOperations = _config.getInt(
				"quarryOperations", 
				_categoryQuarry, 
				quarryOperations, 
				1, 
				Integer.MAX_VALUE, 
				"How many Quarry one-cost-operations should be taken as power from adjacent power source.");
		
		_config.save();
	}
}