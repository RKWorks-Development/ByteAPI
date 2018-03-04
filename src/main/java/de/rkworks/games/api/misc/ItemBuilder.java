package de.rkworks.games.api.misc;

import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;


/**
 *
 * @author RKWorks
 *
 * Copyright (c) 2015 - 2018 by RKWorks.de to present. All rights reserved.
 */
public class ItemBuilder {

    private final ItemStack itemStack;

    private ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, short data) {
        this.itemStack = new ItemStack(material, 1, data);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        this.itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        this.itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemBuilder setSkullOwner(String owner) {
        if (itemStack.getType() == Material.SKULL_ITEM) {
            SkullMeta meta = (SkullMeta) this.itemMeta;
            meta.setOwner(owner);
            this.itemMeta = meta;
        }
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.itemMeta.addEnchant(enchantment, level, true);
        return this;
    }
    
    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }
}
