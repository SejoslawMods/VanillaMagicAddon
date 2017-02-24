package seia.vanillamagicaddon.tileentity.quarry.upgrade.power;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import seia.vanillamagic.api.tileentity.machine.IQuarryUpgrade;

public class QuarryUpgradePower implements IQuarryUpgrade
{
	public String getUpgradeName() 
	{
		return "Power Battery";
	}
	
	public Block getBlock() 
	{
		return Blocks.REDSTONE_LAMP; // BlockRedstoneLight
	}
}