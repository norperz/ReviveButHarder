package net.revive.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "renderBackground", at = @At("TAIL"))
    private void renderBackgroundMixin(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (this.client != null && this.client.player != null && this.client.player.isDead()) {
            context.fillGradient(0, 0, this.width, this.height, 1615855616, -1602211792);
        }
    }

}
