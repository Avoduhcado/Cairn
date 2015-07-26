package core.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AvoFileDecoder {

	public static byte[] decodeAVLFile(File avl) {
		byte[] byteArray = null;
		try (FileInputStream fis = new FileInputStream(avl)) {
			byteArray = new byte[fis.available()];
			fis.read(byteArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return byteArray;
	}
	
}
