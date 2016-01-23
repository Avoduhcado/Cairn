package core;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.util.ResourceLoader;

import core.entities_new.Entity;
import core.render.DrawUtils;
import core.setups.GameSetup;
import core.setups.Stage_new;
import core.utilities.MathFunctions;
import core.utilities.mouse.MouseInput;
import core.utilities.text.Text;

public class Camera {
	
	/** Default Window width */
	private final int WIDTH = 1280;
	/** Default Window height */
	private final int HEIGHT = 720;
	/** Current Window width */
	public int displayWidth = WIDTH;
	/** Current Window height */
	public int displayHeight = HEIGHT;
	/** Target FPS for application to run at */
	public static final int TARGET_FPS = 60;
	/** Window aspect ratio */
	public static final float ASPECT_RATIO = 0.667f;
	/** Maximum draw distance for entities */
	public static int DRAW_DISTANCE = 5000;
	
	/** View Matrix */
	//private Matrix3f view = new Matrix3f();

	/** Current Camera frame */
	public Rectangle2D frame = new Rectangle2D.Double(0, 0, WIDTH, HEIGHT);
	private Vector2f frameSpeed = new Vector2f();
	private Rectangle2D frameBorder = new Rectangle2D.Float();
	
	/** World scale variable */
	private float scale = 1f;
	/** VSync status */
	private boolean vsync;
	
	private Entity focus;
	
	private Vector4f fillColor = new Vector4f(1f, 1f, 1f, 1f);
	
	/** Current duration of fade effect */
	private float fadeTime;
	/** Total duration of fade effect */
	private float fadeDuration;
	/** Current fade alpha value */
	private float fadeValue;

	/** Pan variables */
	private float panTime;
	private float panDuration;
	private Vector2f panValue = new Vector2f(0, 0);
	private Vector4f panLimit = new Vector4f(0, 0, 0, 0);
	private float panDelay;
	
	/** Rotate variables */
	private float rotateTime;
	private float rotateDuration;
	private Vector3f rotation = new Vector3f(0, 0, 0);
	
	/** Shake variables */
	private float shakeTotal;
	private float shakeTime;
	private float shakePower;
	private Vector2f shakeOffset;
	
	/** Zoom variables */
	private float zoomTime;
	private float zoomDuration;
	private Vector2f zoom = new Vector2f(0, 0);
	
	/** Determine whether window should upscale or increase view distance on resize */
	private boolean upscale = true;
	
	/** Screen singleton */
	private static Camera camera;
	
	/** Initialize Screen singleton */
	public static void init() {
		camera = new Camera();
	}
	
	/** Return Screen singleton */
	public static Camera get() {
		return camera;
	}
	
	public Camera() {
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			updateHeader();
			try {
				Display.setIcon(loadIcon(System.getProperty("resources") + "/sprites/ui/Icon.png"));
			} catch (IOException e) {
				System.out.println("Failed to load icon");
			}
			Display.setResizable(true);
			Display.create(new PixelFormat(0, 8, 1));
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, displayWidth, displayHeight, 0, -1, 1);
			GL11.glViewport(0, 0, displayWidth, displayHeight);
			GL11.glClearColor(0f, 0f, 0f, 0f);
			GL11.glClearStencil(0);
		} catch (LWJGLException e) {
			System.err.println("Could not create display.");
		}
		
		frame = new Rectangle2D.Double(0, 0, WIDTH, HEIGHT);
		setFade(-1f);
		
		//view.m20 = 1;
		//view.m21 = 1;
		//view.m22 = 1;
	}
	
	public static ByteBuffer[] loadIcon(String ref) throws IOException {
        InputStream fis = ResourceLoader.getResourceAsStream(ref);
        try {
            PNGDecoder decoder = new PNGDecoder(fis);
            ByteBuffer bb = ByteBuffer.allocateDirect(decoder.getWidth()*decoder.getHeight()*4);
            decoder.decode(bb, decoder.getWidth()*4, PNGDecoder.RGBA);
            bb.flip();
            ByteBuffer[] buffer = new ByteBuffer[1];
            buffer[0] = bb;
            return buffer;
        } finally {
            fis.close();
        }
    }
	
	public void update() {
		Display.update();
		Display.sync(TARGET_FPS);
		
		if(frameSpeed.length() != 0) {
			frame.setFrame(frame.getX() + frameSpeed.x, frame.getY() + frameSpeed.y, frame.getWidth(), frame.getHeight());
			frameSpeed.set(0, 0);
		}
		
		if(resized())
			resize();
	}
	
	public void updateHeader() {
		Display.setTitle(Theater.title + "  FPS: " + Theater.fps + " " + Theater.version);
	}
	
	/*private void applyViewMatrix() {
		GL11.glTranslated(frame.getWidth() / 2f, frame.getHeight() / 2f, 0);
		GL11.glScalef(view.m20, view.m21, view.m22);
		GL11.glRotatef(view.m10, 0, 0, 1f);
		GL11.glTranslated(-frame.getWidth() / 2f, -frame.getHeight() / 2f, 0);
		
		GL11.glTranslatef(view.m00, view.m01, view.m02);
	}*/
	
	public void draw(GameSetup setup) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		DrawUtils.fillColor(fillColor.x, fillColor.y, fillColor.z, fillColor.w);
		
		//applyViewMatrix();
		
		// TODO Make a matrix dawg
		// Zoom in/out camera
		zoom();
		// Shake screen
		shake();
		// Pan screen
		pan();
		// Rotate screen
		rotate();
		
		// Draw current game setup
		setup.draw();
						
		// Negate screen shake
		settle();
		
		//DrawUtils.setColor(new Vector3f(1f, 0f, 1f));
		//DrawUtils.drawRect((float) frame.getX(), (float) frame.getY(), frame);

		// Reload identity to draw UI
		GL11.glLoadIdentity();
		
		setup.drawUI();
		
		// Process fading
		fade();
		
		if(Theater.get().paused) {
			Text.getDefault().setStill(true);
			Text.getDefault().setCentered(true);
			Text.getDefault().drawString("Paused", getDisplayWidth(0.5f), getDisplayHeight(0.5f));
		}
		
		// Draw debug info
		if(Theater.get().debug) {
			Text.getFont("DEBUG").setStill(true);
			//Text.getFont("DEBUG").setSize(0.3f);
			Text.getFont("DEBUG").drawString("Current Setup: " + Theater.get().getSetup().getClass().getName(), 15, 15);
			
			Text.getFont("DEBUG").setStill(true);
			//Text.getFont("DEBUG").setSize(0.3f);
			Text.getFont("DEBUG").drawString("Avogine v" + Theater.AVOGINE_VERSION, 15, 45);
			
			Text.getFont("DEBUG").setStill(true);
			//Text.getFont("DEBUG").setSize(0.3f);
			Text.getFont("DEBUG").drawString("Scene scale: " + scale, 15, 75);
			
			Text.getFont("DEBUG").setStill(true);
			Text.getFont("DEBUG").setColor(Color.lightGray);
			Text.getFont("DEBUG").drawString(MouseInput.getScreenMouseX() + ", " + MouseInput.getScreenMouseY(),
					MouseInput.getMouseX(), MouseInput.getMouseY() - 32);
		}
	}
	
	public boolean getUpscale() {
		return upscale;
	}
	
	public void setUpscale(boolean upscale) {
		this.upscale = upscale;
	}
	
	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void setZoom(float duration, float change) {
		zoomTime = 0;
		zoomDuration = duration;
		zoom.x = scale;
		zoom.y = change;
	}

	public boolean isZooming() {
		return zoomTime != 0;
	}

	public void zoomTo(float duration, float target) {
		zoomTime = 0;
		zoomDuration = duration;
		zoom.x = scale;
		zoom.y = target - scale;
	}
	
	public void zoom() {
		if(zoomDuration != 0) {
			zoomTime = MathFunctions.clamp(zoomTime + Theater.getDeltaSpeed(0.025f), 0, zoomDuration);
			
			scale = MathFunctions.linearTween(zoomTime, zoom.x, zoom.y, zoomDuration);
			if(zoomTime >= zoomDuration) {
				zoomDuration = 0;
			}
		}
		
		GL11.glTranslated(frame.getWidth() / 2f, frame.getHeight() / 2f, 0);
		GL11.glScalef(scale, scale, 1f);
		GL11.glTranslated(-frame.getWidth() / 2f, -frame.getHeight() / 2f, 0);
	}

	public boolean resized() {
		if(Display.getWidth() != displayWidth || Display.getHeight() != displayHeight)
			return true;
		
		return false;
	}
	
	public void resize() {
		displayWidth = Display.getWidth();
		displayHeight = Display.getHeight();
		/*if((double) displayWidth / (double) displayHeight != (double) WIDTH / (double) HEIGHT) {
			int aspectHeight = (int) (displayWidth / ((double) WIDTH / (double) HEIGHT));
			System.out.println(aspectHeight);
			GL11.glViewport(0, ((Display.getHeight() - aspectHeight) / 2), displayWidth, aspectHeight);
			frame = new Rectangle2D.Double(frame.getX(), frame.getY(), displayWidth, aspectHeight);
		} else {
			GL11.glViewport(0, 0, displayWidth, displayHeight);
			frame = new Rectangle2D.Double(frame.getX(), frame.getY(), displayWidth, displayHeight);
		}*/
		GL11.glViewport(0, 0, displayWidth, displayHeight);
		//frame = new Rectangle2D.Double(frame.getX(), frame.getY(), displayWidth, displayHeight);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		if(upscale) {
			// Upscale
			GL11.glOrtho(0, WIDTH, HEIGHT, 0, -1, 1);
		} else {
			// Increase view window
			GL11.glOrtho(0, displayWidth, displayHeight, 0, -1, 1);
			Theater.get().getSetup().resizeRefresh();
		}
	}
	
	public Entity getFocus() {
		return focus;
	}
	
	public void setFocus(Entity focus) {
		this.focus = focus;
		centerOn();
	}
	
	public Vector4f getFillColor() {
		return fillColor;
	}

	public void setFillColor(Vector4f fillColor) {
		this.fillColor = fillColor;
	}

	public Vector2f getFrameSpeed() {
		return frameSpeed;
	}
	
	public void follow() {
		if(focus.getBody().getLinearVelocity().length() != 0) {
			// Always centered movement
			//frameSpeed.set(focus.getBody().getLinearVelocity().mul(0.5f).x, focus.getBody().getLinearVelocity().mul(0.5f).y);
			
			// Frame border movement
			frameBorder.setFrame(frame.getX() + (frame.getWidth() * 0.45f), frame.getY() + (frame.getHeight() * 0.45f), 
					frame.getWidth() * 0.1f, frame.getHeight() * 0.25f);
			
			switch(frameBorder.outcode(focus.getBody().getPosition().x * Stage_new.SCALE_FACTOR,
					focus.getBody().getPosition().y * Stage_new.SCALE_FACTOR)) {
			case Rectangle2D.OUT_RIGHT | Rectangle2D.OUT_BOTTOM:
			case Rectangle2D.OUT_RIGHT | Rectangle2D.OUT_TOP:
			case Rectangle2D.OUT_LEFT | Rectangle2D.OUT_BOTTOM:
			case Rectangle2D.OUT_LEFT | Rectangle2D.OUT_TOP:
				frameSpeed.set(focus.getBody().getLinearVelocity().mul(0.5f).x, focus.getBody().getLinearVelocity().mul(0.5f).y);
				break;
			case Rectangle2D.OUT_RIGHT:
			case Rectangle2D.OUT_LEFT:
				frameSpeed.setX(focus.getBody().getLinearVelocity().mul(0.5f).x);
				break;
			case Rectangle2D.OUT_BOTTOM:
			case Rectangle2D.OUT_TOP:
				frameSpeed.setY(focus.getBody().getLinearVelocity().mul(0.5f).y);
				break;
			}
		}
	}
	
	public void centerOn() {
		if(focus != null) {
			frame.setFrameFromCenter(focus.getBody().getPosition().x * Stage_new.SCALE_FACTOR,
					focus.getBody().getPosition().y * Stage_new.SCALE_FACTOR,
					(focus.getBody().getPosition().x * Stage_new.SCALE_FACTOR) - (WIDTH / 2f),
					(focus.getBody().getPosition().y * Stage_new.SCALE_FACTOR) - (HEIGHT / 2f));
		}
	}

	public boolean isFullscreen() {
		return Display.isFullscreen();
	}
	
	public boolean setFullscreen(boolean fullscreen) {
		try {
			Display.setFullscreen(fullscreen);
			if(fullscreen) {
				Display.setDisplayMode(Display.getDesktopDisplayMode());
			} else if(!fullscreen && Display.isFullscreen()){
				Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean isVSyncEnabled() {
		return vsync;
	}
	
	public void setVSync(boolean vsync) {
		this.vsync = vsync;
		Display.setVSyncEnabled(vsync);
	}
	
	public boolean isPanning() {
		return this.panDuration > 0;
	}
	
	public void setPan(Vector2f panLimit, float panDuration, float panDelay) {
		this.panDuration = panDuration;
		panTime = 0;
		this.panLimit = new Vector4f(panLimit.x, panLimit.y, 0, 0);
		this.panDelay = panDelay;
	}
	
	public void pan() {
		if(isPanning()) {
			if(panDelay > 0) {
				panDelay = MathFunctions.clamp(panDelay - Theater.getDeltaSpeed(0.025f), 0, panDelay);
				if(panValue.x != 0) {
					panLimit.setX(panLimit.x + panValue.x);
					panLimit.setZ(panValue.x);
				}
				if(panValue.y != 0) {
					panLimit.setY(panLimit.y + panValue.y);
					panLimit.setW(panValue.y);
				}
			} else {
				if(panTime < panDuration) {
					panTime = MathFunctions.clamp(panTime + Theater.getDeltaSpeed(0.025f), 0, panDuration);
					if(panLimit.x != 0) {
						panValue.setX(MathFunctions.easeOut(panTime, panLimit.z, panLimit.x, panDuration));
					}
					if(panLimit.y != 0) {
						panValue.setY(MathFunctions.easeOut(panTime, panLimit.w, panLimit.y, panDuration));
					}
				}
			}
		} else if(panValue.length() != 0) {
			if(panLimit.length() == 0) {
				panLimit.set(panValue.x, panValue.y, 0, 0);
			}
			panTime = MathFunctions.clamp(panTime + Theater.getDeltaSpeed(0.025f), 0, 1);
			if(panLimit.x != 0) {
				panValue.setX(MathFunctions.easeIn(panTime, panLimit.x, -panLimit.x, 1f));
			}
			if(panLimit.y != 0) {
				panValue.setY(MathFunctions.easeIn(panTime, panLimit.y, -panLimit.y, 1f));
			}
		}
		
		frame.setFrame(frame.getX() + panValue.x, frame.getY() + panValue.y, frame.getWidth(), frame.getHeight());
	}
	
	public void setRotate(float rotateSpeed, float rotateMax, float rotateDuration) {
		this.rotation.set(0, rotateSpeed, rotateMax);
		this.rotateTime = 0;
		this.rotateDuration = rotateDuration;
	}
	
	public void rotate() {
		if(rotation.y != 0) {
			rotateTime = MathFunctions.clamp(rotateTime + Theater.getDeltaSpeed(0.025f), 0, rotateDuration);
			if((rotation.x > 0 && rotation.y < 0) || (rotation.x < 0 && rotation.y > 0)) {
				rotation.setX(MathFunctions.easeIn(rotateTime, (rotation.y > 0 ? -rotation.z : rotation.z),
						(rotation.y > 0 ? rotation.z : -rotation.z), rotateDuration));
			} else {
				rotation.setX(MathFunctions.easeOut(rotateTime, 0, (rotation.y > 0 ? rotation.z : -rotation.z), rotateDuration));
			}
			
			if(Math.abs(rotation.x) >= Math.abs(rotation.z)) {
				rotation.y = -rotation.y;
				rotateTime = 0;
				//rotateDuration = (float) ((Math.random() * 1.5f) + 2f);
			} else if(rotation.x == 0) {
				rotateTime = 0;
			}
			
			GL11.glTranslated(frame.getWidth() / 2f, frame.getHeight() / 2f, 0);
			GL11.glRotatef(rotation.x, 0, 0, 1f);
			GL11.glTranslated(-frame.getWidth() / 2f, -frame.getHeight() / 2f, 0);
		}
	}
	
	public boolean isShaking() {
		if(shakeTotal != 0)
			return (shakeTime / shakeTotal > 0.25f);
		
		return false;
	}
	
	public void setShake(Vector2f speed, float power, float duration) {
		shakeOffset = new Vector2f(speed);
		shakePower = power;
		shakeTotal = duration;
		shakeTime = shakeTotal;
	}
	
	public void shake() {
		if(shakeTotal != 0) {
			shakeOffset.set((-(float)Math.sin(shakeOffset.x) * shakePower) * (shakeTime / shakeTotal),
					(-(float)Math.sin(shakeOffset.y) * shakePower) * (shakeTime / shakeTotal));
			frame.setFrame(frame.getX() + shakeOffset.x, frame.getY() + shakeOffset.y, frame.getWidth(), frame.getHeight());
			
			shakeTime -= Theater.getDeltaSpeed(0.025f);
			if(shakeTime <= 0 || shakeOffset.length() < 0.01f) {
				shakeOffset.negate();
				frame.setFrame(frame.getX() + shakeOffset.x, frame.getY() + shakeOffset.y, frame.getWidth(), frame.getHeight());
				shakeTotal = 0;
			}
		}
	}
	
	public void settle() {
		if(shakeTotal != 0) {
			shakeOffset.negate();
			frame.setFrame(frame.getX() + shakeOffset.x, frame.getY() + shakeOffset.y, frame.getWidth(), frame.getHeight());
		}
		
		if(panValue.length() != 0) {
			frame.setFrame(frame.getX() - panValue.x, frame.getY() - panValue.y, frame.getWidth(), frame.getHeight());
		}
	}

	public boolean isFading() {
		return fadeValue != 0 && fadeValue != 1;
	}

	/**
	 * Set the screen to fade in or out over a specified time.
	 * 
	 * @param fadeDuration Time to fade, positive to fade out, negative to fade in, 0 for no fade
	 */
	public void setFade(float fadeDuration) {
		this.fadeDuration = fadeDuration;
		this.fadeTime = 0f;
	}
	
	public void fade() {
		if(fadeTime < Math.abs(fadeDuration)) {
			fadeTime = MathFunctions.clamp(fadeTime + Theater.getDeltaSpeed(0.025f), 0, Math.abs(fadeDuration));
			if(fadeDuration < 0) {
				fadeValue = MathFunctions.easeIn(fadeTime, 1, -1, Math.abs(fadeDuration));
			} else {
				fadeValue = MathFunctions.easeIn(fadeTime, 0, 1, Math.abs(fadeDuration));
			}
		}
		
		DrawUtils.fillColor(0f, 0f, 0f, fadeValue);
	}

	public float getFrameXScale() {
		if(upscale) {
			return (float) ((float) WIDTH / frame.getWidth());
			//return (float) (frame.getWidth() / fixedFrame.getWidth());
		} else {
			return 1f;
		}
	}
	
	public float getWindowXScale() {
		return (displayWidth / (float) WIDTH);
	}
	
	public float getFrameYScale() {
		if(upscale) {
			return (float) (HEIGHT / frame.getHeight());
			//return (float) (frame.getHeight() / fixedFrame.getHeight());
		} else {
			return 1f;
		}
	}
	
	public float getWindowYScale() {
		return (displayHeight / (float) HEIGHT);
	}
	
	public float getDisplayWidth() {
		//return displayWidth / getFrameXScale();
		return (float) (frame.getWidth() * getFrameXScale());
	}
	
	public float getDisplayHeight() {
		//return displayHeight / getFrameYScale();
		return (float) (frame.getHeight() * getFrameYScale());
	}
	
	public float getDisplayWidth(float mod) {
		//return (displayWidth * mod) / getFrameXScale();
		return (float) ((frame.getWidth() * mod) * getFrameXScale());
	}
	
	public float getDisplayHeight(float mod) {
		//return (displayHeight * mod) / getFrameYScale();
		return (float) ((frame.getHeight() * mod) * getFrameYScale());
	}
	
	public boolean isFocus() {
		return Display.isActive();
	}
	
	public boolean toBeClosed() {
		return Display.isCloseRequested();
	}
	
	public void close() {
		Display.destroy();
	}

}
