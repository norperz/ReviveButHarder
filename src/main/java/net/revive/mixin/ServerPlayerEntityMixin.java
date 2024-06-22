package net.revive.mixin;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.revive.ReviveMain;
import net.revive.network.packet.DeathReasonPacket;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "onDeath", at = @At(value = "HEAD"))
    private void onDeathMixin(DamageSource source, CallbackInfo info) {
        ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, new DeathReasonPacket(source.equals(((ServerPlayerEntity) (Object) this).getDamageSources().outOfWorld())));
    }

    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;drop(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void onDeathRedirectMixin(ServerPlayerEntity serverPlayerEntity, ServerWorld serverWorld, DamageSource damageSource) {
        if (ReviveMain.CONFIG.dropLoot) {
            this.drop(serverPlayerEntity.getServerWorld(), damageSource);
        } else if ((ReviveMain.CONFIG.dropRandomOnExplosion && damageSource.equals(((ServerPlayerEntity) (Object) this).getDamageSources().explosion(null))) || ReviveMain.CONFIG.dropRandom) {
            for (int i = 0; i < this.getInventory().size(); ++i) {
                ItemStack itemStack = this.getInventory().getStack(i);
                if (!itemStack.isEmpty() && this.random.nextFloat() < ReviveMain.CONFIG.dropChance) {
                    if (!itemStack.getEnchantments().getEnchantments().stream().anyMatch(entry -> entry.matchesId(Enchantments.VANISHING_CURSE.getRegistry()))) {
                        this.dropStack(itemStack);
                    }
                    this.getInventory().removeStack(i);
                }
            }
        }
    }
}
