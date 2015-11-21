package thecodewarrior.favorite.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.tree.ClassNode;

public abstract class TransformerBase implements IClassTransformer {
    public boolean transformRenderItem(ClassNode clazz) {return false;}
}
