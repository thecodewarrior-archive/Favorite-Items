package thecodewarrior.favorite;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ASMHooks {
	public static boolean overrideTrue = false;
	public static boolean completeLock = false;
	
	public static void drawFavoriteUnderlay(RenderItem renderItem, ItemStack stack, int x, int y) {
		((ClientProxy)FavoriteMod.proxy).drawFavoriteUnderlay(renderItem, stack, x, y);
	}
	
	public static boolean isFavorite(ItemStack stack) {
		return FavoriteMod.proxy.isFavorite(stack);
	}
	
	public static boolean canRemove(Slot slot, EntityPlayer player) {
//		if(overrideTrue && !completeLock)
//			return true;
//		
//		if(slot != null && slot.getStack() != null) {
//			ItemStack stack = slot.getStack();
//			if(stack.getTagCompound() != null) {
//				if(stack.getTagCompound().getBoolean("favorite")) {
//					return false;
//				}
//			}
//		}
		
		return true;
	}
	
	public static void inventoryKeyPress(GuiContainer container, Slot slot, char pressed, int keyCode) {
		if(keyCode == ((ClientProxy)FavoriteMod.proxy).fave.getKeyCode()) {
			if(slot != null) {
				ItemStack stack = slot.getStack();
				if(stack != null) {
					FavoriteMod.proxy.toggleFavorite( slot.slotNumber );
				}
			}
		}
	}
	
	public static int actionOffset() {
		return 100;
	}
	
	public static boolean shouldAbortClick(int windowId, int slotId, int data, int action, EntityPlayer player) {
		if(slotId == -999) {
			if(isFavorite(player.inventory.getItemStack())) {
				return true;
			}
			return false;
		}
		Slot slot = player.openContainer.getSlot(slotId);
		ItemStack stack = slot.getStack();
		if(stack == null) {
			return false;
		}
		if(action == actionOffset()) { // action will be 0+actionOffset() if it is a normal physical click, we don't want to abort those
			return false;
		}
		if(isFavorite(stack)) {
			return true;
		}
		return false;
	}
	
	public static ItemStack abortedClickStack(int windowId, int slotId, int data, int action, EntityPlayer player) {
		return null;
	}
}	
