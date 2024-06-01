package net.revive.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RevivePacket(boolean isSupportiveRevival) implements CustomPayload {

    public static final CustomPayload.Id<RevivePacket> PACKET_ID = new CustomPayload.Id<>(new Identifier("revive", "revive_packet"));

    public static final PacketCodec<RegistryByteBuf, RevivePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBoolean(value.isSupportiveRevival);
    }, buf -> new RevivePacket(buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
