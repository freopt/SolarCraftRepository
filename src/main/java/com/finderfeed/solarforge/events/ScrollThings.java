package com.finderfeed.solarforge.events;


import com.finderfeed.solarforge.config.SolarcraftClientConfig;
import com.finderfeed.solarforge.misc_things.IScrollable;
import com.finderfeed.solarforge.magic.items.solar_lexicon.unlockables.AncientFragment;
import com.finderfeed.solarforge.magic.items.solar_lexicon.unlockables.BookEntry;
import com.finderfeed.solarforge.magic.items.solar_lexicon.unlockables.ProgressionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

@Mod.EventBusSubscriber(modid = "solarforge",bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class ScrollThings {

    @SubscribeEvent
    public static void listenToHotkeys(final InputEvent.KeyInputEvent event){
        if (Minecraft.getInstance().screen instanceof IScrollable){
            ((IScrollable) Minecraft.getInstance().screen).performScroll(event.getScanCode());

        }


    }

    @SubscribeEvent
    public static void initMaps(final ClientPlayerNetworkEvent.LoggedInEvent event){

        //BookEntry.initMap();
        if (event.getPlayer() != null && !SolarcraftClientConfig.DISABLE_WELCOME_MESSAGE.get()) {
            event.getPlayer().sendMessage(new TranslatableComponent("solarcraft.welcome_message"), event.getPlayer().getUUID());
            event.getPlayer().sendMessage(new TranslatableComponent("solarcraft.welcome_message2"), event.getPlayer().getUUID());

        }
    }

    @SubscribeEvent
    public static void initRecipes(final RecipesUpdatedEvent event){
//        ProgressionHelper.initInfRecipesMap(event.getRecipeManager());
//        ProgressionHelper.initSmeltingRecipesMap(event.getRecipeManager());
//        ProgressionHelper.initInfusingCraftingRecipes(event.getRecipeManager());
    }

}
