package com.yuedong.opengl_series

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL
import javax.microedition.khronos.opengles.GL10

class MainActivity : AppCompatActivity() {
    private lateinit var glSurfaceView: GLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Play with points"
        glSurfaceView = GLSurfaceView(this)
        setContentView(glSurfaceView)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(PointsRender)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY  //主动刷新
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    companion object PointsRender : GLSurfaceView.Renderer {

        private const val TAG = "PointsRender"
        private const val VERTEX_SHADER = "void main() {\n" + "gl_Position = vec4(0.0, 0.0, 0.0, 1.0);\n" + "gl_PointSize = 100.0;\n" + "}\n"    //右手坐标系，中心点在屏幕中点
        private const val FRAGMENT_SHADER = "void main() {\n" + "gl_FragColor = vec4(1., 1., 0.0, 0.5);\n" + "}\n"
        private var mGLProgram: Int = -1

        override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
            GLES20.glClearColor(0f, 0f, 0f, 1f)   //设置背景
            val vsh = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
            GLES20.glShaderSource(vsh, VERTEX_SHADER)
            GLES20.glCompileShader(vsh)

            val fsh = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
            GLES20.glShaderSource(fsh, FRAGMENT_SHADER)
            GLES20.glCompileShader(fsh)

            mGLProgram = GLES20.glCreateProgram()
            GLES20.glAttachShader(mGLProgram, vsh)
            GLES20.glAttachShader(mGLProgram, fsh)
            GLES20.glLinkProgram(mGLProgram)    // 做链接，可以理解为把两种shader进行融合，做好投入使用的最后准备工作

            GLES20.glValidateProgram(mGLProgram)// 让OpenGL来验证一下我们的shader program，并获取验证的状态

            val status = IntArray(1)
            GLES20.glGetProgramiv(mGLProgram, GLES20.GL_VALIDATE_STATUS, status, 0)
            Log.d(TAG, "validate shader program: " + GLES20.glGetProgramInfoLog(mGLProgram))
        }

        override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
            GLES20.glViewport(0, 0, p1, p2)   // 参数是left, top, width, height
        }

        override fun onDrawFrame(p0: GL10?) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)  // 清除颜色缓冲区，因为我们要开始新一帧的绘制了，所以先清理，以免有脏数据。
            GLES20.glUseProgram(
                mGLProgram)             // 告诉OpenGL，使用我们在onSurfaceCreated里面准备好了的shader program来渲染
            GLES20.glDrawArrays(GLES20.GL_POINTS, 0,
                1)  // 开始渲染，发送渲染点的指令， 第二个参数是offset，第三个参数是点的个数。目前只有一个点，所以是1。
        }

    }
}