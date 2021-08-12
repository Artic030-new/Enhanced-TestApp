package ic2.core.block.machine.tileentity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cpw.mods.fml.relauncher.ReflectionHelper;
import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.machine.ContainerMiner;
import ic2.core.item.ElectricItem;
import ic2.core.item.tool.ItemElectricToolDDrill;
import ic2.core.item.tool.ItemElectricToolDrill;
import ic2.core.item.tool.ItemScanner;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ISidedInventory;

public class TileEntityMiner
  extends TileEntityElecMachine
  implements IHasGui, ISidedInventory
{
  public TileEntityMiner()
  {
    super(4, 0, 1000, 32, IC2.enableMinerLapotron ? 3 : 1);
  }
  
  public void updateEntity()
  {
    super.updateEntity();
    
    boolean wasOperating = isOperating();
    boolean needsInvUpdate = false;
    if (isOperating())
    {
      this.energy -= 1;
      if ((this.inventory[1] != null) && ((Item.itemsList[this.inventory[1].itemID] instanceof ItemScanner))) {
        this.energy -= ElectricItem.charge(this.inventory[1], this.energy, 2, false, false);
      }
      if ((this.inventory[3] != null) && (((Item.itemsList[this.inventory[3].itemID] instanceof ItemElectricToolDrill)) || ((Item.itemsList[this.inventory[3].itemID] instanceof ItemElectricToolDDrill)))) {
        this.energy -= ElectricItem.charge(this.inventory[3], this.energy, 1, false, false);
      }
    }
    if (this.energy <= this.maxEnergy) {
      needsInvUpdate = provideEnergy();
    }
    if (wasOperating) {
      needsInvUpdate = mine();
    } else if (this.inventory[3] == null) {
      if ((this.energy >= 2) && (canWithdraw()))
      {
        this.targetY = -1;
        this.miningTicker = ((short)(this.miningTicker + 1));
        this.energy -= 2;
        if (this.miningTicker >= 20)
        {
          this.miningTicker = 0;
          needsInvUpdate = withdrawPipe();
        }
      }
      else if (isStuck())
      {
        this.miningTicker = 0;
      }
    }
    setActive(isOperating());
    if (wasOperating != isOperating()) {
      needsInvUpdate = true;
    }
    if(needsInvUpdate) {
        this.onInventoryChanged();
     }
  }
  
  public void onUnloaded()
  {
    if ((IC2.platform.isRendering()) && (this.audioSource != null))
    {
      IC2.audioManager.removeSources(this);
      this.audioSource = null;
    }
    super.onUnloaded();
  }
  
  public int targetX = 0;
  public int targetY = -1;
  public int targetZ = 0;
  public short miningTicker = 0;
  public String stuckOn = null;
  private AudioSource audioSource;
  
  public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.targetX = nbttagcompound.getInteger("targetX");
      this.targetY = nbttagcompound.getInteger("targetY");
      this.targetZ = nbttagcompound.getInteger("targetZ");
      this.miningTicker = nbttagcompound.getShort("miningTicker");
   }

   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setInteger("targetX", (short)this.targetX);
      nbttagcompound.setInteger("targetY", (short)this.targetY);
      nbttagcompound.setInteger("targetZ", (short)this.targetZ);
      nbttagcompound.setShort("miningTicker", this.miningTicker);
   }
   
   public boolean mine() {
	      if(this.targetY < 0) {
	         this.aquireTarget();
	         return false;
	      } else {
	         int id;
	         if(!this.canReachTarget(this.targetX, this.targetY, this.targetZ, true)) {
	            id = this.targetX - super.xCoord;
	            int z = this.targetZ - super.zCoord;
	            if(Math.abs(id) > Math.abs(z)) {
	               if(id > 0) {
	                  --this.targetX;
	               } else {
	                  ++this.targetX;
	               }
	            } else if(z > 0) {
	               --this.targetZ;
	            } else {
	               ++this.targetZ;
	            }

	            return false;
	         } else if(this.canMine(this.targetX, this.targetY, this.targetZ)) {
	            this.stuckOn = null;
	            ++this.miningTicker;
	            --super.energy;
	            if(super.inventory[3].itemID == Ic2Items.diamondDrill.itemID) {
	               this.miningTicker = (short)(this.miningTicker + 3);
	               super.energy -= 14;
	            }

	            if(this.miningTicker >= 90) {
	               this.miningTicker = 0;
	               this.mineBlock();
	               return true;
	            } else {
	               return false;
	            }
	         } else {
	            id = super.worldObj.getBlockId(this.targetX, this.targetY, this.targetZ);
	            if((id == Block.waterMoving.blockID || id == Block.waterStill.blockID || id == Block.lavaMoving.blockID || id == Block.lavaStill.blockID) && this.isAnyPumpConnected()) {
	               return false;
	            } else {
	               this.miningTicker = -1;
	               this.stuckOn = Block.blocksList[id].translateBlockName();
	               return false;
	            }
	         }
	      }
	   }
  
  public void mineBlock()
  {
    if ((this.inventory[3].getItem() instanceof ItemElectricToolDrill)) {
      ElectricItem.use(this.inventory[3], 50, null);
    } else if ((this.inventory[3].getItem() instanceof ItemElectricToolDDrill)) {
      ElectricItem.use(this.inventory[3], 80, null);
    }
    int id = super.worldObj.getBlockId(this.targetX, this.targetY, this.targetZ);
    int meta = super.worldObj.getBlockMetadata(this.targetX, this.targetY, this.targetZ);
    
    boolean liquid = false;
    if(id == Block.waterMoving.blockID || id == Block.waterStill.blockID || id == Block.lavaMoving.blockID || id == Block.lavaStill.blockID) 
    {
      liquid = true;
      if (meta != 0) {
        id = 0;
      }
    }
    if(id != 0) {
        if(!liquid) {
           Block ore = Block.blocksList[id];
           StackUtil.distributeDrop(this, ore.getBlockDropped(super.worldObj, this.targetX, this.targetY, this.targetZ, meta, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, super.inventory[3])));
        } else {
           if(id == Block.waterMoving.blockID || id == Block.waterStill.blockID) {
              this.usePump(Block.waterStill.blockID);
           }

           if(id == Block.lavaMoving.blockID || id == Block.lavaStill.blockID) {
              this.usePump(Block.lavaStill.blockID);
           }
        }

        super.worldObj.setBlockWithNotify(this.targetX, this.targetY, this.targetZ, 0);
        super.energy -= 2 * (super.yCoord - this.targetY);
     }
    
    if ((this.targetX == this.xCoord) && (this.targetZ == this.zCoord))
    {
    	super.worldObj.setBlock(this.targetX, this.targetY, this.targetZ, Ic2Items.miningPipe.itemID);
      if (this.inventory[2].stackSize == 0) {
        this.inventory[2] = null;
      }
      this.energy -= 10;
    }
    updateMineTip(this.targetY);
    
    this.targetY = -1;
  }
  
  public boolean withdrawPipe() {
      int y = this.getPipeTip();
      int blockId = super.worldObj.getBlockId(super.xCoord, y, super.zCoord);
      if(blockId != 0) {
         StackUtil.distributeDrop(this, Block.blocksList[blockId].getBlockDropped(super.worldObj, super.xCoord, y, super.zCoord, super.worldObj.getBlockMetadata(super.xCoord, y, super.zCoord), 0));
         super.worldObj.setBlockWithNotify(super.xCoord, y, super.zCoord, 0);
      }

      if(super.inventory[2] != null && super.inventory[2].itemID != Ic2Items.miningPipe.itemID && super.inventory[2].itemID < Block.blocksList.length && Block.blocksList[super.inventory[2].itemID] != null && Block.blocksList[super.inventory[2].itemID].blockID != 0) {
         super.worldObj.setBlockAndMetadataWithNotify(super.xCoord, y, super.zCoord, super.inventory[2].itemID, super.inventory[2].getItemDamage());
         --super.inventory[2].stackSize;
         if(super.inventory[2].stackSize == 0) {
            super.inventory[2] = null;
         }

         this.updateMineTip(y + 1);
         return true;
      } else {
         this.updateMineTip(y + 1);
         return false;
      }
   }
  
  public void updateMineTip(int low) {
      if(low != super.yCoord) {
         int x = super.xCoord;
         int y = super.yCoord - 1;

         int z;
         for(z = super.zCoord; y > low; --y) {
            int id = super.worldObj.getBlockId(x, y, z);
            if(id != Ic2Items.miningPipe.itemID && super.inventory[2] != null && super.inventory[2].stackSize > 0) {
               super.worldObj.setBlockWithNotify(x, y, z, Ic2Items.miningPipe.itemID);
               --super.inventory[2].stackSize;
               if(super.inventory[2].stackSize <= 0) {
                  super.inventory[2] = null;
               }
            }
         }

         super.worldObj.setBlockWithNotify(x, low, z, Ic2Items.miningPipeTip.itemID);
      }

   }
  
  public boolean canReachTarget(int x, int y, int z, boolean ignore)
  {
    if ((this.xCoord == x) && (this.zCoord == z)) {
      return true;
    }
    if ((!ignore) && (!canPass(this.worldObj.getBlockId(x, y, z)))) {
      return false;
    }
    int xdif = x - this.xCoord;
    int zdif = z - this.zCoord;
    if (Math.abs(xdif) > Math.abs(zdif))
    {
      if (xdif > 0) {
        x--;
      } else {
        x++;
      }
    }
    else if (zdif > 0) {
      z--;
    } else {
      z++;
    }
    return canReachTarget(x, y, z, false);
  }
  
  public void aquireTarget()
  {
    int y = getPipeTip();
    if ((y >= this.yCoord) || (this.inventory[1] == null) || (!(this.inventory[1].getItem() instanceof ItemScanner)))
    {
      setTarget(this.xCoord, y - 1, this.zCoord);
      
      return;
    }
    int scanrange = ((ItemScanner)this.inventory[1].getItem()).startLayerScan(this.inventory[1]);
    if (scanrange > 0) {
      for (int x = this.xCoord - scanrange; x <= this.xCoord + scanrange; x++) {
        for (int z = this.zCoord - scanrange; z <= this.zCoord + scanrange; z++)
        {
          int n = this.worldObj.getBlockId(x, y, z);
          int m = this.worldObj.getBlockMetadata(x, y, z);
          if (((ItemScanner.isValuable(n, m)) && (canMine(x, y, z))) || ((isAnyPumpConnected()) && (this.worldObj.getBlockMetadata(x, y, z) == 0) && ((n == Block.lavaMoving.blockID) || (n == Block.lavaStill.blockID))))
          {
            setTarget(x, y, z);
            
            return;
          }
        }
      }
    }
    setTarget(this.xCoord, y - 1, this.zCoord);
  }
  
  public void setTarget(int x, int y, int z)
  {
    this.targetX = x;
    this.targetY = y;
    this.targetZ = z;
  }
  
  public int getPipeTip()
  {
    int y = this.yCoord;
    while ((this.worldObj.getBlockId(this.xCoord, y - 1, this.zCoord) == Ic2Items.miningPipe.itemID) || (this.worldObj.getBlockId(this.xCoord, y - 1, this.zCoord) == Ic2Items.miningPipeTip.itemID)) {
      y--;
    }
    return y;
  }
  
  public boolean canPass(int id)
  {
    if ((id == 0) || (id == Block.waterMoving.blockID) || (id == Block.waterStill.blockID) || (id == Block.lavaMoving.blockID) || (id == Block.lavaStill.blockID) || (id == Ic2Items.miner.itemID) || (id == Ic2Items.miningPipe.itemID) || (id == Ic2Items.miningPipeTip.itemID)) {
      return true;
    }
    return false;
  }
  
  public boolean isOperating()
  {
    return (this.energy > 100) && (canOperate());
  }
  
  public boolean canOperate()
  {
    if ((this.inventory[2] == null) || (this.inventory[3] == null)) {
      return false;
    }
    if (this.inventory[2].itemID != Ic2Items.miningPipe.itemID) {
      return false;
    }
    if ((this.inventory[3].itemID != Ic2Items.miningDrill.itemID) && (this.inventory[3].itemID != Ic2Items.diamondDrill.itemID)) {
      return false;
    }
    return !isStuck();
  }
  
  public boolean isStuck()
  {
    return this.miningTicker < 0;
  }
  
  public String getStuckOn()
  {
    return this.stuckOn;
  }
  
  public boolean canMine(int x, int y, int z)
  {
    int id = this.worldObj.getBlockId(x, y, z);
    int meta = this.worldObj.getBlockMetadata(x, y, z);
    if (id == 0) {
      return true;
    }
    if ((id == Ic2Items.miningPipe.itemID) || (id == Ic2Items.miningPipeTip.itemID) ||
    	(id == Block.chest.blockID) || (id == Block.wood.blockID) || 
    	(id == Block.planks.blockID) || (id == Block.glass.blockID) || 
    	(id == 230) || (id == 231) || (id == 222) ||  (id == 221) ||  (id == 975) ||
    	(id == 35) || (id == 44) || (id == 45) || (id == 49) || (id == 194) || (id == 223) || 
    	(id == 225)|| (id == 257) || (id == 246) || (id == 250) || (id == 255) || (id == 754)) 
    {
      return false;
    }
    if (((id == Block.waterMoving.blockID) || (id == Block.waterStill.blockID) || (id == Block.lavaMoving.blockID) || (id == Block.lavaStill.blockID)) && (isPumpConnected())) {
      return true;
    }
    
    Block block = Block.blocksList[id];
    if (block.getBlockHardness(this.worldObj, x, y, z) < 0.0F) {
      return false;
    }
    if ((block.canCollideCheck(meta, false)) && (block.blockMaterial.isToolNotRequired())) {
      return true;
    }
    if (id == Block.web.blockID) {
      return true;
    }
    if ((this.inventory[3] != null) && ((this.inventory[3].itemID != Ic2Items.miningDrill.itemID) || (this.inventory[3].itemID != Ic2Items.diamondDrill.itemID))) {
      try
      {
        HashMap toolClasses = (HashMap)ReflectionHelper.getPrivateValue(ForgeHooks.class, null, new String[] { "toolClasses" });
        List tc = (List)toolClasses.get(Integer.valueOf(this.inventory[3].itemID));
        if (tc == null) {
          return this.inventory[3].canHarvestBlock(block);
        }
        Object[] ta = tc.toArray();
        String cls = (String)ta[0];int hvl = ((Integer)ta[1]).intValue();
        
        HashMap toolHarvestLevels = (HashMap)ReflectionHelper.getPrivateValue(ForgeHooks.class, null, new String[] { "toolHarvestLevels" });
        Integer bhl = (Integer)toolHarvestLevels.get(Arrays.asList(new Serializable[] { Integer.valueOf(block.blockID), Integer.valueOf(meta), cls }));
        if (bhl == null) {
          return this.inventory[3].canHarvestBlock(block);
        }
        if (bhl.intValue() > hvl) {
          return false;
        }
        return this.inventory[3].canHarvestBlock(block);
      }
      catch (Throwable e)
      {
        return false;
      }
    }
    return false;
  }
  
  public boolean canWithdraw()
  {
      return super.worldObj.getBlockId(super.xCoord, super.yCoord - 1, super.zCoord) == Ic2Items.miningPipe.itemID || super.worldObj.getBlockId(super.xCoord, super.yCoord - 1, super.zCoord) == Ic2Items.miningPipeTip.itemID;
  }
  
  public boolean isPumpConnected()
  {
      return super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord + 1, super.zCoord) instanceof TileEntityPump && ((TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord + 1, super.zCoord)).canHarvest()?true:(super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord - 1, super.zCoord) instanceof TileEntityPump && ((TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord - 1, super.zCoord)).canHarvest()?true:(super.worldObj.getBlockTileEntity(super.xCoord + 1, super.yCoord, super.zCoord) instanceof TileEntityPump && ((TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord + 1, super.yCoord, super.zCoord)).canHarvest()?true:(super.worldObj.getBlockTileEntity(super.xCoord - 1, super.yCoord, super.zCoord) instanceof TileEntityPump && ((TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord - 1, super.yCoord, super.zCoord)).canHarvest()?true:(super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord + 1) instanceof TileEntityPump && ((TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord + 1)).canHarvest()?true:super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord - 1) instanceof TileEntityPump && ((TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord - 1)).canHarvest()))));
  }
  
  public boolean isAnyPumpConnected()
  {
    if (super.worldObj.getBlockTileEntity(this.xCoord, this.yCoord + 1, this.zCoord) instanceof TileEntityPump) {
      return true;
    }
    if ((super.worldObj.getBlockTileEntity(this.xCoord, this.yCoord - 1, this.zCoord) instanceof TileEntityPump)) {
      return true;
    }
    if ((super.worldObj.getBlockTileEntity(this.xCoord + 1, this.yCoord, this.zCoord) instanceof TileEntityPump)) {
      return true;
    }
    if ((super.worldObj.getBlockTileEntity(this.xCoord - 1, this.yCoord, this.zCoord) instanceof TileEntityPump)) {
      return true;
    }
    if ((super.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof TileEntityPump)) {
      return true;
    }
    if ((super.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof TileEntityPump)) {
      return true;
    }
    return false;
  }
  
  public void usePump(int id)
  {
    if (((super.worldObj.getBlockTileEntity(this.xCoord, this.yCoord + 1, this.zCoord) instanceof TileEntityPump)) && ((TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord + 1, super.zCoord)).canHarvest())
    {
      ((TileEntityPump)this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord + 1, this.zCoord)).pumpThis(id);
      return;
    }
    if (((super.worldObj.getBlockTileEntity(this.xCoord, this.yCoord - 1, this.zCoord) instanceof TileEntityPump)) && ((TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord - 1, super.zCoord)).canHarvest()) 
    {
      ((TileEntityPump)this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord - 1, this.zCoord)).pumpThis(id);
      return;
    }
    if (((super.worldObj.getBlockTileEntity(this.xCoord + 1, this.yCoord, this.zCoord) instanceof TileEntityPump)) && ((TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord + 1, super.yCoord, super.zCoord)).canHarvest())
    {
      ((TileEntityPump)this.worldObj.getBlockTileEntity(this.xCoord + 1, this.yCoord, this.zCoord)).pumpThis(id);
      return;
    }
    if (((super.worldObj.getBlockTileEntity(this.xCoord - 1, this.yCoord, this.zCoord) instanceof TileEntityPump)) && ((TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord - 1, super.yCoord, super.zCoord)).canHarvest())
    {
      ((TileEntityPump)this.worldObj.getBlockTileEntity(this.xCoord - 1, this.yCoord, this.zCoord)).pumpThis(id);
      return;
    }
    if (((super.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof TileEntityPump)) && ((TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord + 1)).canHarvest())
    {
      ((TileEntityPump)this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord + 1)).pumpThis(id);
      return;
    }
    if (((super.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof TileEntityPump)) && ((TileEntityPump)super.worldObj.getBlockTileEntity(super.xCoord + 1, super.yCoord, super.zCoord - 1)).canHarvest())
    {
      ((TileEntityPump)this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord - 1)).pumpThis(id);
      return;
    }
  }
  
  public String getInvName()
  {
    return "Miner";
  }
  
  public int gaugeEnergyScaled(int i)
  {
    if (this.energy <= 0) {
      return 0;
    }
    int r = this.energy * i / 1000;
    if (r > i) {
      r = i;
    }
    return r;
  }
  
  public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer)
  {
    return new ContainerMiner(entityPlayer, this);
  }
  
  public String getGuiClassName(EntityPlayer entityPlayer)
  {
    return "block.machine.gui.GuiMiner";
  }
  
  public void onGuiClosed(EntityPlayer entityPlayer) {}
  
  public void onNetworkUpdate(String field)
  {
    if ((field.equals("active")) && (this.prevActive != getActive()))
    {
      if (this.audioSource == null) {
        this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Machines/MinerOp.ogg", true, false, IC2.audioManager.defaultVolume);
      }
      if (getActive())
      {
        if (this.audioSource != null) {
          this.audioSource.play();
        }
      }
      else if (this.audioSource != null) {
        this.audioSource.stop();
      }
    }
    super.onNetworkUpdate(field);
  }
  
  public int getStartInventorySide(ForgeDirection side)
  {
    ForgeDirection leftSide;
    ForgeDirection rightSide;
    ForgeDirection frontSide;
    ForgeDirection backSide;
    switch (getFacing())
    {
    case 2: 
      leftSide = ForgeDirection.WEST;
      rightSide = ForgeDirection.EAST;
      frontSide = ForgeDirection.SOUTH;
      backSide = ForgeDirection.NORTH;
      break;
    case 3: 
      leftSide = ForgeDirection.EAST;
      rightSide = ForgeDirection.WEST;
      frontSide = ForgeDirection.NORTH;
      backSide = ForgeDirection.SOUTH;
      break;
    case 4: 
      leftSide = ForgeDirection.SOUTH;
      rightSide = ForgeDirection.NORTH;
      frontSide = ForgeDirection.EAST;
      backSide = ForgeDirection.WEST;
      break;
    default: 
      leftSide = ForgeDirection.NORTH;
      rightSide = ForgeDirection.SOUTH;
      frontSide = ForgeDirection.WEST;
      backSide = ForgeDirection.EAST;
    }
    if ((side == leftSide) || (side == frontSide)) {
      return 3;
    }
    if ((side == rightSide) || (side == backSide)) {
      return 1;
    }
    switch (side)
    {
    case DOWN: 
      return 0;
	default:
		break;
    }
    return 2;
  }
  
  public int getSizeInventorySide(ForgeDirection side)
  {
    return 1;
  }
}
