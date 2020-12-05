    package database;

    import net.dv8tion.jda.api.entities.Member;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;

    import static database.Path.DirectoryPath.userDataBaseDirectoryPath;

    public class Command {

        public static String readCommandLogType(String StatusValue, Member member){
            Long playerIdLong = member.getIdLong();
            String sCommandLogPath = userDataBaseDirectoryPath + playerIdLong + "\\log\\command\\LatestCommand.txt";
            String[] dataType = {"MessageChannel",  "Form", "GuildLocation", "ChannelLocation","Value"};
            Path playerLogPath = Paths.get(sCommandLogPath);
            String result = null;
            try {
                BufferedReader br = Files.newBufferedReader(playerLogPath);
                for(int i=0; i<dataType.length; i++){
                    String line = br.readLine();
                    if(line.split(":")[0].equalsIgnoreCase(StatusValue)){
                        result = line.split(":")[1];
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }


    }
