package com.finderfeed.solarforge.magic.items;


import com.finderfeed.solarforge.Helpers;
import com.finderfeed.solarforge.magic.items.primitive.solacraft_item_classes.SolarcraftBlockItem;
import com.finderfeed.solarforge.magic.items.solar_lexicon.achievements.Progression;
import com.finderfeed.solarforge.magic.items.solar_lexicon.unlockables.AncientFragment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class SolarLensBlockItem extends SolarcraftBlockItem {
    public SolarLensBlockItem(Block p_i48527_1_, Properties p_i48527_2_, Supplier<AncientFragment> fragmentSupplier) {
        super(p_i48527_1_, p_i48527_2_,fragmentSupplier);

    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, Level p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        if (!p_77663_2_.isClientSide && p_77663_3_ instanceof Player){

            Helpers.fireProgressionEvent((Player) p_77663_3_, Progression.CRAFT_SOLAR_LENS);
        }
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
    }

}
