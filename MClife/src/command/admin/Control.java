package command.admin;

import database.Admin;
import database.Path.DirectoryPath;
import database.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import static object.Main.jda;

public class Control {
    public static void createUserFile(GuildMessageReceivedEvent event) {
        int size = event.getGuild().getMembers().size();
        for(int i=0; i<size; i++){
            Member mb = event.getGuild().getMembers().get(i);
            Player.checkFolderExist(DirectoryPath.userDataBaseDirectoryPath+mb.getIdLong());
        }
    }

    public static void printUserLogToAdmins(GuildMessageReceivedEvent event,int[] playerStatus) {
        int finalPlayerLevel = playerStatus[0];
        int finalPlayerExp = playerStatus[1];
        int finalNextLvUpExp = playerStatus[2];
        int rdExp = playerStatus[3];

        for(int i = 0; i< Admin.adminIdLongs.length; i++){
            jda.getGuildById(651446773260484668L).getMemberById(Admin.adminIdLongs[i]).getUser().openPrivateChannel().queue((channel)->
            {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(event.getMember().getUser().getAsTag()+ " | 채팅 성장");
                eb.setDescription("경험치 획득 : "+rdExp
                        +"\n현재 레벨 : "+ finalPlayerLevel +" ("+ finalPlayerExp +"/"+ finalNextLvUpExp +")");
                eb.setThumbnail(event.getMember().getUser().getEffectiveAvatarUrl());
                eb.setFooter("ID : "+event.getMember().getUser().getIdLong());
                channel.sendMessage(eb.build()).queue();
            });
        }
    }
    public static void printTownLogToAdmins(GuildMessageReceivedEvent event,int[] playerStatus) {
        int finalPlayerLevel = playerStatus[0];
        int finalPlayerExp = playerStatus[1];
        int finalNextLvUpExp = playerStatus[2];
        int rdExp = playerStatus[3];

        for(int i = 0; i< Admin.adminIdLongs.length; i++){
            jda.getGuildById(651446773260484668L).getMemberById(Admin.adminIdLongs[i]).getUser().openPrivateChannel().queue((channel)->
            {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(event.getChannel().getName()+ " | 마을 성장");
                eb.setDescription("경험치 획득 : "+rdExp
                        +"\n현재 레벨 : "+ finalPlayerLevel +" ("+ finalPlayerExp +"/"+ finalNextLvUpExp +")");
                eb.setThumbnail(event.getGuild().getIconUrl());
                eb.setFooter("ID : "+event.getChannel().getIdLong());
                channel.sendMessage(eb.build()).queue();
            });
        }
    }

}
