package de.epiceric.shopchest.utils;

import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class ClickType {

	private static HashMap<OfflinePlayer, ClickType> playerClickType = new HashMap<>();

	public static ClickType getPlayerClickType(OfflinePlayer player) {
		if (playerClickType.containsKey(player))
			return playerClickType.get(player);
		else
			return null;
	}

	public static void removePlayerClickType(OfflinePlayer player) {
		playerClickType.remove(player);
	}

	public static void addPlayerClickType(OfflinePlayer player, ClickType clickType) {
		playerClickType.put(player, clickType);
	}

	public enum EnumClickType {
		CREATE, REMOVE, INFO;
	}

	private EnumClickType enumClickType;
	private ItemStack product;
	private double buyPrice;
	private double sellPrice;
	private boolean infinite;

	public ClickType(EnumClickType enumClickType) {
		this.enumClickType = enumClickType;
	}

	public ClickType(EnumClickType enumClickType, ItemStack product, double buyPrice, double sellPrice, boolean infinite) {
		this.enumClickType = enumClickType;
		this.product = product;
		this.sellPrice = sellPrice;
		this.buyPrice = buyPrice;
		this.infinite = infinite;
	}

	public EnumClickType getClickType() {
		return enumClickType;
	}

	public ItemStack getProduct() {
		return product;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public boolean isInfinite() {
		return infinite;
	}

}
