package database;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static database.Path.DirectoryPath.questDirectoryPathString;

public class Quest {
    public static void checkFolderExist(String folderDirectoryString){
        File folder = new File(folderDirectoryString);
        if(folder.exists()==false){
            folder.mkdirs();
        }
    }

    public static String[] readQuestList(){
        File questFolder = new File(questDirectoryPathString);

        if(questFolder.exists()==true){
            return questFolder.list();
        }
        return null;
    }

}
