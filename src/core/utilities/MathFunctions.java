package core.utilities;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class MathFunctions {

	public static int clamp(int value, int min, int max) {
		if(value < min)
			return min;
		if(value > max)
			return max;
		return value;
	}

	public static float clamp(float value, float min, float max) {
		if(value < min)
			return min;
		if(value > max)
			return max;
		return value;
	}
	
	public static float linearTween(float time, float begin, float change, float duration) {
		return change * time / duration + begin;
	}
	
	public static float easeIn(float t,float b, float c, float d) {
		return c*(t/=d)*t + b;
	}
	
	public static float easeOut(float t, float b, float c, float d) {
		return -c *(t/=d)*(t-2) + b;
	}
	
	public static float easeInOut(float t, float b, float c, float d) {
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * ((--t)*(t-2) - 1) + b;
	}
	
	public static float lerp(float startValue, float endValue, float time) {
		  return (1 - time) * startValue + time * endValue;
	}
	
	public static Vector2f limitVector(Vector2f srcVector, float limit) {
		double lengthSquared = srcVector.x * srcVector.x + srcVector.y * srcVector.y;

		Vector2f limVector = new Vector2f(srcVector);
		if((lengthSquared > limit * limit) && (lengthSquared > 0)) {
			double ratio = (limit / Math.sqrt(lengthSquared));
			limVector.x *= ratio;
			limVector.y *= ratio;
		}
		
		return limVector;
	}
	
	public static Vector2f rotateVector(Vector2f vector, double degrees){
		return new Vector2f((float) ((vector.x * Math.cos(degrees)) - (vector.y * Math.sin(degrees))),
				(float) ((vector.x * Math.sin(degrees)) + (vector.y * Math.cos(degrees))));
	}

	public static Vector4f valueOfHex(String hex) {
		int r = Integer.valueOf(hex.substring(0, 2), 16);
		int g = Integer.valueOf(hex.substring(2, 4), 16);
		int b = Integer.valueOf(hex.substring(4, 6), 16);
		int a = hex.length() != 8 ? 255 : Integer.valueOf(hex.substring(6, 8), 16);
		return new Vector4f(r / 255f, g / 255f, b / 255f, a / 255f);
	}
	
}
