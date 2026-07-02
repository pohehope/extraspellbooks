package org.pohehope.extraspellbooks.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.pohehope.extraspellbooks.Extraspellbooks;
import org.pohehope.extraspellbooks.entity.mobs.FromazenEntity;
import org.pohehope.extraspellbooks.entity.spells.Meteor.Meteor;
import org.pohehope.extraspellbooks.entity.spells.carrots.Carrots;
import org.pohehope.extraspellbooks.entity.spells.cucumber.Cucumber;
import org.pohehope.extraspellbooks.entity.spells.plasmadamagebox.PlasmaDamageBoxEntity;
import org.pohehope.extraspellbooks.entity.spells.frostenstrosity.FrostEnstrosityEntity;
import org.pohehope.extraspellbooks.entity.spells.snowflake.SnowFlake;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

public class ModEntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Extraspellbooks.MODID);

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    public static final RegistryObject<EntityType<Cucumber>> CUCUMBER =
            ENTITIES.register("cucumber", () -> EntityType.Builder.<Cucumber>of(Cucumber::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .clientTrackingRange(64)
                    .build(ResourceLocationHelper.getResouceLocation("cucumber").toString()));

    public static final RegistryObject<EntityType<Carrots>> CARROTS =
            ENTITIES.register("carrots", () -> EntityType.Builder.<Carrots>of(Carrots::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .clientTrackingRange(64)
                    .build(ResourceLocationHelper.getResouceLocation( "carrots").toString()));

    public static final RegistryObject<EntityType<Meteor>> METEOR =
            ENTITIES.register("meteor", () -> EntityType.Builder.<Meteor>of(Meteor::new, MobCategory.MISC)
                    .sized(1.75F, 1.75F)
                    .clientTrackingRange(64)
                    .build(ResourceLocationHelper.getResouceLocation("meteor").toString()));

    public static final RegistryObject<EntityType<SnowFlake>> SNOWFLAKE =
            ENTITIES.register("snowflake", () -> EntityType.Builder.<SnowFlake>of(SnowFlake::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .clientTrackingRange(64)
                    .build(ResourceLocationHelper.getResouceLocation("snowflake").toString()));

    public static final RegistryObject<EntityType<FromazenEntity>> FROMAZEN =
            ENTITIES.register("fromazen", () -> EntityType.Builder.<FromazenEntity>of(FromazenEntity::new, MobCategory.MONSTER)
                    .sized(1.75F, 1.75F)
                    .clientTrackingRange(64)
                    .build(ResourceLocationHelper.getResouceLocation("fromazen").toString()));

    public static final RegistryObject<EntityType<FrostEnstrosityEntity>> FROST_ENSTROSITY =
            ENTITIES.register("frost_enstrosity", () -> EntityType.Builder.<FrostEnstrosityEntity>of(FrostEnstrosityEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .clientTrackingRange(64)
                    .build(ResourceLocationHelper.getResouceLocation("frost_enstrosity").toString()));

    public static final RegistryObject<EntityType<PlasmaDamageBoxEntity>> DAMAGE_BOX =
            ENTITIES.register("damage_box", () -> EntityType.Builder.<PlasmaDamageBoxEntity>of(PlasmaDamageBoxEntity::new, MobCategory.MISC)
                    .sized(1.5F, 1.5F)
                    .clientTrackingRange(64)
                    .build(ResourceLocationHelper.getResouceLocation("damage_box").toString()));
}