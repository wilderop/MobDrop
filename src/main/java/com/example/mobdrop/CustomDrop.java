package com.example.mobdrop;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public record CustomDrop(EntityType mob, Material item, double chance) {}
