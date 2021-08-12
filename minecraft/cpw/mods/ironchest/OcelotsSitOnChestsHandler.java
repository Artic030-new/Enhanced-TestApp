package cpw.mods.ironchest;

import cpw.mods.ironchest.IronChestAIOcelotSit;
import java.util.List;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.ai.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class OcelotsSitOnChestsHandler {

   @ForgeSubscribe
   public void changeSittingTaskForOcelots(LivingUpdateEvent evt) {
      if(evt.entityLiving.ticksExisted < 5 && evt.entityLiving instanceof EntityOcelot) {
         EntityOcelot ocelot = (EntityOcelot)evt.entityLiving;
         List tasks = ocelot.tasks.taskEntries;

         for(int i = 0; i < tasks.size(); ++i) {
            EntityAITaskEntry task = (EntityAITaskEntry)tasks.get(i);
            if(task.priority == 6 && task.action instanceof EntityAIOcelotSit && !(task.action instanceof IronChestAIOcelotSit)) {
               task.action = new IronChestAIOcelotSit(ocelot, 0.4F);
            }
         }
      }

   }
}
