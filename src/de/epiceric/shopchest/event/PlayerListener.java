package de.epiceric.shopchest.event;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import de.epiceric.shopchest.ShopChest;
import de.epiceric.shopchest.utils.ShopUtils;

public class PlayerListener implements Listener {

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
	public void onInventoryMove(InventoryPickupItemEvent e) {
		if (e.getItem() == null)
			return;
		List<MetadataValue> metas = e.getItem().getMetadata("shopItem");
		if (metas != null && metas.size() > 0) {
			e.setCancelled(true);
			e.getItem().remove();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onMove(InventoryClickEvent e) {
		if (e.getCurrentItem().hasItemMeta()) {
			ItemMeta im = e.getCurrentItem().getItemMeta();
			if (im.getLore().contains("#invalid#shop#item")) {
				e.setCancelled(true);
				e.getInventory().setItem(e.getSlot(), null);
			}
		}
	}

}
