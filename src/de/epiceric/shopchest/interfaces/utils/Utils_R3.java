package de.epiceric.shopchest.interfaces.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import de.epiceric.shopchest.ShopChest;
import de.epiceric.shopchest.config.Config;
import de.epiceric.shopchest.interfaces.Utils;
import de.epiceric.shopchest.interfaces.Hologram;
import de.epiceric.shopchest.shop.Shop;
import de.epiceric.shopchest.utils.ShopUtils;
import net.minecraft.server.v1_8_R3.EntityArmorStand;

public class Utils_R3 extends Utils {

	@Override
	public void reload(Player player) {
		for (Shop shop : ShopUtils.getShops()) {
			Hologram hologram = shop.getHologram();
			shop.getItem().remove();
			ShopUtils.removeShop(shop);
			for (Player p : ShopChest.getInstance().getServer().getOnlinePlayers()) {
				hologram.hidePlayer(p);
			}
			for (Object o : hologram.getEntities()) {
				EntityArmorStand e = (EntityArmorStand) o;
				e.getWorld().removeEntity(e);
			}
		}

		int count = 0;
		for (int id = 1; id < ShopChest.sqlite.getHighestID() + 1; id++) {
			try {
				Shop shop = ShopChest.sqlite.getShop(id);
				shop.createHologram();
				shop.createItem();
				ShopUtils.addShop(shop);
			} catch (NullPointerException e) {
				continue;
			}
			count++;
		}

		if (player != null)
			player.sendMessage(Config.reloaded_shops(count));

		for (Player p : Bukkit.getOnlinePlayers()) {
			Bukkit.getPluginManager().callEvent(new PlayerMoveEvent(p, p.getLocation(), p.getLocation()));
		}

	}

	@Override
	public void removeShops() {
		List<String> worldsdone = new ArrayList<String>();

		for (Shop shop : ShopUtils.getShops()) {
			Hologram hologram = shop.getHologram();

			for (Player p : Bukkit.getOnlinePlayers()) {
				hologram.hidePlayer(p);
			}

			for (Object o : hologram.getEntities()) {
				EntityArmorStand e = (EntityArmorStand) o;
				e.getWorld().removeEntity(e);
			}
			shop.getItem().remove();

			String world = shop.getLocation().getWorld().getName();

			if (!worldsdone.contains(world)) {
				List<Entity> toremove = new ArrayList<Entity>();
				for (Entity entity : Bukkit.getWorld(world).getEntities()) {
					if (entity.hasMetadata("shopItem")) {
						toremove.add(entity);
					}
				}
				for (Entity entity : toremove) {
					entity.remove();
				}
				worldsdone.add(world);
			}
		}

	}

}
