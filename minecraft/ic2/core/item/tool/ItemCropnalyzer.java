package ic2.core.item.tool;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.BlockCrop;
import ic2.core.item.ElectricItem;
import ic2.core.item.IHandHeldInventory;
import ic2.core.item.ItemIC2;
import ic2.core.item.tool.ContainerCropnalyzer;
import ic2.core.item.tool.HandHeldCropnalyzer;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemCropnalyzer extends ItemElectricTool {
	
	static int tier = 2;
	static int energyAtOperation = 1888;

	 public ItemCropnalyzer(int id, int sprite) {
	      super(id, sprite, EnumToolMaterial.IRON, 50);
	      super.maxCharge = 100000;
	      super.transferLimit = 100;
	      super.tier = 2;
	      
	   }


   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.rare;
   }


	public static  int getEuConsume() 
	{
		return energyAtOperation;
	}
	
	public static  int getTir() 
	{
		return tier;
	}
	
	 public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean debugTooltips) {
	      info.add("§dИспользуйте ПКМ по жёрдочке");
	   }
 

  
}
