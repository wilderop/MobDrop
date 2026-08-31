package com.example.mobdrop;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class DropListener implements Listener {

    private final MobDropPlugin plugin;
    private final Random random = new Random();

    public DropListener(MobDropPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        EntityType killedType = event.getEntity().getType();

        // Only process/log if this mob type is in our config
        boolean isConfiguredMob = plugin.getCustomDrops().stream()
                .anyMatch(drop -> drop.mob() == killedType);

        if (!isConfiguredMob) {
            return;  // Skip entirely – no spam from other mobs
        }

        if (plugin.isDebugEnabled()) {
            plugin.getLogger().info("Configured mob death: " + killedType);
        }

        for (CustomDrop drop : plugin.getCustomDrops()) {
            if (drop.mob() == killedType) {
                double roll = random.nextDouble() * 100;

                if (plugin.isDebugEnabled()) {
                    plugin.getLogger().info("  Roll for " + drop.item() + ": " + String.format("%.2f", roll) + " < " + drop.chance() + "?");
                }

                if (roll < drop.chance()) {
                    ItemStack item = new ItemStack(drop.item(), 1);
                    event.getDrops().add(item);

                    if (plugin.isDebugEnabled()) {
                        plugin.getLogger().info("  → Dropped " + drop.item());
                    }
                } else if (plugin.isDebugEnabled()) {
                    plugin.getLogger().info("  → No drop");
                }
            }
        }
    }
}
