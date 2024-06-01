package net.revive.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "revive")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class ReviveConfig implements ConfigData {

    @Comment("Time in ticks: -1 disables timer")
    public int timer = -1;
    @Comment("Drops player loot directly")
    public boolean dropLoot = false;
    @Comment("Drops player inventory items randomly")
    public boolean dropRandom = false;
    @Comment("Drops random items when killed by explosion")
    public boolean dropRandomOnExplosion = true;
    @Comment("Chance for each inventory item to drop")
    public float dropChance = 0.3F;
    @Comment("Mainly for compatibility with RpgZ")
    public boolean allowLootablePlayer = true;
    @Comment("Revive without a potion")
    public boolean allowReviveWithHand = false;
    public int reviveHealthPoints = 2;
    public int reviveSupportiveHealthPoints = 10;
    public boolean showDeathCoordinates = true;
    public boolean thirdPersonOnDeath = true;
    public float rotationSpeed = 0.4F;

    @Comment("Apply effects on revival")
    public boolean reviveEffects = true;
    public int effectLivelyAftermath = 300;
    public int effectAftermath = 600;
}