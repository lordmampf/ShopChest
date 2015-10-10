package de.epiceric.shopchest.interfaces;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import net.minecraft.server.v1_8_R3.EntityArmorStand;

public interface Hologram {
	public Location getLocation();

	public List<EntityArmorStand> getEntities();

	public void showPlayer(OfflinePlayer p);

	public void hidePlayer(OfflinePlayer p);

	public boolean isVisible(OfflinePlayer p);
}
