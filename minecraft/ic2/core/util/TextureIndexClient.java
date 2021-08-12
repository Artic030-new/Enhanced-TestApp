package ic2.core.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.util.TextureIndex;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class TextureIndexClient extends TextureIndex {

   private Map textureIndexes = new HashMap();


   public int get(int blockId, int index) {
      String textureFile = Block.blocksList[blockId].getTextureFile();
      if(!this.textureIndexes.containsKey(textureFile)) {
         int[] image = Minecraft.getMinecraft().renderEngine.getTextureContents(textureFile);
         int subtextureSize = (int)Math.sqrt((double)image.length) / 16;
         Vector values = new Vector(256);
         int compareIndex = 0;

         label43:
         for(int genIndex = 0; genIndex < 256; ++genIndex) {
            int imageBaseIndexA = genIndex / 16 * 16 * subtextureSize * subtextureSize + genIndex % 16 * subtextureSize;
            int cmpIndex = 0;

            label40:
            while(cmpIndex < genIndex) {
               int imageIndexA = imageBaseIndexA;
               int imageIndexB = cmpIndex / 16 * 16 * subtextureSize * subtextureSize + cmpIndex % 16 * subtextureSize;

               for(int y = 0; y < subtextureSize; ++y) {
                  for(int x = 0; x < subtextureSize; ++x) {
                     if(image[imageIndexA] != image[imageIndexB]) {
                        ++cmpIndex;
                        continue label40;
                     }

                     ++imageIndexA;
                     ++imageIndexB;
                  }

                  imageIndexA += 15 * subtextureSize;
                  imageIndexB += 15 * subtextureSize;
               }

               values.add(values.get(cmpIndex));
               continue label43;
            }

            values.add(Integer.valueOf(compareIndex));
            ++compareIndex;
         }

         this.textureIndexes.put(textureFile, values);
      }

      return ((Integer)((List)this.textureIndexes.get(textureFile)).get(index)).intValue();
   }

   public void reset() {
      this.textureIndexes.clear();
   }
}
