package net.revive.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Invoker("drop")
    void callDrop(ServerWorld world, DamageSource damageSource);
}
