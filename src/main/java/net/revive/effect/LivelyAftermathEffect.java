package net.revive.effect;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.revive.ReviveMain;
import net.revive.accessor.PlayerEntityAccessor;
import net.revive.network.packet.RevivablePacket;

public class LivelyAftermathEffect extends StatusEffect {

    public LivelyAftermathEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient() && entity instanceof PlayerEntity) {
            ((PlayerEntity) entity).getHungerManager().add(amplifier + 1, 1.0f);
        }
        return super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
        if (!entity.getWorld().isClient() && entity.isDead() && entity instanceof PlayerEntity && !((PlayerEntityAccessor) entity).canRevive()) {
            ServerPlayNetworking.send((ServerPlayerEntity) entity, new RevivablePacket(true, true));
            entity.getWorld().playSound(null, entity.getBlockPos(), ReviveMain.REVIVE_SOUND_EVENT, SoundCategory.PLAYERS, 1.0F, 0.9F + entity.getWorld().getRandom().nextFloat() * 0.2F);
        }
    }

}
