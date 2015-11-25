package thecodewarrior.favorite;

import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ASMHooks {
	public static boolean overrideTrue = false;
	public static boolean completeLock = false;
	
	public static void drawFavoriteUnderlay(RenderItem renderItem, ItemStack stack, int x, int y) {
		((ClientProxy)FavoriteMod.proxy).drawFavoriteUnderlay(renderItem, stack, x, y);
	}
	public static void drawFavoriteOverlay(RenderItem renderItem, ItemStack stack, int x, int y) {
		((ClientProxy)FavoriteMod.proxy).drawFavoriteOverlay(renderItem, stack, x, y);
	}
	
	public static boolean isFavorite(ItemStack stack) {
		return FavoriteMod.proxy.isFavorite(stack);
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
	
	public static boolean shouldAbortThrowOutsideGui(EntityClientPlayerMP player, boolean isCtrlKeyPressed) {
		if(isFavorite(player.getHeldItem())) {
			return true;
		}
		return false;
	}
	
	public static void guiPreClose(EntityClientPlayerMP player) {
		Container c = player.openContainer;
		ItemStack held = player.inventory.getItemStack();
		if(held != null) {
			for(Slot s : (List<Slot>)c.inventorySlots) {
				if(s.inventory == player.inventory && s.getStack() == null) {
					((ClientProxy)FavoriteMod.proxy).getPlayerController().windowClick(c.windowId, s.slotNumber, 0, 100, player);
				}
			}
		}
	}
	
	public static boolean shouldAbortClick(int windowId, int slotId, int data, int action, EntityPlayer player) {
		if(slotId < 0) {
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
