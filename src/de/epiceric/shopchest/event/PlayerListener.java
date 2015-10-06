package de.epiceric.shopchest.event;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

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
	public void onPistonEvent(BlockPistonExtendEvent e) {
		/*
		 * for (Block b : e.getBlocks()) {
		 * Location loc = b.getLocation();
		 * if (ShopUtils.isShop(loc)) {
		 * e.setCancelled(true);
		 * return;
		 * }
		 * if (ShopUtils.isShop(loc.add(0, -1, 0))) {
		 * e.setCancelled(true);
		 * return;
		 * }
		 * }
		 */
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryMove(InventoryPickupItemEvent e) {
		if (e.getItem() == null)
			return;
		if (e.getItem().getMetadata("shopItem") != null) {
			e.setCancelled(true);
			e.getItem().remove();
		}
	}

}
