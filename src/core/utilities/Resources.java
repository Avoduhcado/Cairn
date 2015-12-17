package core.utilities;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

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
			zipFile = new ZipFile(System.getProperty("resources") + "/sprites.zip");

			if(zipFile.isEncrypted()) {
				zipFile.setPassword(new char[]{'b','u','t','t','s'});
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
	
	public FileHeader getResourceHeader(String resource) {
		try {
			return zipFile.getFileHeader(resource);
		} catch (ZipException e) {
			System.out.println(resource);
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<FileHeader> getSubList(String resource) {
		List<FileHeader> subList = new LinkedList<FileHeader>();
		try {
			for(Object o : zipFile.getFileHeaders()) {
				FileHeader fh = (FileHeader) o;
				if(fh.getFileName().startsWith(resource) && !fh.isDirectory()) {
					subList.add(fh);
				}
			}
		} catch (ZipException e) {
			e.printStackTrace();
		}
		
		return subList;
	}
	
}
