package Firelight.RedstoneUtils.Items;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Firelight.BlockyMachines.Items.EnergyContainer;

public class RedstoneTransmitter extends EnergyContainer {
	/* The API for custom items is terrible right now. I'm still working
	 * on making it better. Please don't kill me for how ugly this is at
	 * the moment.
	 */
	
	public RedstoneTransmitter(int energy) {
		super(Material.PAPER, (short)0, energy, 4096);
		
		ItemMeta meta = getItemMeta();
		meta.setDisplayName("\u00A7r\u00A79Redstone Transmitter\u00A7f");
		List<String> lore = meta.getLore();
		lore.set(3, "\u00A7r\u00A71ID: Unlinked\u00A7f");
		meta.setLore(lore);
		setItemMeta(meta);
	}
	
	public static boolean isRedstoneTransmitter(ItemStack item) {
		if (!EnergyContainer.isChargeable(item)) return false;
		EnergyContent ec = EnergyContainer.getEnergyContent(item);
		if (ec.getCapacity() != 4096) return false;
		
		return item.getType() == Material.PAPER;
	}
	
	public static String getIdRaw(ItemStack item) {
		if (!isRedstoneTransmitter(item)) return null;
		ItemMeta meta = item.getItemMeta();
		String idString = meta.getLore().get(3);
		String id = idString.substring(8, idString.length()-2);
		return id;
	}
	
	public static void setId(ItemStack item,String id,int discriminator) {
		if (!isRedstoneTransmitter(item)) return;
		
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.set(3, "\u00A7r\u00A71ID: " + id + "-" + discriminator + "\u00A7f");
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
	
	public static int getDiscriminator(ItemStack item) {
		if (!isRedstoneTransmitter(item)) return -1;
		String id = getIdRaw(item);
		if (id != null) {
			int pos = id.indexOf("-");
			if (pos > -1) {
				try {
					return Integer.parseInt(id.substring(pos+1));
				} finally {
				}
			}
		}
		return -1;
	}
	
	public static String getId(ItemStack item) {
		if (!isRedstoneTransmitter(item)) return null;
		String id = getIdRaw(item);
		if (id != null) {
			int pos = id.indexOf("-");
			if (pos > -1) {
				return id.substring(0, pos);
			}
		}
		return id;
	}
}
