package net.revive.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DeathReasonPacket(boolean isOutOfWorld) implements CustomPayload {

    public static final CustomPayload.Id<DeathReasonPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("revive", "death_reason_packet"));

    public static final PacketCodec<RegistryByteBuf, DeathReasonPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBoolean(value.isOutOfWorld);
    }, buf -> new DeathReasonPacket(buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
