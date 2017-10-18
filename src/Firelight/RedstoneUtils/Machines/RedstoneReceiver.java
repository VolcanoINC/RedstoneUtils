package Firelight.RedstoneUtils.Machines;

import java.util.Hashtable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import Firelight.BlockyMachines.DataHandling.TeleporterHandler;
import Firelight.BlockyMachines.Framework.BaseMachine;
import Firelight.BlockyMachines.PowerUtil.PowerReceiver;
import Firelight.BlockyMachines.PowerUtil.PowerUtil;
import Firelight.BlockyMachines.Utility.InventoryLimiter;
import Firelight.BlockyMachines.Utility.InventoryLimiter.InventoryLimits;
import Firelight.BlockyMachines.Utility.InventoryLimiter.InventoryLimits.ItemLimitType;
import Firelight.BlockyMachines.Utility.MachineUtil;
import Firelight.RedstoneUtils.RedstoneUtils;
import Firelight.RedstoneUtils.Items.RedstoneTransmitter;

public class RedstoneReceiver implements BaseMachine,Listener {
	/*
	 * Machine Structure Setup
	 */
	private static Material O = Material.OBSIDIAN;
	private static Material Q = Material.QUARTZ_BLOCK;
	private static Material R = Material.REDSTONE_BLOCK;
	private static Material W = Material.WOOD;
	private static Material[][][] struct = {
			{
				{ O, W, O }, 
				{ W, O, W }, 
				{ O, W, O } 
			}, 
			{
				{ W, R, W }, 
				{ R, Q, R }, 
				{ W, R, W } 
			}, 
			{
				{ O, W, O }, 
				{ W, R, W }, 
				{ O, W, O } 
			} 
		};
	
	private static int[] centerPos = { 1, 2, 1 };
	private static Material[][] blockAliases = { { Material.WOOD, Material.BEDROCK } };
	
	public Material[][][] getStructure() { return struct; }
	public Material[][] getMaterialAliases() { return blockAliases; }
	public int[] getStructureCenter() { return centerPos; }
	public RedstoneReceiver() {} //KEEP THE EMPTY CONSTRUCTOR; THIS IS USED FOR BASE-CLASS HANDLING
	
	private static Hashtable<String,RedstoneReceiver> receiverList = new Hashtable<String,RedstoneReceiver>();
	
	//Machine variables
	private Location center;
	private PowerReceiver receiver;
	private Inventory inventory;
	private String receiverId;
	
	private Location nOutput;
	private Location eOutput;
	private Location sOutput;
	private Location wOutput;
	
	//Machine creation handling
	public RedstoneReceiver(Location center) {
		this.center = center;
		inventory = Bukkit.createInventory(null, 27, "Remote Control");
		receiver = PowerUtil.newReceiver(center.clone().add(0,-3,0), 2048, 0);
		RedstoneUtils.pluginManager.registerEvents(this, RedstoneUtils.plugin);
		receiverId = TeleporterHandler.generate(center);
		receiverList.put(receiverId, this);
		
		nOutput = center.clone().add( 0,-1,-1);
		eOutput = center.clone().add( 1,-1, 0);
		sOutput = center.clone().add( 0,-1, 1);
		wOutput = center.clone().add(-1,-1, 0);
	}
	
	@Override
	public BaseMachine create(Location center,String owner) {
		RedstoneReceiver machine = new RedstoneReceiver(center);
		return machine;
	}
	
	@Override
	public void init(Location center,String owner) {
		InventoryLimits limits = new InventoryLimits();
		limits.setLimitType(ItemLimitType.BLACKLIST);
		
		for (int i = 0; i < 27; i++) {
			ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE,1);
				stack.setDurability((short)8);
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName("\u00A7r\u00A74Blank\u00A7f\u00A7f");
				stack.setItemMeta(meta);
				inventory.setItem(i, stack);
			
			limits.lockSlot(i);
		}
		
		InventoryLimiter.setLimit(inventory, limits);
		this.owner = owner;
	}
	
	private String owner = "";
	@Override
	public String getOwner() { return owner; }

	@Override
	public void loadData(Hashtable<String,Object> data) {
		if (data.get("energy") != null) {
			PowerUtil.disableReceiver(receiver);
			receiver = PowerUtil.newReceiver(center.clone().add(0,-3,0), 2048,(int)((Long)data.get("energy")).intValue());
		}
	}

	@Override
	public Hashtable<String, Object> saveData() {
		Hashtable<String,Object> data = new Hashtable<String,Object>();
		
		data.put("energy",receiver.getInternalPower());
		return data;
	}
	
	/*
	 * Machine running
	 */
	private int lastMachineTick = 0;
	
	@Override
	public boolean onGameTick() {
		lastMachineTick ++;
		return true;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!inventory.equals(event.getInventory())) return;
		
		int slot = event.getRawSlot();
		if (slot < 0) return;
		
		if (slot ==  4) { toggleBlock(nOutput); receiver.takePower(128); }
		if (slot == 14) { toggleBlock(eOutput); receiver.takePower(128); }
		if (slot == 22) { toggleBlock(sOutput); receiver.takePower(128); }
		if (slot == 12) { toggleBlock(wOutput); receiver.takePower(128); }
		renderInventory();
	}
	
	@Override
	public boolean onMachineTick() {
		lastMachineTick = (int)(-Math.random()*10d);
		
		receiver.run();
		
		MachineUtil.setInfo(
				this, 
				"RECEIVER", 
				"Power: " + receiver.getInternalPower() + " RE",
				(int)(receiver.getInternalPower()*100d/2048) + "%"
			);
		
		return true;
	}
	
	@Override
	public boolean onHopperTick() {
		return true;
	}

	@Override
	public Location getCenter() { return center; }

	@Override
	public boolean isMachineTickReady() {
		return lastMachineTick > 50;
	}

	@Override
	public void onBreak(Block block,Player player) {
		PowerUtil.disableReceiver(receiver);
		receiverList.remove(receiverId);
		
		nOutput.getBlock().setType(Material.REDSTONE_BLOCK);
		eOutput.getBlock().setType(Material.REDSTONE_BLOCK);
		sOutput.getBlock().setType(Material.REDSTONE_BLOCK);
		wOutput.getBlock().setType(Material.REDSTONE_BLOCK);
	}
	
	@Override
	public void onUnload() {
		PowerUtil.disableReceiver(receiver);
		receiverList.remove(receiverId);
	}

	@Override
	public void onUse(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.isSneaking()) {
			ItemStack item = player.getEquipment().getItemInMainHand();
			if (item != null && item.hasItemMeta() && RedstoneTransmitter.isRedstoneTransmitter(item)) {
				RedstoneTransmitter.setId(item, receiverId,(int)(Math.random()*65536));
				event.getPlayer().sendRawMessage(
						"\u00A7a[\u00A72Redstone Receiver\u00A7a]\u00A72 Linked your Redstone Transmitter");
				return;
			}
		}
		
		event.getPlayer().sendRawMessage(
				"\u00A7a[\u00A72Redstone Receiver\u00A7a]\u00A72 Power: " + receiver.getInternalPower() + " RE ("
				+ (int)(receiver.getInternalPower()*100d/2048) + "%)");
	}
	
	private void renderInventory() {
		Block nBlock = nOutput.getBlock();
		Block eBlock = eOutput.getBlock();
		Block sBlock = sOutput.getBlock();
		Block wBlock = wOutput.getBlock();
		
		short nData = (short)((nBlock.getType() == Material.REDSTONE_BLOCK) ? 5 : 14);
		short eData = (short)((eBlock.getType() == Material.REDSTONE_BLOCK) ? 5 : 14);
		short sData = (short)((sBlock.getType() == Material.REDSTONE_BLOCK) ? 5 : 14);
		short wData = (short)((wBlock.getType() == Material.REDSTONE_BLOCK) ? 5 : 14);
		
		ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE,1);
			stack.setDurability(nData);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName("\u00A7r\u00A76Toggle North output\u00A7f\u00A7f");
			stack.setItemMeta(meta);
			inventory.setItem(4, stack);
		
		stack = new ItemStack(Material.STAINED_GLASS_PANE,1);
			stack.setDurability(eData);
			meta = stack.getItemMeta();
			meta.setDisplayName("\u00A7r\u00A76Toggle East output\u00A7f\u00A7f");
			stack.setItemMeta(meta);
			inventory.setItem(14, stack);
		
		stack = new ItemStack(Material.STAINED_GLASS_PANE,1);
			stack.setDurability(sData);
			meta = stack.getItemMeta();
			meta.setDisplayName("\u00A7r\u00A76Toggle South output\u00A7f\u00A7f");
			stack.setItemMeta(meta);
			inventory.setItem(22, stack);
		
		stack = new ItemStack(Material.STAINED_GLASS_PANE,1);
			stack.setDurability(wData);
			meta = stack.getItemMeta();
			meta.setDisplayName("\u00A7r\u00A76Toggle West output\u00A7f\u00A7f");
			stack.setItemMeta(meta);
			inventory.setItem(12, stack);
	}
	
	private static void toggleBlock(Location loc) {
		Block b = loc.getBlock();
		if (b.getType() == Material.REDSTONE_BLOCK) {
			b.setType(Material.BEDROCK);
		} else {
			b.setType(Material.REDSTONE_BLOCK);
		}
	}
	
	public void openInventory(Player player) {
		renderInventory();
		player.openInventory(inventory);
	}
	
	public static RedstoneReceiver getReceiverById(String id) {
		return receiverList.get(id);
	}
}
