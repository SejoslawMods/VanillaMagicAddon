package seia.vanillamagicaddon.tileentity.quarry.upgrade.power;

import java.util.List;

import javax.annotation.Nullable;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import mekanism.api.energy.IStrictEnergyStorage;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;
import seia.vanillamagic.api.tileentity.CustomTileEntityBase;
import seia.vanillamagic.api.tileentity.machine.IQuarry;
import seia.vanillamagicaddon.config.VMAConfig;

@Optional.InterfaceList(value = {
		@Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = "CoFHAPI"),
		@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla"),
		@Optional.Interface(iface = "reborncore.api.power.IEnergyInterfaceTile", modid = "reborncore"),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2"),
//		@Optional.Interface(iface = "blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxReceiver", modid = "immersiveengineering"),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor", modid = "mekanism")
//		@Optional.Interface(iface = "szewek.mcflux.api.ex.IEnergy", modid = "mcflux")
})
public class TileQuarryBattery extends CustomTileEntityBase implements 
				IEnergyReceiver, ITeslaConsumer, net.minecraftforge.energy.IEnergyStorage, 
				IEnergyHandler, IStrictEnergyStorage, IEnergySink
//				IEnergyInterfaceTile
{
	public static final String REGISTRY_NAME = TileQuarryBattery.class.getName();
	
	private IQuarry _quarry;
	
	public void setQuarry(IQuarry tileQuarry) 
	{
		this._quarry = tileQuarry;
	}
	
	public List<String> getAdditionalInfo()
	{
		List<String> list = super.getAdditionalInfo();
		list.add("1 Quarry tick = " + 
				VMAConfig.ratioRF + " RF = " + 
				VMAConfig.ratioTesla + " Tesla = " + 
				VMAConfig.ratioIC2 + " EU = " + 
				VMAConfig.ratioMekanism + " Mekanism Joules");
		return list;
	}
	
	public int addPower(int received, boolean simulated)
	{
		if (_quarry.getMaxTicks() < _quarry.getCurrentTicks()) return 0;
		
		_quarry.setCurrentTicks(_quarry.getCurrentTicks() + received);
		return received;
	}
	
	public void update()
	{
		if (_quarry.getCurrentTicks() < _quarry.getMaxTicks()) // We can get more power
		{
			for (EnumFacing face : EnumFacing.values()) // Try to take power from each side of the block
			{
				BlockPos powerSourcePos = this.getPos().offset(face);
				TileEntity powerSourceTile = world.getTileEntity(powerSourcePos);
				face = face.getOpposite();
				if (powerSourceTile != null) // If there is a TileEntity on the checking position
				{
					if (powerSourceTile instanceof IEnergyProvider) // RedstoneFlux-API
					{
						IEnergyProvider iep = (IEnergyProvider) powerSourceTile;
						addPower(iep.extractEnergy(face, _quarry.getOneOperationCost() * VMAConfig.quarryOperations * VMAConfig.ratioRF, false) / VMAConfig.ratioRF, false);
					}
					if (powerSourceTile instanceof cofh.api.energy.IEnergyStorage) // RedstoneFlux-API
					{
						cofh.api.energy.IEnergyStorage storage = (cofh.api.energy.IEnergyStorage) powerSourceTile;
						addPower(storage.extractEnergy(_quarry.getOneOperationCost() * VMAConfig.quarryOperations * VMAConfig.ratioRF, false) / VMAConfig.ratioRF, false);
					}
					if (powerSourceTile.hasCapability(CapabilityEnergy.ENERGY, face)) // Forge Capability Energy
					{
						net.minecraftforge.energy.IEnergyStorage storage = (net.minecraftforge.energy.IEnergyStorage) powerSourceTile.getCapability(CapabilityEnergy.ENERGY, face);
						addPower(storage.extractEnergy(_quarry.getOneOperationCost() * VMAConfig.quarryOperations * VMAConfig.ratioRF, false) / VMAConfig.ratioRF, false);
					}
					if (powerSourceTile.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, face)) // Tesla API
					{
						ITeslaProducer producer = (ITeslaProducer) powerSourceTile.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, face);
						addPower((int) producer.takePower(_quarry.getOneOperationCost() * VMAConfig.quarryOperations * VMAConfig.ratioTesla, false), false);
					}
//					if (powerSourceTile instanceof IEnergy) // MinecraftFlux
//					{
//						IEnergy energy = (IEnergy) powerSourceTile;
//						addPower((int) energy.outputEnergy(_quarry.getOneOperationCost() * VMAConfig.quarryOperations * VMAConfig.ratioRF, false), false);
//					}
//					if (powerSourceTile instanceof IEnergyInterfaceTile) // RebornCore
//					{
//						IEnergyInterfaceTile ieit = (IEnergyInterfaceTile) powerSourceTile;
//						if(ieit.canProvideEnergy(face))
//						{
//							addPower((int) ieit.useEnergy(getMaxInput()), false);
//						}
//					}
					if (powerSourceTile instanceof IEnergySource) // IC2
					{
						IEnergySource source = (IEnergySource) powerSourceTile;
						source.drawEnergy(_quarry.getOneOperationCost() * VMAConfig.quarryOperations * VMAConfig.ratioIC2);
						addPower((int) source.getOfferedEnergy(), false);
					}
//					if (powerSourceTile instanceof IFluxProvider) // ImmersiveEngineering
//					{
//						IFluxProvider ifp = (IFluxProvider) powerSourceTile;
//						addPower(ifp.extractEnergy(face, _quarry.getOneOperationCost() * VMAConfig.quarryOperations * VMAConfig.ratioRF, false) / VMAConfig.ratioRF, false);
//					}
//					if (powerSourceTile instanceof IFluxStorage) // ImmersiveEngineering
//					{
//						IFluxStorage storage = (IFluxStorage) powerSourceTile;
//						addPower(storage.extractEnergy(_quarry.getOneOperationCost() * VMAConfig.quarryOperations * VMAConfig.ratioRF, false) / VMAConfig.ratioRF, false);
//					}
					if (powerSourceTile instanceof IStrictEnergyStorage) // Mekanism
					{
						IStrictEnergyStorage storage = (IStrictEnergyStorage) powerSourceTile;
						double takenEnergy = storage.getEnergy() * VMAConfig.ratioMekanism;
						if(_quarry.getOneOperationCost() * VMAConfig.quarryOperations < takenEnergy)
						{
							addPower((int) (takenEnergy / VMAConfig.ratioMekanism), false);
							storage.setEnergy(storage.getEnergy() - takenEnergy);
						}
					}
				}
			}
		}
	}
	
	//====================================== Forge Energy ======================================
	
	public int receiveEnergy(int maxReceive, boolean simulate) 
	{
		int receive = maxReceive / VMAConfig.ratioRF; // VM ticks
		return addPower(receive, simulate);
	}
	
	public int extractEnergy(int maxExtract, boolean simulate) 
	{
		return 0;
	}
	
	public int getEnergyStored() 
	{
		return _quarry.getCurrentTicks() * VMAConfig.ratioRF;
	}
	
	public int getMaxEnergyStored() 
	{
		return _quarry.getMaxTicks() * VMAConfig.ratioRF;
	}
	
	public boolean canExtract() 
	{
		return false;
	}
	
	public boolean canReceive() 
	{
		return true;
	}
	
	//====================================== Forge Capability Energy ======================================
	
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		return this.getCapability(capability, facing) != null;
	}
	
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		if (capability == CapabilityEnergy.ENERGY) return (T) this; // Forge Energy
		else if(capability == TeslaCapabilities.CAPABILITY_CONSUMER) return (T) this; // TeslaAPI
		
		return super.getCapability(capability, facing);
	}
	
	//====================================== RedstoneFlux API ======================================
	
//	@Optional.Method(modid = "CoFHAPI")
	public int getEnergyStored(EnumFacing face) 
	{
		return _quarry.getCurrentTicks() * VMAConfig.ratioRF;
	}
	
//	@Optional.Method(modid = "CoFHAPI")
	public int getMaxEnergyStored(EnumFacing face) 
	{
		return _quarry.getMaxTicks() * VMAConfig.ratioRF;
	}
	
//	@Optional.Method(modid = "CoFHAPI")
	public boolean canConnectEnergy(EnumFacing face) 
	{
		return true;
	}
	
//	@Optional.Method(modid = "CoFHAPI")
	public int receiveEnergy(EnumFacing face, int maxReceive, boolean simulate) 
	{
		return addPower(maxReceive / VMAConfig.ratioRF, simulate);
	}
	
	//====================================== Tesla API ======================================
	
//	@Optional.Method(modid = "tesla")
	public long givePower(long power, boolean simulated) 
	{
		int receive = (int) (Math.min(power, Integer.MAX_VALUE) / VMAConfig.ratioTesla);
		return addPower(receive, simulated);
	}
	
	//====================================== RebornCore ======================================
	
//	public double getEnergy() 
//	{
//		return _quarry.getCurrentTicks() * VMAConfig.ratioIC2;
//	}
//	
//	public void setEnergy(double energy) 
//	{
//		_quarry.setCurrentTicks((int) energy);
//	}
	
	public double getMaxPower() 
	{
		return _quarry.getMaxTicks();
	}
	
	public boolean canAddEnergy(double energy) 
	{
		return _quarry.getCurrentTicks() < _quarry.getMaxTicks();
	}
	
	public double addEnergy(double energy) 
	{
		int power = (int) (energy / VMAConfig.ratioIC2);
		if (canAddEnergy(energy))
		{
			_quarry.setCurrentTicks(_quarry.getCurrentTicks() + power);
			return power * VMAConfig.ratioIC2;
		}
		return 0;
	}
	
	public double addEnergy(double energy, boolean simulate) 
	{
		return addEnergy(energy);
	}
	
	public boolean canUseEnergy(double energy) 
	{
		int power = (int) (energy / VMAConfig.ratioIC2);
		return power > _quarry.getCurrentTicks();
	}
	
	public double useEnergy(double energy) 
	{
		int power = (int) (energy / VMAConfig.ratioIC2);
		if (canUseEnergy(energy))
		{
			_quarry.setCurrentTicks(_quarry.getCurrentTicks() - power);
			return energy;
		}
		return 0;
	}
	
	public double useEnergy(double energy, boolean simulate) 
	{
		return useEnergy(energy);
	}
	
	public boolean canAcceptEnergy(EnumFacing direction) 
	{
		return true;
	}
	
	public boolean canProvideEnergy(EnumFacing direction) 
	{
		return false;
	}
	
	public double getMaxOutput() 
	{
		return -1;
	}
	
	public double getMaxInput() 
	{
		return _quarry.getOneOperationCost() * VMAConfig.ratioIC2;
	}
	
//	public EnumPowerTier getTier() 
//	{
//		return EnumPowerTier.INFINITE;
//	}
	
	//====================================== IC2 ======================================
	
	public boolean acceptsEnergyFrom(IEnergyEmitter energyEmitter, EnumFacing face) 
	{
		return true;
	}
	
	public double getDemandedEnergy() 
	{
		return _quarry.getOneOperationCost() * VMAConfig.quarryOperations * VMAConfig.ratioIC2;
	}
	
	public int getSinkTier() 
	{
		return 1;
	}
	
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) 
	{
		if(_quarry.getCurrentTicks() < _quarry.getMaxTicks())
		{
			addPower((int) (amount / VMAConfig.ratioIC2), false);
			return amount;
		}
		return amount;
	}
	
	//====================================== ImmersiveEngineering ======================================
	
//	public final IFluxReceiver _fluxReceiverIE = new IFluxReceiver()
//	{
//		public boolean canConnectEnergy(EnumFacing face) 
//		{
//			return true;
//		}
//		
//		public int getEnergyStored(EnumFacing face) 
//		{
//			return _quarry.getCurrentTicks() * VMAConfig.ratioRF;
//		}
//		
//		public int getMaxEnergyStored(EnumFacing face) 
//		{
//			return _quarry.getMaxTicks() * VMAConfig.ratioRF;
//		}
//		
//		public int receiveEnergy(EnumFacing face, int maxReceive, boolean simulate) 
//		{
//			return addPower(maxReceive / VMAConfig.ratioRF, simulate);
//		}		
//	};
	
	//====================================== Mekanism ======================================
	
	public double getEnergy() 
	{
		return _quarry.getCurrentTicks() * VMAConfig.ratioMekanism;
	}
	
	public void setEnergy(double energy) 
	{
		addPower((int) (energy / VMAConfig.ratioMekanism), false);
	}
	
	public double getMaxEnergy() 
	{
		return _quarry.getMaxTicks() * VMAConfig.ratioMekanism;
	}
	
	public boolean canReceiveEnergy(EnumFacing face) 
	{
		return true;
	}
	
	public double transferEnergyToAcceptor(EnumFacing face, double amount) 
	{
		if (canAddEnergy(amount))
		{
			addEnergy(amount);
			return 0;
		}
		return amount;
	}
	
	//====================================== MinecraftFlux ======================================
	
//	public final IEnergy _minecraftFlux = new IEnergy()
//	{
//		public boolean canInputEnergy() 
//		{
//			return true;
//		}
//		
//		public boolean canOutputEnergy() 
//		{
//			return false;
//		}
//		
//		public long getEnergy() 
//		{
//			return _quarry.getCurrentTicks() * VMAConfig.ratioRF;
//		}
//		
//		public long getEnergyCapacity() 
//		{
//			return _quarry.getMaxTicks() * VMAConfig.ratioRF;
//		}
//		
//		public long inputEnergy(long amount, boolean simulation) 
//		{
//			if(canAddEnergy(amount))
//			{
//				addEnergy(amount);
//				return amount;
//			}
//			return 0;
//		}
//		
//		public long outputEnergy(long amount, boolean simulation) 
//		{
//			return 0;
//		}
//	};
}