package Firelight.RedstoneUtils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import Firelight.BlockyMachines.Items.EnergyContainer;
import Firelight.RedstoneUtils.Items.RedstoneTransmitter;
import Firelight.RedstoneUtils.Machines.RedstoneReceiver;

public class GlobalEventHandler implements Listener {
	@EventHandler(priority=EventPriority.LOWEST)
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		
		ItemStack item = player.getEquipment().getItemInMainHand();
		if (item != null && item.hasItemMeta() 
				&& RedstoneTransmitter.isRedstoneTransmitter(item) 
				&& EnergyContainer.getEnergyContent(item).getEnergy() >= 128) {
			EnergyContainer.takeEnergy(item, 128);
			String id = RedstoneTransmitter.getId(item);
			
			RedstoneReceiver panel = RedstoneReceiver.getReceiverById(id);
			if (panel != null) {
				Location l = panel.getCenter().clone().add(0,1,0);
				if (l.getBlock().getType() != Material.REDSTONE_TORCH_OFF) {
					player.sendRawMessage(
							"\u00A7c[\u00A74Redstone Transmitter\u00A7c] \u00A74"
							+ "The linked receiver is unable to receive. Is there a redstone torch placed on top?\u00A7f");
					return;
				}
				panel.openInventory(player);
			} else {
				player.sendRawMessage(
						"\u00A7c[\u00A74Redstone Transmitter\u00A7c] \u00A74"
						+ "This transmitter seems to be unlinked, or receiver isn't loaded.\u00A7f");
			}
		}
	}
}
