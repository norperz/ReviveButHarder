package net.revive.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.revive.ReviveMain;
import net.revive.accessor.PlayerEntityAccessor;
import net.revive.network.packet.RevivablePacket;
import net.revive.screen.PlayerLootScreenHandler;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccessor {

    private boolean isOutOfWorld = false;
    private boolean canRevive = false;
    private boolean supportiveRevival = false;

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    public void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.isOutOfWorld = nbt.getBoolean("IsOutOfWorld");
        this.canRevive = nbt.getBoolean("CanRevive");
        this.supportiveRevival = nbt.getBoolean("SupportiveRevival");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putBoolean("IsOutOfWorld", this.isOutOfWorld);
        nbt.putBoolean("CanRevive", this.canRevive);
        nbt.putBoolean("SupportiveRevival", this.supportiveRevival);
    }

    @Override
    protected void updatePostDeath() {
        ++this.deathTime;
        if (!this.getWorld().isClient()) {
            if (ReviveMain.CONFIG.timer != -1 && ReviveMain.CONFIG.timer < this.deathTime) {
                if (!ReviveMain.CONFIG.dropLoot) {
                    this.drop(this.getDamageSources().generic());
                }
                this.getWorld().sendEntityStatus(this, (byte) 60);
                this.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        if (this.deathTime > 20 && (ReviveMain.CONFIG.allowLootablePlayer || ReviveMain.CONFIG.allowReviveWithHand) && player.getMainHandStack().get(DataComponentTypes.POTION_CONTENTS) == null) {
            if (!this.getWorld().isClient()) {
                PlayerEntity otherPlayerEntity = (PlayerEntity) (Object) this;
                if (ReviveMain.CONFIG.allowReviveWithHand && player.isSneaking()) {
                    ServerPlayNetworking.send((ServerPlayerEntity) otherPlayerEntity, new RevivablePacket(true, false));
                    this.getWorld().playSound(null, otherPlayerEntity.getBlockPos(), ReviveMain.REVIVE_SOUND_EVENT, SoundCategory.PLAYERS, 1.0F,
                            0.9F + this.getWorld().getRandom().nextFloat() * 0.2F);
                } else if (ReviveMain.CONFIG.allowLootablePlayer) {
                    player.openHandledScreen(
                            new SimpleNamedScreenHandlerFactory((syncId, inv, p) -> new PlayerLootScreenHandler(syncId, inv, otherPlayerEntity.getInventory()), otherPlayerEntity.getName()));
                }
            }
            return ActionResult.SUCCESS;
        } else
            return super.interactAt(player, hitPos, hand);
    }

    @Override
    public void setDeathReason(boolean outOfWorld) {
        this.isOutOfWorld = outOfWorld;
    }

    @Override
    public boolean getDeathReason() {
        return this.isOutOfWorld;
    }

    @Override
    public void setCanRevive(boolean canRevive) {
        this.canRevive = canRevive;
    }

    @Override
    public boolean canRevive() {
        return this.canRevive;
    }

    @Override
    public void setSupportiveRevival(boolean supportiveRevival) {
        this.supportiveRevival = supportiveRevival;
    }

    @Override
    public boolean isSupportiveRevival() {
        return this.supportiveRevival;
    }

}
