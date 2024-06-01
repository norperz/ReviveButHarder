package net.revive.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record FirstPersonPacket() implements CustomPayload {

    public static final CustomPayload.Id<FirstPersonPacket> PACKET_ID = new CustomPayload.Id<>(new Identifier("revive", "first_person_packet"));

    public static final PacketCodec<RegistryByteBuf, FirstPersonPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
    }, buf -> new FirstPersonPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
