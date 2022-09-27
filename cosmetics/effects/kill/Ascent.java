package net.plexpvp.core.cosmetics.effects.kill;

import net.plexpvp.core.PlexPvP;
import net.plexpvp.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Ascent {

    public static void play(Location location, Player player) {

        var start = location.clone().add(0, -1, 0);
        var dummy = (LivingEntity) location.getWorld().spawnEntity(start, EntityType.ARMOR_STAND);
        var skull = ItemBuilder.of(Material.PLAYER_HEAD).setHeadSkin(player).build();

        dummy.setInvulnerable(true);
        dummy.setAI(false);
        dummy.setGravity(false);
        dummy.setInvisible(false);
        dummy.getEquipment().setHelmet(skull, true);

        var stand = (ArmorStand) dummy;
        stand.setArms(true);
        stand.setBasePlate(false);
        stand.setSmall(true);

        var equipment = player.getEquipment();
        stand.getEquipment().setChestplate(equipment.getChestplate() != null ? equipment.getChestplate() : new ItemStack(Material.AIR, 1), true);
        stand.getEquipment().setLeggings(equipment.getLeggings() != null ? equipment.getLeggings() : new ItemStack(Material.AIR, 1), true);
        stand.getEquipment().setBoots(equipment.getBoots() != null ? equipment.getBoots() : new ItemStack(Material.AIR, 1), true);

        for (int i = 0; i < 90; i++) {
            final int finalI = i;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PlexPvP.get(), () -> {

                var loc = dummy.getLocation();
                dummy.teleport(loc.add(0, 0.1, 0));
                dummy.setRotation(loc.getYaw()+15, 0);
                loc.getWorld().spawnParticle(Particle.END_ROD, loc.clone().add(0, 0.5, 0), 5, 0, 0, 0, 0.08);

            }, (finalI));
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PlexPvP.get(), dummy::remove, (91));

    }

}
