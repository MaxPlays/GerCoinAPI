package me.MaxPlays.SpliceCoinAPI;

import me.MaxPlays.SpliceCoinAPI.util.CoinManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by MaxPlays on 20.07.2017.
 */
public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        CoinManager.insertPlayer(e.getPlayer().getUniqueId().toString());
    }

}
