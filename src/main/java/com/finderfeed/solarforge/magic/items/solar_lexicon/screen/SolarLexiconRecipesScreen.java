package com.finderfeed.solarforge.magic.items.solar_lexicon.screen;

import com.finderfeed.solarforge.ClientHelpers;
import com.finderfeed.solarforge.local_library.helpers.RenderingTools;
import com.finderfeed.solarforge.SolarForge;
import com.finderfeed.solarforge.magic.items.solar_lexicon.structure.Book;
import com.finderfeed.solarforge.misc_things.IScrollable;
import com.finderfeed.solarforge.recipe_types.infusing_new.InfusingRecipe;
import com.finderfeed.solarforge.registries.items.ItemsRegister;
import com.finderfeed.solarforge.magic.items.solar_lexicon.unlockables.AncientFragment;
import com.finderfeed.solarforge.magic.items.solar_lexicon.unlockables.ProgressionHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SolarLexiconRecipesScreen extends Screen implements IScrollable {
    public final ResourceLocation MAIN_SCREEN = new ResourceLocation("solarforge","textures/gui/solar_lexicon_recipes_page.png");
    public final ResourceLocation FRAME = new ResourceLocation("solarforge","textures/misc/frame.png");
    public final ResourceLocation MAIN_SCREEN_SCROLLABLE = new ResourceLocation("solarforge","textures/gui/solar_lexicon_main_page_scrollablet.png");

//    public Map<BookEntry,List<AncientFragment>> map = new HashMap<>();
    private List<AncientFragment> FRAGMENTS = new ArrayList<>();
    private Book BOOK;
//    private Map<BookEntry,Integer[]> TO_DRAW = new HashMap<>();

    private boolean showNoFragmentsMessage = true;

    public IItemHandler handler;
    public final ItemStackButton goBack = new ItemStackButton(0,10,12,12,(button)->{minecraft.setScreen(new SolarLexiconScreen());}, SolarForge.SOLAR_FORGE_ITEM.get().getDefaultInstance(),0.7f,false);
    public final ItemStackButton nothing = new ItemStackButton(0,10,12,12,(button)->{}, Items.CRAFTING_TABLE.getDefaultInstance(),0.7f,false);
    public List<Runnable> postRender = new ArrayList<>();



//    public Point structures;

    public int scrollX;
    public int scrollY;
    public int prevscrollX;
    public int prevscrollY;
    public int relX;
    public int relY;

    @Override
    public void performScroll(int keyCode) {

        int scroll = 4;
        if ((keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_LEFT) || keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_A))
                && !(scrollX +scroll > 0)){
            scrollX+=scroll;
        } else if ((keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_UP) || keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_W)) && !(scrollY +scroll > 0)){
            scrollY+=scroll;
        }else if((keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_DOWN) || keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_S)) && !(scrollY -scroll < -700)){
            scrollY-=scroll;
        }else if ((keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_RIGHT) || keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_D)) && !(scrollX -scroll < -700)){
            scrollX-=scroll;
        }

        if (this.prevscrollX != scrollX){
            List<AbstractWidget> list = ClientHelpers.getScreenButtons(this);
            list.remove(goBack);
            list.remove(nothing);
            for (AbstractWidget a : list) {
                if (prevscrollX < scrollX) {
                    a.x += scroll;
                } else {
                    a.x -= scroll;
                }

            }
            this.prevscrollX = scrollX;
        }
        if (this.prevscrollY != scrollY){
            List<AbstractWidget> list = ClientHelpers.getScreenButtons(this);
            list.remove(goBack);
            list.remove(nothing);
            for (AbstractWidget a : list) {
                if (prevscrollY < scrollY) {

                    a.y += scroll;
                } else {

                    a.y -= scroll;
                }


            }
            this.prevscrollY = scrollY;
        }
    }

    @Override
    public int getCurrentScrollX() {
        return scrollX;
    }

    @Override
    public int getCurrentScrollY() {
        return scrollY;
    }

    public SolarLexiconRecipesScreen() {
        super(new TextComponent(""));
    }


    @Override
    protected void init() {
        super.init();


        FRAGMENTS.clear();
        handler = getLexiconInventory();
        collectFragments();

        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        int scale = (int) minecraft.getWindow().getGuiScale();
        this.relX = (width/scale - 183)/2-30;
        this.relY = (height - 218*scale)/2/scale;
        scrollX = 0;
        scrollY = 0;
        prevscrollX = 0;
        prevscrollY = 0;
        BOOK = new Book(relX+25,relY+25);
        Book.initializeBook(BOOK,FRAGMENTS);
        BOOK.init();
        BOOK.getButtons().forEach(this::addRenderableWidget);


        addRenderableWidget(goBack);
        addRenderableWidget(nothing);

        nothing.x = relX +207+35;
        nothing.y = relY + 184;
        goBack.x = relX +207+35;
        goBack.y = relY + 164;
    }

    private void collectFragments(){
        for (int i = 0;i < handler.getSlots();i++){
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.getItem() == ItemsRegister.INFO_FRAGMENT.get()){
                if (stack.getTagElement(ProgressionHelper.TAG_ELEMENT) != null) {
                    showNoFragmentsMessage = false;
                    AncientFragment frag = AncientFragment.getFragmentByID(stack.getTagElement(ProgressionHelper.TAG_ELEMENT).getString(ProgressionHelper.FRAG_ID));
                    if (frag != null){
                        FRAGMENTS.add(frag);
                    }
                }
            }
        }
    }
    @Override
    public void tick() {
        super.tick();
        if (this.BOOK != null){
            this.BOOK.tick();
        }
    }
    @Override
    public boolean isPauseScreen() {
        return false;
    }




    private boolean isButtonPressable(int x,int y){
        if (((x + 24 > relX+7) && (x  < relX+7+220)) && ((y + 24 > relY+7) && (y  < relY+7+193))){
            return true;
        }
        return false;
    }

    @Override
    public void render(PoseStack matrices, int mousex, int mousey, float partialTicks) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        int scale = (int)minecraft.getWindow().getGuiScale();
        GL11.glScissor(width/2-((113)*scale),height/2-(89*scale),(223*scale),190*scale);

        ClientHelpers.bindText(MAIN_SCREEN_SCROLLABLE);
        blit(matrices,relX,relY,0,0,256,256);

        ClientHelpers.bindText(FRAME);
        if (BOOK != null){
            BOOK.render(matrices);
        }
        super.render(matrices,mousex,mousey,partialTicks);
        if (showNoFragmentsMessage){
            drawString(matrices,font,"No fragments present :(",relX+20+scrollX,relY+40+scrollY,0xffffff);
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        ClientHelpers.bindText(MAIN_SCREEN);
        blit(matrices,relX,relY,0,0,256,256);

        goBack.render(matrices,mousex,mousey,partialTicks);
        nothing.render(matrices,mousex,mousey,partialTicks);

        this.renderables.forEach((widget)->{

                if (widget instanceof AbstractWidget button) {
                    boolean a = isButtonPressable(button.x, button.y);
                    button.active = a;
                    button.visible = a;
                }

        });
        goBack.active = true;
        nothing.active = true;
        goBack.visible = true;
        nothing.visible = true;

        for (Runnable r : postRender){
            r.run();
        }
        postRender.clear();

    }

    public IItemHandler getLexiconInventory(){
        return Minecraft.getInstance().player.getMainHandItem().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
    }

}
