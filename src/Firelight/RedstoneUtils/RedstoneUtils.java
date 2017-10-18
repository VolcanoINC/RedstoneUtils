package Firelight.RedstoneUtils;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import Firelight.BlockyMachines.BlockyAddon;
import Firelight.BlockyMachines.BlockyMachines;
import Firelight.BlockyMachines.Items.BlockyItem;
import Firelight.BlockyMachines.Items.FactoryRecipes.FactoryRecipeHandler;
import Firelight.RedstoneUtils.Items.*;
import Firelight.RedstoneUtils.Machines.RedstoneReceiver;
import Firelight.RedstoneUtils.Recipes.*;

public class RedstoneUtils extends JavaPlugin {
	public static RedstoneUtils plugin = null;
	public static PluginManager pluginManager = null;
	public void onEnable() {
		//Register the new machine class with the BlockyMachines plugin
		plugin = this;
		pluginManager = getServer().getPluginManager();
		getLogger().info("Adding machines");
		
		BlockyAddon addon = new BlockyAddon(this);
		addon.registerMachineClass(new RedstoneReceiver());
		BlockyMachines.registerAddon(addon);
		
		//Register items
		BlockyItem.registerType("redstonetransmitter", new RedstoneTransmitter(0));
		BlockyItem.registerType("redstonetransmitter_charged", new RedstoneTransmitter(4096));
		
		//Register factory recipes
		FactoryRecipeHandler.addRecipe(new TransmitterRecipe());
		
		pluginManager.registerEvents(new GlobalEventHandler(), this);
	}
}
