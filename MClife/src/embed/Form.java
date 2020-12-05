package embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import user.Account;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static database.Path.DirectoryPath.levelChartTXTPath;
import static database.Path.DirectoryPath.userDataBaseDirectoryPath;
import static database.Player.getPlayerStatus;
import static object.Main.jda;

public class Form {
    public static void Menu(String commandType, Member member, TextChannel channel, String guildId, String channelId) {
        String userInfoString = userDataBaseDirectoryPath + member.getIdLong() + "\\Info.txt";
        String levelChartString = levelChartTXTPath;

        Path userInfoPath = Paths.get(userInfoString);
        Path levelChartPath = Paths.get(levelChartString);

        int playerLevel;
        int playerExp;
        int playerNowHP;
        int playerMaxHP;
        int nextLvUpExp;

        try {
            BufferedReader userInfo = Files.newBufferedReader(userInfoPath);
            BufferedReader ChartInfo = Files.newBufferedReader(levelChartPath);

            playerLevel = Integer.valueOf(userInfo.readLine().split(":")[1]);
            playerExp = Integer.valueOf(userInfo.readLine().split(":")[1]);
            playerNowHP = getPlayerStatus("nowhp", member.getIdLong());
            playerMaxHP = getPlayerStatus("maxhp", member.getIdLong());
            ChartInfo.readLine();
            for (int i = 1; i < playerLevel; i++) {
                ChartInfo.readLine();
            }
            String Line = ChartInfo.readLine();
            nextLvUpExp = Integer.valueOf(Line.split("\t")[1]) + Integer.valueOf(Line.split("\t")[2]);

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(member.getEffectiveName() + " 용사님의 정보");
            eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());
            eb.setDescription(
                    "` LV `│ \t**" + playerLevel + "** ( " + playerExp + " / " + nextLvUpExp + " ) " + "\n"
                            + "\n"
                            + "` HP `│ \t**" + getPlayerStatus("nowhp", member.getIdLong()) + "** / " + getPlayerStatus("maxhp", member.getIdLong()) + "\n"
                            + "` AP `│ \t**" + getPlayerStatus("nowmp", member.getIdLong()) + "** / " + getPlayerStatus("maxmp", member.getIdLong()) + "\n"
            );
            eb.addField(":small_red_triangle_down:Select",
                    "\n:one: 스테이터스 창" + "\n"
                            + ":two: 인벤토리 ( 준비중 )" + "\n"
                            + ":three: 월드맵" + "\n"
                            + ":four: 가이드북" + "\n"
                            + ":five: 마을 안으로" + "\n"
                            + ":six: 퀘스트 ( 준비중 )", true);
            eb.addField("", "", true);
            eb.addField("- STATUS -",
                    "` STR `│ \t**" + String.format("%4d", getPlayerStatus("str", member.getIdLong())) + "**" + "\n"
                            + "` AGI `│ \t**" + String.format("%4d", getPlayerStatus("agi", member.getIdLong())) + "**" + "\n"
                            + "` VIT `│ \t**" + String.format("%4d", getPlayerStatus("vit", member.getIdLong())) + "**" + "\n"
                            + "` WIS `│ \t**" + String.format("%4d", getPlayerStatus("int", member.getIdLong())) + "**" + "\n"
                    , true);
            String footerLore = "다음 레벨업까지 남은 경험치량: " + (nextLvUpExp - playerExp)+"\n"
                    +"현재 위치 : "+jda.getGuildById(guildId).getName()+" 나라의 "+jda.getGuildById(guildId).getTextChannelById(channelId).getName()+" 마을";
            if ((float) playerNowHP / playerMaxHP < 0.3) {
                footerLore += "\n경고 : 체력이 30% 미만입니다.";
            }

            eb.setFooter(footerLore);
            channel.sendMessage(eb.build()).queue(message -> {
                message.addReaction("1️⃣").queue();
                message.addReaction("2️⃣").queue();
                message.addReaction("3️⃣").queue();
                message.addReaction("4️⃣").queue();
                message.addReaction("5️⃣").queue();
                message.addReaction("6️⃣").queue();
                if(! channelId.equalsIgnoreCase(channel.getId())) {
                    message.addReaction("◀").queue();
                }
                message.addReaction("❌").queue();
                database.player.log.writeCommandLog(commandType, message.getId(),guildId, channelId, member, null);
            });
            eb.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void SelectStatus(String commandType, Member member, TextChannel channel, String guildId, String channelId){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(member.getEffectiveName()+ " 용사님의 선택 : "+commandType);
        eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());

        eb.addField(":small_red_triangle_down: Select",
                ":one: +1"+"\n"
                +":two: +"  + getPlayerStatus("sp", member.getIdLong())/4+" (4분의 1)" +"\n"
                +":three: +"+ getPlayerStatus("sp", member.getIdLong())/2+" (4분의 2)" +"\n"
                +":four: +" + getPlayerStatus("sp", member.getIdLong()) + " (전부 다)"
                ,false);

        channel.sendMessage(eb.build()).queue(message -> {
            message.addReaction("1️⃣").queue();
            message.addReaction("2️⃣").queue();
            message.addReaction("3️⃣").queue();
            message.addReaction("4️⃣").queue();
            message.addReaction("◀").queue();
            database.player.log.writeCommandLog("Select/"+commandType, message.getId(), guildId, channelId, member, null);
        });
    }

    public static void UpdateStatus(String commandType, Member member, TextChannel channel, String guildId, String channelId, int value) {
        Account.setPlayerStatus(channel,member,commandType,value);
    }

    public static void GuideBook(String commandType, Member member, TextChannel channel, String guildId, String channelId) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("가이드북");
        eb.setThumbnail("https://koreasouth1-mediap.svc.ms/transform/thumbnail?provider=spo&inputFormat=png&cs=fFNQTw&docid=https%3A%2F%2Fo365seoil-my.sharepoint.com%3A443%2F_api%2Fv2.0%2Fdrives%2Fb!OKfALBAy-kiYbaBwVf2erowdgby7WsFEkHPWCMDqyB6Xs3VYxbHQTLgCl2GJbRyo%2Fitems%2F01NDXQEF6OLZ4V337ICVBKBQALLXH3M3S5%3Fversion%3DPublished&access_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJub25lIn0.eyJhdWQiOiIwMDAwMDAwMy0wMDAwLTBmZjEtY2UwMC0wMDAwMDAwMDAwMDAvbzM2NXNlb2lsLW15LnNoYXJlcG9pbnQuY29tQDgyNDQzZWFmLTc0ODEtNGI4MS05NDQ4LWRlYjU1ZjgwMDUyYiIsImlzcyI6IjAwMDAwMDAzLTAwMDAtMGZmMS1jZTAwLTAwMDAwMDAwMDAwMCIsIm5iZiI6IjE1OTYwMjQxNTYiLCJleHAiOiIxNTk2MDQ1NzU2IiwiZW5kcG9pbnR1cmwiOiJuNFRDZ2ltN3ludGY5T00raXAwTTVtNWVUbU5QcElPTVJmVlE1bXl2KzhVPSIsImVuZHBvaW50dXJsTGVuZ3RoIjoiMTE5IiwiaXNsb29wYmFjayI6IlRydWUiLCJjaWQiOiJNR05qWVRaaE9XWXRPREEyWmkxaU1EQXdMV1JqWldZdFltUmlNakU0TW1ObE5qTTEiLCJ2ZXIiOiJoYXNoZWRwcm9vZnRva2VuIiwic2l0ZWlkIjoiTW1Oak1HRTNNemd0TXpJeE1DMDBPR1poTFRrNE5tUXRZVEEzTURVMVptUTVaV0ZsIiwic2lnbmluX3N0YXRlIjoiW1wia21zaVwiXSIsIm5hbWVpZCI6IjAjLmZ8bWVtYmVyc2hpcHxraW1pdDEyNzlAb2ZmaWNlLnNlb2lsLmFjLmtyIiwibmlpIjoibWljcm9zb2Z0LnNoYXJlcG9pbnQiLCJpc3VzZXIiOiJ0cnVlIiwiY2FjaGVrZXkiOiIwaC5mfG1lbWJlcnNoaXB8MTAwMzIwMDA5ZWFhOGE4NUBsaXZlLmNvbSIsInR0IjoiMCIsInVzZVBlcnNpc3RlbnRDb29raWUiOiIzIn0.MU0yT3pJdnZhajVLVXVQOXJWRzBsS0pjN2FNaVVqaUs3MzJzbWlYM2l0dz0&encodeFailures=1&srcWidth=&srcHeight=&width=1345&height=617&action=Access");
        eb.setDescription("각종 도감을 볼 수 있습니다.");
        eb.addField(":small_red_triangle_down: Select",
                ":one: 아이템"+"\n"
                        +":two: 무기" +"\n"
                        +":three: 방어구"+"\n"
                        +":four: 장신구"
                ,false);

        channel.sendMessage(eb.build()).queue(message -> {
            message.addReaction("1️⃣").queue();
            message.addReaction("2️⃣").queue();
            message.addReaction("3️⃣").queue();
            message.addReaction("4️⃣").queue();
            message.addReaction("◀").queue();
            database.player.log.writeCommandLog(commandType, message.getId(), guildId, channelId, member, null);
        });
    }

    public static void TownInfo(String commandType, Member member, TextChannel channel, String guildId, String channelId){
        Guild country = jda.getGuildById(guildId);
        TextChannel town = country.getTextChannelById(channelId);
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("현재 위치: "+town.getName()+" 마을 앞");
        eb.addField("마을 공지사항", "준비중입니다.", false);

        eb.addField(":small_red_triangle_down:Select", ":one:",true);
        String description =
                ":one: 내 정보"+"\n"
                        +":two: 마을 안으로"+"\n"
                        +":three: 여행";
        eb.setThumbnail(country.getIconUrl());

        channel.sendMessage(eb.build()).queue(message -> {
            message.addReaction("1️⃣").queue();
            message.addReaction("2️⃣").queue();
            message.addReaction("3️⃣").queue();
            database.player.log.writeCommandLog(commandType,message.getId(),country.getId(), town.getId(),member, null);
        });
    }
}
