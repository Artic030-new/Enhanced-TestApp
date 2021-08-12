package advsolar;

import advsolar.AdvancedSolarPanel;
import advsolar.TileEntitySolarPanel;

public class TileEntityAdvancedSolarPanel extends TileEntitySolarPanel {

   public TileEntityAdvancedSolarPanel() {
      super("Улучшенная солнечная панель", 2, AdvancedSolarPanel.advGenDay, AdvancedSolarPanel.advGenNight, AdvancedSolarPanel.advOutput, AdvancedSolarPanel.advStorage);
   }

   public String getInvName() {
      return "Adv Solar Panel";
   }
}
