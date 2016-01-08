package thecodewarrior.favorite;

import java.util.Collections;
import java.util.Set;

import net.minecraft.item.ItemStack;

public class FavoriteItemsAPI {
	
	public static boolean isFavorite(ItemStack stack) {
		return FavoriteMod.proxy.isFavorite(stack);
	}
	
	public static void makeFavorite(ItemStack stack) {
		FavoriteMod.proxy.setFavorite(stack, true);
	}
	
	public static void makeNotFavorite(ItemStack stack) {
		FavoriteMod.proxy.setFavorite(stack, false);
	}
	
	public static void addFavoriteEntry(String entry) {
		FavoriteMod.proxy.setFavorite(entry, true);
	}
	
	public static void removeFavoriteEntry(String entry) {
		FavoriteMod.proxy.setFavorite(entry, false);
	}
	
	public static Set<String> getFavoriteEntries() {
		return Collections.unmodifiableSet(FavoriteMod.proxy.getFavoriteSet());
	}
	
	public static boolean isFavorite(String name) {
		return FavoriteMod.proxy.isFavorite(name);
	}
	
}
