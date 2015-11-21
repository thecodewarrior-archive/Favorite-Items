package thecodewarrior.favorite.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import thecodewarrior.favorite.FavoriteMod;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Transformer implements IClassTransformer {
	
	boolean isObf = false;
	
    public Transformer() {
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

    	if(name != transformedName) {
    		isObf = true;
    	}
    	
        // Sanity checking so it doesn't look like this mod caused crashes when things were missing.
        if(bytes == null || bytes.length == 0) {
            return bytes;
        }

        ClassReader cr = new ClassReader(bytes);
        ClassNode cn = new ClassNode(Opcodes.ASM4);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        cr.accept(cn, 0);

        if("net.minecraft.inventory.Slot".equals(transformedName)) {
            FavoriteMod.l.info("transforming Slot");
        	transformSlot(cn);

            cn.accept(cw);
            return cw.toByteArray();
        }
        
        if("net.minecraft.client.gui.inventory.GuiContainer".equals(transformedName)) {
            FavoriteMod.l.info("transforming GuiContainer");
            transformGuiContainer(cn);

            cn.accept(cw);
            return cw.toByteArray();
        }
        
        if("net.minecraft.inventory.Container".equals(transformedName)) {
            FavoriteMod.l.info("transforming Container");
            transformContainer(cn);

            cn.accept(cw);
            return cw.toByteArray();
        }
        
        if("net.minecraft.client.multiplayer.PlayerControllerMP".equals(transformedName)) {
            FavoriteMod.l.info("transforming PlayerControllerMP");
            transformPlayerControllerMP(cn);

            cn.accept(cw);
            return cw.toByteArray();
        }

        if("net.minecraft.client.renderer.entity.RenderItem".equals(transformedName)) {
        	FavoriteMod.l.info("transforming RenderItem");
        	cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        	if(transformRenderItem(cn)) {
            	cn.accept(cw);
            	return cw.toByteArray();
        	}
        }
        
        return bytes;
    }
    
    public boolean transformRenderItem(ClassNode clazz) {
    	String stack = mType("net/minecraft/item/ItemStack");
    	String renderItem = mType("net/minecraft/client/renderer/entity/RenderItem");
    	for (MethodNode method : clazz.methods)
        {
            boolean obf = false;
            if(isMethod(method,
            		new String[]{"renderItemAndEffectIntoGUI", "func_82406_b", "b"},
            		new String[]{
            			"(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;II)V",
            			"(Lbbu;Lbqf;Ladd;II)V"
            		}
            )) {
                FavoriteMod.l.info("	transforming renderItemAndEffectIntoGUI (actually " + method.name + method.desc + ")");
                
                InsnList list = new InsnList();
            	
            	list.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            	list.add(new VarInsnNode(Opcodes.ALOAD, 3)); // stack
            	list.add(new VarInsnNode(Opcodes.ILOAD, 4)); // x
            	list.add(new VarInsnNode(Opcodes.ILOAD, 5)); // y
            	list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "thecodewarrior/favorite/ASMHooks", "drawFavoriteUnderlay", "(L"+renderItem+";L" + stack + ";II)V", false));

            	method.instructions.insertBefore(method.instructions.getFirst(), list);
            }
        }
    	return true;
    }
    
    public void transformSlot(ClassNode clazz) {
		String playerClass = FMLDeobfuscatingRemapper.INSTANCE.map("net/minecraft/entity/player/EntityPlayer");
		String slotClass   = FMLDeobfuscatingRemapper.INSTANCE.map("net/minecraft/inventory/Slot");
    	
    	for (MethodNode method : clazz.methods)
        {
    		String unmappedName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(clazz.name, method.name, method.desc);
            String unmappedDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(method.desc);

            if(isMethod(method,
            		new String[]{"canTakeStack", "func_82869_a", "a"},
            		new String[]{"(Lnet/minecraft/entity/player/EntityPlayer;)Z", "(Lyz;)Z"}
            )) {
                FavoriteMod.l.info("	transforming canTakeStack (actually " + method.name + method.desc + ")");
            	InsnList list = new InsnList();
            	list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            	list.add(new VarInsnNode(Opcodes.ALOAD, 1));
            	list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "thecodewarrior/favorite/ASMHooks", "canRemove", "(L"+slotClass+";L"+playerClass+";)Z", false));
            	list.add(new InsnNode(Opcodes.IRETURN));
            	method.instructions.insertBefore(method.instructions.getFirst(), list);
            }
        }
    }
    
    public void transformGuiContainer(ClassNode clazz) {
    	String guiContainer = mType("net/minecraft/client/gui/inventory/GuiContainer");
    	String slot = mType("net/minecraft/inventory/Slot");
    	String player = mType("net/minecraft/entity/player/EntityPlayer");
    	
    	for (MethodNode method : clazz.methods)
        {
    		String unmappedName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(clazz.name, method.name, method.desc);
            String unmappedDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(method.desc);

            if(isMethod(method,
            		new String[]{"keyTyped", "func_73869_a", "a"},
            		new String[]{"(CI)V"}
            )) {
                FavoriteMod.l.info("		transforming keyTyped (actually " + method.name + method.desc + ")");
            	InsnList list = new InsnList();
            	list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            	list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            	list.add(new FieldInsnNode(Opcodes.GETFIELD,
            			guiContainer,
            			isObf ? "field_147006_u" : "theSlot",
            			"L"+slot+";"));
            	list.add(new VarInsnNode(Opcodes.ILOAD, 1));
            	list.add(new VarInsnNode(Opcodes.ILOAD, 2));
            	list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "thecodewarrior/favorite/ASMHooks", "inventoryKeyPress","(L"+ guiContainer +";L"+ slot +";CI)V", false));
            	method.instructions.insertBefore(method.instructions.getFirst(), list);
            }
            
            if(isMethod(method,
            		new String[]{"handleMouseClick", "a"},
            		new String[]{"(Lnet/minecraft/inventory/Slot;III)V", "(Laay;III)V"}
            )) {
                FavoriteMod.l.info("		transforming handleMouseClick (actually " + method.name + method.desc + ")");
            	InsnList list = new InsnList();
            	list.add(new VarInsnNode(Opcodes.ILOAD, 4));
            	list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "thecodewarrior/favorite/ASMHooks", "actionOffset","()I", false));
            	list.add(new InsnNode(Opcodes.IADD));
            	list.add(new VarInsnNode(Opcodes.ISTORE, 4));
            	method.instructions.insertBefore(method.instructions.getFirst(), list);
            }
        }
    }
    
    public void transformContainer(ClassNode clazz) {
    	for (MethodNode method : clazz.methods)
        {
    		String unmappedName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(clazz.name, method.name, method.desc);
            String unmappedDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(method.desc);
            boolean obf = false;
            if(isMethod(method,
            		new String[]{"slotClick", "func_75144_a", "a"},
            		new String[]{"(IIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;","(IIILyz;)Ladd;"}
            )) {
            	if(method.name.equals("a")) {
            		obf = true;
            	}
                FavoriteMod.l.info("	transforming slotClick (actually " + method.name + method.desc + ")");
                AbstractInsnNode line1 = null;
                AbstractInsnNode line2 = null;
            	for (int i = 0; i < method.instructions.size(); i++) {
					AbstractInsnNode insn = method.instructions.get(i);
					
					if(insn instanceof LineNumberNode && ((LineNumberNode)insn).line == ( obf ? 209 : 305)) {
						line1 = insn;
						FavoriteMod.l.info("		found overrideTrue=true insertion point");
					}
					if(insn instanceof LineNumberNode && ((LineNumberNode)insn).line == ( obf ? 277 : 399)) {
						line2 = insn;
						FavoriteMod.l.info("		found overrideTrue=false insertion point");
					}
				}
            	InsnList list1 = new InsnList();
            	InsnList list2 = new InsnList();
            	
            	list1.add(new InsnNode(Opcodes.ICONST_1));
            	list1.add(new FieldInsnNode(Opcodes.PUTSTATIC, "thecodewarrior/favorite/ASMHooks", "overrideTrue", "Z"));
            	
            	list2.add(new InsnNode(Opcodes.ICONST_0));
            	list2.add(new FieldInsnNode(Opcodes.PUTSTATIC, "thecodewarrior/favorite/ASMHooks", "overrideTrue", "Z"));
            	
            	if(line1 != null && line2 != null) {
            		method.instructions.insertBefore(line1, list1);
            		method.instructions.insertBefore(line2, list2);
            	}
            }
        }
    }
    
    public void transformPlayerControllerMP(ClassNode clazz) {
    	String guiContainer = mType("net/minecraft/client/gui/inventory/GuiContainer");
    	String slot = mType("net/minecraft/inventory/Slot");
    	String player = mType("net/minecraft/entity/player/EntityPlayer");
    	String stack = mType("net/minecraft/item/ItemStack");

    	for (MethodNode method : clazz.methods)
        {
    		String unmappedName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(clazz.name, method.name, method.desc);
            String unmappedDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(method.desc);

            if(isMethod(method,
            		new String[]{"windowClick", "func_78753_a", "a"},
            		new String[]{"(IIIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", "(IIIILyz;)Ladd;"}
            )) {
                FavoriteMod.l.info("	transforming windowClick (actually " + method.name + method.desc + ")");
            	InsnList list = new InsnList();
            	LabelNode label = new LabelNode();
            	LabelNode label2 = new LabelNode();
            	
            	list.add(new VarInsnNode(Opcodes.ILOAD, 1));
            	list.add(new VarInsnNode(Opcodes.ILOAD, 2));
            	list.add(new VarInsnNode(Opcodes.ILOAD, 3));
            	list.add(new VarInsnNode(Opcodes.ILOAD, 4));
            	list.add(new VarInsnNode(Opcodes.ALOAD, 5));
            	list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "thecodewarrior/favorite/ASMHooks", "shouldAbortClick","(IIIIL"+ player +";)Z", false));
            	
            	list.add(new InsnNode(Opcodes.ICONST_0));
            	list.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, label));
            	
	            	list.add(new VarInsnNode(Opcodes.ILOAD, 1));
	            	list.add(new VarInsnNode(Opcodes.ILOAD, 2));
	            	list.add(new VarInsnNode(Opcodes.ILOAD, 3));
	            	list.add(new VarInsnNode(Opcodes.ILOAD, 4));
	            	list.add(new VarInsnNode(Opcodes.ALOAD, 5));
	            	list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "thecodewarrior/favorite/ASMHooks", "abortedClickStack","(IIIIL"+ player +";)L" + stack + ";", false));
	            	list.add(new InsnNode(Opcodes.ARETURN));
	            	
            	list.add(label);
            	
            	list.add(new VarInsnNode(Opcodes.ILOAD, 4));
            	list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "thecodewarrior/favorite/ASMHooks", "actionOffset","()I", false));
            	list.add(new JumpInsnNode(Opcodes.IF_ICMPLT, label2));
            	
	            	list.add(new VarInsnNode(Opcodes.ILOAD, 4)); // load action
	            	list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "thecodewarrior/favorite/ASMHooks", "actionOffset","()I", false));
	        		list.add(new InsnNode(Opcodes.ISUB));
	        		list.add(new VarInsnNode(Opcodes.ISTORE, 4));
        		
	        	list.add(label2);
	        	
            	method.instructions.insertBefore(method.instructions.getFirst(), list);
            }
        }
    }
    
    public static boolean isMethod(MethodNode method,
    		String[] names,
    		String[] descs) {
    	boolean matchName = names.length == 0, matchDesc = descs.length == 0;
    	for (int i = 0; i < names.length; i++) {
			matchName = matchName || method.name.equals(names[i]);
		}
    	for (int i = 0; i < descs.length; i++) {
			matchDesc = matchDesc || method.desc.equals(descs[i]);
		}
    	return matchName && matchDesc;
    }
    
    public static String mType(String name) {
    	return FMLDeobfuscatingRemapper.INSTANCE.map(name);
    }
    public static String mField(String type, String name, String desc) {
    	return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(type, name, desc);
    }
    public static String mMethod(String type, String name, String desc) {
    	return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(type, name, desc);
    }
}
