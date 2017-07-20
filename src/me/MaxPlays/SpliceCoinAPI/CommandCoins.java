package me.MaxPlays.SpliceCoinAPI;

import me.MaxPlays.SpliceCoinAPI.util.CoinManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by MaxPlays on 20.07.2017.
 */
public class CommandCoins implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player && cmd.getName().equalsIgnoreCase("coins")) {
            Player p = (Player) sender;
            if(args.length == 0){
                CoinAPI.getCoins(p, new CoinManager.CoinQuery() {
                    @Override
                    public void onSuccessfulResult(int result) {
                        p.sendMessage(CoinAPI.prefix + "Dein Kontostand beträgt §c" + result + " §7" + (result != 1 ? "Coins" : "Coin"));
                    }

                    @Override
                    public void onPlayerNotExists() {
                        p.sendMessage(CoinAPI.prefix + "Du hast noch kein Konto. Es wird automatisch ein Konto erstellt, wenn die erste Transaktion durchgeführt wird");
                    }
                });
            }else if(args.length == 1){

                if(args[0].equalsIgnoreCase("help")){
                    sendHelp(p);
                    return true;
                }
                if(args[0].equalsIgnoreCase("top")){
                    p.sendMessage(CoinAPI.prefix + "Lade top 5 Spieler...");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try{
                                ResultSet rs = CoinAPI.instance.sql.query("SELECT * FROM coins ORDER BY score DESC LIMIT 5;");
                                if(rs != null && rs.next()){
                                    int i = 1;
                                    do{
                                        p.sendMessage(CoinAPI.prefix + "§6§l" + i + ". §c" + Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("uuid"))).getName() + " §7§o(Coins: §c§o" + rs.getInt("score") + "§7§o)");
                                        i++;
                                    }while (rs.next());
                                }else{
                                    p.sendMessage(CoinAPI.prefix + "Es wurden keine Spieler in der Datenbank gefunden");
                                }
                            }catch (SQLException e){
                                e.printStackTrace();
                            }
                        }
                    }.runTaskAsynchronously(CoinAPI.instance);

                    return true;
                }

                CoinAPI.getCoins(args[0], new CoinManager.CoinQuery() {
                    @Override
                    public void onSuccessfulResult(int result) {
                        p.sendMessage(CoinAPI.prefix + "§c" + args[0] + "§7" + (args[0].toLowerCase().toCharArray()[args[0].length() - 1] == 's' ? "" : "s") +  " Kontostand beträgt §c" + result + " §7" + (result != 1 ? "Coins" : "Coin"));
                    }

                    @Override
                    public void onPlayerNotExists() {
                        p.sendMessage(CoinAPI.prefix + "Der Spieler §c" + args[0] + " §7ist nicht in der Datenbank enthalten");
                    }
                });
            }else if(args.length == 3 && p.hasPermission("Coins.admin")){
                int value = 0;
                try{
                    value = Integer.valueOf(args[2]);
                }catch(Exception e){
                    p.sendMessage(CoinAPI.prefix + "Das 3. Argument muss eine gültige Zahl sein");
                    return true;
                }
                if(args[0].equalsIgnoreCase("set")){
                    CoinAPI.setCoins(args[1], value);
                    p.sendMessage(CoinAPI.prefix + "Der Kontostand des Spielers §c" + args[1] + " §7wurde auf §c" + value + "§7 "+ (value != 1 ? "Coins": "Coin") + " gesetzt");
                    if(Bukkit.getPlayer(args[1]) != null)
                        Bukkit.getPlayer(args[1]).sendMessage(CoinAPI.prefix + "Dein Kontostand wurde von §c" + p.getName() + " §7auf §c" + value + "§7 "+ (value != 1 ? "Coins": "Coin") + " gesetzt");
                }else if(args[0].equalsIgnoreCase("add")){
                    CoinAPI.addCoins(args[1], value);
                    p.sendMessage(CoinAPI.prefix + "Dem Kontostand des Spielers §c" + args[1] + " §7wurde" +(value != 1 ? "n": "") + " §c" + value + "§7 "+ (value != 1 ? "Coins": "Coin") + " hinzugefügt");
                    if(Bukkit.getPlayer(args[1]) != null)
                        p.sendMessage(CoinAPI.prefix + "Deinem Kontostand §7wurde" +(value != 1 ? "n": "") + " vom Spieler §c" + p.getName() + " "  + value + "§7 "+ (value != 1 ? "Coins": "Coin") + " hinzugefügt");
                }else if(args[0].equalsIgnoreCase("remove")){
                    CoinAPI.removeCoins(args[1], value);
                    p.sendMessage(CoinAPI.prefix + "Dem Kontostand des Spielers §c" + args[1] + " §7wurde" +(value != 1 ? "n": "") + " §c" + value + "§7 "+ (value != 1 ? "Coins": "Coin") + " abgezogen");
                    if(Bukkit.getPlayer(args[1]) != null)
                        p.sendMessage(CoinAPI.prefix + "Deinem Kontostand §7wurde" +(value != 1 ? "n": "") + " vom Spieler §c" + p.getName() + " "  + value + "§7 "+ (value != 1 ? "Coins": "Coin") + " abgezogen");
                }else{
                    sendHelp(p);
                }
            }else{
                sendHelp(p);
            }
        }
        return true;
    }
    private void sendHelp(Player p){
        p.sendMessage("§8============== [§cCoins§8] §8==============");
        p.sendMessage("§c/coins §7Zeige deinen Kontostand an");
        p.sendMessage("§c/coins <Spieler> §7Zeige den Kontostand eines Spieler an");
        p.sendMessage("§c/coins top §7Zeige die 5 Spieler mit den meisten Coins an");
        p.sendMessage("§c/coins help §7Zeige diesen Hilfstext an");
        if(p.hasPermission("Coins.admin")){
            p.sendMessage("§c/coins set <Spieler> <Zahl> §7Verändere den Kontostand eines Spielers");
            p.sendMessage("§c/coins add <Spieler> <Zahl> §7Füge dem Konto eines Spielers Coins hinzu");
            p.sendMessage("§c/coins remove <Spieler> <Zahl> §7Ziehe dem Konto eines Spielers Coins ab");
        }
    }
}