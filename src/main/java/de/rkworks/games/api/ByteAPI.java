package de.rkworks.games.api;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import de.rkworks.games.api.manager.ConfigManager;
import de.rkworks.games.api.manager.EventManager;
import de.rkworks.games.api.misc.GuiBuilder;
import de.rkworks.games.api.misc.GuiItem;
import de.rkworks.games.api.misc.ItemBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author RKWorks
 *
 * Copyright (c) 2015 - 2018 by RKWorks.de to present. All rights reserved.
 */
public class ByteAPI extends JavaPlugin {

    private static ByteAPI instance;

    private LoadingCache<String, ConfigManager> configCache;
    
    private EventManager eventManager;
    
    @Override
    public void onEnable() {
        instance = this;
        init();
        initCache();
    }

    //<editor-fold defaultstate="collapsed" desc="init">
    private void init() {
        this.eventManager = new EventManager(this);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initCache">
    private void initCache() {
        
        configCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(30, TimeUnit.MINUTES).build(new CacheLoader<String, ConfigManager>() {
            @Override
            public ConfigManager load(String name) throws Exception {
                return new ConfigManager("config/" + name.toLowerCase() + ".json");
            }
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getInstance">
    public static ByteAPI getInstance() {
        return instance;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getConfig">
    public ConfigManager getConfig(String name) {
        try {
            return configCache.get(name);
        } catch (ExecutionException ex) {
            Logger.getLogger(ByteAPI.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="registerEvent">
    public void registerEvent(Class<? extends Event> cls, EventManager.EventListener listener) {
        this.eventManager.registerEvent(cls, listener);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="unregisterEvent">
    public void unregisterEvent(Class<? extends Event> cls, EventManager.EventListener listener) {
        this.eventManager.unregisterEvent(cls, listener);
    }
    //</editor-fold>
        
    //<editor-fold defaultstate="collapsed" desc="addDeniedCommand">
    public void addDeniedCommand(String command) {
        this.eventManager.addDeniedCommand(command);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="removeDeniedCommand">
    public void removeDeniedCommand(String command) {
        this.eventManager.removeDeniedCommand(command);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="isDeniedCommand">
    public boolean isDeniedCommand(String command) {
        return this.eventManager.isDeniedCommand(command);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="onCommand">
    public void onCommand(String command, CommandExecutor executor) {
        this.eventManager.onCommand(command, executor);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="removeMetadata">
    public void removeMetadata(Entity entity, String name) {
        if(entity.hasMetadata(name))
            entity.removeMetadata(name, this);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="setMetadata">
    public void setMetadata(Entity entity, String name, Object value) {
        removeMetadata(entity, name);
        entity.setMetadata(name, new FixedMetadataValue(this, value));
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="removeMetadata">
    public void removeMetadata(Block block, String name) {
        if(block.hasMetadata(name))
            block.removeMetadata(name, this);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="setMetadata">
    public void setMetadata(Block block, String name, Object value) {
        removeMetadata(block, name);
        block.setMetadata(name, new FixedMetadataValue(this, value));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="runTask">
    public BukkitTask runTask(Runnable runnable) {
        return getServer().getScheduler().runTask(this, runnable);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="runTaskAsync">
    public BukkitTask runTaskAsync(Runnable runnable) {
        return getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="runTaskLater">
    public BukkitTask runTaskLater(long delay, Runnable runnable) {
        return getServer().getScheduler().runTaskLater(this, runnable, delay);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="runTaskLaterAsync">
    public BukkitTask runTaskLaterAsync(long delay, Runnable runnable) {
        return getServer().getScheduler().runTaskLaterAsynchronously(this, runnable, delay);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="runTaskTimer">
    public BukkitTask runTaskTimer(long delay, long repeat, Runnable runnable) {
        return getServer().getScheduler().runTaskTimer(this, runnable, delay, repeat);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="runTaskTimerAsync">
    public BukkitTask runTaskTimerAsync(long delay, long repeat, Runnable runnable) {
        return getServer().getScheduler().runTaskTimerAsynchronously(this, runnable, delay, repeat);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="gui">
    public GuiBuilder gui(int size) {
        size = size <= 9 ? 9 : size <= 18 ? 18 : size <= 27 ? 27 : size <= 36 ? 36 : size <= 45 ? 45 : 54;
        return new GuiBuilder(size, this);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="gui">
    public GuiBuilder gui(Inventory inventory) {
        return new GuiBuilder(inventory, this);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="guiItem">
    public GuiItem guiItem(ItemStack item, Consumer<Player> callback) {
        return new GuiItem(item, callback);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="item">
    public ItemBuilder item(Material material) {
        return new ItemBuilder(material);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="item">
    public ItemBuilder item(Material material, short data) {
        return new ItemBuilder(material, data);
    }
    //</editor-fold>
}
