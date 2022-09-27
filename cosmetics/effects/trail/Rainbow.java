package net.plexpvp.core.cosmetics.effects.trail;

import net.plexpvp.core.PlexPvP;
import net.plexpvp.core.util.HSLColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

public class Rainbow {

    public static void play(Entity e, Player p, ArrayList<UUID> active) {

        var color = new HSLColor(0, 100, 50);

        for (int i = 0; i <= 45; i++) {

            final var fi = i;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PlexPvP.get(), () -> {

                Color c = color.adjustHue(fi * 8);
                e.getWorld().spawnParticle(Particle.REDSTONE, e.getLocation(), 5, 0, 0, 0, 0.01, new Particle.DustOptions(org.bukkit.Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()), 2));

            }, i);

        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PlexPvP.get(), () -> active.remove(e.getUniqueId()), (45));

    }

}
