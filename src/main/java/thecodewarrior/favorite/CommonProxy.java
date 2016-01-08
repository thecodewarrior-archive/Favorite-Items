package thecodewarrior.favorite;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;

public class CommonProxy {
	public void init() {}
	
	public Set<String> getFavoriteSet() { return new HashSet<String>(); }
	
	public void toggleFavorite(int slot) {}
	
	public void setFavorite(ItemStack stack, boolean favorite) {}
	public void setFavorite(String name, boolean favorite) {}
	
	public boolean isFavorite(ItemStack stack) {return false;}
	public boolean isFavorite(String name) {return false;}
}
