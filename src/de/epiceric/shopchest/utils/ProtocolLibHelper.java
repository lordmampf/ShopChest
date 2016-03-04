package de.epiceric.shopchest.utils;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import de.epiceric.shopchest.ShopChest;
import de.epiceric.shopchest.shop.Shop;

public class ProtocolLibHelper {

	public static void init(final Plugin pPlugin) {

		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		protocolManager.addPacketListener(new PacketAdapter(pPlugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
			@Override
			public void onPacketReceiving(final PacketEvent event) {
				final int entityid = event.getPacket().getIntegers().read(0);
				Bukkit.getScheduler().runTaskAsynchronously(pPlugin, new Runnable() {
					@Override
					public void run() {
						final Shop s = ShopChest.utils.getShopFromArmorStandEntity(entityid);
						if (s == null)
							return;

						EntityUseAction action = event.getPacket().getEntityUseActions().read(0);
						Action baction = Action.RIGHT_CLICK_BLOCK;
						if (action == EntityUseAction.ATTACK)
							baction = Action.LEFT_CLICK_BLOCK;

						final Action caction = baction;
						final Player player = event.getPlayer();

						Bukkit.getScheduler().scheduleSyncDelayedTask(pPlugin, new Runnable() {
							@Override
							public void run() {
								PlayerInteractEvent pie = new PlayerInteractEvent(player, caction, null, s.getLocation().getBlock(), BlockFace.NORTH);
								Bukkit.getServer().getPluginManager().callEvent(pie);

								if (!pie.isCancelled()) {
									final Chest c = (Chest) s.getLocation().getBlock().getState();
									player.closeInventory();
									player.openInventory(c.getInventory());
								}
							}
						}, 2L);
					}
				});
			}
		});
	}

}
