package advsolar;

import advsolar.AdvancedSolarPanel;
import advsolar.TileEntitySolarPanel;

public class TileEntityUltimateSolarPanel extends TileEntitySolarPanel {

   public TileEntityUltimateSolarPanel() {
      super("Совершенная солнечная панель", 3, AdvancedSolarPanel.uhGenDay, AdvancedSolarPanel.uhGenNight, AdvancedSolarPanel.uhOutput, AdvancedSolarPanel.uhStorage);
   }

   public String getInvName() {
      return "Ult Solar Panel";
   }
}
