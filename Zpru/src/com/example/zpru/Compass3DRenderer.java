package com.example.zpru;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class Compass3DRenderer implements GLSurfaceView.Renderer {
    Context mContext;

    // a raw buffer to hold indices
    ShortBuffer _indexBuffer;    
    // raw buffers to hold the vertices
    FloatBuffer _vertexBuffer0;
    FloatBuffer _vertexBuffer1;
    FloatBuffer _vertexBuffer2;
    FloatBuffer _vertexBuffer3;
    FloatBuffer _vertexBuffer4;
    FloatBuffer _vertexBuffer5;
    int _numVertices = 3; //standard triangle vertices = 3

    FloatBuffer _textureBuffer0123;



    //private FloatBuffer _light0Position;
    //private FloatBuffer _light0Ambient;
    float _light0Position[] = new float[]{10.0f, 10.0f, 10.0f, 0.0f};
    float _light0Ambient[] = new float[]{0.05f, 0.05f, 0.05f, 1.0f};
    float _light0Diffuse[] = new float[]{0.5f, 0.5f, 0.5f, 1.0f};
    float _light0Specular[] = new float[]{0.7f, 0.7f, 0.7f, 1.0f};
    float _matAmbient[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
    float _matDiffuse[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };




    private float _angleX=0f;
    private float _angleY=0f;
    private float _angleZ=0f;


    Compass3DRenderer(Context context){
        super();
        mContext = context;
    }

    public void setAngleX(float angle) {
        _angleX = angle;
    }

    public void setAngleY(float angle) {
        _angleY = angle;
    }

    public void setAngleZ(float angle) {
        _angleZ = angle;
    }

    FloatBuffer InitFloatBuffer(float[] src){
        ByteBuffer bb = ByteBuffer.allocateDirect(4*src.length);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer inBuf = bb.asFloatBuffer();
        inBuf.put(src);
        return inBuf;
    }

    ShortBuffer InitShortBuffer(short[] src){
        ByteBuffer bb = ByteBuffer.allocateDirect(2*src.length);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer inBuf = bb.asShortBuffer();
        inBuf.put(src);
        return inBuf;
    }

    //Init data for our rendered pyramid
    private void initTriangles() {

        //Side faces triangles
        float[] coords = {
            -0.25f, -0.5f, 0.25f,
            0.25f, -0.5f, 0.25f,
            0f, 0.5f, 0f
        };

        float[] coords1 = {
            0.25f, -0.5f, 0.25f,
            0.25f, -0.5f, -0.25f,
            0f, 0.5f, 0f
        };

        float[] coords2 = {
            0.25f, -0.5f, -0.25f,
            -0.25f, -0.5f, -0.25f,
            0f, 0.5f, 0f
        };

        float[] coords3 = {
            -0.25f, -0.5f, -0.25f,
            -0.25f, -0.5f, 0.25f,
            0f, 0.5f, 0f
        };

        //Base triangles
        float[] coords4 = {
            -0.25f, -0.5f, 0.25f,
            0.25f, -0.5f, -0.25f,
            0.25f, -0.5f, 0.25f
        };

        float[] coords5 = {
            -0.25f, -0.5f, 0.25f,
            -0.25f, -0.5f, -0.25f, 
            0.25f, -0.5f, -0.25f
        };


        float[] textures0123 = {
                // Mapping coordinates for the vertices (UV mapping CW)
                0.0f, 0.0f,     // bottom left                    
                1.0f, 0.0f,     // bottom right
                0.5f, 1.0f,     // top ctr              
        };


        _vertexBuffer0 = InitFloatBuffer(coords);
        _vertexBuffer0.position(0);

        _vertexBuffer1 = InitFloatBuffer(coords1);
        _vertexBuffer1.position(0);    

        _vertexBuffer2 = InitFloatBuffer(coords2);
        _vertexBuffer2.position(0);

        _vertexBuffer3 = InitFloatBuffer(coords3);
        _vertexBuffer3.position(0);

        _vertexBuffer4 = InitFloatBuffer(coords4);
        _vertexBuffer4.position(0);

        _vertexBuffer5 = InitFloatBuffer(coords5);
        _vertexBuffer5.position(0);

        _textureBuffer0123 = InitFloatBuffer(textures0123);
        _textureBuffer0123.position(0);

        short[] indices = {0, 1, 2};
        _indexBuffer = InitShortBuffer(indices);        
        _indexBuffer.position(0);

    }


    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        gl.glEnable(GL10.GL_CULL_FACE); // enable the differentiation of which side may be visible 
        gl.glShadeModel(GL10.GL_SMOOTH);

        gl.glFrontFace(GL10.GL_CCW); // which is the front? the one which is drawn counter clockwise
        gl.glCullFace(GL10.GL_BACK); // which one should NOT be drawn

        initTriangles();

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    public void onDrawFrame(GL10 gl) {


        gl.glPushMatrix();

        gl.glClearColor(0, 0, 0, 1.0f); //clipping backdrop color
        // clear the color buffer to show the ClearColor we called above...
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // set rotation       
        gl.glRotatef(_angleY, 0f, 1f, 0f); //ROLL
        gl.glRotatef(_angleX, 1f, 0f, 0f); //ELEVATION
        gl.glRotatef(_angleZ, 0f, 0f, 1f); //AZIMUTH

        //Draw our pyramid

        //4 side faces
        gl.glColor4f(0.5f, 0f, 0f, 0.5f);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer0);
        gl.glDrawElements(GL10.GL_TRIANGLES, _numVertices, GL10.GL_UNSIGNED_SHORT, _indexBuffer);

        gl.glColor4f(0.5f, 0.5f, 0f, 0.5f);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer1);
        gl.glDrawElements(GL10.GL_TRIANGLES, _numVertices, GL10.GL_UNSIGNED_SHORT, _indexBuffer);

        gl.glColor4f(0f, 0.5f, 0f, 0.5f);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer2);
        gl.glDrawElements(GL10.GL_TRIANGLES, _numVertices, GL10.GL_UNSIGNED_SHORT, _indexBuffer);

        gl.glColor4f(0f, 0.5f, 0.5f, 0.5f);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer3);
        gl.glDrawElements(GL10.GL_TRIANGLES, _numVertices, GL10.GL_UNSIGNED_SHORT, _indexBuffer);

        //Base face
        gl.glColor4f(0f, 0f, 0.5f, 0.5f);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer4);
        gl.glDrawElements(GL10.GL_TRIANGLES, _numVertices, GL10.GL_UNSIGNED_SHORT, _indexBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer5);
        gl.glDrawElements(GL10.GL_TRIANGLES, _numVertices, GL10.GL_UNSIGNED_SHORT, _indexBuffer);

        gl.glPopMatrix();
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        gl.glViewport(0, 0, w, h);
        gl.glViewport(0, 0, w, h);

    }



}