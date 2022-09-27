package net.plexpvp.core.cosmetics;

import net.plexpvp.core.PlexPvP;
import net.plexpvp.core.cosmetics.effects.kill.Ascent;
import net.plexpvp.core.cosmetics.effects.kill.Confetti;
import net.plexpvp.core.cosmetics.effects.kill.KeepInventory;
import net.plexpvp.core.cosmetics.effects.trail.Crown;
import net.plexpvp.core.cosmetics.effects.trail.Rainbow;
import net.plexpvp.core.cosmetics.enums.CosmeticType;
import net.plexpvp.core.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CosmeticsListener implements Listener {

    ArrayList<UUID> activeTrails = new ArrayList<>();
    ArrayList<UUID> activeProjectiles = new ArrayList<>();

    Map<UUID, Integer> projectileTasks = new HashMap<>();

    @EventHandler
    private void onKill(EntityDeathEvent event) {
        if (/*event.getEntityType() != EntityType.PLAYER ||*/ event.getEntity().getKiller() == null) return;

        var data = PlexPvP.getPlayerController().get(event.getEntity().getKiller());
        var selectedCosmetic = data.getSelectedCosmetic().get(CosmeticType.KILL);
        var p = (Player) event.getEntity().getKiller(); // look at meeeeee
        var loc = event.getEntity().getLocation();

        if (selectedCosmetic != null) {
            switch (selectedCosmetic) {
                case ASCENT -> Ascent.play(loc, p);
                case KEEP_INVENTORY -> KeepInventory.play(loc, p);
                case CONFETTI -> Confetti.play(loc, p);
            }
        }

    }

    @EventHandler
    private void onTrail(PlayerMoveEvent event) {

        var p = event.getPlayer();
        var loc = event.getPlayer().getLocation();
        var selectedCosmetic = PlexPvP.getPlayerController().get(p).getSelectedCosmetic().get(CosmeticType.TRAIL);

        if (selectedCosmetic == null) return;
        if (!Util.isInRegion(loc, "spawn")) return;
        if (activeTrails.contains(p.getUniqueId())) return;

        switch (selectedCosmetic) {
            case RAINBOW -> Rainbow.play(p, p, activeTrails);
            case CROWN -> Crown.play(p, p, activeTrails);
        }

        activeTrails.add(p.getUniqueId());

    }

    @EventHandler
    private void onProjectile(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {

            var data = PlexPvP.getPlayerController().get(player);
            var selectedCosmetic = data.getSelectedCosmetic().get(CosmeticType.PROJECTILE);
            if (selectedCosmetic == null) return;

            var task = 0;
            task = Bukkit.getScheduler().scheduleSyncRepeatingTask(PlexPvP.get(), () -> {
                if (!activeProjectiles.contains(event.getProjectile().getUniqueId())) {

                    switch (selectedCosmetic) {
                        case RAINBOW -> Rainbow.play(event.getProjectile(), player, activeProjectiles);
                    }

                    activeProjectiles.add(event.getProjectile().getUniqueId());

                }
            }, 0L, 1L );

            projectileTasks.put(event.getProjectile().getUniqueId(), task);

        }
    }

    @EventHandler
    private void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {

            var id = projectileTasks.get(event.getEntity().getUniqueId());
            if (id != null) Bukkit.getScheduler().cancelTask(id);

        }
        if (event.getEntityType() == EntityType.ARROW) {
            event.getEntity().remove();
        }
    }

}
