package com.finderfeed.solarforge.events.other_events;


import com.finderfeed.solarforge.Helpers;
import com.finderfeed.solarforge.config.enchanter_config.EnchanterConfigInit;
import com.finderfeed.solarforge.config.JsonFragmentsHelper;
import com.finderfeed.solarforge.magic.items.solar_lexicon.unlockables.AncientFragment;
import com.finderfeed.solarforge.magic.items.solar_lexicon.unlockables.BookEntry;
import com.finderfeed.solarforge.misc_things.RunicEnergy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,modid = "solarforge")
public class OnPlayerJoin {

    @SubscribeEvent
    public static void onPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() != null) {
            Player player = event.getPlayer();
            for (RunicEnergy.Type type : RunicEnergy.Type.values()){
                Helpers.updateRunicEnergyOnClient(type,RunicEnergy.getEnergy(event.getPlayer(),type),event.getPlayer());
            }
            Helpers.updateProgression((ServerPlayer) player);

            if (JsonFragmentsHelper.fragmentsShouldBeRead()){
                List<AncientFragment> fragsDes = AncientFragment.deserializeFragments(JsonFragmentsHelper.readFragments());

                if (fragsDes != null) {
                    AncientFragment.ALL_FRAGMENTS.addAll(fragsDes);
                }
            }

            JsonFragmentsHelper.sendUpdatePacketToClient((ServerPlayer) player);

            if (EnchanterConfigInit.shouldBeRead()){
                EnchanterConfigInit.readJson();
            }

            Helpers.updateFragmentsOnClient((ServerPlayer) player);


            AncientFragment.initFragmentsMap();
            BookEntry.initMap();
//            ProgressionHelper.initInfRecipesMap(event.getPlayer().level.getRecipeManager());
//            ProgressionHelper.initSmeltingRecipesMap(event.getPlayer().level.getRecipeManager());
//            ProgressionHelper.initInfusingCraftingRecipes(event.getPlayer().level.getRecipeManager());

        }
    }


}
