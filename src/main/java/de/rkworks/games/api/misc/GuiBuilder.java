package de.rkworks.games.api.misc;

import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import de.rkworks.games.api.ByteAPI;


/**
 *
 * @author RKWorks
 *
 * Copyright (c) 2015 - 2018 by RKWorks.de to present. All rights reserved.
 */
public class GuiBuilder {
    
    private final ByteAPI plugin;
    
    private final GuiItem[] items;
    
    private String name;
    private Consumer<Player> closeEvent;
    
    private Inventory inventory;
    private boolean destroyOnClose;
    
    public GuiBuilder(int size, ByteAPI plugin) {
        this.plugin = plugin;
        this.items = new GuiItem[size];
    }
    
    public GuiBuilder(Inventory inventory, ByteAPI plugin) {
        this.plugin = plugin;
        this.inventory = inventory;
        this.items = new GuiItem[inventory.getSize()];
    }
    
    //<editor-fold defaultstate="collapsed" desc="addItem">
    public GuiBuilder addItem(int slot, GuiItem guiItem) {
        this.items[slot] = guiItem;
        return this;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getItem">
    public GuiItem getItem(int slot) {
        return this.items[slot];
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="setName">
    public GuiBuilder setName(String name) {
        this.name = (name.length() > 32 ? "TOO LONG" : ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="onClose">
    public GuiBuilder onClose(Consumer<Player> closeEvent) {
        this.closeEvent = closeEvent;
        return this;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="destroyOnClose">
    public GuiBuilder destroyOnClose() {
        this.destroyOnClose = true;
        return this;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getSize">
    public int getSize() {
        return this.items.length;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="build">
    public InventoryGui build() {
        if(this.name == null)
            this.name = "";
        InventoryGui gui = new InventoryGui(plugin, items, name, inventory, closeEvent);
        if(destroyOnClose)
            gui.destroy();
        return gui;
    }
    //</editor-fold>
}
