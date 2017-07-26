package me.MaxPlays.CoinAPI;

import me.MaxPlays.CoinAPI.util.CoinManager;
import me.MaxPlays.CoinAPI.util.SQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by MaxPlays on 20.07.2017.
 */
public class CoinAPI extends JavaPlugin{

    public SQL sql;
    public static CoinAPI instance;
    public static String prefix = "§8[§cCoins§8] §7";

    public void onEnable(){
        instance = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        if(getConfig().getBoolean("MySQL.enabled")){
            sql = new SQL(getConfig().getString("MySQL.host"), getConfig().getString("MySQL.database"), getConfig().getString("MySQL.username"), getConfig().getString("MySQL.password"), String.valueOf(getConfig().getInt("MySQL.port")), this);
        }else{
            sql = new SQL("data", this);
        }
        sql.connect();
        sql.update("CREATE TABLE IF NOT EXISTS coins(uuid VARCHAR(64), score INT);");

        getCommand("coins").setExecutor(new CommandCoins());
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
    }
    public void onDisable(){
        sql.disconnect();
    }

    public static void getCoins(String name, CoinManager.CoinQuery query){
        new BukkitRunnable() {
            @Override
            public void run() {
                String uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
                CoinManager.getResults(uuid, new CoinManager.CoinQuery() {
                    @Override
                    public void onSuccessfulResult(int result) {
                        query.onSuccessfulResult(result);
                    }

                    @Override
                    public void onPlayerNotExists() {
                        query.onPlayerNotExists();
                    }
                });
            }
        }.runTaskAsynchronously(instance);
    }
    public static void getCoins(Player p, CoinManager.CoinQuery query){
        CoinManager.getResults(p.getUniqueId().toString(), new CoinManager.CoinQuery() {
            @Override
            public void onSuccessfulResult(int result) {
                query.onSuccessfulResult(result);
            }

            @Override
            public void onPlayerNotExists() {
                query.onPlayerNotExists();
            }
        });
    }
    public static void setCoins(Player p, int value){
        if(value < 0)
            value = 0;
        final int i = value;
        CoinManager.insertPlayer(p.getUniqueId().toString(), new CoinManager.runAfter() {
            @Override
            public void run() {
                instance.sql.update("UPDATE coins SET score=" + i + " WHERE uuid='" + p.getUniqueId().toString() + "';");
            }
        });
    }
    public static void setCoins(String name, int value){
        if(value < 0)
            value = 0;
        final int i = value;
        new BukkitRunnable() {
            @Override
            public void run() {
                String uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
                CoinManager.insertPlayer(uuid, new CoinManager.runAfter() {
                    @Override
                    public void run() {
                        instance.sql.update("UPDATE coins SET score=" + i + " WHERE uuid='" + uuid + "';");
                    }
                });
            }
        }.runTaskAsynchronously(instance);
    }
    public static void addCoins(Player p, int value){
        CoinManager.getResults(p.getUniqueId().toString(), new CoinManager.CoinQuery() {
            @Override
            public void onSuccessfulResult(int result) {
                setCoins(p, result + value);
            }

            @Override
            public void onPlayerNotExists() {
                setCoins(p, value);
            }
        });
    }
    public static void addCoins(String name, int value){
        new BukkitRunnable() {
            @Override
            public void run() {
                String uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
                CoinManager.getResults(uuid, new CoinManager.CoinQuery() {
                    @Override
                    public void onSuccessfulResult(int result) {
                        setCoins(name, result + value);
                    }

                    @Override
                    public void onPlayerNotExists() {
                        setCoins(name, value);
                    }
                });
            }
        }.runTaskAsynchronously(instance);
    }
    public static void removeCoins(Player p, int value){
        CoinManager.getResults(p.getUniqueId().toString(), new CoinManager.CoinQuery() {
            @Override
            public void onSuccessfulResult(int result) {
                setCoins(p, result - value);
            }

            @Override
            public void onPlayerNotExists() {
                setCoins(p, -value);
            }
        });
    }
    public static void removeCoins(String name, int value){
        new BukkitRunnable() {
            @Override
            public void run() {
                String uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
                CoinManager.getResults(uuid, new CoinManager.CoinQuery() {
                    @Override
                    public void onSuccessfulResult(int result) {
                        setCoins(name, result - value);
                    }

                    @Override
                    public void onPlayerNotExists() {
                        //Value smaller than zero
                    }
                });
            }
        }.runTaskAsynchronously(instance);
    }

}
