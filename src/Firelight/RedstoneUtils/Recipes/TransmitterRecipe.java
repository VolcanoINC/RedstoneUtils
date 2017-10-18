package Firelight.RedstoneUtils.Recipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import Firelight.BlockyMachines.Items.FactoryRecipes.FactoryRecipeHandler.BaseRecipe;
import Firelight.RedstoneUtils.Items.RedstoneTransmitter;

public class TransmitterRecipe implements BaseRecipe {
	private static final Material N = null;
	private static final Material W = Material.WOOD;
	private static final Material P = Material.PAPER;
	private static final Material R = Material.REDSTONE_BLOCK;
	private static final Material B = Material.STONE_BUTTON;
	private static final Material I = Material.IRON_INGOT;
	private static final Material A = Material.REDSTONE_TORCH_ON;
	
	private static final ItemStack display = new RedstoneTransmitter(0);
	
	private static Material[][] recipe = {
			{ N, N, A, N, N },
			{ N, I, W, N, N },
			{ N, I, P, B, N },
			{ N, I, R, N, N },
			{ N, N, N, N, N }
	};
	
	@Override
	public boolean isRecipeCorrect(ItemStack[][] items) {
		for (int y = 0; y < recipe.length; y++) {
			for (int x = 0; x < recipe.length; x++) {
				if (items[y][x] != null && recipe[y][x] == null) return false;
				if (items[y][x] == null && recipe[y][x] != null) return false;
				if (items[y][x] != null && recipe[y][x] != null 
						&& items[y][x].getType() != recipe[y][x]) return false;
			}
		}
		
		return true;
	}

	@Override
	public ItemStack getOutputDisplay(ItemStack[][] items) {
		return display;
	}

	@Override
	public ItemStack getOutputItems(ItemStack[][] items) {
		return new RedstoneTransmitter(0);
	}

	@Override
	public int getDuration() { return 8; }
}
