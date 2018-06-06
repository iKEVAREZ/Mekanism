package mekanism.common.util;

import mekanism.common.MekanismFluids;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.UniversalBucket;

public final class CoolingUtils {
    public static boolean isCoolingFluid(ItemStack itemstack)
    {
        ItemStack bucket = ForgeModContainer.getInstance().universalBucket.getFilledBucket(
                (new UniversalBucket()),
                MekanismFluids.Brine.getFluid()
        );

        return itemstack.getItem() == bucket.getItem();
    }
}
