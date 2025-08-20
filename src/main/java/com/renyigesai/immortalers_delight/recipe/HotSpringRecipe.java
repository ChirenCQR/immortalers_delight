package com.renyigesai.immortalers_delight.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.renyigesai.immortalers_delight.ImmortalersDelightMod;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HotSpringRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ResourceLocation id;

    public HotSpringRecipe(NonNullList<Ingredient> ingredient, ItemStack output, ResourceLocation id) {
        this.inputItems = ingredient;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide) return false;
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack item = pContainer.getItem(i);
            if (!item.isEmpty()) inputs.add(item);
        }
        if (inputs.size() != this.inputItems.size()) return false;
        List<Ingredient> ingredientsToCheck = new ArrayList<>(this.inputItems);
        outer:
        for (ItemStack input : inputs) {
            for (Ingredient ingredient : ingredientsToCheck) {
                if (ingredient.test(input)) {
                    ingredientsToCheck.remove(ingredient);
                    continue outer;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<HotSpringRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "hot_spring";
    }

    public static class Serializer implements RecipeSerializer<HotSpringRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(ImmortalersDelightMod.MODID, "hot_spring");

        @Override
        public HotSpringRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            // 动态获取原料数量
            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.create();

            for (int i = 0; i < ingredients.size(); i++) {
                inputs.add(Ingredient.fromJson(ingredients.get(i)));
            }

            return new HotSpringRecipe(inputs, output,pRecipeId);
        }

        @Override
        public @Nullable HotSpringRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int ingredientCount = pBuffer.readInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);

            for (int i = 0; i < ingredientCount; i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
            }
            ItemStack output = pBuffer.readItem();
            return new HotSpringRecipe(inputs, output,pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, HotSpringRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.inputItems.size());

            for (Ingredient ingredient : pRecipe.getIngredients()) {
                ingredient.toNetwork(pBuffer);
            }
            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);
        }
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }
}
