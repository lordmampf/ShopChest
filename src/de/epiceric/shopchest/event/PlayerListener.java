package de.epiceric.shopchest.event;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import de.epiceric.shopchest.ShopChest;
import de.epiceric.shopchest.utils.ShopUtils;

public class PlayerListener implements Listener {

	public final static String invalidStr = "#invalid#shop#item";

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent e) {
		Location loc = e.getBlockPlaced().getLocation();
		if (ShopUtils.isShop(loc.clone().add(0, 0, 1))) {
			e.setCancelled(true);
			return;
		}
		if (ShopUtils.isShop(loc.clone().add(0, 0, -1))) {
			e.setCancelled(true);
			return;
		}
		if (ShopUtils.isShop(loc.clone().add(1, 0, 0))) {
			e.setCancelled(true);
			return;
		}
		if (ShopUtils.isShop(loc.clone().add(-1, 0, 0))) {
			e.setCancelled(true);
			return;
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryPickupItemEvent(InventoryPickupItemEvent e) {
		if (checkItemEntity(e.getItem())) {
			e.setCancelled(true);
			checkInventory(e.getInventory());
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClickEvent(final InventoryClickEvent e) {
		if (checkItem(e.getCurrentItem())) {
			e.setCancelled(true);
			checkInventory(e.getClickedInventory());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onInventory(PlayerPickupItemEvent e) {
		if (checkItemEntity(e.getItem())) {
			e.setCancelled(true);
			checkInventory(e.getPlayer().getInventory());
		}
	}

	private boolean checkItemEntity(Item pItem) {
		if (pItem == null)
			return false;
		List<MetadataValue> metas = pItem.getMetadata("shopItem");
		if (metas != null && metas.size() > 0) {
			pItem.remove();
			return true;
		}
		ItemStack is = pItem.getItemStack();
		if (checkItem(is)) {
			pItem.remove();
			return true;
		}
		return false;
	}

	private boolean checkItem(ItemStack pIs) {
		if (pIs == null)
			return false;

		if (pIs.hasItemMeta()) {
			ItemMeta im = pIs.getItemMeta();
			if (im == null || im.getLore() == null)
				return false;
			if (im.getLore().contains(invalidStr)) {
				return true;
			}
		}
		return false;
	}

	private void checkInventory(final Inventory pInventory) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(ShopChest.getInstance(), new Runnable() {
			@Override
			public void run() {
				ItemStack[] content = pInventory.getContents();
				for (int i = 0; i < content.length; i++) {
					ItemStack is = content[i];
					if (is == null)
						continue;
					ItemMeta im = is.getItemMeta();
					if (im == null || im.getLore() == null)
						continue;
					if (im.getLore().contains(invalidStr)) {
						content[i] = null;
					}
				}
				pInventory.setContents(content);
			}
		}, 3L);
	}

}
