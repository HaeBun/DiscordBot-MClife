package database;

import database.Path.DirectoryPath;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import object.Main;

import java.io.File;
import static database.Player.getPlayerStatus;

public class Admin {
    public static Long[] adminIdLongs = { 366474559999836161L, 342181633878654977L, 369045907888668672L };

    public static void printUserDatabase(GuildMessageReceivedEvent event) {
        File directory = new File("C:\\Users\\ninin\\OneDrive - 서일대학교\\Resource\\Database\\User");
        String userDataBase[] = directory.list();
        String printUserDB ="";

        for(int i=0; i<userDataBase.length; i++) {
            Member member = event.getGuild().getMemberById(Long.valueOf(userDataBase[i]));
            if(member != null){
                printUserDB += userDataBase[i]+" → "+member.getUser().getAsTag()+"\n";
            }
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("탐색: 유저 데이터베이스");
        eb.setDescription(printUserDB);

        event.getChannel().sendMessage(eb.build()).queue();

    }

    public static void createUserRankFile (){
        JDA jda = Main.jda;
        String description = "";

        for(int i=0; i<jda.getUsers().size(); i++){
            Long PlayerIdLong = jda.getUsers().get(i).getIdLong();


            File directory = new File(DirectoryPath.userDataBaseDirectoryPath+PlayerIdLong+"\\Info.txt");

            if(directory.exists()){
                int playerLevel = getPlayerStatus("level", PlayerIdLong);
                int playerExp = getPlayerStatus("exp", PlayerIdLong);
                description += jda.getUsers().get(i).getAsTag()+" | Lv:"+playerLevel+"("+playerExp+")\n";
            }
        }

        for(int i=0; i<adminIdLongs.length; i++){
            String finalDescription = description;
            jda.getGuildById(651446773260484668L).getMemberById(Admin.adminIdLongs[i]).getUser().openPrivateChannel().queue((channel)-> {
                EmbedBuilder eb = new EmbedBuilder();

                eb.setFooter("mr !UserLevelRank");
                eb.setTitle("유저 레벨 정보");
                eb.setDescription(finalDescription);
                channel.sendMessage(eb.build()).queue();
            });
        }

    }

}
