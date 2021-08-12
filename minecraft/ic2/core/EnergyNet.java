package ic2.core;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.WorldData;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

public final class EnergyNet {

   public static final double minConductionLoss = 1.0E-4D;
   private static final Direction[] directions = Direction.values();
   private static EnergyNet.EventHandler eventHandler;
   private final Map energySourceToEnergyPathMap = new WeakHashMap();
   private final Map entityLivingToShockEnergyMap = new WeakHashMap();


   public static void initialize() {
      eventHandler = new EnergyNet.EventHandler();
   }

   public static EnergyNet getForWorld(World world) {
      WorldData worldData = WorldData.get(world);
      return worldData.energyNet;
   }

   public static void onTick(World world) {
      IC2.platform.profilerStartSection("Shocking");
      EnergyNet energyNet = getForWorld(world);
      Iterator i$ = energyNet.entityLivingToShockEnergyMap.entrySet().iterator();

      while(i$.hasNext()) {
         Entry entry = (Entry)i$.next();
         EntityLiving target = (EntityLiving)entry.getKey();
         int damage = (((Integer)entry.getValue()).intValue() + 63) / 64;
         if(target.isEntityAlive()) {
            target.attackEntityFrom(IC2DamageSource.electricity, damage);
         }
      }

      energyNet.entityLivingToShockEnergyMap.clear();
      IC2.platform.profilerEndSection();
   }

   public void addTileEntity(TileEntity addedTileEntity) {
      if(addedTileEntity instanceof IEnergyTile && !((IEnergyTile)addedTileEntity).isAddedToEnergyNet()) {
         if(addedTileEntity instanceof IEnergyAcceptor) {
            List reverseEnergyPaths = this.discover(addedTileEntity, true, Integer.MAX_VALUE);
            Iterator i$ = reverseEnergyPaths.iterator();

            while(i$.hasNext()) {
               EnergyNet.EnergyPath reverseEnergyPath = (EnergyNet.EnergyPath)i$.next();
               IEnergySource energySource = (IEnergySource)reverseEnergyPath.target;
               if(this.energySourceToEnergyPathMap.containsKey(energySource) && (double)energySource.getMaxEnergyOutput() > reverseEnergyPath.loss) {
                  this.energySourceToEnergyPathMap.remove(energySource);
               }
            }
         }

         if(addedTileEntity instanceof IEnergySource) {
            ;
         }

      }
   }

   public void removeTileEntity(TileEntity removedTileEntity) {
      if(removedTileEntity instanceof IEnergyTile && ((IEnergyTile)removedTileEntity).isAddedToEnergyNet()) {
         if(removedTileEntity instanceof IEnergyAcceptor) {
            List reverseEnergyPaths1 = this.discover(removedTileEntity, true, Integer.MAX_VALUE);
            Iterator i$ = reverseEnergyPaths1.iterator();

            while(i$.hasNext()) {
               EnergyNet.EnergyPath reverseEnergyPath = (EnergyNet.EnergyPath)i$.next();
               IEnergySource energySource = (IEnergySource)reverseEnergyPath.target;
               if(this.energySourceToEnergyPathMap.containsKey(energySource) && (double)energySource.getMaxEnergyOutput() > reverseEnergyPath.loss) {
                  if(removedTileEntity instanceof IEnergyConductor) {
                     this.energySourceToEnergyPathMap.remove(energySource);
                  } else {
                     Iterator it = ((List)this.energySourceToEnergyPathMap.get(energySource)).iterator();

                     while(it.hasNext()) {
                        if(((EnergyNet.EnergyPath)it.next()).target == removedTileEntity) {
                           it.remove();
                           break;
                        }
                     }
                  }
               }
            }
         }

         if(removedTileEntity instanceof IEnergySource) {
            this.energySourceToEnergyPathMap.remove(removedTileEntity);
         }

      } else {
         boolean reverseEnergyPaths = removedTileEntity instanceof IEnergyTile?!((IEnergyTile)removedTileEntity).isAddedToEnergyNet():true;
         IC2.log.warning("removing " + removedTileEntity + " from the EnergyNet failed, already removed: " + reverseEnergyPaths);
      }
   }

   public int emitEnergyFrom(IEnergySource energySource, int amount) {
      if(!energySource.isAddedToEnergyNet()) {
         IC2.log.warning("EnergyNet.emitEnergyFrom: " + energySource + " is not added to the enet");
         return amount;
      } else {
         if(!this.energySourceToEnergyPathMap.containsKey(energySource)) {
            this.energySourceToEnergyPathMap.put(energySource, this.discover((TileEntity)energySource, false, energySource.getMaxEnergyOutput()));
         }

         Vector activeEnergyPaths = new Vector();
         double totalInvLoss = 0.0D;
         Iterator suppliedEnergyPaths = ((List)this.energySourceToEnergyPathMap.get(energySource)).iterator();

         EnergyNet.EnergyPath i$;
         while(suppliedEnergyPaths.hasNext()) {
            i$ = (EnergyNet.EnergyPath)suppliedEnergyPaths.next();

            assert i$.target instanceof IEnergySink;

            IEnergySink entry = (IEnergySink)i$.target;
            if(entry.demandsEnergy() > 0 && i$.loss < (double)amount) {
               totalInvLoss += 1.0D / i$.loss;
               activeEnergyPaths.add(i$);
            }
         }

         Collections.shuffle(activeEnergyPaths);

         for(int var19 = activeEnergyPaths.size() - amount; var19 > 0; --var19) {
            i$ = (EnergyNet.EnergyPath)activeEnergyPaths.remove(activeEnergyPaths.size() - 1);
            totalInvLoss -= 1.0D / i$.loss;
         }

         HashMap var20 = new HashMap();
         new Vector();

         Iterator i$1;
         int maxShockEnergy;
         while(!activeEnergyPaths.isEmpty() && amount > 0) {
            int var21 = 0;
            double var22 = 0.0D;
            Vector energyInjected = activeEnergyPaths;
            activeEnergyPaths = new Vector();
            activeEnergyPaths.iterator();
            i$1 = energyInjected.iterator();

            while(i$1.hasNext()) {
               EnergyNet.EnergyPath energyConductor = (EnergyNet.EnergyPath)i$1.next();
               IEnergySink energyConductor1 = (IEnergySink)energyConductor.target;
               maxShockEnergy = (int)Math.floor((double)Math.round((double)amount / totalInvLoss / energyConductor.loss * 100000.0D) / 100000.0D);
               int i$2 = (int)Math.floor(energyConductor.loss);
               if(maxShockEnergy > i$2) {
                  int energyConductor2 = energyConductor1.injectEnergy(energyConductor.targetDirection, maxShockEnergy - i$2);
                  if(energyConductor2 == 0 && energyConductor1.demandsEnergy() > 0) {
                     activeEnergyPaths.add(energyConductor);
                     var22 += 1.0D / energyConductor.loss;
                  } else if(energyConductor2 >= maxShockEnergy - i$2) {
                     energyConductor2 = maxShockEnergy - i$2;
                     IC2.log.warning("API ERROR: " + energyConductor1 + " didn\'t implement demandsEnergy() properly, no energy from injectEnergy accepted although demandsEnergy() returned true.");
                  }

                  var21 += maxShockEnergy - energyConductor2;
                  int te = maxShockEnergy - i$2 - energyConductor2;
                  if(!var20.containsKey(energyConductor)) {
                     var20.put(energyConductor, Integer.valueOf(te));
                  } else {
                     var20.put(energyConductor, Integer.valueOf(te + ((Integer)var20.get(energyConductor)).intValue()));
                  }
               } else {
                  activeEnergyPaths.add(energyConductor);
                  var22 += 1.0D / energyConductor.loss;
               }
            }

            if(var21 == 0 && !activeEnergyPaths.isEmpty()) {
               EnergyNet.EnergyPath var25 = (EnergyNet.EnergyPath)activeEnergyPaths.remove(activeEnergyPaths.size() - 1);
               var22 -= 1.0D / var25.loss;
            }

            totalInvLoss = var22;
            amount -= var21;
         }

         Iterator var23 = var20.entrySet().iterator();

         while(var23.hasNext()) {
            Entry var24 = (Entry)var23.next();
            EnergyNet.EnergyPath energyPath = (EnergyNet.EnergyPath)var24.getKey();
            int var26 = ((Integer)var24.getValue()).intValue();
            energyPath.totalEnergyConducted += (long)var26;
            if(var26 > energyPath.minInsulationEnergyAbsorption) {
               List var28 = ((TileEntity)energySource).worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox((double)(energyPath.minX - 1), (double)(energyPath.minY - 1), (double)(energyPath.minZ - 1), (double)(energyPath.maxX + 2), (double)(energyPath.maxY + 2), (double)(energyPath.maxZ + 2)));
               Iterator var27 = var28.iterator();

               while(var27.hasNext()) {
                  EntityLiving var31 = (EntityLiving)var27.next();
                  maxShockEnergy = 0;
                  Iterator var32 = energyPath.conductors.iterator();

                  while(true) {
                     if(var32.hasNext()) {
                        IEnergyConductor var34 = (IEnergyConductor)var32.next();
                        TileEntity var33 = (TileEntity)var34;
                        if(!var31.boundingBox.intersectsWith(AxisAlignedBB.getBoundingBox((double)(var33.xCoord - 1), (double)(var33.yCoord - 1), (double)(var33.zCoord - 1), (double)(var33.xCoord + 2), (double)(var33.yCoord + 2), (double)(var33.zCoord + 2)))) {
                           continue;
                        }

                        int shockEnergy = var26 - var34.getInsulationEnergyAbsorption();
                        if(shockEnergy > maxShockEnergy) {
                           maxShockEnergy = shockEnergy;
                        }

                        if(var34.getInsulationEnergyAbsorption() != energyPath.minInsulationEnergyAbsorption) {
                           continue;
                        }
                     }

                     if(this.entityLivingToShockEnergyMap.containsKey(var31)) {
                        this.entityLivingToShockEnergyMap.put(var31, Integer.valueOf(((Integer)this.entityLivingToShockEnergyMap.get(var31)).intValue() + maxShockEnergy));
                     } else {
                        this.entityLivingToShockEnergyMap.put(var31, Integer.valueOf(maxShockEnergy));
                     }
                     break;
                  }
               }

               if(var26 >= energyPath.minInsulationBreakdownEnergy) {
                  var27 = energyPath.conductors.iterator();

                  while(var27.hasNext()) {
                     IEnergyConductor var29 = (IEnergyConductor)var27.next();
                     if(var26 >= var29.getInsulationBreakdownEnergy()) {
                        var29.removeInsulation();
                        if(var29.getInsulationEnergyAbsorption() < energyPath.minInsulationEnergyAbsorption) {
                           energyPath.minInsulationEnergyAbsorption = var29.getInsulationEnergyAbsorption();
                        }
                     }
                  }
               }
            }

            if(var26 >= energyPath.minConductorBreakdownEnergy) {
               i$1 = energyPath.conductors.iterator();

               while(i$1.hasNext()) {
                  IEnergyConductor var30 = (IEnergyConductor)i$1.next();
                  if(var26 >= var30.getConductorBreakdownEnergy()) {
                     var30.removeConductor();
                  }
               }
            }
         }

         return amount;
      }
   }

   @Deprecated
   public long getTotalEnergyConducted(TileEntity tileEntity) {
      long ret = 0L;
      if(tileEntity instanceof IEnergyConductor || tileEntity instanceof IEnergySink) {
         List i$ = this.discover(tileEntity, true, Integer.MAX_VALUE);
         Iterator energyPath = i$.iterator();

         while(energyPath.hasNext()) {
            EnergyNet.EnergyPath reverseEnergyPath = (EnergyNet.EnergyPath)energyPath.next();
            IEnergySource energySource = (IEnergySource)reverseEnergyPath.target;
            if(this.energySourceToEnergyPathMap.containsKey(energySource) && (double)energySource.getMaxEnergyOutput() > reverseEnergyPath.loss) {
               Iterator i$1 = ((List)this.energySourceToEnergyPathMap.get(energySource)).iterator();

               while(i$1.hasNext()) {
                  EnergyNet.EnergyPath energyPath1 = (EnergyNet.EnergyPath)i$1.next();
                  if(tileEntity instanceof IEnergySink && energyPath1.target == tileEntity || tileEntity instanceof IEnergyConductor && energyPath1.conductors.contains(tileEntity)) {
                     ret += energyPath1.totalEnergyConducted;
                  }
               }
            }
         }
      }

      EnergyNet.EnergyPath energyPath2;
      if(tileEntity instanceof IEnergySource && this.energySourceToEnergyPathMap.containsKey(tileEntity)) {
         for(Iterator i$2 = ((List)this.energySourceToEnergyPathMap.get(tileEntity)).iterator(); i$2.hasNext(); ret += energyPath2.totalEnergyConducted) {
            energyPath2 = (EnergyNet.EnergyPath)i$2.next();
         }
      }

      return ret;
   }

   public long getTotalEnergyEmitted(TileEntity tileEntity) {
      long ret = 0L;
      if(tileEntity instanceof IEnergyConductor) {
         List i$ = this.discover(tileEntity, true, Integer.MAX_VALUE);
         Iterator energyPath = i$.iterator();

         while(energyPath.hasNext()) {
            EnergyNet.EnergyPath reverseEnergyPath = (EnergyNet.EnergyPath)energyPath.next();
            IEnergySource energySource = (IEnergySource)reverseEnergyPath.target;
            if(this.energySourceToEnergyPathMap.containsKey(energySource) && (double)energySource.getMaxEnergyOutput() > reverseEnergyPath.loss) {
               Iterator i$1 = ((List)this.energySourceToEnergyPathMap.get(energySource)).iterator();

               while(i$1.hasNext()) {
                  EnergyNet.EnergyPath energyPath1 = (EnergyNet.EnergyPath)i$1.next();
                  if(tileEntity instanceof IEnergyConductor && energyPath1.conductors.contains(tileEntity)) {
                     ret += energyPath1.totalEnergyConducted;
                  }
               }
            }
         }
      }

      EnergyNet.EnergyPath energyPath2;
      if(tileEntity instanceof IEnergySource && this.energySourceToEnergyPathMap.containsKey(tileEntity)) {
         for(Iterator i$2 = ((List)this.energySourceToEnergyPathMap.get(tileEntity)).iterator(); i$2.hasNext(); ret += energyPath2.totalEnergyConducted) {
            energyPath2 = (EnergyNet.EnergyPath)i$2.next();
         }
      }

      return ret;
   }

   public long getTotalEnergySunken(TileEntity tileEntity) {
      long ret = 0L;
      if(tileEntity instanceof IEnergyConductor || tileEntity instanceof IEnergySink) {
         List reverseEnergyPaths = this.discover(tileEntity, true, Integer.MAX_VALUE);
         Iterator i$ = reverseEnergyPaths.iterator();

         while(i$.hasNext()) {
            EnergyNet.EnergyPath reverseEnergyPath = (EnergyNet.EnergyPath)i$.next();
            IEnergySource energySource = (IEnergySource)reverseEnergyPath.target;
            if(this.energySourceToEnergyPathMap.containsKey(energySource) && (double)energySource.getMaxEnergyOutput() > reverseEnergyPath.loss) {
               Iterator i$1 = ((List)this.energySourceToEnergyPathMap.get(energySource)).iterator();

               while(i$1.hasNext()) {
                  EnergyNet.EnergyPath energyPath = (EnergyNet.EnergyPath)i$1.next();
                  if(tileEntity instanceof IEnergySink && energyPath.target == tileEntity || tileEntity instanceof IEnergyConductor && energyPath.conductors.contains(tileEntity)) {
                     ret += energyPath.totalEnergyConducted;
                  }
               }
            }
         }
      }

      return ret;
   }

   private List discover(TileEntity emitter, boolean reverse, int lossLimit) {
      HashMap reachedTileEntities = new HashMap();
      LinkedList tileEntitiesToCheck = new LinkedList();
      tileEntitiesToCheck.add(emitter);

      while(!tileEntitiesToCheck.isEmpty()) {
         TileEntity energyPaths = (TileEntity)tileEntitiesToCheck.remove();
         if(!energyPaths.isInvalid()) {
            double i$ = 0.0D;
            if(energyPaths != emitter) {
               i$ = ((EnergyNet.EnergyBlockLink)reachedTileEntities.get(energyPaths)).loss;
            }

            List tileEntity = this.getValidReceivers(energyPaths, reverse);
            Iterator energyBlockLink = tileEntity.iterator();

            while(energyBlockLink.hasNext()) {
               EnergyNet.EnergyTarget energyPath = (EnergyNet.EnergyTarget)energyBlockLink.next();
               if(energyPath.tileEntity != emitter) {
                  double energyConductor = 0.0D;
                  if(energyPath.tileEntity instanceof IEnergyConductor) {
                     energyConductor = ((IEnergyConductor)energyPath.tileEntity).getConductionLoss();
                     if(energyConductor < 1.0E-4D) {
                        energyConductor = 1.0E-4D;
                     }

                     if(i$ + energyConductor >= (double)lossLimit) {
                        continue;
                     }
                  }

                  if(!reachedTileEntities.containsKey(energyPath.tileEntity) || ((EnergyNet.EnergyBlockLink)reachedTileEntities.get(energyPath.tileEntity)).loss > i$ + energyConductor) {
                     reachedTileEntities.put(energyPath.tileEntity, new EnergyNet.EnergyBlockLink(energyPath.direction, i$ + energyConductor));
                     if(energyPath.tileEntity instanceof IEnergyConductor) {
                        tileEntitiesToCheck.remove(energyPath.tileEntity);
                        tileEntitiesToCheck.add(energyPath.tileEntity);
                     }
                  }
               }
            }
         }
      }

      LinkedList energyPaths1 = new LinkedList();
      Iterator i$1 = reachedTileEntities.entrySet().iterator();

      label112:
      while(i$1.hasNext()) {
         Entry entry = (Entry)i$1.next();
         TileEntity tileEntity1 = (TileEntity)entry.getKey();
         if(!reverse && tileEntity1 instanceof IEnergySink || reverse && tileEntity1 instanceof IEnergySource) {
            EnergyNet.EnergyBlockLink energyBlockLink1 = (EnergyNet.EnergyBlockLink)entry.getValue();
            EnergyNet.EnergyPath energyPath1 = new EnergyNet.EnergyPath();
            if(energyBlockLink1.loss > 0.1D) {
               energyPath1.loss = energyBlockLink1.loss;
            } else {
               energyPath1.loss = 0.1D;
            }

            energyPath1.target = tileEntity1;
            energyPath1.targetDirection = energyBlockLink1.direction;
            if(!reverse && emitter instanceof IEnergySource) {
               while(true) {
                  tileEntity1 = energyBlockLink1.direction.applyToTileEntity(tileEntity1);
                  if(tileEntity1 == emitter) {
                     break;
                  }

                  if(!(tileEntity1 instanceof IEnergyConductor)) {
                     if(tileEntity1 != null) {
                        System.out.println("EnergyNet: EnergyBlockLink corrupted (" + energyPath1.target + " [" + energyPath1.target.xCoord + " " + energyPath1.target.yCoord + " " + energyPath1.target.zCoord + "] -> " + tileEntity1 + " [" + tileEntity1.xCoord + " " + tileEntity1.yCoord + " " + tileEntity1.zCoord + "] -> " + emitter + " [" + emitter.xCoord + " " + emitter.yCoord + " " + emitter.zCoord + "])");
                     }
                     continue label112;
                  }

                  IEnergyConductor energyConductor1 = (IEnergyConductor)tileEntity1;
                  if(tileEntity1.xCoord < energyPath1.minX) {
                     energyPath1.minX = tileEntity1.xCoord;
                  }

                  if(tileEntity1.yCoord < energyPath1.minY) {
                     energyPath1.minY = tileEntity1.yCoord;
                  }

                  if(tileEntity1.zCoord < energyPath1.minZ) {
                     energyPath1.minZ = tileEntity1.zCoord;
                  }

                  if(tileEntity1.xCoord > energyPath1.maxX) {
                     energyPath1.maxX = tileEntity1.xCoord;
                  }

                  if(tileEntity1.yCoord > energyPath1.maxY) {
                     energyPath1.maxY = tileEntity1.yCoord;
                  }

                  if(tileEntity1.zCoord > energyPath1.maxZ) {
                     energyPath1.maxZ = tileEntity1.zCoord;
                  }

                  energyPath1.conductors.add(energyConductor1);
                  if(energyConductor1.getInsulationEnergyAbsorption() < energyPath1.minInsulationEnergyAbsorption) {
                     energyPath1.minInsulationEnergyAbsorption = energyConductor1.getInsulationEnergyAbsorption();
                  }

                  if(energyConductor1.getInsulationBreakdownEnergy() < energyPath1.minInsulationBreakdownEnergy) {
                     energyPath1.minInsulationBreakdownEnergy = energyConductor1.getInsulationBreakdownEnergy();
                  }

                  if(energyConductor1.getConductorBreakdownEnergy() < energyPath1.minConductorBreakdownEnergy) {
                     energyPath1.minConductorBreakdownEnergy = energyConductor1.getConductorBreakdownEnergy();
                  }

                  energyBlockLink1 = (EnergyNet.EnergyBlockLink)reachedTileEntities.get(tileEntity1);
                  if(energyBlockLink1 == null) {
                     IC2.platform.displayError("An energy network pathfinding entry is corrupted.\nThis could happen due to incorrect Minecraft behavior or a bug.\n\n(Technical information: energyBlockLink, tile entities below)\nE: " + emitter + " (" + emitter.xCoord + "," + emitter.yCoord + "," + emitter.zCoord + ")\n" + "C: " + tileEntity1 + " (" + tileEntity1.xCoord + "," + tileEntity1.yCoord + "," + tileEntity1.zCoord + ")\n" + "R: " + energyPath1.target + " (" + energyPath1.target.xCoord + "," + energyPath1.target.yCoord + "," + energyPath1.target.zCoord + ")");
                  }
               }
            }

            energyPaths1.add(energyPath1);
         }
      }

      return energyPaths1;
   }

   public List discoverTargets(TileEntity emitter, boolean reverse, int lossLimit) {
      List paths = this.discover(emitter, reverse, lossLimit);
      LinkedList targets = new LinkedList();
      Iterator i$ = paths.iterator();

      while(i$.hasNext()) {
         EnergyNet.EnergyPath path = (EnergyNet.EnergyPath)i$.next();
         targets.add(path.target);
      }

      return targets;
   }

   private List getValidReceivers(TileEntity emitter, boolean reverse) {
      LinkedList validReceivers = new LinkedList();
      Direction[] arr$ = directions;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Direction direction = arr$[i$];
         TileEntity target = direction.applyToTileEntity(emitter);
         if(target instanceof IEnergyTile && ((IEnergyTile)target).isAddedToEnergyNet()) {
            Direction inverseDirection = direction.getInverse();
            if((!reverse && emitter instanceof IEnergyEmitter && ((IEnergyEmitter)emitter).emitsEnergyTo(target, direction) || reverse && emitter instanceof IEnergyAcceptor && ((IEnergyAcceptor)emitter).acceptsEnergyFrom(target, direction)) && (!reverse && target instanceof IEnergyAcceptor && ((IEnergyAcceptor)target).acceptsEnergyFrom(emitter, inverseDirection) || reverse && target instanceof IEnergyEmitter && ((IEnergyEmitter)target).emitsEnergyTo(emitter, inverseDirection))) {
               validReceivers.add(new EnergyNet.EnergyTarget(target, inverseDirection));
            }
         }
      }

      return validReceivers;
   }


   static class EnergyBlockLink {

      Direction direction;
      double loss;


      EnergyBlockLink(Direction direction, double loss) {
         this.direction = direction;
         this.loss = loss;
      }
   }

   static class EnergyPath {

      TileEntity target = null;
      Direction targetDirection;
      Set conductors = new HashSet();
      int minX = Integer.MAX_VALUE;
      int minY = Integer.MAX_VALUE;
      int minZ = Integer.MAX_VALUE;
      int maxX = Integer.MIN_VALUE;
      int maxY = Integer.MIN_VALUE;
      int maxZ = Integer.MIN_VALUE;
      double loss = 0.0D;
      int minInsulationEnergyAbsorption = Integer.MAX_VALUE;
      int minInsulationBreakdownEnergy = Integer.MAX_VALUE;
      int minConductorBreakdownEnergy = Integer.MAX_VALUE;
      long totalEnergyConducted = 0L;


   }

   public static class EventHandler {

      public EventHandler() {
         MinecraftForge.EVENT_BUS.register(this);
      }

      @ForgeSubscribe
      public void onEnergyTileLoad(EnergyTileLoadEvent event) {
         EnergyNet.getForWorld(event.world).addTileEntity((TileEntity)event.energyTile);
      }

      @ForgeSubscribe
      public void onEnergyTileUnload(EnergyTileUnloadEvent event) {
         EnergyNet.getForWorld(event.world).removeTileEntity((TileEntity)event.energyTile);
      }

      @ForgeSubscribe
      public void onEnergyTileSource(EnergyTileSourceEvent event) {
         event.amount = EnergyNet.getForWorld(event.world).emitEnergyFrom((IEnergySource)event.energyTile, event.amount);
      }
   }

   static class EnergyTarget {

      TileEntity tileEntity;
      Direction direction;


      EnergyTarget(TileEntity tileEntity, Direction direction) {
         this.tileEntity = tileEntity;
         this.direction = direction;
      }
   }
}
