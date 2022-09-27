package net.plexpvp.core.cosmetics.effects.kill;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.plexpvp.core.PlexPvP;
import net.plexpvp.core.util.Util;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Confetti {

    public static void play(Location loc, Player p) {

        var radius = 10;

        for (var i = radius; i > 0; i--) {

            final var fi = i;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PlexPvP.get(), () -> {

                var blocks = List.of(
                        Material.RED_STAINED_GLASS,
                        Material.ORANGE_STAINED_GLASS,
                        Material.YELLOW_STAINED_GLASS,
                        Material.LIME_STAINED_GLASS,
                        Material.GREEN_STAINED_GLASS,
                        Material.LIGHT_BLUE_STAINED_GLASS,
                        Material.CYAN_STAINED_GLASS,
                        Material.BLUE_STAINED_GLASS,
                        Material.MAGENTA_STAINED_GLASS,
                        Material.PINK_STAINED_GLASS,
                        Material.PURPLE_STAINED_GLASS
                );

                loc.getWorld().spawnParticle(Particle.ITEM_CRACK, loc.clone().add(0, 1, 0), 50, 0.5, 1, 0.5, 0.1, new ItemStack(blocks.get(fi), 1));
                if (fi % 2 == 0) loc.getWorld().playSound(loc, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2, ThreadLocalRandom.current().nextFloat());

                getBlocks(loc, fi, 1000).forEach(b -> {
                    var block = blocks.get(ThreadLocalRandom.current().nextInt(0, blocks.size()));
                    spawnRainbowGlass(b, block);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PlexPvP.get(), () -> {
                        destroy(b, b.getBlock().getBlockData());
                        loc.getWorld().spawnParticle(Particle.ITEM_CRACK, b.clone().add(0.5, 1, 0.5), 50, 0.5, 0, 0.5, 0.1, new ItemStack(block, 1));
                    }, ThreadLocalRandom.current().nextInt(20, 100));
                });

            }, i);

        }

    }

    private static void spawnRainbowGlass(Location loc, Material material) {
        var manager = ProtocolLibrary.getProtocolManager();

        var packet = manager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
        packet.getBlockPositionModifier().write(0, new BlockPosition((int) Math.floor(loc.getX()), (int) Math.floor(loc.getY()), (int) Math.floor(loc.getZ())));
        packet.getBlockData().write(0, WrappedBlockData.createData(material));

        Util.broadcastPacket(packet);

    }

    private static void destroy(Location loc, BlockData data) {
        var manager = ProtocolLibrary.getProtocolManager();

        var packet = manager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
        packet.getBlockPositionModifier().write(0, new BlockPosition((int) Math.floor(loc.getX()), (int) Math.floor(loc.getY()), (int) Math.floor(loc.getZ())));
        packet.getBlockData().write(0, WrappedBlockData.createData(data));

        Util.broadcastPacket(packet);
    }


    private static ArrayList<Location> getBlocks(Location location, double radius, double density) {
        var blocks = new ArrayList<Location>();

        double a = 4*Math.PI*radius*radius/density;
        double d = Math.sqrt(a);
        double ma = Math.round(Math.PI/d);
        double da = Math.PI/ma;
        double dp = a/da;

        for (int m = 0; m < ma; ++m) {
            double ang = Math.PI * (m + 0.5) / ma;
            double mp  = Math.round(2*Math.PI*Math.sin(ang)/dp);

            for (int ni = 0; ni < mp; ++ni) {
                double p = 2*Math.PI*ni/mp;

                double x = radius * Math.sin(ang) * Math.cos(p);
                double y = radius * Math.sin(ang) * Math.sin(p);
                double z = radius * Math.cos(ang);

                var loc = location.clone().add(x, y, z).getBlock().getLocation().clone().add(0.5, 0, 0.5);
                if (!blocks.contains(loc) && loc.getBlock().isSolid()/* && loc.clone().add(0, 1, 0).getBlock().isEmpty()*/) blocks.add(loc);
            }
        }

        return blocks;
    }



























    private static int spawnDiscoShulker(Location loc) {

        var manager = ProtocolLibrary.getProtocolManager();

        var entityID = ThreadLocalRandom.current().nextInt(0, 1000000);
        var entityUUID = UUID.randomUUID();
        var entityPacket = manager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        var teamPacket = manager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
        var metadataPacket = manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);

        entityPacket.getIntegers().write(0, entityID);
        entityPacket.getDoubles().write(0, loc.getX()).write(1, loc.getY()).write(2, loc.getZ());
        entityPacket.getUUIDs().write(0, entityUUID);
        entityPacket.getIntegers().write(1, 75);

        teamPacket.getIntegers().write(0, 0);
        teamPacket.getStrings().write(0, "shulker");
        teamPacket.getSpecificModifier(Collection.class).write(0, List.of(entityUUID.toString()));

        var metadata = new WrappedDataWatcher();
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x40 | 0x20));
        metadataPacket.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());
        metadataPacket.getIntegers().write(0, entityID);

        Bukkit.getOnlinePlayers().forEach(p -> {
            try {
                manager.sendServerPacket(p, entityPacket);
                manager.sendServerPacket(p, teamPacket);
                manager.sendServerPacket(p, metadataPacket);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });

        return entityID;
    }

}
