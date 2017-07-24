package seia.vanillamagicaddon.tileentity.quarry.upgrade.power;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import seia.vanillamagic.api.event.EventQuarry;
import seia.vanillamagic.api.handler.CustomTileEntityHandlerAPI;
import seia.vanillamagic.api.tileentity.machine.IQuarryUpgrade;
import seia.vanillamagic.api.tileentity.machine.QuarryUpgradeAPI;

public class AddTileQuarryBattery 
{
	@SubscribeEvent
	public void addQuarryTileBattery(EventQuarry.AddUpgrade event)
	{
		IQuarryUpgrade powerUpgrade = QuarryUpgradeAPI.getUpgradeFromBlock(Blocks.REDSTONE_LAMP);
		IQuarryUpgrade eventUpgrade = event.getUpgrade();
		if (QuarryUpgradeAPI.isTheSameUpgrade(powerUpgrade, eventUpgrade)) // QuarryUpgradeHelper added Power Upgrade
		{
			World world = event.getWorld();
			BlockPos powerUpgradePos = event.getUpgradePos();
			if (world.getTileEntity(powerUpgradePos) == null) // This should always be NULL
			{
				TileQuarryBattery tile = new TileQuarryBattery();
				tile.init(world, powerUpgradePos);
				tile.setQuarry(event.getTileQuarry());
				CustomTileEntityHandlerAPI.addCustomTileEntity(tile, world.provider.getDimension());
			}
		}
	}
	
	@SubscribeEvent
	public void removePowerUpgrade(BreakEvent event)
	{
		IBlockState state = event.getState();
		Block block = state.getBlock();
		if (Block.isEqualTo(block, Blocks.REDSTONE_LAMP) || Block.isEqualTo(block, Blocks.LIT_REDSTONE_LAMP))
		{
			World world = event.getWorld();
			CustomTileEntityHandlerAPI.removeCustomTileEntityAtPos(world, event.getPos());
		}
	}
}