package io.ejekta.bountiful.common.mixin;

import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModelPredicateProviderRegistry.class)
public interface ModelPredicateProviderRegistrar {

    @Invoker("register")
    public static void registerInvoker(Item item, Identifier id, ModelPredicateProvider provider) {
        throw new AssertionError();
    }

}
