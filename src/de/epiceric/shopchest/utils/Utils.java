package de.epiceric.shopchest.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.epiceric.shopchest.ShopChest;
import de.epiceric.shopchest.config.Config;
import de.epiceric.shopchest.interfaces.Hologram;
import de.epiceric.shopchest.shop.Shop;

public class Utils {

	public void reload(Player player) {
		for (Shop shop : ShopUtils.getShops()) {
			Hologram hologram = shop.getHologram();
			shop.getItem().remove();
			ShopUtils.removeShop(shop);
			for (Player p : ShopChest.getInstance().getServer().getOnlinePlayers()) {
				hologram.hidePlayer(p);
			}
			hologram.killAllEntities();
		}

		int count = 0;
		for (int id = 1; id < ShopChest.sqlite.getHighestID() + 1; id++) {
			try {
				Shop shop = ShopChest.sqlite.getShop(id);
				if (shop == null)
					continue;
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

	public void removeShops() {
		List<String> worldsdone = new ArrayList<String>();

		for (Shop shop : ShopUtils.getShops()) {
			Hologram hologram = shop.getHologram();

			for (Player p : Bukkit.getOnlinePlayers()) {
				hologram.hidePlayer(p);
			}

			hologram.killAllEntities();

			if (shop.getItem() != null)
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

	public Shop getShopFromArmorStandEntity(int pEntityId) {
		for (final Shop s : ShopUtils.getShops()) {
			if (s.getHologram() != null && s.getHologram().hasEntityId(pEntityId))
				return s;
		}
		return null;
	}

	public static int getAmount(Inventory inventory, Material type, short damage, ItemMeta itemMeta) {
		ItemStack[] items = inventory.getContents();
		int amount = 0;
		for (ItemStack item : items) {
			if ((item != null) && (item.getType().equals(type)) && (item.getDurability() == damage) && (item.getAmount() > 0)
					&& (item.getItemMeta().equals(itemMeta))) {
				amount += item.getAmount();
			}
		}
		return amount;
	}

	public static String getVersion(Server server) {
		String packageName = server.getClass().getPackage().getName();

		return packageName.substring(packageName.lastIndexOf('.') + 1);
	}

	public static boolean isUUID(String string) {
		return string.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");
	}

	public static String encode(ItemStack itemStack) {
		YamlConfiguration config = new YamlConfiguration();
		config.set("i", itemStack);
		return new String(Base64.encodeBase64(config.saveToString().getBytes()));
	}

	public static String toString(ItemStack itemStack) {
		YamlConfiguration config = new YamlConfiguration();
		config.set("i", itemStack);
		return config.saveToString();
	}

	public static ItemStack decode(String string) {
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.loadFromString(new String(Base64.decodeBase64(string.getBytes())));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return config.getItemStack("i", null);
	}

}
