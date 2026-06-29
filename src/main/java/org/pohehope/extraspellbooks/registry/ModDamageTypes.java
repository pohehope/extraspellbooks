package org.pohehope.extraspellbooks.registry;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.registries.DeferredRegister;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

public class ModDamageTypes {
    public static ResourceKey<DamageType> NIGHT = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocationHelper.getResouceLocation("night"));
    public static final RegistrySetBuilder DAMAGEBUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, ModDamageTypes::bootstrap);

    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(NIGHT, new DamageType("night", DamageScaling.ALWAYS, 0.1f));
    }

    public static HolderLookup.Provider append(HolderLookup.Provider provider) {
        return DAMAGEBUILDER.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), provider);
    }
}
