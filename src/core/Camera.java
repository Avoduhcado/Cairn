package core;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.util.ResourceLoader;

import core.entities.Entity;
import core.entities.interfaces.Mobile;
import core.render.DrawUtils;
import core.scene.collisions.Collidable;
import core.scene.collisions.HitMaps;
import core.scene.collisions.Slope;
import core.setups.GameSetup;
import core.setups.Stage;
import core.utilities.MathFunctions;
import core.utilities.mouse.MouseInput;
import core.utilities.text.Text;
import de.matthiasmann.twl.utils.PNGDecoder;

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
	public static final float ASPECT_RATIO = 720f / 1080f;
		
	/** View frame fixed to default size */
	public final Rectangle2D fixedFrame = new Rectangle2D.Double(0, 0, WIDTH, HEIGHT);
	/** Current Camera frame */
	public Rectangle2D frame = new Rectangle2D.Double(0, 0, WIDTH, HEIGHT);
	private Vector2f frameSpeed = new Vector2f();
	
	/** World scale variable */
	private float scale = 1f;
	
	private Mobile focus;
	
	/** Total time to fade over */
	private float fadeTotal;
	/** Current fading time */
	private float fadeTimer;
	/** Screen fade value */
	private float fade = 0f;
	
	/** Panning settings */
	private Vector3f pan = new Vector3f(0,0,0);
	private Vector2f panCurrent = new Vector2f(0,0);
	private Vector2f panLimit = new Vector2f(WIDTH * 0.15f, HEIGHT * 0.3f);
	private float panDelay;
	
	/** Shake timer */
	private float shakeTotal;
	private float shakeTime;
	private float shakePower;
	private Vector2f shakeOffset;
	
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
				Display.setIcon(loadIcon(System.getProperty("resources") + "/ui/AGDG Logo.png"));
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
			GL11.glClearStencil(0);
		} catch (LWJGLException e) {
			System.err.println("Could not create display.");
		}
		
		frame = new Rectangle2D.Double(0, 0, WIDTH, HEIGHT);
		setFadeTimer(-1f);
	}
	
	public static ByteBuffer[] loadIcon(String ref) throws IOException {
        InputStream fis = ResourceLoader.getResourceAsStream(ref);
        try {
            PNGDecoder decoder = new PNGDecoder(fis);
            ByteBuffer bb = ByteBuffer.allocateDirect(decoder.getWidth()*decoder.getHeight()*4);
            decoder.decode(bb, decoder.getWidth()*4, PNGDecoder.Format.RGBA);
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
	
	public void draw(GameSetup setup) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
				
		DrawUtils.fillColor(1f, 1f, 1f, 1f);
		
		// Zoom in/out camera
		zoom();
		// Shake screen
		shake();
		// Pan screen
		pan();
		
		// Draw current game setup
		setup.draw();
						
		// Negate screen shake
		settle();
		
		if(Theater.get().paused) {
			Text.getDefault().setStill(true);
			Text.getDefault().setCentered(true);
			Text.getDefault().drawString("Paused", getDisplayWidth(0.5f), getDisplayHeight(0.5f));
		}
		
		// Process fading
		fade();
		
		// Draw debug info
		if(Theater.get().debug) {
			for(Collidable c : HitMaps.getMapSectors(HitMaps.getCollisionMap(), focus != null ? ((Entity) focus).getBox() : frame)) {
				if(c instanceof Slope)
					DrawUtils.drawLine((Line2D) c.getBox());
				else
					DrawUtils.drawRect((float) c.getBounds().getX(), (float) c.getBounds().getY(), c.getBounds());
			}
			
			Text.getFont("DEBUG").setStill(true);
			Text.getFont("DEBUG").setSize(0.3f);
			Text.getFont("DEBUG").drawString("Current Setup: " + Theater.get().getSetup().getClass().getName(), 15, 15);
			
			Text.getFont("DEBUG").setStill(true);
			Text.getFont("DEBUG").setSize(0.3f);
			Text.getFont("DEBUG").drawString("Avogine v" + Theater.AVOGINE_VERSION, 15, 45);
			
			Text.getFont("DEBUG").setStill(true);
			Text.getFont("DEBUG").setSize(0.3f);
			Text.getFont("DEBUG").drawString("Scene scale: " + scale, 15, 75);
			
			Text.getFont("DEBUG").setStill(true);
			Text.getFont("DEBUG").setColor(Color.lightGray);
			Text.getFont("DEBUG").drawString((int) (MouseInput.getMouseX() + frame.getX())
					+ ", " + (int) ((MouseInput.getMouseY()) + frame.getY()),
					(float) MouseInput.getMouseX(), MouseInput.getMouseY() - 32);
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
	
	public void zoom() {
		// TODO Frame scale
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
		GL11.glViewport(0, 0, displayWidth, displayHeight);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		frame = new Rectangle2D.Double(frame.getX(), frame.getY(), displayWidth, displayHeight);
		
		if(upscale) {
			// Upscale
			GL11.glOrtho(0, WIDTH, HEIGHT, 0, -1, 1);
		} else {
			// Increase view window
			GL11.glOrtho(0, displayWidth, displayHeight, 0, -1, 1);
			Theater.get().getSetup().resizeRefresh();
		}
	}
	
	public Mobile getFocus() {
		return focus;
	}
	
	public void setFocus(Mobile focus) {
		this.focus = focus;
	}
	
	public Vector2f getFrameSpeed() {
		return frameSpeed;
	}
	
	public void follow() {
		// Always centered movement
		/*if(focus.getVelocity().length() != 0) {
			frame.setFrame(frame.getX() + focus.getVelocity().x, frame.getY() + focus.getVelocity().y, frame.getWidth(), frame.getHeight());
		}*/
		// Frame border movement
		if(focus.getVelocity().length() != 0) {
			Line2D border = new Line2D.Double(0,0,0,0);
			//Vector2f velocity = new Vector2f();

			// TODO Broken in different screen sizes due to frame scaling
			// Scroll camera down
			if(focus.getVelocity().y > 0) {
				border = new Line2D.Double(frame.getX(), frame.getY() + (fixedFrame.getHeight() * 0.7f),
						frame.getMaxX(), frame.getY() + (fixedFrame.getHeight() * 0.7f));
				frameSpeed.setY(focus.getVelocity().y);
			} else if(focus.getVelocity().y < 0) {
				// Scroll camera up
				border = new Line2D.Double(frame.getX(), frame.getY() + (fixedFrame.getHeight() * 0.45f),
						frame.getMaxX(), frame.getY() + (fixedFrame.getHeight() * 0.45f));
				frameSpeed.setY(focus.getVelocity().y);
			}

			if(!((Entity) focus).getBox().intersectsLine(border)) {
				//frame.setFrame(frame.getX(), frame.getY() + velocity.y, frame.getWidth(), frame.getHeight());
				frameSpeed.setY(0);
			}

			// Scroll camera right
			if(focus.getVelocity().x > 0) {
				border = new Line2D.Double(frame.getX() + (fixedFrame.getWidth() * 0.55f), frame.getY(),
						frame.getX() + (fixedFrame.getWidth() * 0.55f), frame.getMaxY());
				frameSpeed.setX(focus.getVelocity().x);
			} else if(focus.getVelocity().x < 0) {
				// Scroll camera left
				border = new Line2D.Double(frame.getX() + (fixedFrame.getWidth() * 0.45f), frame.getY(),
						frame.getX() + (fixedFrame.getWidth() * 0.45f), frame.getMaxY());
				frameSpeed.setX(focus.getVelocity().x);
			}

			if(!((Entity) focus).getBox().intersectsLine(border)) {
				//frame.setFrame(frame.getX() + velocity.x, frame.getY(), frame.getWidth(), frame.getHeight());
				frameSpeed.setX(0);
			}
		}
	}
	
	public void centerOn(Stage stage) {
		if(focus != null) {
			frame.setFrameFromCenter(((Entity) focus).getBox().getCenterX(), ((Entity) focus).getBox().getCenterY(),
					((Entity) focus).getBox().getCenterX() - (WIDTH/2), ((Entity) focus).getBox().getCenterY() - (HEIGHT/2));
			
			//if(Theater.get().getSetup() != null && Theater.get().getSetup() instanceof Stage) {
				/*if(frame.getX() < 0) {
					frame.setFrame(0, frame.getY(), frame.getWidth(), frame.getHeight());
				} else if(frame.getMaxX() > stage.getMapWidth()) {
					frame.setFrame(stage.getMapWidth() - frame.getWidth(), frame.getY(), frame.getWidth(), frame.getHeight());
				}
				
				if(frame.getY() < 0) {
					frame = new Rectangle2D.Double(frame.getX(), 0, frame.getWidth(), frame.getHeight());
				} else if(frame.getMaxY() > stage.getMapHeight()) {
					frame = new Rectangle2D.Double(frame.getX(), stage.getMapHeight() - frame.getHeight(),
							frame.getWidth(), frame.getHeight());
				}*/
			//}
		}
	}
	
	public void setPanning(int horizontal, int vertical, float speed) {
		pan.set(horizontal, vertical, speed);
		if(speed == 0) {
			panDelay = 0f;
		}
	}
	
	public void pan() {
		if(pan.z != 0) {
			if(panDelay < 0.45f) {
				panDelay += Theater.getDeltaSpeed(0.025f);
			} else {
				if(pan.x != 0 && Math.abs(panCurrent.x) < Math.abs(panLimit.x)) {
					panCurrent.x = MathFunctions.clamp(panCurrent.x + (Theater.getDeltaSpeed(pan.z) * pan.x),
							-panLimit.x, panLimit.x);
				}
				if(pan.y != 0 && Math.abs(panCurrent.y) < Math.abs(panLimit.y)) {
					panCurrent.y = MathFunctions.clamp(panCurrent.y + (Theater.getDeltaSpeed(pan.z) * pan.y),
							-panLimit.y, panLimit.y);
				}
			}
		} else if(panCurrent.x != 0 || panCurrent.y != 0) {
			if(panCurrent.x != 0) {
				panCurrent.x = MathFunctions.clamp(panCurrent.x - (Theater.getDeltaSpeed(7.5f) * (panCurrent.x / Math.abs(panCurrent.x))),
						panCurrent.x < 0 ? -panLimit.x : 0, panCurrent.x < 0 ? 0 : -panLimit.x);
			}
			if(panCurrent.y != 0) {
				panCurrent.y = MathFunctions.clamp(panCurrent.y - (Theater.getDeltaSpeed(7.5f) * (panCurrent.y / Math.abs(panCurrent.y))),
						panCurrent.y < 0 ? -panLimit.y : 0, panCurrent.y < 0 ? 0 : panLimit.y);
			}
		}

		frame.setFrame(frame.getX() + panCurrent.x, frame.getY() + panCurrent.y, frame.getWidth(), frame.getHeight());
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
		
		if(panCurrent.length() != 0) {
			frame.setFrame(frame.getX() - panCurrent.x, frame.getY() - panCurrent.y, frame.getWidth(), frame.getHeight());
		}
	}
	
	public float getFadeTimer() {
		return fadeTimer;
	}
	
	/**
	 * Set the screen to fade in or out over a specified time.
	 * 
	 * @param fadeTimer Time to fade, positive to fade out, negative to fade in, 0 for no fade
	 */
	public void setFadeTimer(float fadeTimer) {
		this.fadeTimer = fadeTimer;
		this.fadeTotal = fadeTimer;
		
		if(fadeTimer >= 0f)
			fade = 0f;
		else
			fade = 1f;
	}
	
	public void fade() {
		if(fadeTotal > 0f) {
			fade += (1f / fadeTotal) * Theater.getDeltaSpeed(0.025f);
			fadeTimer -= Theater.getDeltaSpeed(0.025f);
		} else if(fadeTotal < 0f) {
			fade -= (1f / Math.abs(fadeTotal)) * Theater.getDeltaSpeed(0.025f);
			fadeTimer += Theater.getDeltaSpeed(0.025f);
		}
		
		if(fadeTotal > 0f ? fadeTimer < 0f : fadeTimer > 0f) {
			fadeTimer = 0f;
			fadeTotal = 0f;
		}
		
		DrawUtils.fillColor(0f, 0f, 0f, fade);
	}

	public float getFrameXScale() {
		if(upscale) {
			return (float) (frame.getWidth() / fixedFrame.getWidth());
		} else {
			return 1f;
		}
	}
	
	public float getFrameYScale() {
		if(upscale) {
			return (float) (frame.getHeight() / fixedFrame.getHeight());
		} else {
			return 1f;
		}
	}
	
	public float getDisplayWidth() {
		return displayWidth / getFrameXScale();
	}
	
	public float getDisplayHeight() {
		return displayHeight / getFrameYScale();
	}
	
	public float getDisplayWidth(float mod) {
		return (displayWidth * mod) / getFrameXScale();
	}
	
	public float getDisplayHeight(float mod) {
		return (displayHeight * mod) / getFrameYScale();
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
