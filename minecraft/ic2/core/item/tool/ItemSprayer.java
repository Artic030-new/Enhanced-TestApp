package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.item.ItemGradual;
import ic2.core.item.armor.ItemArmorCFPack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class ItemSprayer extends ItemGradual {

   public ItemSprayer(int id, int index) {
      super(id, index);
      this.setMaxDamage(1602);
   }

   public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float a, float b, float c) {
      if(!IC2.platform.isSimulating()) {
         return true;
      } else {
         ItemStack pack = entityplayer.inventory.armorInventory[2];
         boolean pulledFromCFPack = pack != null && pack.itemID == Ic2Items.cfPack.itemID && ((ItemArmorCFPack)pack.getItem()).getCFPellet(entityplayer, pack);
         if(!pulledFromCFPack && itemstack.getItemDamage() > 1501) {
            return false;
         } else if(world.getBlockId(i, j, k) == Ic2Items.scaffold.itemID) {
            this.sprayFoam(world, i, j, k, calculateDirectionsFromPlayer(entityplayer), true);
            if(!pulledFromCFPack) {
               itemstack.damageItem(100, (EntityLiving)null);
            }

            return true;
         } else {
            if(l == 0) {
               --j;
            }

            if(l == 1) {
               ++j;
            }

            if(l == 2) {
               --k;
            }

            if(l == 3) {
               ++k;
            }

            if(l == 4) {
               --i;
            }

            if(l == 5) {
               ++i;
            }

            world.getBlockId(i, j, k);
            if(this.sprayFoam(world, i, j, k, calculateDirectionsFromPlayer(entityplayer), false)) {
               if(!pulledFromCFPack) {
                  itemstack.damageItem(100, (EntityLiving)null);
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   public static boolean[] calculateDirectionsFromPlayer(EntityPlayer player) {
      float yaw = player.rotationYaw % 360.0F;
      float pitch = player.rotationPitch;
      boolean[] r = new boolean[]{true, true, true, true, true, true};
      if(pitch >= -65.0F && pitch <= 65.0F) {
         if(yaw >= 300.0F && yaw <= 360.0F || yaw >= 0.0F && yaw <= 60.0F) {
            r[2] = false;
         }

         if(yaw >= 30.0F && yaw <= 150.0F) {
            r[5] = false;
         }

         if(yaw >= 120.0F && yaw <= 240.0F) {
            r[3] = false;
         }

         if(yaw >= 210.0F && yaw <= 330.0F) {
            r[4] = false;
         }
      }

      if(pitch <= -40.0F) {
         r[0] = false;
      }

      if(pitch >= 40.0F) {
         r[1] = false;
      }

      return r;
   }

   public boolean sprayFoam(World world, int i, int j, int k, boolean[] directions, boolean scaffold) {
      int blockId = world.getBlockId(i, j, k);
      if((scaffold || Block.blocksList[Ic2Items.constructionFoam.itemID].canPlaceBlockAt(world, i, j, k) || blockId == Ic2Items.copperCableBlock.itemID && world.getBlockMetadata(i, j, k) != 13) && (!scaffold || blockId == Ic2Items.scaffold.itemID)) {
         ArrayList check = new ArrayList();
         ArrayList place = new ArrayList();
         int foamcount = getSprayMass();
         check.add(new ChunkPosition(i, j, k));

         ChunkPosition pos;
         int targetBlockId;
         for(int i$ = 0; i$ < check.size() && foamcount > 0; ++i$) {
            pos = (ChunkPosition)check.get(i$);
            targetBlockId = world.getBlockId(pos.x, pos.y, pos.z);
            if(!scaffold && (Block.blocksList[Ic2Items.constructionFoam.itemID].canPlaceBlockAt(world, pos.x, pos.y, pos.z) || targetBlockId == Ic2Items.copperCableBlock.itemID && world.getBlockMetadata(pos.x, pos.y, pos.z) != 13) || scaffold && targetBlockId == Ic2Items.scaffold.itemID) {
               this.considerAddingCoord(pos, place);
               this.addAdjacentSpacesOnList(pos.x, pos.y, pos.z, check, directions, scaffold);
               --foamcount;
            }
         }

         Iterator var15 = place.iterator();

         while(var15.hasNext()) {
            pos = (ChunkPosition)var15.next();
            targetBlockId = world.getBlockId(pos.x, pos.y, pos.z);
            if(targetBlockId == Ic2Items.scaffold.itemID) {
               Block.blocksList[Ic2Items.scaffold.itemID].dropBlockAsItem(world, pos.x, pos.y, pos.z, world.getBlockMetadata(pos.x, pos.y, pos.z), 0);
               world.setBlockWithNotify(pos.x, pos.y, pos.z, Ic2Items.constructionFoam.itemID);
            } else if(targetBlockId == Ic2Items.copperCableBlock.itemID) {
               TileEntity te = world.getBlockTileEntity(pos.x, pos.y, pos.z);
               if(te instanceof TileEntityCable) {
                  ((TileEntityCable)te).changeFoam((byte)1);
               }
            } else {
               world.setBlockWithNotify(pos.x, pos.y, pos.z, Ic2Items.constructionFoam.itemID);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void addAdjacentSpacesOnList(int x, int y, int z, ArrayList foam, boolean[] directions, boolean ignoreDirections) {
      int[] order = this.generateRngSpread(IC2.random);

      for(int i = 0; i < order.length; ++i) {
         if(ignoreDirections || directions[order[i]]) {
            switch(order[i]) {
            case 0:
               this.considerAddingCoord(new ChunkPosition(x, y - 1, z), foam);
               break;
            case 1:
               this.considerAddingCoord(new ChunkPosition(x, y + 1, z), foam);
               break;
            case 2:
               this.considerAddingCoord(new ChunkPosition(x, y, z - 1), foam);
               break;
            case 3:
               this.considerAddingCoord(new ChunkPosition(x, y, z + 1), foam);
               break;
            case 4:
               this.considerAddingCoord(new ChunkPosition(x - 1, y, z), foam);
               break;
            case 5:
               this.considerAddingCoord(new ChunkPosition(x + 1, y, z), foam);
            }
         }
      }

   }

   public void considerAddingCoord(ChunkPosition coord, ArrayList list) {
      for(int i = 0; i < list.size(); ++i) {
         if(((ChunkPosition)list.get(i)).x == coord.x && ((ChunkPosition)list.get(i)).y == coord.y && ((ChunkPosition)list.get(i)).z == coord.z) {
            return;
         }
      }

      list.add(coord);
   }

   public int[] generateRngSpread(Random random) {
      int[] re = new int[]{0, 1, 2, 3, 4, 5};

      for(int i = 0; i < 16; ++i) {
         int first = random.nextInt(6);
         int second = random.nextInt(6);
         int save = re[first];
         re[first] = re[second];
         re[second] = save;
      }

      return re;
   }

   public static int getSprayMass() {
      return 13;
   }
}
