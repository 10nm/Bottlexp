package run.saba.bottlexp;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Bottlexp extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Bottlexp has been enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Bottlexp has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("bottlexp")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be run by a player.");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage("引数が不足しています。 \n Not enough arguments.");
                return false;
            }

            Player player = (Player) sender;
            int player_level = player.getLevel();

            if (player_level == 0) {
                player.sendMessage("変換する経験値がありません！\nYou have no experience to bottle.");
                return true;
            }

            int amount = 0;
            if (Objects.equals(args[0], "all")){
                amount = getExpCost(player_level);
                player.setLevel(0);
            } else if (args[0].matches("[0-9]+")) { // ほしい経験値瓶の個数 1個: 10経験値
                amount = Integer.parseInt(args[0]);
                int bottle_exp = amount * 10;
                int player_exp = LeveltoExp(player_level);
                if (bottle_exp > player_exp) {
                    player.sendMessage("変換に必要な量の経験値がありません。 \n You do not have enough experience to bottle.");
                    return false;
                } else {
                    player.setLevel(player_level - ExptoLevel(bottle_exp));
                }
            } else {
                player.sendMessage("不正な入力です。整数値を入力してください。 \n Invalid argument. Please enter a number.");
                return false;
            }
            ItemStack expPotion = new ItemStack(Material.EXPERIENCE_BOTTLE, amount);
            player.getInventory().addItem(expPotion);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            player.sendMessage("[Bottlexp] 経験値をボトリングしました！ \n You have bottled your experience.");
            player.sendMessage("[Bottlexp] あなたは " + amount + "個のエンチャント瓶を獲得しました。" + "\nYou have received " + amount + " experience bottles.");
            return true;
        }
        return false;
    }

    // EXP to Bottle
    int getExpCost(int level) { // 経験値から経験値瓶の個数を求める
        int bottle_exp = 10;
        if (level <= 16) {
            return (int) ((double) (level*level + 6*level) / bottle_exp);
        } else if (level <= 31) {
            return (int) ((2.5*level*level - 40.5*level + 360) / bottle_exp);
        } else {
            return (int) ((4.5*level*level - 162.5*level + 2220) / bottle_exp);
        }
    }

    // EXP to Level
    int ExptoLevel(int exp) {
        if (exp <= 352) {
            return (int) (Math.sqrt(exp+9)-3);
        } else if (exp <= 1507) {
            return (int) ((double) 81 / 10 + Math.sqrt((2.0 / 5) * (exp - (7839.0 / 40))));
        } else {
            return (int) ((double) 325 / 18 + Math.sqrt(2.0 / 9 * (exp - 54215.0 / 72)));
        }
    }

    // Level to EXP
    int LeveltoExp(int level) {
        if (level <= 16) {
            return (int) ((double) (level*level + 6*level) );
        } else if (level <= 31) {
            return (int) ((2.5*level*level - 40.5*level + 360) );
        } else {
            return (int) ((4.5*level*level - 162.5*level + 2220) );
        }
    }

}
