package net.plexpvp.core.cosmetics;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.plexpvp.core.PlexPvP;
import net.plexpvp.core.cosmetics.enums.CosmeticType;
import net.plexpvp.core.gui.GuiManager;
import net.plexpvp.core.localization.PlayerResolver;
import net.plexpvp.core.util.ItemBuilder;
import net.plexpvp.core.util.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.text;
import static net.plexpvp.core.localization.Localization.localize;
import static net.plexpvp.core.localization.Localization.localizeList;

public class CosmeticsNpc {

    public static void open(Player player) {

        var gui = GuiManager.createGui(3, text("Cosmetics"));
        var data = PlexPvP.getPlayerController().get(player);
        var playerResolver = new PlayerResolver("player", player);

        gui.setSlot(
                11,
                new ItemBuilder(Material.GOLDEN_SWORD)
                        .setItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .name(localize("gui.cosmetics.main.combat.name"))
                        .lore(localizeList("gui.cosmetics.main.combat.lore",
                                Placeholder.unparsed("unlocked", String.valueOf(data.getCosmetics().getOrDefault("combat", new ArrayList<>()).size())),
                                Placeholder.unparsed("total", "0")
                        ))
                        .build(),
                () -> {
                    Util.genericClick(player);
                    CosmeticsGUI.open(player, CosmeticType.KILL);
                }
        );

        gui.setSlot(
                13,
                new ItemBuilder(Material.TIPPED_ARROW)
                        .setItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
                        .name(localize("gui.cosmetics.main.projectile.name"))
                        .lore(localizeList("gui.cosmetics.main.projectile.lore",
                                Placeholder.unparsed("unlocked", String.valueOf(data.getCosmetics().getOrDefault("projectile", new ArrayList<>()).size())),
                                Placeholder.unparsed("total", "0")
                        ))
                        .build(),
                () -> {
                    Util.genericClick(player);
                    CosmeticsGUI.open(player, CosmeticType.PROJECTILE);
                }
        );

        gui.setSlot(
                15,
                new ItemBuilder(Material.BLAZE_POWDER)
                        .glow()
                        .name(localize("gui.cosmetics.main.trail.name"))
                        .lore(localizeList("gui.cosmetics.main.trail.lore",
                                Placeholder.unparsed("unlocked", String.valueOf(data.getCosmetics().getOrDefault("trail", new ArrayList<>()).size())),
                                Placeholder.unparsed("total", "0")
                        ))
                        .build(),
                () -> {
                    Util.genericClick(player);
                    CosmeticsGUI.open(player, CosmeticType.TRAIL);
                }
        );

        gui.open(player);

    }


}
