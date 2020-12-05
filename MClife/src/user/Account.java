package user;


import database.Path.DirectoryPath;
import database.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static database.Player.checkPlayerInfoExist;
import static database.Player.getPlayerStatus;

public class Account {

    public static void loadInfo(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**"+event.getMember().getEffectiveName()+"** 용사님의 정보");
        eb.setThumbnail(event.getMember().getUser().getEffectiveAvatarUrl());
        eb.setDescription(
                ":one: mr 계정 **만들기**```계정을 새로 만듭니다.```\n"
                +":two: mr 계정 **삭제**```계정 정보를 삭제합니다.```\n"
        );
        event.getChannel().sendMessage(eb.build()).queue(message -> {
            message.addReaction("1️⃣").queue();
            message.addReaction("2️⃣").queue();
        });
        eb.clear();
    }
    public static void setPlayerStatus(TextChannel channel, Member member, String type, int value) {
        Long PlayerIdLong = member.getIdLong();
        String PlayerInfoPathString = DirectoryPath.userDataBaseDirectoryPath+member.getIdLong()+"\\Info.txt";
        Path PlayerInfoPath = Paths.get(PlayerInfoPathString);
        checkPlayerInfoExist(PlayerInfoPathString);

        int playerLevel = getPlayerStatus("level", PlayerIdLong);
        int playerExp = getPlayerStatus("exp", PlayerIdLong);
        int playerStr = getPlayerStatus("str", PlayerIdLong);
        int playerAgi = getPlayerStatus("agi", PlayerIdLong);
        int playerVit = getPlayerStatus("vit", PlayerIdLong);
        int playerInt = getPlayerStatus("int", PlayerIdLong);
        int playerMaxHP = getPlayerStatus("maxhp", PlayerIdLong);
        int playerNowHP = getPlayerStatus("nowhp", PlayerIdLong);
        int playerMaxMP = getPlayerStatus("maxmp", PlayerIdLong);
        int playerNowMP = getPlayerStatus("nowmp", PlayerIdLong);
        int playerSP = getPlayerStatus("sp", PlayerIdLong);

        if(playerSP<value){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("보유 SP가 부족합니다.");
            eb.setDescription("현재 남은 SP는 "+playerSP+" 입니다.");
            channel.sendMessage(eb.build()).queue();
        }

        else {
            checkPlayerInfoExist(PlayerInfoPathString);

            Member mb = member;
            Player.checkFolderExist(DirectoryPath.userDataBaseDirectoryPath+mb.getIdLong());
            EmbedBuilder eb = new EmbedBuilder();

            if(type.equalsIgnoreCase("str")||type.equalsIgnoreCase("STR")){
                playerStr += value;
                playerSP -= value;
                eb.setTitle("STR +"+value+" 증가");
                eb.setThumbnail("https://mir-s3-cdn-cf.behance.net/project_modules/disp/d11e0340211889.577615c15f5d4.gif");
                eb.setDescription("힘은 모험(채팅,게임,던전) 시 강력한 몬스터를 잡을 수 있습니다.\n" +
                        "최소 경험치 증가량과 관련되어 있습니다.");
                eb.setFooter("STR : "+playerStr+" (+"+value+")");
            }
            else if(type.equalsIgnoreCase("agi")||type.equalsIgnoreCase("AGI")){
                playerAgi += value;
                playerSP -= value;
                eb.setTitle("AGI +"+value+" 증가");
                eb.setThumbnail("https://mir-s3-cdn-cf.behance.net/project_modules/disp/39b6b640211889.577622c5ac7ff.gif");
                eb.setDescription("손재주는 아이템 드랍률을 높여줍니다.\n" +
                        "최대 경험치 획득량이 증가합니다.");
                eb.setFooter("AGI : "+playerAgi+" (+"+value+")");
            }
            else if(type.equalsIgnoreCase("vit")||type.equalsIgnoreCase("VIT")) {
                playerVit += value;
                playerMaxHP += value*4;
                playerNowHP += value*4;
                playerSP -= value;
                eb.setTitle("VIT +"+value+" 증가");
                eb.setThumbnail("https://mir-s3-cdn-cf.behance.net/project_modules/disp/1a008340211889.577615c1129fc.gif");
                eb.setDescription("생명력은 1당 HP가 4 증가합니다.\n" +
                        "더 많은 활동을 할 수 있습니다.");
                eb.setFooter("VIT : "+playerVit+" (+"+value+")");
            }
            else if(type.equalsIgnoreCase("int")||type.equalsIgnoreCase("INT")){
                playerInt += value;
                playerMaxMP += value*4;
                playerNowMP += value*4;
                playerSP -= value;
                eb.setTitle("INT +"+value+" 증가");
                eb.setThumbnail("https://mir-s3-cdn-cf.behance.net/project_modules/disp/3b087b40211889.577615c13cb84.gif");
                eb.setDescription("지력은 1당 MP가 4 증가합니다.\n" +
                        "더 많은 마법을 쓸 수 있습니다.");
                eb.setFooter("INT : "+playerInt+" (+"+value+")");
            }

            try {
                BufferedWriter bw = Files.newBufferedWriter(PlayerInfoPath);
                bw.write("level:"+playerLevel);
                bw.newLine();
                bw.write("exp:"+playerExp);
                bw.newLine();
                bw.write("str:"+playerStr);
                bw.newLine();
                bw.write("agi:"+playerAgi);
                bw.newLine();
                bw.write("vit:"+playerVit);
                bw.newLine();
                bw.write("int:"+playerInt);
                bw.newLine();
                bw.write("maxhp:"+playerMaxHP);
                bw.newLine();
                bw.write("nowhp:"+playerNowHP);
                bw.newLine();
                bw.write("maxmp:"+playerMaxMP);
                bw.newLine();
                bw.write("nowmp:"+playerNowMP);
                bw.newLine();
                bw.write("sp:"+playerSP);

                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            channel.sendMessage(eb.build()).queue();
        }
    }
}
