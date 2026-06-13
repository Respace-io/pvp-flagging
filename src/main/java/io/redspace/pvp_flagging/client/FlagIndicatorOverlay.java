package io.redspace.pvp_flagging.client;

import io.redspace.pvp_flagging.PvpFlagging;
import io.redspace.pvp_flagging.config.ClientConfig;
import io.redspace.pvp_flagging.config.PvpConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.GuiLayer;

import java.util.function.Function;

public class FlagIndicatorOverlay implements GuiLayer {
    public static final FlagIndicatorOverlay INSTANCE = new FlagIndicatorOverlay();
    private static final Identifier FLAG = Identifier.fromNamespaceAndPath(PvpFlagging.MODID, "textures/gui/pvp_flag.png");

    public enum HudAnchor {
        TopLeft(width -> 0, height -> 0),
        TopRight(width -> width, height -> 0),
        BottomLeft(width -> 0, height -> height),
        BottomRight(width -> width, height -> height),
        MiddleBottomLeft(width -> width / 4 - 21, height -> height),
        MiddleBottomRight(width -> 3 * width / 4 + 21, height -> height);
        final Function<Integer, Integer> widthToX, heightToY;

        HudAnchor(Function<Integer, Integer> widthToX, Function<Integer, Integer> heightToY) {
            this.widthToX = widthToX;
            this.heightToY = heightToY;
        }
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        var screenWidth = guiGraphics.guiWidth();
        var screenHeight = guiGraphics.guiHeight();
        var player = Minecraft.getInstance().player;
        if (player == null || !PvpConfig.CLIENT.INDICATOR_ENABLED.get() || player.isSpectator() || Minecraft.getInstance().options.hideGui) {
            return;
        }
        boolean flagged = ClientHelper.isFlagged(player);
        if (!flagged) {
            return;
        }
        int unflagTicksLeft = ClientHelper.getUnflagTimestamp() - Minecraft.getInstance().player.tickCount;
        boolean unflagging = unflagTicksLeft >= 0;

        var anchor = PvpConfig.CLIENT.INDICATOR_HUD_ANCHOR.get();
        var halfHeight = screenHeight / 2;
        var halfWidth = screenWidth / 2;
        int spriteSize = 21;
        float spriteScale = 1.5f;
        int iconSize = (int) (21 * spriteScale);
        int buffer = 5 + iconSize / 2;
        int xOffset = PvpConfig.CLIENT.INDICATOR_X_OFFSET.get();
        int yOffset = PvpConfig.CLIENT.INDICATOR_Y_OFFSET.get();

        int x = anchor.widthToX.apply(screenWidth) - iconSize / 2;
        int y = anchor.heightToY.apply(screenHeight) - iconSize / 2;
        x += (x > halfWidth ? -buffer : buffer + iconSize / 2) + xOffset;
        y += (y > halfHeight ? -buffer : buffer + iconSize / 2) + yOffset;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(spriteScale, spriteScale);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, FLAG, (int) (x / spriteScale) - spriteSize / 2, (int) (y / spriteScale) - spriteSize / 2, 0, 0, 21, 21, 21, 21);
        guiGraphics.pose().popMatrix();

        if (unflagging) {
            float secondsLeft = unflagTicksLeft / 20f;
            String timer = secondsLeft >= 60 ? String.format("%s:%s", (int) secondsLeft / 60, (int) secondsLeft % 60) : String.valueOf(((int) (secondsLeft * 10)) / 10f);
            float f = 1 - (unflagTicksLeft) / (float) PvpConfig.SERVER.UNFLAG_WAIT_TIME_TICKS.get();
            var colorL = (int) Mth.lerp(f * f, 50, 255);
            var color = 255 << 24 | 255 << 16 | colorL << 8 | colorL;

            guiGraphics.text(Minecraft.getInstance().font, timer, x - Minecraft.getInstance().font.width(timer) / 2, y + iconSize - 14, color, true);
        }
    }
}
