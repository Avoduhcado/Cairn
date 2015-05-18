package core.utilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Screenshot {
	
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    private static ByteBuffer buffer;
    private static byte pixelData[];
    private static int imageData[];

    /**
     * Takes a screenshot and saves it to the screenshots directory. Returns the filename of the screenshot.
     */
    public static String saveScreenshot(File directory, int width, int height) {
        return takeScreenshot(directory, null, width, height);
    }

    public static String takeScreenshot(File directory, String name, int width, int height) {
        try {
            File file = new File(directory, "screenshots");
            file.mkdir();

            if (buffer == null || buffer.capacity() < width * height) {
                buffer = BufferUtils.createByteBuffer(width * height * 3);
            }

            if (imageData == null || imageData.length < width * height * 3) {
                pixelData = new byte[width * height * 3];
                imageData = new int[width * height];
            }

            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            buffer.clear();
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
            buffer.clear();
            String s = "CairnGDG " + (new StringBuilder()).append("").append(dateFormat.format(new Date())).toString();
            File file1;

            if (name == null) {
                for (int i = 1; (file1 = new File(file, (new StringBuilder()).append(s).append(i != 1 ? (new StringBuilder()).append("_").append(i).toString() : "").append(".png").toString())).exists(); i++) { }
            } else {
                file1 = new File(file, name);
            }

            buffer.get(pixelData);

            for (int j = 0; j < width; j++) {
                for (int k = 0; k < height; k++) {
                    int l = j + (height - k - 1) * width;
                    int i1 = pixelData[l * 3 + 0] & 0xff;
                    int j1 = pixelData[l * 3 + 1] & 0xff;
                    int k1 = pixelData[l * 3 + 2] & 0xff;
                    int l1 = 0xff000000 | i1 << 16 | j1 << 8 | k1;
                    imageData[j + k * width] = l1;
                }
            }

            BufferedImage bufferedimage = new BufferedImage(width, height, 1);
            bufferedimage.setRGB(0, 0, width, height, imageData, 0, width);
            ImageIO.write(bufferedimage, "png", file1);
            return (new StringBuilder()).append("Saved screenshot as ").append(file1.getName()).toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            return (new StringBuilder()).append("Failed to save: ").append(exception).toString();
        }
    }
}