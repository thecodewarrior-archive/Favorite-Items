package thecodewarrior.favorite;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CommonProxy {
	public void init() {}
	
	public void toggleFavorite(int slot) {}
	
	public void setFavorite(ItemStack stack, boolean favorite) {}
	
	public boolean isFavorite(ItemStack stack) {return false;}
}
