package thecodewarrior.favorite;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid=FavoriteMod.MODID, name=FavoriteMod.MODNAME, version=FavoriteMod.VERSION, acceptableRemoteVersions = "*")
public class FavoriteMod {
	public static final String MODID = "favorites";
	public static final String MODNAME = "Favorites!";
	public static final String VERSION = "1.0";
	public static final Logger l = LogManager.getLogger("Favorites");
	
	@SidedProxy(serverSide="thecodewarrior.favorite.CommonProxy", clientSide="thecodewarrior.favorite.ClientProxy")
	public static CommonProxy proxy;
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent event) {
	    MinecraftForge.EVENT_BUS.register(proxy);
	    proxy.init();
	    
	    if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
	    	FavoriteMod.l.error("=================================================================");
	    	FavoriteMod.l.error("         !!! FavoriteItems is a client side only mod !!!         ");
	    	FavoriteMod.l.error("=================================================================");
	    	throw new RuntimeException("FavoriteItems can't be run on a server!");
	    }
	    
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        
        ASMHooks.completeLock = config.getBoolean("complete_lock", Configuration.CATEGORY_GENERAL, false, "Don't let favorited items be moved by picking them up.");
        
        config.save();
	}
}
