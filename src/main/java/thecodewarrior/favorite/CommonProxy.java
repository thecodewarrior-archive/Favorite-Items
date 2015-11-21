package thecodewarrior.favorite;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommonProxy {
	public void init() {}
	
	public void toggleFavorite(int slot) {};
	
	public void setFavorite(ItemStack stack, boolean favorite) {
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		if(favorite) {
			tag.setBoolean("favorite", true);
		} else {
			tag.removeTag("favorite");
			if(tag.hasNoTags()) {
				stack.setTagCompound(null);
			}
		}
	}
	
	public boolean isFavorite(ItemStack stack) {
		if(stack == null)
			return false;
		boolean favorite = false;
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null) {
			favorite = false;
		} else {
			favorite = tag.getBoolean("favorite");
		}
		return favorite;
	}
	
	@SubscribeEvent
	public void drop(ItemTossEvent event) {
		if(ASMHooks.isFavorite( event.entityItem.getEntityItem() )) {
			event.entityItem.delayBeforeCanPickup = 0;
		}
	}
}
