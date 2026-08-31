package com.example.mobdrop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class MobDropPlugin extends JavaPlugin {

    private List<CustomDrop> customDrops = new ArrayList<>();
    private boolean debugEnabled = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        getLogger().info("MobDrop enabled. Loaded " + customDrops.size() + " custom drops. Debug: " + debugEnabled);
        getServer().getPluginManager().registerEvents(new DropListener(this), this);
    }

    public void loadConfigValues() {
        reloadConfig();
        debugEnabled = getConfig().getBoolean("debug", false);

        customDrops.clear();
        ConfigurationSection dropsSection = getConfig().getConfigurationSection("drops");
        if (dropsSection != null) {
            for (String key : dropsSection.getKeys(false)) {
                ConfigurationSection dropSection = dropsSection.getConfigurationSection(key);
                if (dropSection != null) {
                    String mobStr = dropSection.getString("mob");
                    String itemStr = dropSection.getString("item");
                    double chance = dropSection.getDouble("chance", 0.0);
                    try {
                        org.bukkit.entity.EntityType mob = org.bukkit.entity.EntityType.valueOf(mobStr.toUpperCase());
                        org.bukkit.Material item = org.bukkit.Material.valueOf(itemStr.toUpperCase());
                        customDrops.add(new CustomDrop(mob, item, chance));
                    } catch (IllegalArgumentException e) {
                        getLogger().warning("Invalid mob or item in config: " + key);
                    }
                }
            }
        }
    }

    public List<CustomDrop> getCustomDrops() {
        return customDrops;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("mobdrop")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("mobdrop.reload")) {
                    sender.sendMessage("§cNo permission!");
                    return true;
                }
                loadConfigValues();
                sender.sendMessage("§aMobDrop reloaded! Loaded " + customDrops.size() + " drops. Debug: " + debugEnabled);
                getLogger().info("Reloaded by " + sender.getName() + " - " + customDrops.size() + " drops, Debug: " + debugEnabled);
                return true;
            }
            sender.sendMessage("§eUsage: /mobdrop reload");
            return true;
        }
        return false;
    }
}
