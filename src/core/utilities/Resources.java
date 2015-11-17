package core.utilities;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ProviderNotFoundException;

public class Resources {

	private FileSystem fileSystem;
	
	private static Resources resources;
	
	public static void init() {
		resources = new Resources();
	}
	
	public static Resources get() {
		return resources;
	}
	
	private Resources() {
		Path resFile = Paths.get(System.getProperty("resources"), "sprites.avo");
		System.out.println(resFile.toString());
		try {
			fileSystem = FileSystems.newFileSystem(resFile, null);
		} catch (IOException | ProviderNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public FileSystem getFileSystem() {
		return fileSystem;
	}
	
	public void close() {
		if(fileSystem.isOpen()) {
			try {
				fileSystem.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
