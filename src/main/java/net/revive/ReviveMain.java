package net.revive;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import net.revive.config.ReviveConfig;
import net.revive.effect.*;
import net.revive.network.ReviveServerPacket;

public class ReviveMain implements ModInitializer {

    public static final boolean isBackSlotLoaded = FabricLoader.getInstance().isModLoaded("backslot");

    public static ReviveConfig CONFIG = new ReviveConfig();
    public static final Item REVIVE_ITEM = new Item(new Item.Settings());

    public static final Identifier REVIVE_SOUND = new Identifier("revive:revive");
    public static SoundEvent REVIVE_SOUND_EVENT = SoundEvent.of(REVIVE_SOUND);

    public static final RegistryEntry<StatusEffect> AFTERMATH_EFFECT = register("revive:aftermath",
            new AftermathEffect(StatusEffectCategory.HARMFUL, 11838975)
                    .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "66462626-74d7-42db-858f-2419f1fd711a", -0.30F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "e0a9b1d4-1611-444b-90bf-02aa2638371f", -5.0F, EntityAttributeModifier.Operation.ADD_VALUE));

    public static final RegistryEntry<StatusEffect> LIVELY_AFTERMATH_EFFECT = register("revive:lively_aftermath", new LivelyAftermathEffect(StatusEffectCategory.HARMFUL, 10323199)
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "6d4a232f-5439-41b1-bfb6-d665beed14e7", -0.15F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final RegistryEntry<Potion> REVIVIFY_POTION = register("revivify_potion", new Potion(new StatusEffectInstance(AFTERMATH_EFFECT, 600)));
    public static final RegistryEntry<Potion> SUPPORTIVE_REVIVIFY_POTION = register("supportive_revivify_potion", new Potion(new StatusEffectInstance(LIVELY_AFTERMATH_EFFECT, 600)));

    @Override
    public void onInitialize() {
        AutoConfig.register(ReviveConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ReviveConfig.class).getConfig();
        ReviveServerPacket.init();
        Registry.register(Registries.ITEM, new Identifier("revive", "revival_star"), REVIVE_ITEM);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.add(REVIVE_ITEM));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CLERIC, 1, (factories -> {
            factories.add((entity, random) -> new TradeOffer(new TradedItem(Items.EMERALD, 28), new ItemStack(REVIVE_ITEM), 4, 1, 0.4F));
        }));
        Registry.register(Registries.SOUND_EVENT, REVIVE_SOUND, REVIVE_SOUND_EVENT);
        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder) -> {
            builder.registerPotionRecipe(Potions.STRONG_REGENERATION, ReviveMain.REVIVE_ITEM, ReviveMain.SUPPORTIVE_REVIVIFY_POTION);
            builder.registerPotionRecipe(Potions.MUNDANE, Items.GOLDEN_APPLE, ReviveMain.REVIVIFY_POTION);
        });
    }

    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, new Identifier(id), statusEffect);
    }

    private static RegistryEntry<Potion> register(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, new Identifier(name), potion);
    }

}
