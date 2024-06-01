package net.revive.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RevivablePacket(boolean canRevive, boolean isSupportiveRevival) implements CustomPayload {

    public static final CustomPayload.Id<RevivablePacket> PACKET_ID = new CustomPayload.Id<>(new Identifier("revive", "revivable_packet"));

    public static final PacketCodec<RegistryByteBuf, RevivablePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBoolean(value.canRevive);
        buf.writeBoolean(value.isSupportiveRevival);
    }, buf -> new RevivablePacket(buf.readBoolean(), buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
