package thecodewarrior.favorite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class ClientProxy extends CommonProxy {
	
	Set<String> favorites = new HashSet<String>();
	public KeyBinding fave = new KeyBinding("key.item.favorite.desc", Keyboard.KEY_F, "key.categories.inventory");
	Minecraft mc;
	
	public void init() {
		mc = Minecraft.getMinecraft();
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
	
	public void drawFavoriteUnderlay(RenderItem renderItem, ItemStack stack, int x, int y) {
		if(!ASMHooks.isFavorite(stack))
			return;
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
	
	public void toggleFavorite(int slotId) {
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		Slot slot = p.openContainer.getSlot(slotId);
		if(slot != null) {
			ItemStack stack = slot.getStack();
			boolean fav = !ASMHooks.isFavorite(stack);
			setFavorite(stack, fav);
//			FavoriteMod.network.sendToServer(new MessageSetFavorite(slotId, fav));
		}
	}
	
	public void setFavorite(ItemStack stack, boolean favorite) {
		if(stack != null) {
			reloadFavoritesFile();
			if(favorite) {
				favorites.add(stack.getDisplayName());
			} else {
				favorites.remove(stack.getDisplayName());
			}
			saveFavoriteFile();
		}
	}
	
	public boolean isFavorite(ItemStack stack) {
		if(stack == null)
			return false;
		String displayName = stack.getDisplayName();
		for(String name : favorites) {
			if(matches(name, displayName)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean matches(String pattern, String name) {
		if( pattern.equals(name) ) {
			return true;
		}
		if( pattern.startsWith("%") || pattern.startsWith("@") ) {
			return name.matches(pattern.substring(1));
		}
		return false;
	}
	
	public PlayerControllerMP getPlayerController() {
		return Minecraft.getMinecraft().playerController;
	}
	
	public String getName() {
		String name;
		if(mc.getIntegratedServer() == null) {
			name = mc.func_147104_D().serverName + "-" + mc.func_147104_D().serverIP;
		} else {
			name = mc.getIntegratedServer().getWorldName();
		}
		return name;
	}
	
	public void reloadFavoritesFile() {
		favorites = new HashSet<String>();
		loadFavoriteConfig();
	}
	
	public void saveFavoriteFile() {
		File f = new File( mc.mcDataDir.getAbsolutePath(), "favorites/" + getName() + ".txt");
		try {
			Writer w = new FileWriter(f);
			w.write("# Start line with '%' to indicate use of wildcards (used to increase performance)\n");
			w.write("# Valid wildcards are * for zero or more word chars, + for one or more word chars, and ? for any word char.\n");
			w.write("# Start line with '@' to use an real regular expression\n");
			for(String name : favorites) {
				if(name.startsWith("%")) {
					name = name.replace("\\w*", "*").replace("\\w+", "+").replace("\\w", "?");
				}
				w.write(name + "\n");
			}
			w.close();
		} catch (IOException e) {
			FavoriteMod.l.error("Error writing favorites file");
			e.printStackTrace();
		}
		
	}
	
	public void loadFavoriteConfig() {
		File f = new File( mc.mcDataDir.getAbsolutePath(), "favorites/" + getName() + ".txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = reader.readLine();
			while(line != null) {
				line = line.split("#", 2)[0].trim();
				if(!line.equals("")) {
					if(line.startsWith("%")) {
						line = line.replaceAll("(?<!\\\\)\\*", "\\\\w*").replaceAll("(?<!\\\\)\\+", "\\\\w+").replaceAll("(?<!\\\\)\\?", "\\\\w");
					}
					favorites.add(line);
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			FavoriteMod.l.warn("Favorite save file '" + f.getAbsolutePath() + "' not found, touching");
			try {
				FileUtils.touch(f);
			} catch (IOException e1) {
				FavoriteMod.l.error("Error touching favorite save file.");
				e1.printStackTrace();
			}
		} catch (IOException e) {
			FavoriteMod.l.error("Error reading favorite save file");
			e.printStackTrace();
		}
	}
	
	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent e) {
		reloadFavoritesFile();
	}
}
