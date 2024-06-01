package net.revive.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DeadPlayerScreenPacket(boolean isOutOfWorld) implements CustomPayload {

    public static final CustomPayload.Id<DeadPlayerScreenPacket> PACKET_ID = new CustomPayload.Id<>(new Identifier("revive", "dead_player_screen_packet"));

    public static final PacketCodec<RegistryByteBuf, DeadPlayerScreenPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBoolean(value.isOutOfWorld);
    }, buf -> new DeadPlayerScreenPacket(buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
