package com.example.zpru;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class Compass3DView extends GLSurfaceView {
    private Compass3DRenderer mRenderer;

    public Compass3DView(Context context) {
        super(context);
        mRenderer = new Compass3DRenderer(context);
        setRenderer(mRenderer);
    }

    public void changeAngles(float angle0, float angle1, float angle2){
        mRenderer.setAngleX(angle0);
        mRenderer.setAngleY(angle1);
        mRenderer.setAngleZ(angle2);
    }

}
