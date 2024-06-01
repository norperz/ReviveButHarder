package net.revive.mixin.client;

import java.util.List;

import com.google.common.collect.Lists;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.revive.ReviveMain;
import net.revive.accessor.PlayerEntityAccessor;
import net.revive.network.packet.RevivePacket;

@Environment(EnvType.CLIENT)
@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {

    @Shadow
    @Final
    @Mutable
    private List<ButtonWidget> buttons = Lists.newArrayList();
    @Shadow
    private int ticksSinceDeath;

    public DeathScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = Shift.AFTER))
    protected void initMixin(CallbackInfo info) {
        if (!((PlayerEntityAccessor) this.client.player).getDeathReason())
            this.buttons.add((ButtonWidget) this.addDrawableChild(ButtonWidget.builder(Text.translatable("text.deathScreen.revive"), (button) -> {
                if (((PlayerEntityAccessor) this.client.player).canRevive() && (ReviveMain.CONFIG.timer == -1 || (ReviveMain.CONFIG.timer != -1 && ReviveMain.CONFIG.timer > this.ticksSinceDeath)))
                    ClientPlayNetworking.send(new RevivePacket(((PlayerEntityAccessor) this.client.player).isSupportiveRevival()));
            }).dimensions(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build()));
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tickMixin(CallbackInfo info) {
        if (this.buttons.get(this.buttons.size() - 1).active && (!((PlayerEntityAccessor) this.client.player).canRevive()
                || (ReviveMain.CONFIG.timer != -1 && ReviveMain.CONFIG.timer < this.ticksSinceDeath && !((PlayerEntityAccessor) this.client.player).getDeathReason()))) {
            this.buttons.get(this.buttons.size() - 1).active = false;
        } else if (((PlayerEntityAccessor) this.client.player).canRevive() && !this.buttons.get(this.buttons.size() - 1).active) {
            this.buttons.get(this.buttons.size() - 1).active = true;
        }

    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V", ordinal = 2))
    private void renderMixin(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (this.client.player != null) {
            if (ReviveMain.CONFIG.timer != -1 && ReviveMain.CONFIG.timer >= this.ticksSinceDeath && !((PlayerEntityAccessor) this.client.player).getDeathReason()) {
                context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("text.deathScreen.timer", (ReviveMain.CONFIG.timer - this.ticksSinceDeath) / 20), this.width / 2, 115,
                        16777215);
            }
            // Coordinates
            if (ReviveMain.CONFIG.showDeathCoordinates) {
                context.drawCenteredTextWithShadow(this.textRenderer,
                        Text.translatable("text.deathScreen.coordinates", this.client.player.getBlockX(), this.client.player.getBlockY(), this.client.player.getBlockZ()), this.width / 2,
                        this.height / 4 + 146 + (!((PlayerEntityAccessor) this.client.player).getDeathReason() ? 0 : -24), 16777215);
            }
        }

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.chatKey.matchesKey(keyCode, scanCode)) {
            ((MinecraftClientAccessor) this.client).callOpenChatScreen("");
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}
