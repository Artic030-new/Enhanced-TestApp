package ic2.core.item.tool;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import ic2.api.IEnergyStorage;
import ic2.api.IReactor;
import ic2.api.network.INetworkItemEventListener;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityCrop;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.machine.tileentity.TileEntityElecMachine;
import ic2.core.block.personal.IPersonalBlock;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import java.lang.reflect.Field;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemDebug extends ItemIC2 implements INetworkItemEventListener {

   private static final String[] modes = new String[]{"Interfaces and Fields", "Tile Data"};


   public ItemDebug(int itemId) {
      super(itemId, 47);
      this.setHasSubtypes(false);
      if(!ObfuscationReflectionHelper.obfuscation) {
         this.setCreativeTab(IC2.tabIC2);
      }

   }

   public String getItemNameIS(ItemStack stack) {
      return "debugItem";
   }

   public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
      NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
      int mode = nbtData.getInteger("mode");
      if(player.isSneaking()) {
         if(IC2.platform.isSimulating()) {
            mode = (mode + 1) % modes.length;
            nbtData.setInteger("mode", mode);
            IC2.network.initiateItemEvent(player, stack, mode, true);
            IC2.platform.messagePlayer(player, "Debug Item Mode: " + modes[mode]);
         }

         return false;
      } else {
         switch(mode) {
         case 0:
            int var22 = world.getBlockId(x, y, z);
            TileEntity var27 = world.getBlockTileEntity(x, y, z);
            String plat = IC2.platform.isRendering()?(IC2.platform.isSimulating()?"sp":"client"):"server";
            String message;
            if(var22 < Block.blocksList.length && Block.blocksList[var22] != null) {
               message = "[" + plat + "] id: " + var22 + " name: " + Block.blocksList[var22].getBlockName() + " te: " + var27;
            } else {
               message = "[" + plat + "] id: " + var22 + " name: null te: " + var27;
            }

            IC2.platform.messagePlayer(player, message);
            System.out.println(message);
            if(var27 != null) {
               message = "[" + plat + "] interfaces:";
               Class c = var27.getClass();

               do {
                  Class[] arr$ = c.getInterfaces();
                  int len$ = arr$.length;

                  for(int i$ = 0; i$ < len$; ++i$) {
                     Class i = arr$[i$];
                     message = message + " " + i.getName();
                  }

                  c = c.getSuperclass();
               } while(c != null);

               IC2.platform.messagePlayer(player, message);
               System.out.println(message);
            }

            if(var22 < Block.blocksList.length && Block.blocksList[var22] != null) {
               System.out.println("block fields:");
               dumpObjectFields(Block.blocksList[var22]);
            }

            if(var27 != null) {
               System.out.println("tile entity fields:");
               dumpObjectFields(var27);
            }
            break;
         case 1:
            if(!IC2.platform.isSimulating()) {
               return false;
            }

            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            if(tileEntity instanceof TileEntityBlock) {
               TileEntityBlock te = (TileEntityBlock)tileEntity;
               IC2.platform.messagePlayer(player, "Block: Active=" + te.getActive() + " Facing=" + te.getFacing());
            }

            if(tileEntity instanceof TileEntityBaseGenerator) {
               TileEntityBaseGenerator var25 = (TileEntityBaseGenerator)tileEntity;
               IC2.platform.messagePlayer(player, "BaseGen: Fuel=" + var25.fuel + " Storage=" + var25.storage);
            }

            if(tileEntity instanceof TileEntityElecMachine) {
               TileEntityElecMachine var26 = (TileEntityElecMachine)tileEntity;
               IC2.platform.messagePlayer(player, "ElecMachine: Energy=" + var26.energy);
            }

            if(tileEntity instanceof IEnergyStorage) {
               IEnergyStorage var23 = (IEnergyStorage)tileEntity;
               IC2.platform.messagePlayer(player, "EnergyStorage: Stored=" + var23.getStored());
            }

            if(tileEntity instanceof IReactor) {
               IReactor var24 = (IReactor)tileEntity;
               IC2.platform.messagePlayer(player, "Reactor: Heat=" + var24.getHeat() + " MaxHeat=" + var24.getMaxHeat() + " HEM=" + var24.getHeatEffectModifier() + " Output=" + var24.getOutput());
            }

            if(tileEntity instanceof IPersonalBlock) {
               IPersonalBlock var29 = (IPersonalBlock)tileEntity;
               IC2.platform.messagePlayer(player, "PersonalBlock: CanAccess=" + var29.canAccess(player));
            }

            if(tileEntity instanceof TileEntityCrop) {
               TileEntityCrop var28 = (TileEntityCrop)tileEntity;
               IC2.platform.messagePlayer(player, "PersonalBlock: Crop=" + var28.id + " Size=" + var28.size + " Growth=" + var28.statGrowth + " Gain=" + var28.statGain + " Resistance=" + var28.statResistance + " Nutrients=" + var28.nutrientStorage + " Water=" + var28.waterStorage + " GrowthPoints=" + var28.growthPoints);
            }
         }

         return IC2.platform.isSimulating();
      }
   }

   private static void dumpObjectFields(Object o) {
      Class fieldDeclaringClass = o.getClass();

      do {
         Field[] fields = fieldDeclaringClass.getDeclaredFields();
         Field[] arr$ = fields;
         int len$ = fields.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Field field = arr$[i$];
            boolean accessible = field.isAccessible();
            field.setAccessible(true);

            try {
               String e = field.get(o).toString();
               if(e.length() > 100) {
                  e = e.substring(0, 90) + "... (" + e.length() + " more)";
               }

               System.out.println("name: " + fieldDeclaringClass.getName() + "." + field.getName() + " type: " + field.getType() + " value: " + e);
            } catch (IllegalAccessException var9) {
               System.out.println("name: " + fieldDeclaringClass.getName() + "." + field.getName() + " type: " + field.getType() + " value: <can\'t access>");
            } catch (NullPointerException var10) {
               System.out.println("name: " + fieldDeclaringClass.getName() + "." + field.getName() + " type: " + field.getType() + " value: <null>");
            }

            field.setAccessible(accessible);
         }

         fieldDeclaringClass = fieldDeclaringClass.getSuperclass();
      } while(fieldDeclaringClass != null);

   }

   public void onNetworkEvent(int metaData, EntityPlayer player, int event) {}

}
