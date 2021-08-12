package cpw.mods.ironchest;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.ItemChestChanger;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

public enum ChestChangerType {
   IRONGOLD("IRONGOLD", 0, IronChestType.IRON, IronChestType.GOLD, "ironGoldUpgrade", "Апгрейд (железный в золотой)", new String[]{"mmm", "msm", "mmm"}),
   GOLDDIAMOND("GOLDDIAMOND", 1, IronChestType.GOLD, IronChestType.DIAMOND, "goldDiamondUpgrade", "Апгрейд (золотой в алмазный)", new String[]{"GGG", "msm", "GGG"}),
   COPPERSILVER("COPPERSILVER", 2, IronChestType.COPPER, IronChestType.SILVER, "copperSilverUpgrade", "Апгрейд (медный в серебряный)", new String[]{"mmm", "msm", "mmm"}),
   SILVERGOLD("SILVERGOLD", 3, IronChestType.SILVER, IronChestType.GOLD, "silverGoldUpgrade", "Апгрейд (серебряный в золотой)", new String[]{"mGm", "GsG", "mGm"}),
   COPPERIRON("COPPERIRON", 4, IronChestType.COPPER, IronChestType.IRON, "copperIronUpgrade", "Апгрейд (медный в железный)", new String[]{"mGm", "GsG", "mGm"}),
   DIAMONDCRYSTAL("DIAMONDCRYSTAL", 5, IronChestType.DIAMOND, IronChestType.CRYSTAL, "diamondCrystalUpgrade", "Апгрейд (алмазный в кристальный)", new String[]{"GGG", "GOG", "GGG"}),
   WOODIRON("WOODIRON", 6, IronChestType.WOOD, IronChestType.IRON, "woodIronUpgrade", "Апгрейд (обычный в железный)", new String[]{"mmm", "msm", "mmm"}),
   WOODCOPPER("WOODCOPPER", 7, IronChestType.WOOD, IronChestType.COPPER, "woodCopperUpgrade", "Апгрейд (обычный в медный)", new String[]{"mmm", "msm", "mmm"}),
   DIAMONDOBSIDIAN("DIAMONDOBSIDIAN", 8, IronChestType.DIAMOND, IronChestType.OBSIDIAN, "diamondObsidianUpgrade", "Апгрейд (алмазный в обсидиановый)", new String[]{"mmm", "mGm", "mmm"});

   private IronChestType source;
   private IronChestType target;
   public String itemName;
   public String descriptiveName;
   private ItemChestChanger item;
   private String[] recipe;
   
   private ChestChangerType(String var1, int var2, IronChestType source, IronChestType target, String itemName, String descriptiveName, String ... recipe) {
      this.source = source;
      this.target = target;
      this.itemName = itemName;
      this.descriptiveName = descriptiveName;
      this.recipe = recipe;
   }

   public boolean canUpgrade(IronChestType from) {
      return from == this.source;
   }

   public int getTarget() {
      return this.target.ordinal();
   }

   public ItemChestChanger buildItem(Configuration cfg, int id) {
      int itemId = cfg.get("item", this.itemName, id).getInt(id);
      this.item = new ItemChestChanger(itemId, this);
      GameRegistry.registerItem(this.item, this.itemName);
      return this.item;
   }

   public void addRecipes() {
      Iterator i$ = this.source.getMatList().iterator();

      while(i$.hasNext()) {
         String sourceMat = (String)i$.next();
         Iterator i$1 = this.target.getMatList().iterator();

         while(i$1.hasNext()) {
            String targetMat = (String)i$1.next();
            Object targetMaterial = IronChestType.translateOreName(targetMat);
            Object sourceMaterial = IronChestType.translateOreName(sourceMat);
            IronChestType.addRecipe(new ItemStack(this.item), this.recipe, 'm', targetMaterial, 's', sourceMaterial, 'G', Block.glass, 'O', Block.obsidian);
         }
      }

   }

   public static void buildItems(Configuration cfg, int defaultId) {
      ChestChangerType[] arr$ = values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         ChestChangerType type = arr$[i$];
         type.buildItem(cfg, defaultId++);
      }

   }

   public static void generateRecipes() {
      ChestChangerType[] arr$ = values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         ChestChangerType item = arr$[i$];
         item.addRecipes();
      }

   }

}
