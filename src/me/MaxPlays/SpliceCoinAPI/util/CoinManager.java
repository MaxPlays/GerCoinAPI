package me.MaxPlays.SpliceCoinAPI.util;

import me.MaxPlays.SpliceCoinAPI.CoinAPI;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by MaxPlays on 20.07.2017.
 */
public class CoinManager {

    public static void insertPlayer(String uuid){
        playerExists(uuid, new exists() {
            @Override
            public void run(boolean result) {
                if(!result)
                    CoinAPI.instance.sql.update("INSERT INTO coins VALUES('" + uuid + "', 0);");
            }
        });
    }
    public static void insertPlayer(String uuid, runAfter a){
        playerExists(uuid, new exists() {
            @Override
            public void run(boolean result) {
                if(!result)
                    CoinAPI.instance.sql.update("INSERT INTO coins VALUES('" + uuid + "', 0);");
                a.run();
            }
        });
    }

    public static void playerExists(String uuid, exists r){
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    r.run(CoinAPI.instance.sql.query("SELECT score FROM coins WHERE uuid='" + uuid + "';").next());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(CoinAPI.instance);
    }

    public static void getResults(String uuid, CoinQuery cq){
        new BukkitRunnable() {
            @Override
            public void run() {
                ResultSet rs = CoinAPI.instance.sql.query("SELECT score FROM coins WHERE uuid='" + uuid + "';");
                try {
                    if(rs.next()){
                        cq.onSuccessfulResult(rs.getInt("score"));
                    }else{
                        cq.onPlayerNotExists();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }.runTaskAsynchronously(CoinAPI.instance);
    }

    public static interface exists{
        public void run(boolean result);
    }
    public static interface CoinQuery{
        public void onSuccessfulResult(int result);
        public void onPlayerNotExists();
    }
    public static interface runAfter{
        public void run();
    }

}
