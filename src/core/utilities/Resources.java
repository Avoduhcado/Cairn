package core.utilities;

import java.io.InputStream;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class Resources {

	private ZipFile zipFile;
	
	private static Resources resources;
	
	public static void init() {
		resources = new Resources();
	}
	
	public static Resources get() {
		return resources;
	}
	
	private Resources() {
		try {
			zipFile = new ZipFile(System.getProperty("resources") + "/sprites.avo");

			if(zipFile.isEncrypted()) {
				zipFile.setPassword("butts");
			}
		} catch (ZipException e) {
			e.printStackTrace();
		}
	}
	
	public InputStream getResource(String resource) {
		try {
			return zipFile.getInputStream(zipFile.getFileHeader(resource));
		} catch (ZipException e) {
			System.out.println(resource);
			e.printStackTrace();
		}
		
		return null;
	}
	
}
