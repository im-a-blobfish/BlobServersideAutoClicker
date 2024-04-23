package me.blobfish.blobserversideautoclicker;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CommandAutoclicker implements TabExecutor {

    private HashMap<UUID, Long> intervals = new HashMap<>();
    private HashMap<UUID, BukkitTask> clickers = new HashMap<>();

    static CommandAutoclicker instance;
    public CommandAutoclicker(){
        instance = this;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)){
            commandSender.sendMessage(ChatColor.RED + "This can only be executed by a player!");
            return true;
        }

        Player player = (Player) commandSender;

        if (args.length == 1){
            if (args[0].equalsIgnoreCase("stop")){
                stopClicker(player, player.getUniqueId());
                return true;
            } else if (args[0].equalsIgnoreCase("start")){
                startClicker(player, player.getUniqueId());
                return true;
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("interval")){
            String interval = args[1].toLowerCase();
            long intervalms;
            try {
                if (interval.endsWith("ms")){
                    intervalms = (long) Math.round(Double.parseDouble(interval.substring(0, interval.length() - 2)));
                } else if (interval.endsWith("s")){
                    intervalms = (long) Math.round(Double.parseDouble(interval.substring(0, interval.length() - 1)) * 1000);
                } else {
                    commandSender.sendMessage(ChatColor.RED + "Invalid interval! Must be in form <number><ms/s>!");
                    player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    return true;
                }
            } catch (NumberFormatException e){
                commandSender.sendMessage(ChatColor.RED + "Invalid interval! Must be in form <number><ms/s>!");
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                return true;
            }

            // convert the ms to ticks with rounding
            long intervalticks = (long) Math.round(intervalms / 50.0);

            // check the interval is within the bounds
            if (intervalticks > BlobServersideAutoClicker.maxinterval){
                commandSender.sendMessage(ChatColor.RED + "Interval too high! Max interval is " + BlobServersideAutoClicker.maxinterval * 50 + "ms!");
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                return true;
            } else if (intervalticks < BlobServersideAutoClicker.mininterval){
                commandSender.sendMessage(ChatColor.RED + "Interval too low! Min interval is " + BlobServersideAutoClicker.mininterval * 50 + "ms!");
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                return true;
            }

            addInterval(player, player.getUniqueId(), intervalticks);
            return true;
        }

        commandSender.sendMessage("" + ChatColor.GOLD + ChatColor.BOLD + ChatColor.UNDERLINE + "Autoclicker Command Usage:");
        commandSender.sendMessage("- " + ChatColor.YELLOW + "/autoclicker start");
        commandSender.sendMessage("- " + ChatColor.YELLOW + "/autoclicker stop");
        commandSender.sendMessage("- " + ChatColor.YELLOW + "/autoclicker interval <interval><ms/s>");
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        return true;
    }



    private void addInterval(Player player, UUID uuid, long interval){
        intervals.put(uuid, interval);
        if (clickers.containsKey(uuid)){
            startClicker(player, player.getUniqueId());
        }
        player.sendMessage(ChatColor.GREEN + "Interval set to " + interval*50 + "ms (" + interval + " ticks)!");
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }
    public void removeInterval(UUID uuid){
        intervals.remove(uuid);
    }

    private void startClicker(Player player, UUID uuid) {
        // cancel existing clicker
        BukkitTask clicker = clickers.get(uuid);
        if (clicker != null){
            clicker.cancel();
        }

        //check an interval exists
        Long interval = intervals.get(uuid);
        if (interval == null){
            player.sendMessage(ChatColor.RED + "You need to select an interval first!");
            player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            return;
        }

        // start clicker
        BukkitTask task = new BukkitRunnableAutoclicker(uuid).runTaskTimer(BlobServersideAutoClicker.plugin, 0, interval);
        clickers.put(uuid, task);
        player.sendMessage(ChatColor.GREEN + "Autoclicker Started!");
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }

    public void stopClicker(Player player, UUID uuid){
        BukkitTask clicker = clickers.get(uuid);
        if (clicker != null){
            clicker.cancel();
            clickers.remove(uuid);
            if (player != null){
                player.sendMessage(ChatColor.GREEN + "Autoclicker Stopped!");
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            }
        } else {
            if (player != null){
                player.sendMessage(ChatColor.RED + "Your autoclicker isn't on!");
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            }
        }
    }

    public void cancelClicker(Player player, UUID uuid){
        BukkitTask clicker = clickers.get(uuid);
        if (clicker != null){
            clicker.cancel();
            clickers.remove(uuid);
            if (player != null){
                player.sendMessage(ChatColor.GREEN + "Autoclicker Stopped!");
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            }
        }
    }

    public boolean isAutoclicking(UUID uuid){
        return clickers.containsKey(uuid);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        ArrayList<String> tabcompletelist = new ArrayList<>();

        if (args.length == 1){
            tabcompletelist.add("start");
            tabcompletelist.add("stop");
            tabcompletelist.add("interval");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("interval")){
            // if args[1] can be converted to a double then add ms and s, else add nothing
            try {
                Double.parseDouble(args[1]);
                tabcompletelist.add(args[1] + "ms");
                tabcompletelist.add(args[1] + "s");
            } catch (NumberFormatException e){
                // do nothing
            }
        }
        return tabcompletelist;
    }
}
