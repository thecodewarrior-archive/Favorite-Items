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

import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.WorldEvent;

public class ClientProxy extends CommonProxy {
	
	Set<String> favorites = new HashSet<String>();
	public KeyBinding fave = new KeyBinding("key.item.favorite.desc", Keyboard.KEY_F, "key.categories.inventory");
	Minecraft mc;
	
	public void init() {
		mc = Minecraft.getMinecraft();
		ClientRegistry.registerKeyBinding(fave);
		super.init();
	}
	
	ResourceLocation underlay = new ResourceLocation("favorite", "underlay.png");
	ResourceLocation overlay  = new ResourceLocation("favorite",  "overlay.png");
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
	
	public void drawFavoriteUnderlay(RenderItem renderItem, TextureManager manager, ItemStack stack, int x, int y) {
	}
	
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	
	public void drawFavoriteOverlay(RenderItem renderItem, TextureManager manager, ItemStack stack, int x, int y) {
		if(!ASMHooks.isFavorite(stack))
			return;
		float zLevel = renderItem.zLevel;
		
		GL11.glDepthFunc(GL11.GL_GEQUAL);
        GL11.glEnable(GL11.GL_BLEND);
        
        manager.bindTexture(RES_ITEM_GLINT);
        GL11.glColor4f(0.1f, 0.3f, 0.1f, 1.0f);
        renderGlint(0, x - 2, y - 2, 20, 20, (float)( renderItem.zLevel+25 )); // the +25 is to get it (hopefully) high enough to 
        
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        
//        renderItem.zLevel = zLevel;
	}
	
	public void renderGlint(int p_77018_1_, int p_77018_2_, int p_77018_3_, int p_77018_4_, int p_77018_5_, float zLevel) {
		for (int j1 = 0; j1 < 1; ++j1)
        {
            OpenGlHelper.glBlendFunc(772, 1, 0, 0);
            float f = 0.00390625F;
            float f1 = 0.00390625F;
            float f2 = 256 - (   (float)(Minecraft.getSystemTime() % (long)(3000 + j1 * 1873)) / (3000.0F + (float)(j1 * 1873)) * 256.0F   );
            float f3 = 0.0F;
            Tessellator tessellator = Tessellator.instance;
            float f4 = 4.0F;

            if (j1 == 1)
            {
                f4 = -1.0F;
            }

            int p_77018_5____ = p_77018_5_;
            int asdf = 12;
            
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)(p_77018_2_ + 0),			(double)(p_77018_3_ + p_77018_5_),	(double)zLevel, (double)((f2 + 0.0F) * f),						(double)((f3 + (float)p_77018_5_) * f1));
            tessellator.addVertexWithUV((double)(p_77018_2_ + p_77018_4_),	(double)(p_77018_3_ + p_77018_5_),	(double)zLevel, (double)((f2 + (float)p_77018_4_) * f),	(double)((f3 + (float)p_77018_5_) * f1));
            tessellator.addVertexWithUV((double)(p_77018_2_ + p_77018_4_),	(double)(p_77018_3_ + 0),			(double)zLevel, (double)((f2 + (float)p_77018_4_ + (float)p_77018_5____ * f4) * f),								(double)((f3 + 0.0F) * f1));
            tessellator.addVertexWithUV((double)(p_77018_2_ + 0),			(double)(p_77018_3_ + 0),			(double)zLevel, (double)((f2 + (float)p_77018_5____ * f4) * f), 											(double)((f3 + 0.0F) * f1));
            tessellator.draw();
        }
	}
	
	public Set<String> getFavoriteSet() {
		return favorites;
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
			setFavorite(stack.getDisplayName(), favorite);
		}
	}
	
	public void setFavorite(String entry, boolean favorite) {
		reloadFavoritesFile();
		if(favorite) {
			favorites.add(entry);
		} else {
			favorites.remove(entry);
		}
		saveFavoriteFile();
	}
	
	public boolean isFavorite(ItemStack stack) {
		if(stack == null)
			return false;
		return isFavorite( stack.getDisplayName() );
	}
	
	public boolean isFavorite(String name) {
		for(String checkName : favorites) {
			if(matches(checkName, name)) {
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
	public void onPlayerJoin(WorldEvent.Load e) {
		reloadFavoritesFile();
	}
}
