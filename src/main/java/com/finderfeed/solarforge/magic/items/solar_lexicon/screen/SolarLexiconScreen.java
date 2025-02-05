package com.finderfeed.solarforge.magic.items.solar_lexicon.screen;

import com.finderfeed.solarforge.ClientHelpers;
import com.finderfeed.solarforge.Helpers;
import com.finderfeed.solarforge.SolarForge;
import com.finderfeed.solarforge.client.particles.screen.RuneTileParticle;
import com.finderfeed.solarforge.local_library.client.particles.ScreenParticlesRenderHandler;
import com.finderfeed.solarforge.local_library.helpers.RenderingTools;
import com.finderfeed.solarforge.magic.items.solar_lexicon.achievements.Progression;
import com.finderfeed.solarforge.misc_things.IScrollable;
import com.finderfeed.solarforge.magic.items.solar_lexicon.achievements.achievement_tree.ProgressionTree;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;


import com.mojang.math.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SolarLexiconScreen extends Screen implements IScrollable {
    private int ticker = 0;
    private int OFFSET_X = 40;
    private int OFFSET_Y = 40;

    public final ResourceLocation MAIN_SCREEN = new ResourceLocation("solarforge","textures/gui/solar_lexicon_main_page.png");
    public final ResourceLocation FRAME = new ResourceLocation("solarforge","textures/misc/frame.png");
    public final ResourceLocation QMARK = new ResourceLocation("solarforge","textures/misc/question_mark.png");
    public final ResourceLocation MAIN_SCREEN_SCROLLABLE = new ResourceLocation("solarforge","textures/gui/solar_lexicon_main_page_scrollablet.png");
    public String currentText = "";
    private String afterTxt = "";
    public  int relX;
    public  int relY;
    public final ProgressionTree tree = ProgressionTree.INSTANCE;
    public Component currAch;
    public Progression currentProgression = null;
    private List<Runnable> postLinesRender = new ArrayList<>();

    public int prevscrollX = 0;
    public int prevscrollY = 0;
    public int scrollX = 0;
    public int scrollY = 0;

    public ItemStackButton stagesPage = new ItemStackButton(relX+100,relY + 20,12,12,(button)->{minecraft.setScreen(new StagesScreen());},Items.BEACON.getDefaultInstance(),0.7f,false);

    public ItemStackButton toggleRecipesScreen = new ItemStackButton(relX+100,relY+100,12,12,(button)->{minecraft.setScreen(new SolarLexiconRecipesScreen());}, Items.CRAFTING_TABLE.getDefaultInstance(),0.7f,false);
    public ItemStackButton justForge = new ItemStackButton(relX+100,relY+100,12,12,(button)->{}, SolarForge.SOLAR_FORGE_ITEM.get().getDefaultInstance(),0.7f,false);

    public HashMap<Integer,List<Progression>> map = new HashMap<>();

    public SolarLexiconScreen() {
        super(new TextComponent("screen_solar_lexicon"));
        this.width = 256;
        this.height = 256;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void performScroll(int keyCode) {

        if ((keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_LEFT) || keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_A))
                && !(scrollX +4 > 0)){
            scrollX+=4;
        } else if ((keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_UP) || keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_W))
                && !(scrollY +4 > 0)){
            scrollY+=4;
        }else if((keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_DOWN) || keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_S))
                && !(scrollY -4 < -340)){
            scrollY-=4;
        }else if ((keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_RIGHT) || keyCode == GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_D))
                && !(scrollX -4 < -80)){
            scrollX-=4;
        }
        if (this.prevscrollX != scrollX){
            List<AbstractWidget> list = ClientHelpers.getScreenButtons(this);
            list.remove(toggleRecipesScreen);
            list.remove(justForge);
            list.remove(stagesPage);
            for (AbstractWidget a : list) {
                if (prevscrollX < scrollX) {
                    a.x += 4;
                } else {
                    a.x -= 4;
                }

            }
            this.prevscrollX = scrollX;
        }
        if (this.prevscrollY != scrollY){
            List<AbstractWidget> list = ClientHelpers.getScreenButtons(this);
            list.remove(toggleRecipesScreen);
            list.remove(justForge);
            list.remove(stagesPage);
            for (AbstractWidget a : list) {
                if (prevscrollY < scrollY) {

                    a.y += 4;
                } else {

                    a.y -= 4;
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




    private void initMap(int tiersCount){
        map.clear();
        for (int i = 1;i < tiersCount+1;i++){
            map.put(i,new ArrayList<>());
        }
    }


    @Override
    protected void init() {
        super.init();

        this.prevscrollX = 0;
        this.prevscrollY = 0;
        this.scrollX = 0;
        this.scrollY = 0;
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        int scale = (int) minecraft.getWindow().getGuiScale();
        this.relX = (width/scale - 183)/2 - 30;
        this.relY = (height - 218*scale)/2/scale;
        initMap(15);


        currentText = "Select Progression";

        currAch = new TextComponent("");
        int offsetX = 0;
        int offsetY = 0;

        for (Progression a : Progression.allProgressions){
            int tier = a.getAchievementTier();
            map.get(tier).add(a);
            offsetX = (map.get(tier).size()-1) * OFFSET_X;


            offsetY = (a.getAchievementTier() -1)* OFFSET_Y;
            boolean c = Helpers.canPlayerUnlock(a,minecraft.player);
            addRenderableWidget(new ItemStackButton(relX+12+offsetX,relY+12+offsetY,16,16,(button)->{


                if (Helpers.hasPlayerUnlocked(a,Minecraft.getInstance().player)){
                    currentText = a.getPretext().getString();
                    afterTxt = a.afterText.getString();
                }else if (Helpers.canPlayerUnlock(a,Minecraft.getInstance().player)){
                    currentText = a.getPretext().getString();
                    afterTxt = "???";
                }
                else{
                    afterTxt = "???";
                    currentText = "???";
                }
                currAch = a.getTranslation();
                currentProgression = a;

                },a.getIcon(),1,c));
        }
        addRenderableWidget(toggleRecipesScreen);
        addRenderableWidget(justForge);
        addRenderableWidget(stagesPage);
        toggleRecipesScreen.x = relX +207+35;
        toggleRecipesScreen.y = relY + 184;
        justForge.x = relX +207+35;
        justForge.y = relY + 164;
        stagesPage.x = relX + 207 + 35;
        stagesPage.y = relY + 13;
    }



    @Override
    public void tick() {
        super.tick();
        if (ticker++ < 5) return;
        ticker = 0;
        List<AbstractWidget> list = ClientHelpers.getScreenButtons(this);
        list.remove(justForge);
        list.remove(stagesPage);
        list.remove(toggleRecipesScreen);
        for (AbstractWidget widget : list){
            widget.active = widget.x >= relX - 5 && widget.x <= relX + 230 && widget.y >= relY - 5 && widget.y <= relY + 115;
        }

        RuneTileParticle particle = new RuneTileParticle(60, relX, relY, 0, 0.5, 0, 0, 255, 255, 255, 255);
        particle.setSize(20);
        ScreenParticlesRenderHandler.addParticle(particle);

    }

    @Override
    public void render(PoseStack matrices, int mousex, int mousey, float partialTicks) {

        matrices.pushPose();

        int stringColor = 0xee2222;

        ClientHelpers.bindText(MAIN_SCREEN_SCROLLABLE);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        int scale = (int)minecraft.getWindow().getGuiScale();

        GL11.glScissor(width/2-((30+83)*scale),height/2-(6*scale),((188+35)*scale),107*scale);
        blit(matrices,relX,relY,0,0,256,256);

        ClientHelpers.bindText(FRAME);
        for (Progression a : tree.PROGRESSION_TREE.keySet()) {
            Point first = new Point(relX+scrollX+21+map.get(a.getAchievementTier()).indexOf(a)*OFFSET_X,relY+scrollY+21+(a.getAchievementTier()-1)*OFFSET_Y);
            for (Progression b : tree.getAchievementRequirements(a)){
                Point second = new Point(relX+scrollX+21+map.get(b.getAchievementTier()).indexOf(b)*OFFSET_X,relY+scrollY+21+(b.getAchievementTier()-1)*OFFSET_Y);
                if (currentProgression != null && (currentProgression == b || currentProgression == a) ) {
                    postLinesRender.add(()->{
                        drawLine(matrices, first.x, first.y, second.x, second.y,255,255,255);
                    });
                }
                else {
                    drawLine(matrices, first.x, first.y, second.x, second.y,50,50,50);
                }

            }

//            blit(matrices,first.x-8,first.y-8,0,0,16,16,16,16);

        }

        postLinesRender.forEach(Runnable::run);
        postLinesRender.clear();
        for (Progression a : tree.PROGRESSION_TREE.keySet()) {
            Point first = new Point(relX+scrollX+18+map.get(a.getAchievementTier()).indexOf(a)*OFFSET_X,relY+scrollY+18+(a.getAchievementTier()-1)*OFFSET_Y);
            blit(matrices,first.x-8,first.y-8,0,0,20,20,20,20);
        }
        List<AbstractWidget> listButtons = ClientHelpers.getScreenButtons(this);
        listButtons.remove(toggleRecipesScreen);
        listButtons.remove(justForge);
        listButtons.remove(stagesPage);

        for (AbstractWidget a :listButtons){
            ItemStackButton button = (ItemStackButton) a;
            if (button.qMark){
                button.render(matrices,mousex,mousey,partialTicks);
            }
        }
        ClientHelpers.bindText(QMARK);
        for (AbstractWidget a :listButtons){
            ItemStackButton button = (ItemStackButton) a;
            if (!button.qMark){

                blit(matrices,button.x,button.y,0,0,16,16,16,16);
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        ClientHelpers.bindText(MAIN_SCREEN);
        blit(matrices,relX,relY,0,0,256,256);
        drawString(matrices,minecraft.font,currAch,relX+12,relY+124,stringColor);
        if (currentText != null && (currentText.length() != 0)) {
            List<String> toRender1 = RenderingTools.splitString(currentText, 40);
            int y = 0;
            for (String s : toRender1) {
                drawString(matrices, font, s, relX + 12, relY + 134 + y, stringColor);
                y += 8;
            }
        }
        if ((afterTxt != null) && (afterTxt.length() != 0)) {
            List<String> toRender2 = RenderingTools.splitString(afterTxt, 40);
            int yOffset = (toRender2.size()-1)*8;
            int y = 0;
            for (String s : toRender2) {
                drawString(matrices, font, s, relX + 12, relY + 187 + y - yOffset, stringColor);
                y += 8;
            }
        }

        toggleRecipesScreen.render(matrices,mousex,mousey,partialTicks);
        justForge.render(matrices,mousex,mousey,partialTicks);
        stagesPage.render(matrices,mousex,mousey,partialTicks);
        matrices.popPose();



    }



    private void drawLine(PoseStack stack,int x1,int y1,int x2,int y2,int red,int green,int blue){

        GlStateManager._disableTexture();
        GlStateManager._depthMask(false);
        GlStateManager._disableCull();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        Tesselator var4 = RenderSystem.renderThreadTesselator();
        BufferBuilder var5 = var4.getBuilder();
        RenderSystem.lineWidth(2.5F);
        var5.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        Vector3d vector3f = new Vector3d(x2-x1,y2-y1,0);
        Vector3d vector3f2 = new Vector3d(x1-x2,y1-y2,0);
        var5.vertex(x1, y1, 0.0D).color(red,green,blue, 255).normal((float)vector3f.x, (float)vector3f.y, 0.0F).endVertex();
        var5.vertex((double)x2, y2, 0.0D).color(red, green, blue, 255).normal((float)vector3f2.x, (float)vector3f2.y, 0.0F).endVertex();


        var4.end();

        GlStateManager._enableCull();
        GlStateManager._depthMask(true);
        GlStateManager._enableTexture();
    }
}
