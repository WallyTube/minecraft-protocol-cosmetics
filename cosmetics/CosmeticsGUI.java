package net.plexpvp.core.cosmetics;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.plexpvp.core.PlexPvP;
import net.plexpvp.core.cosmetics.enums.CosmeticEffect;
import net.plexpvp.core.cosmetics.enums.CosmeticType;
import net.plexpvp.core.gui.Gui;
import net.plexpvp.core.gui.GuiManager;
import net.plexpvp.core.gui.PaginatedGui;
import net.plexpvp.core.util.ItemBuilder;
import net.plexpvp.core.util.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.plexpvp.core.localization.Localization.*;

public class CosmeticsGUI {

    static CosmeticEffect[] kill = new CosmeticEffect[]{
            CosmeticEffect.ASCENT,
            CosmeticEffect.KEEP_INVENTORY,
            CosmeticEffect.CONFETTI,
    };
    static CosmeticEffect[] trail = new CosmeticEffect[]{
            CosmeticEffect.RAINBOW,
            CosmeticEffect.CROWN,
    };
    static CosmeticEffect[] projectile = new CosmeticEffect[]{
            CosmeticEffect.RAINBOW,
    };

    public static void open(Player player, CosmeticType type) {

        var data = PlexPvP.getPlayerController().get(player);
        var cosmetics = data.getCosmetics().get(type) == null ? new ArrayList<CosmeticEffect>() : data.getCosmetics().get(type);
        var selected = data.getSelectedCosmetic().get(type);
        var list = switch (type) {
            case KILL -> kill;
            case TRAIL -> trail;
            case PROJECTILE -> projectile;
        };

        var gui = GuiManager.createGui(5, text("Cosmetics"));

        gui.fillBorder(Gui.BORDER);

        if (selected != null) {
            var material = Material.valueOf(Util.raw(localize("cosmetics.items." + type + "." + selected + ".item")));
            gui.setSlot(40, new ItemBuilder(material)
                    .name(localize("gui.cosmetics.selected.name"))
                    .lore(localizeList("gui.cosmetics.selected.lore",
                            Placeholder.unparsed("effect_name", Util.raw(localize("cosmetics.items." + type + "." + selected + ".name")))
                    ))
                    .build(),
            () -> {

                var a = data.getSelectedCosmetic();
                a.put(type, null);
                data.setSelectedCosmetic(a);

                send(player, "cosmetics.chat.disable_effect",
                        Placeholder.component("effect", localize("cosmetics.items." + type + "." + selected + ".name")),
                        Placeholder.unparsed("type", type.toString().toLowerCase())
                );

                Util.acceptedClick(player);
                open(player, type);

            });
        } else {
            gui.setSlot(40, new ItemBuilder(Material.GRAY_DYE)
                    .name(localize("gui.cosmetics.selected.name"))
                    .lore(localizeList("gui.cosmetics.selected.lore",
                            Placeholder.unparsed("effect_name", "NONE")
                    ))
                    .build()
            );
        }

        var pag = new PaginatedGui(
                gui,
                new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34},
                18,
                26,
                new ItemBuilder(Material.PAPER)
                        .name(localize("gui.cosmetics.pagination.left")),
                new ItemBuilder(Material.PAPER)
                        .name(localize("gui.cosmetics.pagination.right")),
                new ItemBuilder(Gui.BORDER),
                new ItemBuilder(Gui.BORDER),
                list.length
        ).setFormatter((slot, page, index) -> {
            if (list.length > index) {
                Material material;
                Component name;
                List<Component> lore;

                if (cosmetics.contains(list[index])) {
                    material = Material.valueOf(Util.raw(localize("cosmetics.items." + type + "." + list[index] + ".item")));
                    name = text(Util.raw(localize("cosmetics.items." + type + "." + list[index] + ".name")), NamedTextColor.LIGHT_PURPLE);
                    lore = localizeList("cosmetics.items." + type + "." + list[index] + ".lore");
                    lore.addAll(localizeList("gui.cosmetics.effect.has.lore"));
                } else {
                    material = Material.RED_STAINED_GLASS_PANE;
                    name = text(Util.raw(localize("cosmetics.items." + type + "." + list[index] + ".name")), NamedTextColor.RED);
                    lore = localizeList("cosmetics.items." + type + "." + list[index] + ".lore");
                    System.out.println(localizeList("gui.cosmetics.effect.has_not.lore"));
                    System.out.println(localize("cosmetics.items." + type + "." + list[index] + ".cost"));
                    lore.addAll(localizeList("gui.cosmetics.effect.has_not.lore",
                            Placeholder.component("cost", localize("cosmetics.items." + type + "." + list[index] + ".cost"))
                    ));
                }

                if (list[index] == selected) {
                    return new ItemBuilder(material).name(name).lore(lore).setItemFlags(ItemFlag.HIDE_ATTRIBUTES).glow();
                } else {
                    return new ItemBuilder(material).name(name).lore(lore).setItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                }


            } else {
                return new ItemBuilder(Material.AIR);
            }
        }).addClickEvent((slot, page, index) -> {
            if (list.length > index) {
                return () -> {

                    var cos = list[index];
                    if (cosmetics.contains(cos)) {
                        var sel = data.getSelectedCosmetic();
                        sel.put(type, cos);
                        data.setSelectedCosmetic(sel);

                        send(player, "cosmetics.chat.set_effect",
                                Placeholder.unparsed("type", type.toString().toLowerCase()),
                                Placeholder.component("effect", localize("cosmetics.items." + type + "." + list[index] + ".name"))
                        );

                        Util.acceptedClick(player);
                        open(player, type);
                    } else {
                        var cost = Integer.parseInt(Util.raw(localize("cosmetics.items." + type + "." + list[index] + ".cost")));
                        if (player.getInventory().contains(Material.EMERALD, cost)) {

                            player.getInventory().removeItem(new ItemStack(Material.EMERALD, cost));

                            cosmetics.add(cos);
                            var c = data.getCosmetics();
                            c.put(type, cosmetics);
                            data.setCosmetics(c);

                            Util.acceptedClick(player);
                            send(player, "cosmetics.chat.bought_effect",
                                    Placeholder.unparsed("type", type.toString().toLowerCase()),
                                    Placeholder.component("effect", localize("cosmetics.items." + type + "." + list[index] + ".name"))
                            );

                            Util.acceptedClick(player);
                            open(player, type);

                        } else {
                            send(player, "cosmetics.chat.insufficient_emeralds",
                                    Placeholder.component("cost", localize("cosmetics.items." + type + "." + list[index] + ".cost"))
                            );
                            Util.deniedClick(player);
                            player.closeInventory();
                        }
                    }

                };
            }

            return null;
        });

        pag.open(player);

    }

}
