package ic2.core.block.generator.tileentity;

import ic2.core.ContainerIC2;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.generator.container.ContainerWindGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.util.StackUtil;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class TileEntityWindGenerator extends TileEntityBaseGenerator implements ISidedInventory {

   public static Random randomizer = new Random();
   public double subproduction = 0.0D;
   public double substorage = 0.0D;
   public int ticker;
   public int obscuratedBlockCount;


   public TileEntityWindGenerator() {
      super(1, 4, 4);
      this.ticker = randomizer.nextInt(this.tickRate());
   }

   public int gaugeFuelScaled(int i) {
      double prod = this.subproduction / 3.0D;
      int re = (int)(prod * (double)i);
      return re < 0?0:(re > i?i:re);
   }

   public int getOverheatScaled(int i) {
      double prod = (this.subproduction - 5.0D) / 5.0D;
      return this.subproduction <= 5.0D?0:(this.subproduction >= 10.0D?i:(int)(prod * (double)i));
   }

   public void onLoaded() {
      super.onLoaded();
      this.updateObscuratedBlockCount();
   }

   public boolean gainEnergy() {
      ++this.ticker;
      if(this.ticker % this.tickRate() == 0) {
         if(this.ticker % (8 * this.tickRate()) == 0) {
            this.updateObscuratedBlockCount();
         }

         this.subproduction = (double)(IC2.windStrength * (super.yCoord - 64 - this.obscuratedBlockCount)) / 750.0D;
         if(this.subproduction <= 0.0D) {
            return false;
         }

         if(super.worldObj.isThundering()) {
            this.subproduction *= 1.5D;
         } else if(super.worldObj.isRaining()) {
            this.subproduction *= 1.2D;
         }

         if(this.subproduction > 5.0D && (double)super.worldObj.rand.nextInt(5000) <= this.subproduction - 5.0D) {
            this.subproduction = 0.0D;
            super.worldObj.setBlockAndMetadataWithNotify(super.xCoord, super.yCoord, super.zCoord, Ic2Items.generator.itemID, Ic2Items.generator.getItemDamage());

            for(int i = super.worldObj.rand.nextInt(5); i > 0; --i) {
               StackUtil.dropAsEntity(super.worldObj, super.xCoord, super.yCoord, super.zCoord, new ItemStack(Item.ingotIron));
            }

            return false;
         }

         this.subproduction *= (double)IC2.energyGeneratorWind;
         this.subproduction /= 100.0D;
      }

      this.substorage += this.subproduction;
      super.production = (short)((int)this.substorage);
      if(super.storage + super.production >= super.maxStorage) {
         this.substorage = 0.0D;
         return false;
      } else {
         super.storage = (short)(super.storage + super.production);
         this.substorage -= (double)super.production;
         return true;
      }
   }

   public boolean gainFuel() {
      return false;
   }

   public void updateObscuratedBlockCount() {
      this.obscuratedBlockCount = -1;

      for(int x = -4; x < 5; ++x) {
         for(int y = -2; y < 5; ++y) {
            for(int z = -4; z < 5; ++z) {
               if(super.worldObj.getBlockId(x + super.xCoord, y + super.yCoord, z + super.zCoord) != 0) {
                  ++this.obscuratedBlockCount;
               }
            }
         }
      }

   }

   public boolean needsFuel() {
      return true;
   }

   public int getMaxEnergyOutput() {
      return 10;
   }

   public String getInvName() {
      return "Wind Mill";
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.generator.gui.GuiWindGenerator";
   }

   public int tickRate() {
      return 128;
   }

   public String getOperationSoundFile() {
      return "Generators/WindGenLoop.ogg";
   }

   public boolean delayActiveUpdate() {
      return true;
   }

   public ContainerIC2 getGuiContainer(EntityPlayer entityPlayer) {
      return new ContainerWindGenerator(entityPlayer, this);
   }

   public int getStartInventorySide(ForgeDirection side) {
      return 0;
   }

   public int getSizeInventorySide(ForgeDirection side) {
      return 1;
   }

}
