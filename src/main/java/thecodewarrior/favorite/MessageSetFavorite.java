package thecodewarrior.favorite;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageSetFavorite implements IMessage {

	public MessageSetFavorite() {
	}
	
	int slotid;
	boolean setTo;
	
	public MessageSetFavorite(int slotid, boolean setTo) {
		this.slotid = slotid;
		this.setTo = setTo;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		slotid = buf.readInt();
		setTo = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(slotid);
		buf.writeBoolean(setTo);
	}

	public static class Handler implements IMessageHandler<MessageSetFavorite, IMessage> {

		@Override
		public IMessage onMessage(MessageSetFavorite message, MessageContext ctx) {
			Container container = ctx.getServerHandler().playerEntity.openContainer;
			if(message.slotid >= container.getInventory().size())
				return null;
			Slot slot = container.getSlot(message.slotid);
			if(slot != null && slot.getStack() != null) {
				FavoriteMod.proxy.setFavorite( slot.getStack(), message.setTo );
			}
			return null;
		}
		
	}
	
}
