package embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import object.Reaction;

import static database.Player.getPlayerStatus;
import static object.Main.jda;
import static object.Reaction.numberEmojiList;

public class Menu {
    public static void one(String commandType, Member member, TextChannel channel, String guildId, String channelId){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(member.getEffectiveName()+ " 용사님의 스테이터스");
        eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());

        if(getPlayerStatus("sp", member.getIdLong())>0){
            eb.setDescription("스테이터스 포인트가 남았습니다.");
            eb.addField(":small_red_triangle_down: Select",
                    ":one: ` STR `│ \t**" + String.format("%4d", getPlayerStatus("str", member.getIdLong())) + "** (힘)" + "\n"
                            +":two: ` AGI `│ \t**" + String.format("%4d", getPlayerStatus("agi", member.getIdLong())) + "** (손재주)" + "\n"
                            +":three: ` VIT `│ \t**" + String.format("%4d", getPlayerStatus("vit", member.getIdLong())) + "** (생명력)" + "\n"
                            +":four: ` WIS `│ \t**" + String.format("%4d", getPlayerStatus("int", member.getIdLong())) + "** (지력)" + "\n"
                            +"\n"
                            + "` 남은 SP `│ \t**" + String.format("%4d", getPlayerStatus("sp", member.getIdLong())) + "**" + "\n"
                    , false);
            channel.sendMessage(eb.build()).queue(message -> {
                message.addReaction("1️⃣").queue();
                message.addReaction("2️⃣").queue();
                message.addReaction("3️⃣").queue();
                message.addReaction("4️⃣").queue();
                database.player.log.writeCommandLog(commandType, message.getId(), guildId, channelId, member, null);
            });
        }
        else {
            eb.addField("- 스테이터스 -",
                    ":one: ` STR `│ \t**" + String.format("%4d", getPlayerStatus("str", member.getIdLong())) + "** (힘)" + "\n"
                            +":two: ` AGI `│ \t**" + String.format("%4d", getPlayerStatus("agi", member.getIdLong())) + "** (손재주)" + "\n"
                            +":three: ` VIT `│ \t**" + String.format("%4d", getPlayerStatus("vit", member.getIdLong())) + "** (생명력)" + "\n"
                            +":four: ` WIS `│ \t**" + String.format("%4d", getPlayerStatus("int", member.getIdLong())) + "** (지력)" + "\n"
                            +"\n"
                            + "` 남은 SP `│ \t**" + String.format("%4d", getPlayerStatus("sp", member.getIdLong())) + "**" + "\n"
                    , false);
            channel.sendMessage(eb.build()).queue(message -> {
                message.addReaction("◀").queue();
                database.player.log.writeCommandLog("Status/End",message.getId(), guildId, channelId, member, null);
            });
        }
    }

    public static void two(String commandType, Member member, TextChannel channel, String guildId, String channelId){ }

    public static void three(String commandType, Member member, TextChannel channel, String guildId, String channelId){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle( ":map: 여행할 국가를 선택해 주세요.");
        String[] countryList = new String[jda.getGuilds().size()];
        for(int i=0; i<jda.getGuilds().size(); i++){
            countryList[i] = jda.getGuilds().get(i).getName();
        }
        String countryListToString1 = "";
        String countryListToString2 = "";

        for(int i=0; i<jda.getGuilds().size(); i++){
            if(i<5){
                countryListToString1 += numberEmojiList[i]+" "+countryList[i] +" (용사 수:"+jda.getGuilds().get(i).getMembers().size()+")\n";
            }
            else {
                countryListToString2 += numberEmojiList[i]+" "+countryList[i] +" (용사 수:"+jda.getGuilds().get(i).getMembers().size()+")\n";
            }
        }
        eb.addField(":small_red_triangle_down:Select",countryListToString1,true);
        if(jda.getGuilds().size()>5){
            eb.addField("",countryListToString2, true);
        }

        int addReactionSize = jda.getGuilds().size();
        if (addReactionSize>10){
            addReactionSize = 10;
        }
        int finalAddReactionSize = addReactionSize;
        channel.sendMessage(eb.build()).queue(message -> {
            for(int i = 0; i< finalAddReactionSize; i++){
                message.addReaction(numberEmojiList[i]).queue();
            }
            message.addReaction("◀").queue();
            message.addReaction("❌").queue();
            database.player.log.writeCommandLog("SelectCountry",message.getId(),"000000000000000000", channelId, member, null);
        });
    }

    public static void four(String commandType, Member member, TextChannel channel, String guildId, String channelId) {
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
    public static void five(String commandType, Member member, TextChannel channel, String guildId, String channelId) {
        Guild country = jda.getGuildById(guildId);
        TextChannel town = country.getTextChannelById(channelId);
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("현재 위치: " + town.getName() + " 마을 앞");
        eb.addField("마을 공지사항", "준비중입니다.", false);

        eb.addField(":small_red_triangle_down:Select", ":one:", true);
        String description =
                ":one: 내 정보" + "\n"
                        + ":two: 마을 안으로" + "\n"
                        + ":three: 여행";
        eb.setThumbnail(country.getIconUrl());

        channel.sendMessage(eb.build()).queue(message -> {
            message.addReaction("1️⃣").queue();
            message.addReaction("2️⃣").queue();
            message.addReaction("3️⃣").queue();
            database.player.log.writeCommandLog(commandType, message.getId(), country.getId(), town.getId(), member, null);
        });
    }
    public static void six(String commandType, Member member, TextChannel channel, String guildId, String channelId, int select) {
        EmbedBuilder eb = new EmbedBuilder();
        String[] questList = {"진행 임무", "진행전 임무", "완료 임무"};
        String[] readQuestList = database.Quest.readQuestList();
        String[] completeQuestList = {};
        String[] ongoingQuestList = {};

        String[][] playerQuestsInfo = {ongoingQuestList, readQuestList, completeQuestList};
        eb.setTitle("퀘스트 목록");
        eb.setThumbnail("https://vignette.wikia.nocookie.net/castlevania/images/4/41/GoS_Trial_Quill.png/revision/latest?cb=20200714041845");
        for(int i=0; i<questList.length; i++){ // 세 가지이므로 0~2
            String lore ="";
            if(select-1 == i) {
                questList[i] = ":small_red_triangle_down:" + questList[i];
            }
            for(int j=0; j<playerQuestsInfo[i].length; j++) {
                if(playerQuestsInfo[i][j].length()!=0){
                    if(select-1 == i){
                        lore += numberEmojiList[j];
                    }
                    lore += " "+playerQuestsInfo[i][j]+"\n";
                }
            }
            eb.addField(questList[i], lore, true);
        }


        channel.sendMessage(eb.build()).queue(message -> {
            message.addReaction("⬅").queue();
            message.addReaction("➡").queue();
            for(int i=0; i<playerQuestsInfo[select-1].length; i++) {
                if(playerQuestsInfo[select-1].length!=0){
                    message.addReaction(numberEmojiList[i]).queue();
                }
            }
            message.addReaction("◀").queue();

            database.player.log.writeCommandLog(commandType, message.getId(), guildId, channelId, member, String.valueOf(select));
        });
    }
}
