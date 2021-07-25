package com.finderfeed.solarforge.magic_items.items.solar_lexicon;

import com.finderfeed.solarforge.magic_items.items.solar_lexicon.unlockables.AncientFragment;
import com.finderfeed.solarforge.magic_items.items.solar_lexicon.unlockables.ProgressionHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SolarLexiconContainerSlot extends SlotItemHandler {

    private final AncientFragment frag;

    public SolarLexiconContainerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, AncientFragment frag) {
        super(itemHandler, index, xPosition, yPosition);
        this.frag = frag;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {

        CompoundTag nbt = stack.getTagElement(ProgressionHelper.TAG_ELEMENT);
        if (nbt != null){
            if (AncientFragment.getFragmentByID(nbt.getString(ProgressionHelper.FRAG_ID)) != frag){
                return false;
            }
        }else{
            return false;
        }

        return super.mayPlace(stack);
    }
}
