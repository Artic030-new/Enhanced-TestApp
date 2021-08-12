package ic2.core.util;

import ic2.api.Direction;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class AabbUtil {

   public static Direction getIntersection(Vec3 origin, Vec3 direction, AxisAlignedBB bbox, Vec3 intersection) {
      double length = direction.lengthVector();
      Vec3 normalizedDirection = Vec3.createVectorHelper(direction.xCoord / length, direction.yCoord / length, direction.zCoord / length);
      Direction intersectingDirection = intersects(origin, normalizedDirection, bbox);
      if(intersectingDirection == null) {
         return null;
      } else {
         Vec3 planeOrigin;
         if(normalizedDirection.xCoord < 0.0D && normalizedDirection.yCoord < 0.0D && normalizedDirection.zCoord < 0.0D) {
            planeOrigin = Vec3.createVectorHelper(bbox.maxX, bbox.maxY, bbox.maxZ);
         } else if(normalizedDirection.xCoord < 0.0D && normalizedDirection.yCoord < 0.0D && normalizedDirection.zCoord >= 0.0D) {
            planeOrigin = Vec3.createVectorHelper(bbox.maxX, bbox.maxY, bbox.minZ);
         } else if(normalizedDirection.xCoord < 0.0D && normalizedDirection.yCoord >= 0.0D && normalizedDirection.zCoord < 0.0D) {
            planeOrigin = Vec3.createVectorHelper(bbox.maxX, bbox.minY, bbox.maxZ);
         } else if(normalizedDirection.xCoord < 0.0D && normalizedDirection.yCoord >= 0.0D && normalizedDirection.zCoord >= 0.0D) {
            planeOrigin = Vec3.createVectorHelper(bbox.maxX, bbox.minY, bbox.minZ);
         } else if(normalizedDirection.xCoord >= 0.0D && normalizedDirection.yCoord < 0.0D && normalizedDirection.zCoord < 0.0D) {
            planeOrigin = Vec3.createVectorHelper(bbox.minX, bbox.maxY, bbox.maxZ);
         } else if(normalizedDirection.xCoord >= 0.0D && normalizedDirection.yCoord < 0.0D && normalizedDirection.zCoord >= 0.0D) {
            planeOrigin = Vec3.createVectorHelper(bbox.minX, bbox.maxY, bbox.minZ);
         } else if(normalizedDirection.xCoord >= 0.0D && normalizedDirection.yCoord >= 0.0D && normalizedDirection.zCoord < 0.0D) {
            planeOrigin = Vec3.createVectorHelper(bbox.minX, bbox.minY, bbox.maxZ);
         } else {
            planeOrigin = Vec3.createVectorHelper(bbox.minX, bbox.minY, bbox.minZ);
         }

         Vec3 planeNormalVector = null;
         switch(AabbUtil.NamelessClass1867010746.$SwitchMap$ic2$api$Direction[intersectingDirection.ordinal()]) {
         case 1:
         case 2:
            planeNormalVector = Vec3.createVectorHelper(1.0D, 0.0D, 0.0D);
            break;
         case 3:
         case 4:
            planeNormalVector = Vec3.createVectorHelper(0.0D, 1.0D, 0.0D);
            break;
         case 5:
         case 6:
            planeNormalVector = Vec3.createVectorHelper(0.0D, 0.0D, 1.0D);
         }

         Vec3 newIntersection = getIntersectionWithPlane(origin, normalizedDirection, planeOrigin, planeNormalVector);
         intersection.xCoord = newIntersection.xCoord;
         intersection.yCoord = newIntersection.yCoord;
         intersection.zCoord = newIntersection.zCoord;
         return intersectingDirection;
      }
   }

   public static Direction intersects(Vec3 origin, Vec3 direction, AxisAlignedBB bbox) {
      double[] ray = getRay(origin, direction);
      return direction.xCoord < 0.0D && direction.yCoord < 0.0D && direction.zCoord < 0.0D?(origin.xCoord < bbox.minX?null:(origin.yCoord < bbox.minY?null:(origin.zCoord < bbox.minZ?null:(side(ray, getEdgeRay(AabbUtil.Edge.EF, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.EH, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.DH, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.DC, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.BC, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.BF, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.HG, bbox)) > 0.0D && side(ray, getEdgeRay(AabbUtil.Edge.FG, bbox)) < 0.0D?Direction.ZP:(side(ray, getEdgeRay(AabbUtil.Edge.CG, bbox)) < 0.0D?Direction.YP:Direction.XP))))))))))):(direction.xCoord < 0.0D && direction.yCoord < 0.0D && direction.zCoord >= 0.0D?(origin.xCoord < bbox.minX?null:(origin.yCoord < bbox.minY?null:(origin.zCoord > bbox.maxZ?null:(side(ray, getEdgeRay(AabbUtil.Edge.HG, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.DH, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AD, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AB, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.BF, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.FG, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.DC, bbox)) > 0.0D && side(ray, getEdgeRay(AabbUtil.Edge.CG, bbox)) > 0.0D?Direction.XP:(side(ray, getEdgeRay(AabbUtil.Edge.BC, bbox)) < 0.0D?Direction.YP:Direction.ZN))))))))))):(direction.xCoord < 0.0D && direction.yCoord >= 0.0D && direction.zCoord < 0.0D?(origin.xCoord < bbox.minX?null:(origin.yCoord > bbox.maxY?null:(origin.zCoord < bbox.minZ?null:(side(ray, getEdgeRay(AabbUtil.Edge.FG, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.EF, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AE, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AD, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.DC, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.CG, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.EH, bbox)) > 0.0D && side(ray, getEdgeRay(AabbUtil.Edge.HG, bbox)) > 0.0D?Direction.ZP:(side(ray, getEdgeRay(AabbUtil.Edge.DH, bbox)) < 0.0D?Direction.XP:Direction.YN))))))))))):(direction.xCoord < 0.0D && direction.yCoord >= 0.0D && direction.zCoord >= 0.0D?(origin.xCoord < bbox.minX?null:(origin.yCoord > bbox.maxY?null:(origin.zCoord > bbox.maxZ?null:(side(ray, getEdgeRay(AabbUtil.Edge.EH, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AE, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AB, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.BC, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.CG, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.HG, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AD, bbox)) > 0.0D && side(ray, getEdgeRay(AabbUtil.Edge.DH, bbox)) > 0.0D?Direction.YN:(side(ray, getEdgeRay(AabbUtil.Edge.DC, bbox)) < 0.0D?Direction.ZN:Direction.XP))))))))))):(direction.xCoord >= 0.0D && direction.yCoord < 0.0D && direction.zCoord < 0.0D?(origin.xCoord > bbox.maxX?null:(origin.yCoord < bbox.minY?null:(origin.zCoord < bbox.minZ?null:(side(ray, getEdgeRay(AabbUtil.Edge.AB, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AE, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.EH, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.HG, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.CG, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.BC, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.EF, bbox)) > 0.0D && side(ray, getEdgeRay(AabbUtil.Edge.BF, bbox)) < 0.0D?Direction.XN:(side(ray, getEdgeRay(AabbUtil.Edge.FG, bbox)) < 0.0D?Direction.ZP:Direction.YP))))))))))):(direction.xCoord >= 0.0D && direction.yCoord < 0.0D && direction.zCoord >= 0.0D?(origin.xCoord > bbox.maxX?null:(origin.yCoord < bbox.minY?null:(origin.zCoord > bbox.maxZ?null:(side(ray, getEdgeRay(AabbUtil.Edge.DC, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AD, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AE, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.EF, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.FG, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.CG, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AB, bbox)) > 0.0D && side(ray, getEdgeRay(AabbUtil.Edge.BC, bbox)) > 0.0D?Direction.ZN:(side(ray, getEdgeRay(AabbUtil.Edge.BF, bbox)) < 0.0D?Direction.XN:Direction.YP))))))))))):(direction.xCoord >= 0.0D && direction.yCoord >= 0.0D && direction.zCoord < 0.0D?(origin.xCoord > bbox.maxX?null:(origin.yCoord > bbox.maxY?null:(origin.zCoord < bbox.minZ?null:(side(ray, getEdgeRay(AabbUtil.Edge.BF, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AB, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AD, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.DH, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.HG, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.FG, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AE, bbox)) > 0.0D && side(ray, getEdgeRay(AabbUtil.Edge.EF, bbox)) > 0.0D?Direction.XN:(side(ray, getEdgeRay(AabbUtil.Edge.EH, bbox)) < 0.0D?Direction.YN:Direction.ZP))))))))))):(origin.xCoord > bbox.maxX?null:(origin.yCoord > bbox.maxY?null:(origin.zCoord > bbox.maxZ?null:(side(ray, getEdgeRay(AabbUtil.Edge.EF, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.EH, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.DH, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.DC, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.BC, bbox)) < 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.BF, bbox)) > 0.0D?null:(side(ray, getEdgeRay(AabbUtil.Edge.AB, bbox)) < 0.0D && side(ray, getEdgeRay(AabbUtil.Edge.AE, bbox)) > 0.0D?Direction.XN:(side(ray, getEdgeRay(AabbUtil.Edge.AD, bbox)) < 0.0D?Direction.ZN:Direction.YN)))))))))))))))));
   }

   private static double[] getRay(Vec3 origin, Vec3 direction) {
      double[] ret = new double[]{origin.xCoord * direction.yCoord - direction.xCoord * origin.yCoord, origin.xCoord * direction.zCoord - direction.xCoord * origin.zCoord, -direction.xCoord, origin.yCoord * direction.zCoord - direction.yCoord * origin.zCoord, -direction.zCoord, direction.yCoord};
      return ret;
   }

   private static double[] getEdgeRay(AabbUtil.Edge edge, AxisAlignedBB bbox) {
      switch(AabbUtil.NamelessClass1867010746.$SwitchMap$ic2$core$util$AabbUtil$Edge[edge.ordinal()]) {
      case 1:
         return new double[]{-bbox.minY, -bbox.minZ, -1.0D, 0.0D, 0.0D, 0.0D};
      case 2:
         return new double[]{bbox.minX, 0.0D, 0.0D, -bbox.minZ, 0.0D, 1.0D};
      case 3:
         return new double[]{0.0D, bbox.minX, 0.0D, bbox.minY, -1.0D, 0.0D};
      case 4:
         return new double[]{bbox.maxX, 0.0D, 0.0D, -bbox.minZ, 0.0D, 1.0D};
      case 5:
         return new double[]{0.0D, bbox.maxX, 0.0D, bbox.minY, -1.0D, 0.0D};
      case 6:
         return new double[]{-bbox.maxY, -bbox.minZ, -1.0D, 0.0D, 0.0D, 0.0D};
      case 7:
         return new double[]{0.0D, bbox.minX, 0.0D, bbox.maxY, -1.0D, 0.0D};
      case 8:
         return new double[]{-bbox.minY, -bbox.maxZ, -1.0D, 0.0D, 0.0D, 0.0D};
      case 9:
         return new double[]{bbox.minX, 0.0D, 0.0D, -bbox.maxZ, 0.0D, 1.0D};
      case 10:
         return new double[]{0.0D, bbox.maxX, 0.0D, bbox.maxY, -1.0D, 0.0D};
      case 11:
         return new double[]{-bbox.maxY, -bbox.maxZ, -1.0D, 0.0D, 0.0D, 0.0D};
      case 12:
         return new double[]{bbox.maxX, 0.0D, 0.0D, -bbox.maxZ, 0.0D, 1.0D};
      default:
         return new double[0];
      }
   }

   private static double side(double[] ray1, double[] ray2) {
      return ray1[2] * ray2[3] + ray1[5] * ray2[1] + ray1[4] * ray2[0] + ray1[1] * ray2[5] + ray1[0] * ray2[4] + ray1[3] * ray2[2];
   }

   private static Vec3 getIntersectionWithPlane(Vec3 origin, Vec3 direction, Vec3 planeOrigin, Vec3 planeNormalVector) {
      double distance = getDistanceToPlane(origin, direction, planeOrigin, planeNormalVector);
      return Vec3.createVectorHelper(origin.xCoord + direction.xCoord * distance, origin.yCoord + direction.yCoord * distance, origin.zCoord + direction.zCoord * distance);
   }

   private static double getDistanceToPlane(Vec3 origin, Vec3 direction, Vec3 planeOrigin, Vec3 planeNormalVector) {
      Vec3 base = Vec3.createVectorHelper(planeOrigin.xCoord - origin.xCoord, planeOrigin.yCoord - origin.yCoord, planeOrigin.zCoord - origin.zCoord);
      return dotProduct(base, planeNormalVector) / dotProduct(direction, planeNormalVector);
   }

   private static double dotProduct(Vec3 a, Vec3 b) {
      return a.xCoord * b.xCoord + a.yCoord * b.yCoord + a.zCoord * b.zCoord;
   }

   // $FF: synthetic class
   static class NamelessClass1867010746 {

      // $FF: synthetic field
      static final int[] $SwitchMap$ic2$api$Direction;
      // $FF: synthetic field
      static final int[] $SwitchMap$ic2$core$util$AabbUtil$Edge = new int[AabbUtil.Edge.values().length];


      static {
         try {
            $SwitchMap$ic2$core$util$AabbUtil$Edge[AabbUtil.Edge.AD.ordinal()] = 1;
         } catch (NoSuchFieldError var18) {
            ;
         }

         try {
            $SwitchMap$ic2$core$util$AabbUtil$Edge[AabbUtil.Edge.AB.ordinal()] = 2;
         } catch (NoSuchFieldError var17) {
            ;
         }

         try {
            $SwitchMap$ic2$core$util$AabbUtil$Edge[AabbUtil.Edge.AE.ordinal()] = 3;
         } catch (NoSuchFieldError var16) {
            ;
         }

         try {
            $SwitchMap$ic2$core$util$AabbUtil$Edge[AabbUtil.Edge.DC.ordinal()] = 4;
         } catch (NoSuchFieldError var15) {
            ;
         }

         try {
            $SwitchMap$ic2$core$util$AabbUtil$Edge[AabbUtil.Edge.DH.ordinal()] = 5;
         } catch (NoSuchFieldError var14) {
            ;
         }

         try {
            $SwitchMap$ic2$core$util$AabbUtil$Edge[AabbUtil.Edge.BC.ordinal()] = 6;
         } catch (NoSuchFieldError var13) {
            ;
         }

         try {
            $SwitchMap$ic2$core$util$AabbUtil$Edge[AabbUtil.Edge.BF.ordinal()] = 7;
         } catch (NoSuchFieldError var12) {
            ;
         }

         try {
            $SwitchMap$ic2$core$util$AabbUtil$Edge[AabbUtil.Edge.EH.ordinal()] = 8;
         } catch (NoSuchFieldError var11) {
            ;
         }

         try {
            $SwitchMap$ic2$core$util$AabbUtil$Edge[AabbUtil.Edge.EF.ordinal()] = 9;
         } catch (NoSuchFieldError var10) {
            ;
         }

         try {
            $SwitchMap$ic2$core$util$AabbUtil$Edge[AabbUtil.Edge.CG.ordinal()] = 10;
         } catch (NoSuchFieldError var9) {
            ;
         }

         try {
            $SwitchMap$ic2$core$util$AabbUtil$Edge[AabbUtil.Edge.FG.ordinal()] = 11;
         } catch (NoSuchFieldError var8) {
            ;
         }

         try {
            $SwitchMap$ic2$core$util$AabbUtil$Edge[AabbUtil.Edge.HG.ordinal()] = 12;
         } catch (NoSuchFieldError var7) {
            ;
         }

         $SwitchMap$ic2$api$Direction = new int[Direction.values().length];

         try {
            $SwitchMap$ic2$api$Direction[Direction.XN.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            $SwitchMap$ic2$api$Direction[Direction.XP.ordinal()] = 2;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            $SwitchMap$ic2$api$Direction[Direction.YN.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            $SwitchMap$ic2$api$Direction[Direction.YP.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            $SwitchMap$ic2$api$Direction[Direction.ZN.ordinal()] = 5;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            $SwitchMap$ic2$api$Direction[Direction.ZP.ordinal()] = 6;
         } catch (NoSuchFieldError var1) {
            ;
         }

      }
   }

   static enum Edge {

      AD("AD", 0),
      AB("AB", 1),
      AE("AE", 2),
      DC("DC", 3),
      DH("DH", 4),
      BC("BC", 5),
      BF("BF", 6),
      EH("EH", 7),
      EF("EF", 8),
      CG("CG", 9),
      FG("FG", 10),
      HG("HG", 11);
      // $FF: synthetic field
      private static final AabbUtil.Edge[] $VALUES = new AabbUtil.Edge[]{AD, AB, AE, DC, DH, BC, BF, EH, EF, CG, FG, HG};


      private Edge(String var1, int var2) {}

   }
}
