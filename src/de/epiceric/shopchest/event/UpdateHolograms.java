package de.epiceric.shopchest.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import de.epiceric.shopchest.config.Config;
import de.epiceric.shopchest.shop.Shop;
import de.epiceric.shopchest.utils.ShopUtils;

public class UpdateHolograms implements Listener {

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location playerLocation = p.getLocation();
		for (Shop shop : ShopUtils.getShops()) {
			if (shop.getHologram() != null) {
				Location shopLocation = shop.getLocation();
				if (playerLocation.getWorld().equals(shopLocation.getWorld())) {
					if (playerLocation.distance(shop.getHologram().getLocation()) <= Config.maximal_distance()) {
						if (!shop.getHologram().isVisible(p)) {
							shop.getHologram().showPlayer(p);
						}
					} else {
						if (shop.getHologram().isVisible(p)) {
							shop.getHologram().hidePlayer(p);
						}
					}
				}
			}
		}
	}
}
