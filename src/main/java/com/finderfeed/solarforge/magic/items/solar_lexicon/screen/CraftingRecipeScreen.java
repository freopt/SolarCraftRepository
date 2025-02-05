package com.finderfeed.solarforge.magic.items.solar_lexicon.screen;

import com.finderfeed.solarforge.ClientHelpers;
import com.finderfeed.solarforge.SolarForge;
import com.finderfeed.solarforge.client.rendering.PreparedShapedRecipe;
import com.finderfeed.solarforge.magic.items.solar_lexicon.SolarLexicon;
import com.finderfeed.solarforge.recipe_types.infusing_crafting.InfusingCraftingRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CraftingRecipeScreen extends Screen {
    public final ResourceLocation BUTTONS = new ResourceLocation("solarforge","textures/misc/page_buttons.png");

    private static final ResourceLocation MAIN_SCREEN = new ResourceLocation(SolarForge.MOD_ID,"textures/gui/solar_lexicon_crafting_recipe_screen.png");

    private int relX;
    private int relY;
    private final List<PreparedShapedRecipe> recipes;
    private int currentPage = 0;
    private int maxPages;

    public CraftingRecipeScreen(PreparedShapedRecipe recipe) {
        super(new TextComponent(""));
        this.recipes = List.of(recipe);
        this.maxPages = 0;
    }


    public CraftingRecipeScreen(List<PreparedShapedRecipe> recipe) {
        super(new TextComponent(""));
        this.recipes = recipe;
        this.maxPages = recipe.size()-1;
    }

    @Override
    protected void init() {
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        int scale = (int) minecraft.getWindow().getGuiScale();
        this.relX = (width/scale - 183)/2-12;
        this.relY = (height - 218*scale)/2/scale;
        int xoffs = 111;
        if (maxPages != 0) {
            addRenderableWidget(new ImageButton(relX + 180, relY + 33, 16, 16, 0, 0, 0, BUTTONS, 16, 32, (button) -> {
                if ((currentPage + 1 <= maxPages)) {
                    currentPage += 1;
                }
            },(button,matrices,mousex,mousey)->{
                renderTooltip(matrices,new TextComponent("Next recipe"),mousex,mousey);
            },new TextComponent("")));
            addRenderableWidget(new ImageButton(relX + 164, relY + 33, 16, 16, 0, 16, 0, BUTTONS, 16, 32, (button) -> {
                if ((currentPage - 1 >= 0)) {
                    currentPage -= 1;
                }
            },(button,matrices,mousex,mousey)->{
                renderTooltip(matrices,new TextComponent("Previous recipe"),mousex,mousey);
            },new TextComponent("")));
        }
        addRenderableWidget(new ItemStackButton(relX+74+xoffs,relY+9,12,12,(button)->{minecraft.setScreen(new SolarLexiconRecipesScreen());}, Items.CRAFTING_TABLE.getDefaultInstance(),0.7f,false));
        addRenderableWidget(new ItemStackButton(relX+61+xoffs,relY+9,12,12,(button)->{
            Minecraft mc = Minecraft.getInstance();
            SolarLexicon lexicon = (SolarLexicon) mc.player.getMainHandItem().getItem();
            lexicon.currentSavedScreen = this;
            minecraft.setScreen(null);
        }, Items.WRITABLE_BOOK.getDefaultInstance(),0.7f,false));
        super.init();
    }


    @Override
    public void render(PoseStack matrices, int mousex, int mousey, float partialTicks) {
        ClientHelpers.bindText(MAIN_SCREEN);
        blit(matrices, relX, relY, 0, 0, 256, 256, 256, 256);
        PreparedShapedRecipe currentRecipe = recipes.get(currentPage);
        if (currentRecipe != null){
            List<Item> pattern = currentRecipe.getPattern();

        }




        super.render(matrices, mousex, mousey, partialTicks);
    }

    private void renderItemAndTooltip(ItemStack toRender, int place1, int place2, int mousex, int mousey, PoseStack matrices, boolean last){
        if (!last) {
            minecraft.getItemRenderer().renderGuiItem(toRender, place1, place2);
        }else{
            ItemStack renderThis = recipes.get(currentPage).getOutput();
            minecraft.getItemRenderer().renderGuiItem(renderThis, place1, place2);
            minecraft.getItemRenderer().renderGuiItemDecorations(font,renderThis,place1,place2);
        }


        if (((mousex >= place1) && (mousex <= place1+16)) && ((mousey >= place2) && (mousey <= place2+16)) && !toRender.getItem().equals(Items.AIR)){
            matrices.pushPose();
            renderTooltip(matrices,toRender,mousex,mousey);
            matrices.popPose();
        }
    }


    private Item[][] dissolvePattern(InfusingCraftingRecipe recipe){
        String[] pat = recipe.getPattern();
        int rows = pat.length;
        int cols = pat[0].length();
        Item[][] r = new Item[3][3];

        for (int i = 0; i < 3; i ++ ){
            for (int g = 0; g < 3 ; g++){
                if (i < rows && g < cols){
                    r[i][g] = recipe.getDefinitions().get(pat[i].charAt(g));
                }else{
                    r[i][g] = null;
                }
            }
        }
        return r;
    }


}
