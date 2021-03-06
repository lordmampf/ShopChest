package de.epiceric.shopchest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import de.epiceric.shopchest.config.Config;
import de.epiceric.shopchest.event.InteractShop;
import de.epiceric.shopchest.event.ItemCustomNameListener;
import de.epiceric.shopchest.event.PlayerListener;
import de.epiceric.shopchest.event.ProtectChest;
import de.epiceric.shopchest.event.RegenerateShopItem;
import de.epiceric.shopchest.event.UpdateHolograms;
import de.epiceric.shopchest.interfaces.ItemNames;
import de.epiceric.shopchest.interfaces.itemnames.ItemNamesMampfLib;
import de.epiceric.shopchest.interfaces.itemnames.ItemNamesTextFile;
import de.epiceric.shopchest.shop.Shop;
import de.epiceric.shopchest.sql.SQLite;
import de.epiceric.shopchest.utils.ShopUtils;
import de.epiceric.shopchest.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class ShopChest extends JavaPlugin {

	private static ShopChest instance;

	public static Statement statement;
	public static Logger logger;
	public static Economy econ = null;
	public static Permission perm = null;
	public static LWC lwc = null;
	public static ItemNames itemnames;

	public static SQLite sqlite;

	public static boolean mampflib = false;
	public static String latestVersion = "";
	public static String downloadLink = "";
	public static InteractShop interactshop;

	public static Utils utils;

	public static WorldGuardPlugin worldguard;

	public static ShopChest getInstance() {
		return instance;
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perm = rsp.getProvider();
		return perm != null;
	}

	@Override
	public void onEnable() {
		instance = this;

		logger = getLogger();

		if (!setupEconomy()) {
			logger.severe("Could not find any Vault dependency!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		reloadConfig();
		saveDefaultConfig();

		sqlite = new SQLite(this);
		sqlite.load();

		utils = new Utils();

		if (getServer().getPluginManager().getPlugin("LWC") != null) {
			Plugin lwcp = getServer().getPluginManager().getPlugin("LWC");
			lwc = ((LWCPlugin) lwcp).getLWC();
		}

		final Plugin worldGuardPlugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if (worldGuardPlugin instanceof WorldGuardPlugin)
			worldguard = (WorldGuardPlugin) worldGuardPlugin;

		setupPermissions();

		if (getServer().getPluginManager().getPlugin("MampfLib") != null) {
			itemnames = new ItemNamesMampfLib();
			mampflib = true;
			logger.info("using MampfLib Itemnames");
		} else {
			itemnames = new ItemNamesTextFile();
		}

		File itemNamesFile = new File(getDataFolder(), "item_names.txt");

		if (!itemNamesFile.exists())
			try {
				itemNamesFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		copy(getResource("item_names.txt"), itemNamesFile);

		try {
			Commands.registerCommand(new Commands(this, Config.main_command_name(), "Manage Shops.", "", new ArrayList<String>()), this);
			Commands.registerCommand(new Commands(this, "pshop", "Manage Shops.", "", new ArrayList<String>()), this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		initializeShops();

		getServer().getPluginManager().registerEvents(new UpdateHolograms(), this);
		getServer().getPluginManager().registerEvents(new RegenerateShopItem(), this);
		getServer().getPluginManager().registerEvents(new InteractShop(this), this);
		getServer().getPluginManager().registerEvents(new ProtectChest(), this);
		getServer().getPluginManager().registerEvents(new ItemCustomNameListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);

		if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {

		}
	}

	@Override
	public void onDisable() {
		if (utils != null)
			utils.removeShops();
	}

	private void initializeShops() {
		int count = 0;
		for (int id = 1; id < sqlite.getHighestID() + 1; id++) {
			try {
				Shop shop = sqlite.getShop(id);
				if (shop == null)
					continue;

				if (shop.getLocation() == null || shop.getLocation().getWorld() == null
						|| Bukkit.getServer().getWorld(shop.getLocation().getWorld().getName()) == null) {
					String world = "";
					if (shop.getLocation() != null && shop.getLocation().getWorld() != null)
						world = shop.getLocation().getWorld().getName();
					logger.info("shop with id " + id + " world " + world + " of a shop dosn't exists -shop removed");
					ShopUtils.removeShop(shop, id);
					continue;
				}
				if (shop.createHologram()) {
					shop.createItem();
					ShopUtils.addShop(shop);
				} else {
					logger.info("shop with id " + id + " chest not exists " + shop.getLocation().toString() + " -shop removed");
					ShopUtils.removeShop(shop, id); //Remove if chest not exists
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			count++;
		}

		logger.info("Initialized " + String.valueOf(count) + " Shops");
	}

	public static void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
