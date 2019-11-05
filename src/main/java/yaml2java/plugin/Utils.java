package yaml2java.plugin;


import java.io.File;
import java.util.Random;

public class Utils {

  public Utils() {
  }

  public static boolean isNotNullOrEmpty(String str) {
    if(str != null && !str.isEmpty())
      return true;
    return false;
  }

  public void deleteAllFiles(String dir){
    File directory = new File(dir);
    File[] files = directory.listFiles();
    System.out.println("Deleting previous generated files..");
    for (File file : files) {
      // Delete each file
      if (!file.delete()) {
        // Failed to delete file
        System.out.println("Failed to delete "+file);
      }
    }
    System.out.println("Previous generated files succesfully deleted! ✅");

  }

}
