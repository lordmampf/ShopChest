package de.epiceric.shopchest.interfaces;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public interface Hologram {
	public Location getLocation();

	public void showPlayer(OfflinePlayer p);

	public void hidePlayer(OfflinePlayer p);

	public boolean isVisible(OfflinePlayer p);

	public void killAllEntities();

	public boolean hasEntityId(int pEntityId);
}
