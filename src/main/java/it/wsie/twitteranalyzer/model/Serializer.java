package it.wsie.twitteranalyzer.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import it.wsie.twitteranalyzer.model.tasks.Task;

/**
 * @author Simone Papandrea
 *
 */
public class Serializer {

	private static String TEMP_DIR = "";

	static void setOutputDir(String dir) {

		Path path=Paths.get(dir);
		
		if(!Files.exists(path))
			path.toFile().mkdir();
		
		TEMP_DIR = dir;
	}

	static boolean write(Task<?> task) {

		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;

		try {

			fileOut = new FileOutputStream(
					FileSystems.getDefault().getPath(TEMP_DIR, task.getClass().getSimpleName()).toFile());
			out = new ObjectOutputStream(fileOut);
			out.writeObject(task);

		} catch (IOException e) {
			return false;
		} finally {

			if (fileOut != null)
				try {
					fileOut.close();
				} catch (IOException e) {
				}
		}

		return true;
	}

	static Object read(Class<?> cls) throws IOException, ClassNotFoundException {

		Object obj = null;
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
	
		try {

			if (exists(cls)) {

				fileIn = new FileInputStream(FileSystems.getDefault().getPath(TEMP_DIR, cls.getSimpleName()).toFile());
				in = new ObjectInputStream(fileIn);
				obj = in.readObject();
			}

		} finally {

			if (fileIn != null)
				try {
					fileIn.close();
				} catch (IOException e) {
				}
		}

		return obj;
	}

	static void delete(Class<?> cls) throws IOException {

		if (exists(cls))
			Files.delete(FileSystems.getDefault().getPath(TEMP_DIR, cls.getSimpleName()));
	}

	static boolean exists(Class<?> cls) {

		return Files.exists(FileSystems.getDefault().getPath(TEMP_DIR, cls.getSimpleName()));

	}
}
