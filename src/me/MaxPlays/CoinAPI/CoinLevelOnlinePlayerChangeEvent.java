package me.MaxPlays.CoinAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by MaxPlays on 27.08.2017.
 */
public class CoinLevelOnlinePlayerChangeEvent extends Event {

    public static HandlerList handlers = new HandlerList();
    private final Player player;
    private final int now;
    private boolean cancelled = false;

    public CoinLevelOnlinePlayerChangeEvent(Player player, int now){
        this.player = player;
        this.now = now;
    }

    @Override
    public HandlerList getHandlers() {
        return CoinLevelOnlinePlayerChangeEvent.handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public int getNow() {
        return now;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
