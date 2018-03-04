package de.rkworks.games.api.misc;

import com.sun.istack.internal.logging.Logger;

import de.rkworks.games.api.ByteAPI;
import de.rkworks.games.api.manager.EventManager;

import java.util.function.Consumer;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;


/**
 *
 * @author RKWorks
 *
 * Copyright (c) 2015 - 2018 by RKWorks.de to present. All rights reserved.
 */
public class InventoryGui {

    private final ByteAPI plugin;

    private final GuiItem[] guiItems;
    private final String name;
    private final Consumer<Player> closeConsumer;

    private Inventory inventory;
    private boolean destroy, destroyOnClose;

    private EventManager.EventListener<InventoryClickEvent> clickListener;
    private EventManager.EventListener<InventoryCloseEvent> closeListener;

    public InventoryGui(ByteAPI plugin, GuiItem[] guiItems, String name, Inventory inventory, Consumer<Player> closeConsumer) {
        this.plugin = plugin;
        this.guiItems = guiItems;
        this.name = name;
        this.closeConsumer = closeConsumer;

        createInventory(inventory);
        initListener();
    }

    //<editor-fold defaultstate="collapsed" desc="initListener">
    private void initListener() {

        //<editor-fold defaultstate="collapsed" desc="InventoryClickEvent">
        clickListener = (InventoryClickEvent event) -> {
            if (event.getClick().isShiftClick() && event.getInventory().equals(this.inventory)) {
                event.setCancelled(true);
                return;
            }
            if (!event.getWhoClicked().getOpenInventory().getTopInventory().equals(this.inventory))
                return;
            if (event.getClickedInventory() == null || event.getWhoClicked() == null)
                return;
            if (event.getClickedInventory().equals(event.getWhoClicked().getInventory()))
                return;
            event.setCancelled(true);

            for (GuiItem item : this.guiItems) {
                if (item == null || (!item.compareItems(event.getCurrentItem())))
                    continue;
                item.click((Player) event.getWhoClicked());
                break;
            }
        };
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="InventoryCloseEvent">
        closeListener = (InventoryCloseEvent event) -> {
            Player player = (Player) event.getPlayer();

            if (destroy || !event.getInventory().equals(this.inventory))
                return;
            plugin.runTaskAsync(() -> {
                if (closeConsumer == null)
                    closeConsumer.accept(player);
            });
            if (!destroyOnClose)
                return;
            destroy();
        };
        //</editor-fold>

        plugin.registerEvent(InventoryClickEvent.class, clickListener);
        plugin.registerEvent(InventoryCloseEvent.class, closeListener);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="createInventory">
    private void createInventory(Inventory inventory) {
        this.inventory = (inventory == null ? plugin.getServer().createInventory(null, guiItems.length, name) : inventory);

        for (int i = 0; i < guiItems.length; i++) {
            GuiItem item = guiItems[i];
            if (item == null)
                continue;
            this.inventory.setItem(i, item.getItemStack().clone());
            item.setInventory(this);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setItem">
    public void setItem(int slot, GuiItem guiItem) {
        guiItems[slot] = guiItem;
        this.inventory.setItem(slot, guiItem.getItemStack().clone());
        guiItem.setInventory(this);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="removeItem">
    public void removeItem(GuiItem guiItem) {
        for(int i = 0; i < this.guiItems.length; i++) {
            if(this.guiItems[i] != guiItem)
                continue;
            this.guiItems[i] = null;
            this.inventory.remove(guiItem.getItemStack());
            guiItem.setInventory(null);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="destroy">
    public InventoryGui destroy() {
        destroy = true;
        plugin.unregisterEvent(InventoryClickEvent.class, clickListener);
        plugin.unregisterEvent(InventoryCloseEvent.class, closeListener);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="update">
    public InventoryGui update(GuiItem guiItem) {
        for (int i = 0; i < this.guiItems.length; i++) {
            GuiItem item = this.guiItems[i];
            if (!guiItem.equals(item))
                continue;
            this.inventory.setItem(i, item.getItemStack().clone());
        }
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="open">
    public InventoryGui open(Player player) {
        player.openInventory(this.inventory);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="isDestroyed">
    public boolean isDestroyed() {
        return destroy;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setDestroyOnClose">
    public InventoryGui setDestroyOnClose(boolean destroyOnClose) {
        this.destroyOnClose = destroyOnClose;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="isDestroyOnClose">
    public boolean isDestroyOnClose() {
        return destroyOnClose;
    }
    //</editor-fold>

}
