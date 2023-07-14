package party.lemons.sleeprework.handler;

import dev.architectury.hooks.client.screen.ScreenAccess;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import party.lemons.sleeprework.SleepRework;
import party.lemons.sleeprework.client.AbstractContainerScreenAccess;
import party.lemons.sleeprework.client.SleepDisplayWidget;

public class ClientHandler
{
    public static void addSleepWidget(InventoryScreen inventoryScreen, ScreenAccess screen)
    {
        int xx = ((AbstractContainerScreenAccess)inventoryScreen).getLeft() + SleepRework.CONFIG.clientConfig.iconX;
        int yy = ((AbstractContainerScreenAccess)inventoryScreen).getTop() + SleepRework.CONFIG.clientConfig.iconY;

        SleepDisplayWidget sleepDisplayWidget = new SleepDisplayWidget(xx, yy);
        screen.addRenderableWidget(sleepDisplayWidget);
    }
}
