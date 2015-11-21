package thecodewarrior.favorite;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
	
	List<String> favorites = new ArrayList<String>();
	public KeyBinding fave = new KeyBinding("key.item.favorite.desc", Keyboard.KEY_F, "key.categories.inventory");
	
	public void init() {
		ClientRegistry.registerKeyBinding(fave);
		super.init();
	}
	
	ResourceLocation underlay = new ResourceLocation("favorite", "overlay.png");
	IIcon fullIIcon = new IIcon() {
		public float getMinV() { return 0; }
		public float getMinU() { return 0; }
		public float getMaxV() { return 1; }
		public float getMaxU() { return 1; }
		public float getInterpolatedV(double f) { return (float)f; }
		public float getInterpolatedU(double f) { return (float)f; }
		public int getIconWidth() { return 1; }
		public String getIconName() { return null; }
		public int getIconHeight() { return 1; }
	};
	
	public void toggleFavorite(int slotId) {
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		Slot slot = p.openContainer.getSlot(slotId);
		if(slot != null) {
			ItemStack stack = slot.getStack();
			FavoriteMod.network.sendToServer(new MessageSetFavorite(slotId, !ASMHooks.isFavorite(stack)));
		}
	}
	
	public void drawFavoriteUnderlay(RenderItem renderItem, ItemStack stack, int x, int y) {
		if(!ASMHooks.isFavorite(stack))
			return;
		Minecraft mc = Minecraft.getMinecraft();
		float zLevel = renderItem.zLevel + 50.0F;
		GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        mc.renderEngine.bindTexture(underlay);
        
        GL11.glColor4f(1, 1, 1, 1.0F);

        GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, a renderEffect can derp them up.
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        // x, y, icon, w, h
        renderItem.renderIcon(x, y, fullIIcon, 16, 16);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
	}
}
