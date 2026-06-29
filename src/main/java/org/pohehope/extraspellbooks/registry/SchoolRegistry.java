package org.pohehope.extraspellbooks.registry;

import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

public class SchoolRegistry {

    public static final DeferredRegister<SchoolType> SCHOOL =
            DeferredRegister.create(
                    io.redspace.ironsspellbooks.api.registry.SchoolRegistry.SCHOOL_REGISTRY_KEY, "extraspellbooks"
            );

    public static final ResourceLocation NIGHT_RESOURCE =
            ResourceLocationHelper.getResouceLocation("night");

    public static final RegistryObject<SchoolType> NIGHT = SCHOOL.register("night",
            () -> {
                MutableComponent schoolName = Component.translatable("school.extraspellbooks.night")
                        .withStyle(Style.EMPTY.withColor(0xADD8E6));

                // バニラの既存サウンドをHolderで取得（完全無音は不可のためダミー）
                // SoundEvents.INTENTIONALLY_EMPTY は音量0の特殊サウンド
                Holder<SoundEvent> sound = ForgeRegistries.SOUND_EVENTS.getHolder(
                        new ResourceLocation("minecraft", "intentionally_empty")
                ).orElse(ForgeRegistries.SOUND_EVENTS.getDelegateOrThrow(
                        new ResourceLocation("minecraft", "ui.button.click")
                ));

                TagKey<Item> focusTag = TagKey.create(
                        Registries.ITEM,
                        ResourceLocationHelper.getResouceLocation("night_focus")
                );

                Holder<Attribute> power = ForgeRegistries.ATTRIBUTES.getHolder(
                        ModAttributes.NIGHT_SPELL_POWER.getId()
                ).orElseThrow();

                Holder<Attribute> resist = ForgeRegistries.ATTRIBUTES.getHolder(
                        ModAttributes.NIGHT_MAGIC_RESIST.getId()
                ).orElseThrow();

                return new SchoolType(
                        NIGHT_RESOURCE,
                        focusTag,
                        schoolName,
                        power,
                        resist,
                        sound,
                        ModDamageTypes.NIGHT
                );
            }
    );
}
