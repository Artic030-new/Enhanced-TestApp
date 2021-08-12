package ic2.core.block.machine.tileentity;

import ic2.api.Ic2Recipes;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.util.StackUtil;
import java.util.List;
import java.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEntityMacerator extends TileEntityElectricMachine {

   public static List recipes = new Vector();


   public TileEntityMacerator() {
      super(3, 2, 400, 32);
   }

   public static void init() {
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.oreIron), StackUtil.copyWithSize(Ic2Items.ironDust, 2));
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.oreGold), StackUtil.copyWithSize(Ic2Items.goldDust, 2));
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Item.coal), Ic2Items.coalDust);
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Item.ingotIron), Ic2Items.ironDust);
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Item.ingotGold), Ic2Items.goldDust);
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.cloth), new ItemStack(Item.silk));
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.gravel), new ItemStack(Item.flint));
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.stone), new ItemStack(Block.cobblestone));
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.cobblestone), new ItemStack(Block.sand));
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.sandStone), new ItemStack(Block.sand));
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.ice), new ItemStack(Item.snowball));
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.blockClay), StackUtil.copyWithSize(Ic2Items.clayDust, 2));
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.glowStone), new ItemStack(Item.lightStoneDust, 4));
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Item.bone), new ItemStack(Item.dyePowder, 5, 15));
      Ic2Recipes.addMaceratorRecipe(Ic2Items.plantBall, new ItemStack(Block.dirt, 8));
      Ic2Recipes.addMaceratorRecipe(Ic2Items.coffeeBeans, new ItemStack(Ic2Items.coffeePowder.getItem(), 3));
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Item.blazeRod), new ItemStack(Item.blazePowder, 5));
      Ic2Recipes.addMaceratorRecipe(new ItemStack(Item.spiderEye), new ItemStack(Ic2Items.grinPowder.getItem(), 2));
   }

   public ItemStack getResultFor(ItemStack itemStack, boolean adjustInput) {
      return Ic2Recipes.getMaceratorOutputFor(itemStack, adjustInput);
   }

   public String getInvName() {
      return "Macerator";
   }

   public String getGuiClassName(EntityPlayer entityPlayer) {
      return "block.machine.gui.GuiMacerator";
   }

   public String getStartSoundFile() {
      return "Machines/MaceratorOp.ogg";
   }

   public String getInterruptSoundFile() {
      return "Machines/InterruptOne.ogg";
   }

   public float getWrenchDropRate() {
      return 0.85F;
   }

}
