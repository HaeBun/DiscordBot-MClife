package command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;


import java.io.File;


import static database.Path.DirectoryPath.userDataBaseDirectoryPath;


public class Command {


    public static void statusUpSelect(GuildMessageReactionAddEvent event){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("STATUS UP");
        eb.setDescription(
                "어떤 스텟을 올릴까요? "+"\n"
                        +":one: ` STR `"+"\t" +":two: ` AGI `"+"\n"
                        +":three: ` VIT `"+"\t" +":four: ` INT `"+"\n"
                        +"이 기능은 준비중입니다."
        );
        event.getChannel().sendMessage(eb.build()).queue(message -> {
            message.addReaction("1️⃣").queue();
            message.addReaction("2️⃣").queue();
            message.addReaction("3️⃣").queue();
            message.addReaction("4️⃣").queue();
//            database.player.log.writeCommandLog("COMMAND",String.valueOf(message.getIdLong()),"StatusUpSelect", event.getMember());
        });
        eb.clear();
    }
    public static void StrUpSelect(GuildMessageReactionAddEvent event){

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("스텟 업 선택 : STR");
        eb.setDescription(
                "얼마나 올릴까요? "+"\n"
                        +":one: ` 1 `"+"\n"
                        +":two: ` 4 `"+"\n"
                        +":three: ` PlayerSP `"+"\n"
                        +"이 기능은 준비중입니다."
        );
        event.getChannel().sendMessage(eb.build()).queue(message -> {
            message.addReaction("1️⃣").queue();
            message.addReaction("2️⃣").queue();
            message.addReaction("3️⃣").queue();
//            database.player.log.writeCommandLog("COMMAND",String.valueOf(message.getIdLong()),"StrUpSelect", event.getMember());
        });
        eb.clear();
    }
    public static void visitTown(Member member, TextChannel channel, Guild visitGuild, int visitTown){
        Long playerIdLong = member.getIdLong();
        String sCommandLogFolderPath = userDataBaseDirectoryPath + playerIdLong + "\\log\\command\\LatestCommand.txt";
        File playerLogFolder = new File(sCommandLogFolderPath);

        if(playerLogFolder.exists()==true){
            channel.deleteMessageById(database.Command.readCommandLogType("MessageChannel", member)).queue();
        }

        String description = ":one: 내 정보"+"\n"
                +":two: 마을 안으로"+"\n"
                +":three: 여행";

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("현재 위치: "+visitGuild.getTextChannels().get(visitTown).getName()+" 마을");
        eb.setThumbnail(visitGuild.getIconUrl());
        eb.setDescription("마을 문구가 없습니다.\n\n"+description);

        channel.sendMessage(eb.build()).queue(message -> {
            message.addReaction("1️⃣").queue();
            message.addReaction("2️⃣").queue();
            message.addReaction("3️⃣").queue();
//            database.player.log.writeCommandLog("COMMAND",String.valueOf(message.getIdLong()),"Info", member);
        });
    }
}
