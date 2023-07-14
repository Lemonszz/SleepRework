package party.lemons.sleeprework.mixin.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import party.lemons.sleeprework.client.AbstractContainerScreenAccess;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin implements AbstractContainerScreenAccess {
    @Shadow protected int leftPos;
    @Shadow protected int topPos;

    @Override
    public int getLeft() {
        return this.leftPos;
    }

    @Override
    public int getTop() {
        return this.topPos;
    }
}
