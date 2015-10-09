package de.epiceric.shopchest.interfaces.itemnames;

import org.bukkit.inventory.ItemStack;

import de.epiceric.shopchest.interfaces.ItemNames;
import me.lordmampf.Lib.MaterialsHelper;

public class ItemNamesMampfLib implements ItemNames {

	@Override
	public String lookup(ItemStack stack) {
		return MaterialsHelper.getReadAbleName(stack, false);
	}

}
