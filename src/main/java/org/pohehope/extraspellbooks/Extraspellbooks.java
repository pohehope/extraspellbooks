package org.pohehope.extraspellbooks;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.pohehope.extraspellbooks.entity.mobs.FromazenEntity;
import org.pohehope.extraspellbooks.entity.mobs.FromazenRenderer;
import org.pohehope.extraspellbooks.entity.spells.Meteor.MeteorRenderer;
import org.pohehope.extraspellbooks.entity.spells.carrots.CarrotsRenderer;
import org.pohehope.extraspellbooks.entity.spells.cucumber.CucumberRenderer;
import org.pohehope.extraspellbooks.entity.spells.frostenstrosity.FrostEnstrosityEntity;
import org.pohehope.extraspellbooks.entity.spells.frostenstrosity.FrostEnstrosityRenderer;
import org.pohehope.extraspellbooks.entity.spells.plasmadamagebox.PlasmaDamageBoxEntity;
import org.pohehope.extraspellbooks.entity.spells.plasmadamagebox.PlasmaDamageBoxRenderer;
import org.pohehope.extraspellbooks.entity.spells.snowflake.SnowFlakeRenderer;
import org.pohehope.extraspellbooks.registry.*;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Extraspellbooks.MODID)
public class Extraspellbooks {

    public static final String MODID = "extraspellbooks";
    public static final Logger LOGGER = LogUtils.getLogger();


    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public Extraspellbooks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModEntityRegistry.register(modEventBus);
        Modspellregistry.SPELLS.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        SchoolRegistry.SCHOOL.register(modEventBus);
        ModAttributes.ATTRIBUTES.register(modEventBus);
        modEventBus.addListener(this::registerEntityAttributes);


        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
                // あなたのきゅうりエンティティに、上で作った CucumberRenderer を登録する！

                event.registerEntityRenderer(ModEntityRegistry.CUCUMBER.get(), CucumberRenderer::new);
                event.registerEntityRenderer(ModEntityRegistry.CARROTS.get(), CarrotsRenderer::new);
                event.registerEntityRenderer(ModEntityRegistry.METEOR.get(), MeteorRenderer::new);
                event.registerEntityRenderer(ModEntityRegistry.SNOWFLAKE.get(), SnowFlakeRenderer::new);
                event.registerEntityRenderer(ModEntityRegistry.FROMAZEN.get(), (EntityRendererProvider.Context renderManager) -> new FromazenRenderer(renderManager));
                event.registerEntityRenderer(ModEntityRegistry.FROST_ENSTROSITY.get(), FrostEnstrosityRenderer::new);
                event.registerEntityRenderer(ModEntityRegistry.DAMAGE_BOX.get(), PlasmaDamageBoxRenderer::new);

            });

            LOGGER.info("Extra_ISS Registries have been successfully initialized!");
        }
    }
    public static ResourceLocation id(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(Extraspellbooks.MODID, path);
    }
    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityRegistry.FROMAZEN.get(), FromazenEntity.createLivingAttributes().build());
    }
}
