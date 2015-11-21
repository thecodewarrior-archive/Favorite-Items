package thecodewarrior.favorite.asm;


import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.TransformerExclusions({"thecodewarrior.favorite.asm"})
@IFMLLoadingPlugin.MCVersion("") // We're using runtime debof integration, so no point in being specific about version
public class FMLPlugin implements IFMLLoadingPlugin {
    public static boolean runtimeDeobfEnabled = false;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"thecodewarrior.favorite.asm.Transformer"};
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        runtimeDeobfEnabled = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }
}
