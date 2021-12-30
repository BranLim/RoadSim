package com.layhill.roadsim.gameengine.guis;

import com.layhill.roadsim.gameengine.graphics.gl.objects.GLModel;

import java.util.ArrayList;
import java.util.List;

public class GuiQuad {

    private GLModel quad;
    private List<GuiTexture> textures = new ArrayList<>();

    public GuiQuad(GLModel quad) {
        this.quad = quad;
    }

    public void addTexture(GuiTexture guiTexture){
        textures.add(guiTexture);
    }

    public void removeTexture(GuiTexture guiTexture){
        textures.remove(guiTexture);
    }


}
