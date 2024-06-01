package net.revive.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.revive.ReviveMain;
import net.revive.accessor.PlayerEntityAccessor;
import net.revive.network.packet.DeathReasonPacket;
import net.revive.network.packet.FirstPersonPacket;
import net.revive.network.packet.RevivablePacket;
import net.revive.network.packet.ReviveSyncPacket;

public class ReviveClientPacket {

    @SuppressWarnings("resource")
    public static void init() {

        ClientPlayNetworking.registerGlobalReceiver(ReviveSyncPacket.PACKET_ID, (payload, context) -> {
            int entityId = payload.entityId();
            int healthPoints = payload.healthPoints();
            context.client().execute(() -> {
                if (context.player().getId() == entityId) {
                    if (ReviveMain.CONFIG.thirdPersonOnDeath)
                        context.client().options.setPerspective(Perspective.FIRST_PERSON);
                    ((PlayerEntityAccessor) context.player()).setCanRevive(false);
                    context.player().setHealth(healthPoints);
                    context.client().currentScreen.close();
                    context.player().deathTime = 0;
                    context.player().hurtTime = 0;
                    context.player().extinguish();
                } else {
                    PlayerEntity playerEntity = (PlayerEntity) context.client().world.getEntityById(entityId);
                    playerEntity.setHealth(healthPoints);
                    playerEntity.deathTime = 0;
                    playerEntity.hurtTime = 0;
                    playerEntity.extinguish();
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(DeathReasonPacket.PACKET_ID, (payload, context) -> {
            boolean isOutOfWorld = payload.isOutOfWorld();
            context.client().execute(() -> {
                ((PlayerEntityAccessor) context.player()).setDeathReason(isOutOfWorld);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(RevivablePacket.PACKET_ID, (payload, context) -> {
            boolean canRevive = payload.canRevive();
            boolean isSupportiveRevival = payload.isSupportiveRevival();
            context.client().execute(() -> {
                for (int u = 0; u < 30; u++)
                    context.client().world.addParticle(ParticleTypes.END_ROD, (double) context.player().getX() - 1.0D + context.client().world.random.nextFloat() * 2F,
                            context.player().getRandomBodyY(), (double) context.player().getZ() - 1.0D + context.client().world.random.nextFloat() * 2F, 0.0D, 0.2D, 0.0D);
                ((PlayerEntityAccessor) context.player()).setCanRevive(canRevive);
                ((PlayerEntityAccessor) context.player()).setSupportiveRevival(isSupportiveRevival);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(FirstPersonPacket.PACKET_ID, (payload, context) -> {
            context.client().execute(() -> {
                context.client().options.setPerspective(Perspective.FIRST_PERSON);
            });
        });
    }

}
