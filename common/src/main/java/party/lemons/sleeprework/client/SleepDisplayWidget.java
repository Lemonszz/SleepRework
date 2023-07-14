package party.lemons.sleeprework.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import party.lemons.sleeprework.SleepRework;
import party.lemons.sleeprework.config.SleepReworkConfig;

public class SleepDisplayWidget extends AbstractWidget
{
    private static final ResourceLocation ICONS = SleepRework.id("textures/gui/icons.png");
    private float lastTiredness;
    public SleepDisplayWidget(int x, int y) {
        super(x, y, 12, 12, Component.empty());

        setTooltip(createTooltip());
        lastTiredness = SleepRework.localSleepData.getTiredness();
    }

    public Tooltip createTooltip()
    {
        MutableComponent component = getTiredness();
        if(SleepRework.CONFIG.clientConfig.showTutorial)
        {
            //int sleepPerc = (int)(SleepRework.CONFIG.playerConfig.minSleepLevel * 100F);
            int phantPerc = (int)((SleepRework.CONFIG.phantomConfig.phantomSpawnTiredness / SleepRework.CONFIG.playerConfig.minSleepLevel) * 100);

            component = component.append(Component.translatable("sleeprework.tutorial", "100", phantPerc).withStyle(ChatFormatting.ITALIC));
            component = component.append(Component.translatable("sleeprework.tutorial.hide", Minecraft.getInstance().options.keyAttack.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.GRAY));
        }

        return Tooltip.create(component);
    }

    @Override
    public void onClick(double d, double e) {
        if(SleepRework.CONFIG.clientConfig.showTutorial) {
            SleepRework.CONFIG.clientConfig.showTutorial = false;
            setTooltip(createTooltip());

            SleepReworkConfig.writeConfig(SleepRework.CONFIG);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f)
    {
        float sleepLevel = SleepRework.localSleepData.getTiredness();
        if(sleepLevel != lastTiredness) {
            lastTiredness = sleepLevel;
            setTooltip(createTooltip());
        }

        if(isHovered())
        {
            guiGraphics.blit(ICONS, getX(), getY(), 0, 0, 12, 12);
        }
        else
        {

            if(SleepRework.CONFIG.clientConfig.doRecolor) {
                int c1, c2;
                float ratio;
                if (sleepLevel <= SleepRework.CONFIG.playerConfig.minSleepLevel) {
                    c1 = SleepRework.CONFIG.clientConfig.color_1;
                    c2 = SleepRework.CONFIG.clientConfig.color_2;
                    ratio = mapValue(0, SleepRework.CONFIG.playerConfig.minSleepLevel, sleepLevel);
                } else if (sleepLevel < SleepRework.CONFIG.phantomConfig.phantomSpawnTiredness) {
                    c1 = SleepRework.CONFIG.clientConfig.color_2;
                    c2 = SleepRework.CONFIG.clientConfig.color_3;
                    ratio = mapValue(SleepRework.CONFIG.playerConfig.minSleepLevel, SleepRework.CONFIG.phantomConfig.phantomSpawnTiredness, sleepLevel);
                } else {
                    c1 = SleepRework.CONFIG.clientConfig.color_3;
                    c2 = SleepRework.CONFIG.clientConfig.color_4;
                    ratio = mapValue(SleepRework.CONFIG.phantomConfig.phantomSpawnTiredness, SleepRework.CONFIG.playerConfig.maxTiredness, sleepLevel);
                }

                int finalColor = interpolate(c1, c2, ratio);

                float r = ((finalColor >> 16) & 0xff) / 255.0f;
                float g = ((finalColor >> 8) & 0xff) / 255.0f;
                float b = ((finalColor) & 0xff) / 255.0f;

                guiGraphics.setColor(r, g, b, 1.0F);
            }
            guiGraphics.blit(ICONS, getX(), getY(), 0, 12, 12, 12);
            guiGraphics.setColor(1F, 1F, 1F, 1F);
        }
    }

    private static int interpolate(int color1, int color2, float ratio)
    {
        //I no do go math someone make this better
        float r1 = ((color1 >> 16) & 0xff) / 255.0f;
        float g1 = ((color1 >>  8) & 0xff) / 255.0f;
        float b1 = ((color1      ) & 0xff) / 255.0f;

        float[] hsv1 = new float[3];
        RGBtoHSB((int)(r1 * 255F), (int)(g1 * 255F), (int)(b1 * 255F), hsv1);

        float r2 = ((color2 >> 16) & 0xff) / 255.0f;
        float g2 = ((color2 >>  8) & 0xff) / 255.0f;
        float b2 = ((color2      ) & 0xff) / 255.0f;

        float[] hsv2 = new float[3];
        RGBtoHSB((int)(r2 * 255F), (int)(g2 * 255F), (int)(b2 * 255F), hsv2);

        float h = Mth.lerp(ratio, hsv1[0], hsv2[0]);
        float s = Mth.lerp(ratio, hsv1[1], hsv2[1]);
        float v = Mth.lerp(ratio, hsv1[2], hsv2[2]);

        return Mth.hsvToRgb(h, s, v);
    }

    /*
        Stolem from awt/Color lmao :)
     */
    public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals)
    {
        float hue, saturation, brightness;
        if (hsbvals == null) {
            hsbvals = new float[3];
        }
        int cmax = Math.max(r, g);
        if (b > cmax) cmax = b;
        int cmin = Math.min(r, g);
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }


    private static float mapValue(float min, float max, float current)
    {
        return 0F + ((1F - 0F) / (max - min)) * (current - min);
    }

    private MutableComponent getTiredness()
    {
        float currentPerc = SleepRework.localSleepData.getTiredness() / SleepRework.CONFIG.playerConfig.minSleepLevel;

        return Component.translatable("sleeprework.display.tiredness", (int) (currentPerc * 100));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
