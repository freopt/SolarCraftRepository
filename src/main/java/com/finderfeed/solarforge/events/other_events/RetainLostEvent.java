package com.finderfeed.solarforge.events.other_events;

import com.finderfeed.solarforge.Helpers;
import com.finderfeed.solarforge.SolarCraftTags;
import com.finderfeed.solarforge.SolarForge;
import com.finderfeed.solarforge.capabilities.capability_mana.CapabilitySolarMana;
import com.finderfeed.solarforge.magic.items.solar_lexicon.achievements.Progression;
import com.finderfeed.solarforge.misc_things.RunicEnergy;
import com.finderfeed.solarforge.packet_handler.SolarForgePacketHandler;
import com.finderfeed.solarforge.magic.items.solar_lexicon.packets.UpdateProgressionOnClient;
import com.finderfeed.solarforge.magic.items.solar_lexicon.unlockables.AncientFragment;
import com.finderfeed.solarforge.magic.items.solar_lexicon.unlockables.ProgressionHelper;
import com.finderfeed.solarforge.registries.abilities.AbilitiesRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;


import java.util.HashMap;

@Mod.EventBusSubscriber(modid = "solarforge",bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RetainLostEvent {

    public static HashMap<BlockPos, BlockState> map = new HashMap<>();

    @SubscribeEvent
    public static void attackEvent(final LivingAttackEvent event){
      if (event.getSource() != null) {
          Entity ent = event.getSource().getEntity();
          if (ent instanceof LivingEntity) {
              LivingEntity livingEnt = (LivingEntity) ent;
              if (livingEnt.hasEffect(SolarForge.SOLAR_STUN.get())) {
                  event.setCanceled(true);
              }


          }
      }
    }


    @SubscribeEvent
    public static void respawn(PlayerEvent.PlayerRespawnEvent event){
        if (event.getPlayer() instanceof ServerPlayer player){
            Helpers.updateProgression(player);
            for (RunicEnergy.Type type : RunicEnergy.Type.getAll()) {
                Helpers.updateRunicEnergyOnClient(type, RunicEnergy.getEnergy(player, type), player);
            }
            Helpers.updateFragmentsOnClient(player);
        }
    }



    @SubscribeEvent
    public static void retainAbilities(final PlayerEvent.Clone event) {
        Player peorig = event.getOriginal();
        Player playernew = event.getPlayer();
        if (!event.isWasDeath()) {
//            peorig.reviveCaps();
//            playernew.getCapability(CapabilitySolarMana.SOLAR_MANA_PLAYER)
//                    .orElseThrow(RuntimeException::new)
//                    .setMana(peorig.getCapability(CapabilitySolarMana.SOLAR_MANA_PLAYER).orElseThrow(RuntimeException::new).getMana());
//            peorig.invalidateCaps();

            playernew.getPersistentData().putInt(SolarCraftTags.RAW_SOLAR_ENERGY, peorig.getPersistentData().getInt(SolarCraftTags.RAW_SOLAR_ENERGY));
            playernew.getPersistentData().putBoolean("solar_forge_can_player_use_fireball", peorig.getPersistentData().getBoolean("solar_forge_can_player_use_fireball"));
            playernew.getPersistentData().putBoolean("solar_forge_can_player_use_lightning", peorig.getPersistentData().getBoolean("solar_forge_can_player_use_lightning"));
            playernew.getPersistentData().putBoolean("solar_forge_can_player_use_solar_strike", peorig.getPersistentData().getBoolean("solar_forge_can_player_use_solar_strike"));
            playernew.getPersistentData().putBoolean("solar_forge_can_player_use_solar_stun", peorig.getPersistentData().getBoolean("solar_forge_can_player_use_solar_stun"));
            playernew.getPersistentData().putBoolean("solar_forge_can_player_use_meteorite", peorig.getPersistentData().getBoolean("solar_forge_can_player_use_meteorite"));
            playernew.getPersistentData().putBoolean("solar_forge_can_player_use_solar_heal", peorig.getPersistentData().getBoolean("solar_forge_can_player_use_solar_heal"));
            playernew.getPersistentData().putBoolean("solar_forge_can_player_use_alchemist", peorig.getPersistentData().getBoolean("solar_forge_can_player_use_alchemist"));
            RunicEnergy.handleCloneEvent(event);
        }


        for (RunicEnergy.Type type : RunicEnergy.Type.getAll()) {
            if (RunicEnergy.hasFoundType(peorig,type)) {
                RunicEnergy.setFound(playernew, type);
            }
            Helpers.updateRunicEnergyOnClient(type, RunicEnergy.getEnergy(peorig, type), peorig);
        }
        playernew.getPersistentData().putString("solar_forge_ability_binded_1", peorig.getPersistentData().getString("solar_forge_ability_binded_1"));
        playernew.getPersistentData().putString("solar_forge_ability_binded_2", peorig.getPersistentData().getString("solar_forge_ability_binded_2"));
        playernew.getPersistentData().putString("solar_forge_ability_binded_3", peorig.getPersistentData().getString("solar_forge_ability_binded_3"));
        playernew.getPersistentData().putString("solar_forge_ability_binded_4", peorig.getPersistentData().getString("solar_forge_ability_binded_4"));
        playernew.getPersistentData().putBoolean("is_alchemist_toggled", false);
        if (playernew instanceof ServerPlayer s){
            AbilitiesRegistry.ALCHEMIST.setToggled(s, false);
        }


        for (Progression a : Progression.allProgressions){
            Helpers.setAchievementStatus(a,playernew,Helpers.hasPlayerUnlocked(a,peorig));

        }
        if (!playernew.level.isClientSide) {

            for (Progression a : Progression.allProgressions) {
                SolarForgePacketHandler.INSTANCE.sendTo(new UpdateProgressionOnClient(a.getAchievementCode(),playernew.getPersistentData().getBoolean(Helpers.PROGRESSION+a.getAchievementCode())),
                        ((ServerPlayer) playernew).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            }


        }
        if (playernew instanceof ServerPlayer player) {
            for (AncientFragment fragment : AncientFragment.getAllFragments()) {
                if (ProgressionHelper.doPlayerHasFragment(peorig, fragment)) {
                    ProgressionHelper.givePlayerFragment(fragment, playernew);
                }
                if (ProgressionHelper.doPlayerHasFragmentOld(peorig, fragment)) {
                    ProgressionHelper.givePlayerFragmentOld(fragment, playernew);
                }
            }
            Helpers.updateFragmentsOnClient(player);
        }


    }



}
