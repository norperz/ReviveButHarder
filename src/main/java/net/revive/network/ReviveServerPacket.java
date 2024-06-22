package net.revive.network;

import java.util.List;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.revive.ReviveMain;
import net.revive.accessor.PlayerEntityAccessor;
import net.revive.network.packet.DeathReasonPacket;
import net.revive.network.packet.FirstPersonPacket;
import net.revive.network.packet.RevivablePacket;
import net.revive.network.packet.RevivePacket;
import net.revive.network.packet.ReviveSyncPacket;

public class ReviveServerPacket {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(DeathReasonPacket.PACKET_ID, DeathReasonPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(FirstPersonPacket.PACKET_ID, FirstPersonPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(RevivablePacket.PACKET_ID, RevivablePacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ReviveSyncPacket.PACKET_ID, ReviveSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RevivePacket.PACKET_ID, RevivePacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(RevivePacket.PACKET_ID, (payload, context) -> {
            boolean isSupportiveRevival = payload.isSupportiveRevival();
            context.player().server.execute(() -> {
                ((PlayerEntityAccessor) context.player()).setCanRevive(false);
                context.player().deathTime = 0;
                context.player().hurtTime = 0;
                context.player().extinguish();

                int healthPoints = ReviveMain.CONFIG.reviveHealthPoints;
                if (isSupportiveRevival) {
                    healthPoints = ReviveMain.CONFIG.reviveSupportiveHealthPoints;
                }

                context.player().setHealth(healthPoints);
                context.player().onSpawn();

                if (ReviveMain.CONFIG.reviveEffects) {
                    if (isSupportiveRevival) {
                        context.player().addStatusEffect(new StatusEffectInstance(ReviveMain.LIVELY_AFTERMATH_EFFECT, ReviveMain.CONFIG.effectLivelyAftermath, 0, false, false, true));
                    } else {
                        context.player().addStatusEffect(new StatusEffectInstance(ReviveMain.AFTERMATH_EFFECT, ReviveMain.CONFIG.effectAftermath, 0, false, false, true));
                    }
                }
                List<? extends PlayerEntity> list = context.player().getWorld().getPlayers();
                for (int i = 0; i < list.size(); i++) {
                    ServerPlayNetworking.send((ServerPlayerEntity) list.get(i), new ReviveSyncPacket(context.player().getId(), healthPoints));
                }
            });
        });
    }

}
