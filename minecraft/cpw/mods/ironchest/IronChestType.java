package cpw.mods.ironchest;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.ironchest.BlockIronChest;
import cpw.mods.ironchest.TileEntityCopperChest;
import cpw.mods.ironchest.TileEntityCrystalChest;
import cpw.mods.ironchest.TileEntityDiamondChest;
import cpw.mods.ironchest.TileEntityGoldChest;
import cpw.mods.ironchest.TileEntityIronChest;
import cpw.mods.ironchest.TileEntityObsidianChest;
import cpw.mods.ironchest.TileEntitySilverChest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public enum IronChestType {
   IRON(54, 9, true, "Железный сундук", "ironchest.png", 0, Arrays.asList("ingotIron", "ingotRefinedIron"), TileEntityIronChest.class, new String[]{"mmmmPmmmm", "mGmG3GmGm"}),
   GOLD(81, 9, true, "Золотой сундук", "goldchest.png", 1, Arrays.asList("ingotGold"), TileEntityGoldChest.class, new String[]{"mmmmPmmmm", "mGmG4GmGm"}),
   DIAMOND(108, 12, true, "Алмазный сундук", "diamondchest.png", 2, Arrays.asList("gemDiamond"), TileEntityDiamondChest.class, new String[]{"GGGmPmGGG", "GGGG4Gmmm"}),
   COPPER(45, 9, false, "Медный сундук", "copperchest.png", 3, Arrays.asList("ingotCopper"), TileEntityCopperChest.class, new String[]{"mmmmCmmmm"}),
   SILVER(72, 9, false, "Серебряный сундук", "silverchest.png", 4, Arrays.asList("ingotSilver"), TileEntitySilverChest.class, new String[]{"mmmm3mmmm", "mGmG0GmGm"}),
   CRYSTAL(108, 12, true, "Кристальный сундук", "crystalchest.png", 5, Arrays.asList("blockGlass"), TileEntityCrystalChest.class, new String[]{"GGGGPGGGG"}),
   OBSIDIAN(108, 12, false, "Обсидиановый сундук", "obsidianchest.png", 6, Arrays.asList("obsidian"), TileEntityObsidianChest.class, new String[]{"mmmm2mmmm"}),
   WOOD(0, 0, false, "", "", -1, Arrays.asList("plankWood"), (Class)null, new String[0]);
	
   int size;
   private int rowLength;
   public String friendlyName;
   private boolean tieredChest;
   private String modelTexture;
   private int textureRow;
   public Class clazz;
   private String[] recipes;
   private ArrayList matList;

   private IronChestType(int size, int rowLength, boolean tieredChest, String friendlyName, String modelTexture, int textureRow, List mats, Class clazz, String ... recipes) {
      this.size = size;
      this.rowLength = rowLength;
      this.tieredChest = tieredChest;
      this.friendlyName = friendlyName;
      this.modelTexture = "/cpw/mods/ironchest/sprites/" + modelTexture;
      this.textureRow = textureRow;
      this.clazz = clazz;
      this.recipes = recipes;
      this.matList = new ArrayList();
      this.matList.addAll(mats);
   }

   public String getModelTexture() {
      return this.modelTexture;
   }

   public int getTextureRow() {
      return this.textureRow;
   }

   public static TileEntityIronChest makeEntity(int metadata) {
      int chesttype = validateMeta(metadata);
      if(chesttype == metadata) {
         try {
            TileEntityIronChest e = (TileEntityIronChest)values()[chesttype].clazz.newInstance();
            return e;
         } catch (InstantiationException var3) {
            var3.printStackTrace();
         } catch (IllegalAccessException var4) {
            var4.printStackTrace();
         }
      }

      return null;
   }

   public static void registerTranslations() {}

   public static void generateTieredRecipes(BlockIronChest blockResult) {
      ItemStack previous = new ItemStack(Block.chest);
      IronChestType[] arr$ = values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         IronChestType typ = arr$[i$];
         generateRecipesForType(blockResult, previous, typ);
         if(typ.tieredChest) {
            previous = new ItemStack(blockResult, 1, typ.ordinal());
         }
      }

   }

   public static void generateRecipesForType(BlockIronChest blockResult, Object previousTier, IronChestType type) {
      String[] arr$ = type.recipes;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String recipe = arr$[i$];
         String[] recipeSplit = new String[]{recipe.substring(0, 3), recipe.substring(3, 6), recipe.substring(6, 9)};
         Object mainMaterial = null;
         Iterator i$1 = type.matList.iterator();

         while(i$1.hasNext()) {
            String mat = (String)i$1.next();
            mainMaterial = translateOreName(mat);
            addRecipe(new ItemStack(blockResult, 1, type.ordinal()), new Object[]{recipeSplit, Character.valueOf('m'), mainMaterial, Character.valueOf('P'), previousTier, Character.valueOf('G'), Block.glass, Character.valueOf('C'), Block.chest, Character.valueOf('0'), new ItemStack(blockResult, 1, 0), Character.valueOf('1'), new ItemStack(blockResult, 1, 1), Character.valueOf('2'), new ItemStack(blockResult, 1, 2), Character.valueOf('3'), new ItemStack(blockResult, 1, 3), Character.valueOf('4'), new ItemStack(blockResult, 1, 4)});
         }
      }

   }

   public static Object translateOreName(String mat) {
	  if (mat.equals("ingotIron")) {
	     return Item.ingotIron;
	  } else if (mat.equals("ingotGold")) {
         return Item.ingotGold;
	  } else if (mat.equals("gemDiamond")) {
	     return Item.diamond;
	  } else if (mat.equals("blockGlass")) {
	     return Block.glass;
	  } else {
	     return mat.equals("obsidian") ? Block.obsidian : mat;
	  }
   }

   public static void addRecipe(ItemStack is, Object ... parts) {
      ShapedOreRecipe oreRecipe = new ShapedOreRecipe(is, parts);
      GameRegistry.addRecipe(oreRecipe);
   }

   public int getRowCount() {
      return this.size / this.rowLength;
   }

   public int getRowLength() {
      return this.rowLength;
   }

   public boolean isTransparent() {
      return this == CRYSTAL;
   }

   public List getMatList() {
      return this.matList;
   }

   public static int validateMeta(int i) {
      return i < values().length && values()[i].size > 0?i:0;
   }

   public boolean isValidForCreativeMode() {
      return validateMeta(this.ordinal()) == this.ordinal();
   }

   public boolean isExplosionResistant() {
      return this == OBSIDIAN;
   }

}
