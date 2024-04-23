package me.blobfish.blobserversideautoclicker;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class EventStuff implements Listener {

    @EventHandler
    public void onleave(PlayerQuitEvent event){
        CommandAutoclicker.instance.removeInterval(event.getPlayer().getUniqueId());
        CommandAutoclicker.instance.stopClicker(null, event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void slotswitch(PlayerItemHeldEvent event){
        CommandAutoclicker.instance.cancelClicker(event.getPlayer(), event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void switchhand(PlayerSwapHandItemsEvent event){
        CommandAutoclicker.instance.cancelClicker(event.getPlayer(), event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK){
            return;
        }
        CommandAutoclicker.instance.cancelClicker(event.getPlayer(), event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void inventoryopenevent(InventoryOpenEvent event){
        if (!(event.getPlayer() instanceof Player)){
            return;
        }
        CommandAutoclicker.instance.cancelClicker((Player) event.getPlayer(), ((Player) event.getPlayer()).getUniqueId());
    }

    @EventHandler
    public void inventoryclickevent(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player)){
            return;
        }

        CommandAutoclicker.instance.cancelClicker((Player) event.getWhoClicked(), ((Player) event.getWhoClicked()).getUniqueId());
    }

    @EventHandler
    public void attack(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            if (CommandAutoclicker.instance.isAutoclicking(((Player) event.getDamager()).getUniqueId())){
                event.setCancelled(true);
                event.getDamager().sendMessage(ChatColor.RED + "You can not attack players while your autoclicker is enabled!");
            }
        }
    }
}
