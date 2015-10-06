package de.epiceric.shopchest.event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import de.epiceric.shopchest.utils.ShopUtils;

public class PlayerListener implements Listener{

	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent e) {
		Location loc = e.getBlock().getLocation();
		if(ShopUtils.isShop(loc.add(0, 0, 1))) {
			e.setCancelled(true);
			return;
		}
		if(ShopUtils.isShop(loc.add(0, 0, -1))) {
			e.setCancelled(true);
			return;
		}	
		if(ShopUtils.isShop(loc.add(1, 0, 0))) {
			e.setCancelled(true);
			return;
		}
		if(ShopUtils.isShop(loc.add(-1, 0, 0))) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
    public void onPistonEvent(BlockPistonExtendEvent e){
		for(Block b : e.getBlocks()) {
			Location loc = b.getLocation();
			if(ShopUtils.isShop(loc)) {
				e.setCancelled(true);
				return;
			}
			if(ShopUtils.isShop(loc.add(0, -1, 0))) {
				e.setCancelled(true);
				return;
			}
			
		}		
    }
		
	
}
