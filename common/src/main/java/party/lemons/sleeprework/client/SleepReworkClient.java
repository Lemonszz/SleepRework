package party.lemons.sleeprework.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import party.lemons.sleeprework.handler.ClientHandler;

public class SleepReworkClient
{
    public static void init()
    {
        ClientGuiEvent.INIT_POST.register((screen, access) -> {
            if(screen instanceof InventoryScreen inventoryScreen)
            {
                ClientHandler.addSleepWidget(inventoryScreen, access);
            }
        });


    }
}
