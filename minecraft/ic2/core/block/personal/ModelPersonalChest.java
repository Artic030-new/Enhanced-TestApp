package ic2.core.block.personal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelPersonalChest extends ModelBase {

   ModelRenderer wallR;
   ModelRenderer wallL;
   ModelRenderer wallB;
   ModelRenderer wallU;
   ModelRenderer wallD;
   public ModelRenderer door;


   public ModelPersonalChest() {
      super.textureWidth = 64;
      super.textureHeight = 64;
      this.wallR = new ModelRenderer(this, 0, 0);
      this.wallR.addBox(0.0F, 0.0F, 0.0F, 14, 16, 1);
      this.wallR.setRotationPoint(1.0F, 0.0F, 15.0F);
      this.wallR.setTextureSize(64, 64);
      this.wallR.mirror = true;
      this.setRotation(this.wallR, 0.0F, 1.570796F, 0.0F);
      this.wallL = new ModelRenderer(this, 0, 0);
      this.wallL.addBox(0.0F, 0.0F, 0.0F, 14, 16, 1);
      this.wallL.setRotationPoint(15.0F, 0.0F, 1.0F);
      this.wallL.setTextureSize(64, 64);
      this.wallL.mirror = true;
      this.setRotation(this.wallL, 0.0F, -1.570796F, 0.0F);
      this.wallB = new ModelRenderer(this, 1, 1);
      this.wallB.addBox(1.0F, 1.0F, 0.0F, 12, 14, 1);
      this.wallB.setRotationPoint(15.0F, 0.0F, 15.0F);
      this.wallB.setTextureSize(64, 64);
      this.wallB.mirror = true;
      this.setRotation(this.wallB, 0.0F, 3.141593F, 0.0F);
      this.wallU = new ModelRenderer(this, 1, 17);
      this.wallU.addBox(1.0F, 0.0F, 0.0F, 12, 14, 1);
      this.wallU.setRotationPoint(1.0F, 0.0F, 15.0F);
      this.wallU.setTextureSize(64, 64);
      this.wallU.mirror = true;
      this.setRotation(this.wallU, -1.570796F, 0.0F, 0.0F);
      this.wallD = new ModelRenderer(this, 1, 17);
      this.wallD.addBox(1.0F, 0.0F, 0.0F, 12, 14, 1);
      this.wallD.setRotationPoint(15.0F, 15.0F, 1.0F);
      this.wallD.setTextureSize(64, 64);
      this.wallD.mirror = true;
      this.setRotation(this.wallD, -1.570796F, 3.141593F, 0.0F);
      this.door = new ModelRenderer(this, 30, 0);
      this.door.addBox(0.0F, 0.0F, 0.0F, 12, 14, 1);
      this.door.setRotationPoint(2.0F, 1.0F, 2.0F);
      this.door.setTextureSize(64, 64);
      this.door.mirror = true;
   }

   public void renderAll() {
      this.wallR.render(0.0625F);
      this.wallL.render(0.0625F);
      this.wallB.render(0.0625F);
      this.wallU.render(0.0625F);
      this.wallD.render(0.0625F);
      this.door.render(0.0625F);
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }
}
