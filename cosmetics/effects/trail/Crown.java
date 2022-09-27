package net.plexpvp.core.cosmetics.effects.trail;

import net.plexpvp.core.PlexPvP;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class Crown {

    public static void play(Entity e, Player p, ArrayList<UUID> active) {

        int n = 50;
        double radius = 0.35;
        double crownHeight = 0.35;

        for (int x = 0; x < 90; x++) {

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PlexPvP.get(), () -> {

                for (int i = 0; i < n; ++i) {
                    var location = e.getLocation();
                    double angle = (double) i / n * 2 * Math.PI;
                    location.add(radius * Math.sin(angle), 2, radius * Math.cos(angle));
                    e.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, new Particle.DustOptions(Color.YELLOW, 0.2f));
                    location.add(0, crownHeight * Math.abs(Math.sin(angle * 4)), 0);
                    e.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.YELLOW, 0.2f));
                }

            }, x);

        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PlexPvP.get(), () -> active.remove(e.getUniqueId()), (90));

    }

}
