package core.utilities;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.util.ResourceLoader;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public abstract class Resources {
	
	private static Resources resources;
	
	public static void init() {
		// ZipResouces loads from a zip file
		//resources = new ZipResources();
		// FileResources loads from plain folders
		resources = new FileResources();
	}
	
	public static Resources get() {
		return resources;
	}
	
	public abstract InputStream getResource(String resource);
	public abstract List<String> getSubList(String resource);

	public abstract boolean resourceExists(String resource);
	public abstract boolean isDirectory(String resource);
	
}

class ZipResources extends Resources {

	private ZipFile zipFile;
	
	ZipResources() {
		try {
			zipFile = new ZipFile(System.getProperty("resources") + "/sprites.zip");

			if(zipFile.isEncrypted()) {
				zipFile.setPassword(new char[]{'b','u','t','t','s'});
			}
		} catch (ZipException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public InputStream getResource(String resource) {
		try {
			return zipFile.getInputStream(zipFile.getFileHeader(resource));
		} catch (ZipException e) {
			System.out.println(resource);
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public List<String> getSubList(String resource) {
		if(!resourceExists(resource)) {
			return new LinkedList<String>();
		}
		
		List<String> subList = new LinkedList<String>();
		try {
			for(Object o : zipFile.getFileHeaders()) {
				FileHeader fh = (FileHeader) o;
				if(fh.getFileName().startsWith(resource) && !fh.isDirectory()) {
					subList.add(fh.getFileName());
				}
			}
		} catch (ZipException e) {
			e.printStackTrace();
		}
		
		return subList;
	}
	
	@Override
	public boolean resourceExists(String resource) {
		try {
			return zipFile.getFileHeader(resource) != null;
		} catch (ZipException e) {
			System.out.println(resource);
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public boolean isDirectory(String resource) {
		if(!resourceExists(resource)) {
			return false;
		}
		
		try {
			return zipFile.getFileHeader(resource).isDirectory();
		} catch (ZipException e) {
			System.out.println(resource);
			e.printStackTrace();
		}
		
		return false;
	}
	
}

class FileResources extends Resources {

	@Override
	public InputStream getResource(String resource) {
		return ResourceLoader.getResourceAsStream(System.getProperty("resources") + "/sprites/" + resource);
	}

	@Override
	public List<String> getSubList(String resource) {
		if(!resourceExists(resource) || !isDirectory(resource)) {
			return new LinkedList<String>();
		}
		
		File directory = new File(System.getProperty("resources") + "/sprites/" + resource);
		
		List<String> subList = new LinkedList<String>();
		subList.addAll(Arrays.asList(directory.list((e, f) -> f.startsWith(resource))));
		
		return subList;
	}

	@Override
	public boolean resourceExists(String resource) {
		return new File(System.getProperty("resources") + "/sprites/" + resource).exists();
	}

	@Override
	public boolean isDirectory(String resource) {
		File file = new File(System.getProperty("resources") + "/sprites/" + resource);
		return file.exists() && file.isDirectory();
	}

}
