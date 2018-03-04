package de.rkworks.games.api.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import de.rkworks.games.api.ByteAPI;
import de.rkworks.games.api.utils.ListenerExecutor;


/**
 *
 * @author RKWorks
 *
 * Copyright (c) 2015 - 2018 by RKWorks.de to present. All rights reserved.
 */
public class EventManager implements Listener {

    private final ByteAPI plugin;

    private final HashMap<EventListener, CopyOnWriteArrayList<ListenerExecutor>> executors;
    private final HashMap<String, CommandExecutor> commands;
    
    private final List<String> deniedCommands;
    
    public EventManager(ByteAPI plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.executors = new HashMap<>();
        this.commands = new HashMap<>();
        this.deniedCommands = new ArrayList<>();
    }
    
    //<editor-fold defaultstate="collapsed" desc="registerEvent">
    public void registerEvent(Class<? extends Event> cls, EventListener listener) {
        ListenerExecutor executor = new ListenerExecutor(cls, listener);
        
        this.plugin.getServer().getPluginManager().registerEvent(cls, new Listener() {
            
        }, EventPriority.NORMAL, executor, this.plugin);
        if(!this.executors.containsKey(listener))
            this.executors.put(listener, new CopyOnWriteArrayList<>());
        this.executors.get(listener).add(executor);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="unregisterEvent">
    public void unregisterEvent(Class<? extends Event> cls, EventListener listener) {
        if(!this.executors.containsKey(listener))
            return;
        this.executors.get(listener).stream().filter((executor) -> (executor.getListener().equals(listener))).forEach((executor) -> {
            executor.setDisable(true);
        });
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="addDeniedCommand">
    public void addDeniedCommand(String command) {
        if(!this.deniedCommands.contains(command))
            this.deniedCommands.add(command);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="removeDeniedCommand">
    public void removeDeniedCommand(String command) {
        if(this.deniedCommands.contains(command))
            this.deniedCommands.remove(command);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="isDeniedCommand">
    public boolean isDeniedCommand(String command) {
        return this.deniedCommands.contains(command);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="onCommand">
    public void onCommand(String command, CommandExecutor executor) {
        this.commands.put(command.toLowerCase(), executor);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="onCommand">
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        
        List<String> args = new ArrayList(Arrays.asList(event.getMessage().replaceFirst("/", "").split(" ")));
        String command = args.get(0).toLowerCase();
        
        if(command.contains(":") || this.deniedCommands.contains(command) || (!this.commands.containsKey(command))) {
            event.setCancelled(true);
            return;
        }
        CommandExecutor executor = this.commands.get(command);
        args.remove(0);
        executor.onCommand(player, null, command, args.toArray(new String[0]));
        event.setCancelled(true);
    }
    //</editor-fold>
    
    public interface EventListener<T extends Event> {
        
        public void on(T event);
    }
}
