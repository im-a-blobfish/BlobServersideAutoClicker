package me.blobfish.blobserversideautoclicker;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.UUID;

public class BukkitRunnableAutoclicker extends BukkitRunnable {

    private UUID uuid;
    public BukkitRunnableAutoclicker(UUID uuid){
        this.uuid = uuid;
    }

    @Override
    public void run() {

        Player player = Bukkit.getPlayer(uuid);

        if (player == null || !player.isValid()) {
            this.cancel();
            return;
        }

        player.swingMainHand();

        if (player.getWorld().getNearbyEntities(player.getEyeLocation(), 3, 3, 3, (entity) -> !(entity instanceof Player)).isEmpty()) {
            return;
        }

        RayTraceResult result = player.getLocation().getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 3, FluidCollisionMode.NEVER, true, 0, entity -> entity != player);

        if (result == null || result.getHitEntity() == null) {
            return;
        }

        player.attack(result.getHitEntity());
    }
}
