package Listener;

import database.Country;
import embed.Form;
import embed.GuideBook;
import embed.Menu;
import embed.Quest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import object.Reaction;

import java.io.File;

import static database.Path.DirectoryPath.itemDataBaseDirectoryPath;
import static database.Path.DirectoryPath.questDirectoryPathString;
import static database.Player.getPlayerStatus;
import static object.Main.jda;
import static database.Command.*;

public class FormLinker extends ListenerAdapter {

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {

        if(!event.getMember().getUser().isBot()){
            Member member = event.getMember();

            // 이모티콘 반응 -> String 형태로 처리
            String emoteString = event.getReactionEmote().toString();

            String MessageChannelID = readCommandLogType("MessageChannel", member);
            String FormName = readCommandLogType("Form", member);
            String guildID = readCommandLogType("GuildLocation",member);
            String channelID = readCommandLogType("ChannelLocation",member);
            int value = 0;
            if(! readCommandLogType("Value", member).equalsIgnoreCase("null")){
                value = Integer.valueOf(readCommandLogType("Value",member));
            }

            event.getChannel().deleteMessageById(MessageChannelID).queue();

            // Print 구간
            System.out.println(event.getReactionEmote().toString());
            System.out.println("채널명:"+event.getChannel().getIdLong());
            // Print 구간 End

            if(FormName.equalsIgnoreCase("Menu")){
                if(emoteString.equalsIgnoreCase(Reaction.one)) // 내 정보
                    Menu.one("Status", member, event.getChannel(), guildID, channelID);

                else if(emoteString.equalsIgnoreCase(Reaction.two)) // 인벤토리
                    Menu.two("Inventory",member,event.getChannel(),guildID,channelID);

                else if(emoteString.equalsIgnoreCase(Reaction.three)) // 여행
                    Menu.three("SelectCountry", member, event.getChannel(),guildID,channelID);

                else if(emoteString.equalsIgnoreCase(Reaction.four)) // 가이드북
                    Menu.four("GuideBook", member, event.getChannel(), guildID, channelID);

                else if (emoteString.equalsIgnoreCase(Reaction.six)){
                    Menu.six("Quest", member, event.getChannel(), guildID, channelID, 1);
                }
                else if(emoteString.equalsIgnoreCase("RE:U+2b05")) // 마을안으로
                    Menu.five("TownInfo", member, event.getChannel(), guildID, channelID);
            }
            // command -> mr
            // 1️⃣ 스테이터스 창
            // 2️⃣ 인벤토리
            // 3️⃣ 월드맵
            // 4️⃣ 가이드북
            // 5️⃣ 마을 안으로
            // 6️⃣ 퀘스트

            else if (FormName.split("/")[0].equalsIgnoreCase("Quest")){
                if(FormName.split("/").length==1){ // 그냥 Quest 만 적은 경우에 해당.
                    if(emoteString.equalsIgnoreCase(Reaction.right)){
                        if(value==3){
                            value = 1;
                        }
                        else {
                            value++;
                        }
                        Menu.six("Quest", member, event.getChannel(), guildID, channelID, value);
                    }
                    else if(emoteString.equalsIgnoreCase(Reaction.left)){
                        if(Integer.valueOf(value)==1){
                            value = 3;
                        }
                        else {
                            value--;
                        }
                        Menu.six("Quest", member, event.getChannel(), guildID, channelID, value);
                    }
                    else if(emoteString.equalsIgnoreCase(Reaction.back)){
                        Form.Menu("Menu",event.getMember(), event.getChannel(), guildID, channelID);
                    }
                    else {
                        File questFolder = new File(questDirectoryPathString);
                        for(int i=0; i<database.Quest.readQuestList().length;i++){
                            if(emoteString.equalsIgnoreCase(Reaction.numberCodeList[i])){
                                String questName = questFolder.list()[i];
                                Quest.Select("Quest/"+questName, member, event.getChannel(), guildID, channelID, 1);
                            }
                        }
                    }
                }
                else if(FormName.split("/").length==2){
                    File questFolder = new File(questDirectoryPathString);
                    String questName = new String();
                    for(int i=0; i<questFolder.list().length; i++){
                        if(questFolder.list()[i].equalsIgnoreCase(FormName.split("/")[1])){
                            questName = questFolder.list()[i];
                            if(emoteString.equalsIgnoreCase(Reaction.right)){
                                value++;
                                Quest.Select("Quest/"+questName, member, event.getChannel(), guildID, channelID, value);
                            }
                            else if(emoteString.equalsIgnoreCase(Reaction.left)){
                                value--;
                                Quest.Select("Quest/"+questName, member, event.getChannel(), guildID, channelID, value);
                            }
                        }
                    }
                }
            }

            else if(FormName.split("/")[0].equalsIgnoreCase("GuideBook")){
                if(FormName.split("/").length==1){
                    if(emoteString.equalsIgnoreCase(Reaction.two)){
                        GuideBook.two("GuideBook/Weapon", member, event.getChannel(), guildID, channelID);
                    }
                    else if(emoteString.equalsIgnoreCase("RE:U+25c0")){
                        Form.Menu("Menu",member, event.getChannel(), guildID, channelID); // 호출
                    }
                }
                else if(FormName.split("/").length==2){
                    if(FormName.split("/")[1].equalsIgnoreCase("Weapon")){
                        int[] itemValue = {400, 400, 400, 400, 400};
                        for(int i=0; i<itemValue.length; i++){
                            if(emoteString.equalsIgnoreCase(Reaction.numberCodeList[i])){
                                GuideBook.getItemInfo("GuideBook/Item", member, event.getChannel(), guildID, channelID, itemValue[i]);
                            }
                            else if(emoteString.equalsIgnoreCase("RE:U+25c0")){
                                Menu.four("GuideBook", member, event.getChannel(), guildID, channelID);
                            }
                        }

                    }
                    else if(FormName.split("/")[1].equalsIgnoreCase("Item")){
                        File file = new File(itemDataBaseDirectoryPath);
                        int itemCode = Integer.valueOf(value);
                        String[] list = file.list();
                        int index = 0;
                        for(int i=0; i<list.length; i++){
                            if (Integer.valueOf(list[i].split("[.]")[0])==itemCode){
                                index = i;
                            }
                        }
                        if(emoteString.equalsIgnoreCase("RE:U+2b05")){
                            if(index==0){
                                index = list.length-1;
                            }
                            else {
                                index --;
                            }
                            GuideBook.getItemInfo("GuideBook/Item", member, event.getChannel(), guildID, channelID, Integer.valueOf(list[index].split("[.]")[0]));
                        }
                        else if(emoteString.equalsIgnoreCase("RE:U+27a1")){
                            if(index==list.length-1){
                                index = 0;
                            }
                            else {
                                index ++;
                            }
                            GuideBook.getItemInfo("GuideBook/Item", member, event.getChannel(), guildID, channelID, Integer.valueOf(list[index].split("[.]")[0]));
                        }
                        else if(emoteString.equalsIgnoreCase("RE:U+25c0")){
                            Form.GuideBook("GuideBook", member, event.getChannel(), guildID, channelID);
                        }
                    }
                }
            }
            // Menu -> 4️⃣
            // 1️⃣ 아이템
            // 2️⃣ 무기
            // 3️⃣ 방어구
            // 4️⃣ 장신구

            // GuideBook -> 2️
            // 1️⃣ 검
            // 2️⃣ 채찍
            // 3️⃣ 새
            // 4️⃣ 책
            // 5️⃣ 글리프

            else if(FormName.split("/")[0].equalsIgnoreCase("Status")){
                String[] status = {"STR", "AGI", "VIT", "INT"};
                if(FormName.split("/").length==1){
                    for(int i=0; i<4; i++){
                        if(emoteString.equalsIgnoreCase(Reaction.numberCodeList[i])){
                            Form.SelectStatus(status[i], member, event.getChannel(), guildID, channelID);
                        }
                        else if(emoteString.equalsIgnoreCase(Reaction.back)){
                            Form.Menu("Menu", member, event.getChannel(), guildID, channelID);
                        }
                    }
                } else if(FormName.split("/").length==2){
                    if(FormName.split("/")[1].equalsIgnoreCase("End")){
                        if(emoteString.equalsIgnoreCase(Reaction.back)){
                            Form.Menu("Menu", member, event.getChannel(), guildID, channelID);
                        }
                    }
                    else {
                        for(int i=0; i<4; i++){
                            if(FormName.split("/")[1].equalsIgnoreCase(status[i])){ // STR, AGI, VIT, WIS 중 하나 탐색 시
                                int[] selectValue = { 1,
                                        getPlayerStatus("sp", member.getIdLong())/4,
                                        getPlayerStatus("sp", member.getIdLong())/2,
                                        getPlayerStatus("sp", member.getIdLong())};

                            }
                        }
                    }
                }
            }
            // Menu -> 1️⃣ | SP가 남은 경우
            // 1️⃣ STR
            // 2️⃣ AGI
            // 3️⃣ VIT
            // 4️⃣ WIS

            // Menu -> 1️⃣ | SP가 없는 경우
            // ◀ Menu

            else if(FormName.split("/")[0].equalsIgnoreCase("SelectStatus")){
                int[] selectValue = { 1,
                        getPlayerStatus("sp", member.getIdLong())/4,
                        getPlayerStatus("sp", member.getIdLong())/2,
                        getPlayerStatus("sp", member.getIdLong())};
                for(int i=0; i<4; i++){
                    if(emoteString.equalsIgnoreCase(Reaction.numberCodeList[i])){
                        Form.UpdateStatus(FormName.split("/")[1], member, event.getChannel(), guildID, channelID, selectValue[i]);
                    }
                }
            }
            // Status -> Number | STR, AGI, VIT, WIS 중 1 택
            // 1️⃣ 1 올리기
            // 2️⃣ 남은 스텟의 4분의 1
            // 3️⃣ 남은 스텟의 4분의 2
            // 4️⃣ 남은 스텟 전부

            else if(FormName.equalsIgnoreCase("SelectCountry")){
                for(int i=0; i<10; i++){
                    if(emoteString.equalsIgnoreCase(Reaction.numberCodeList[i])){
                        Country.selectCountry(i, event.getChannel(), member, guildID);
                    }
                }
                if(emoteString.equalsIgnoreCase(Reaction.back)){
                    Form.Menu("Menu", member, event.getChannel(), guildID, channelID);
                }
            }


            else if(FormName.equalsIgnoreCase("CountryInfo")){
                for(int i=0; i<10; i++){
                    if(emoteString.equalsIgnoreCase(Reaction.numberCodeList[i])){
                        EmbedBuilder eb = new EmbedBuilder();
                        TextChannel textChannel = jda.getGuildById(guildID).getTextChannels().get(i);
                        if (! event.getChannel().equals(textChannel)) {
                            eb.setTitle(" 누군가 왔다갔어요 !");
                            int playerLevel = getPlayerStatus("level", member.getIdLong());
                            int playerExp = getPlayerStatus("exp", member.getIdLong());

                            eb.setThumbnail(event.getUser().getAvatarUrl());
                            eb.setDescription("방문자 :" + member.getUser().getAsTag() + "\n"
                                    + "` LV `│ \t**" + playerLevel + "** ( " + playerExp + " )" + "\n"
                            );
                            eb.setFooter("From : "+event.getGuild().getName()+" 나라의 "+event.getChannel().getName()+" 마을에서");

                            textChannel.sendMessage(eb.build()).queue();
                        }
                        Form.TownInfo("TownInfo", member, event.getChannel(), guildID, textChannel.getId());
                    }
                }
            }
            else if(FormName.equalsIgnoreCase("TownInfo")){
                if(emoteString.equalsIgnoreCase(Reaction.one)){
                    Form.Menu("Menu",member, event.getChannel(), guildID, channelID); // 호출
                }
            }
            else if(FormName.equalsIgnoreCase("UserStatus")) {
                if(emoteString.equalsIgnoreCase(Reaction.one)){
                    command.Command.statusUpSelect(event);
                }
            }
            else if(FormName.equalsIgnoreCase("StatusUpSelect")) {
                if(emoteString.equalsIgnoreCase(Reaction.one)){
                    command.Command.StrUpSelect(event);
                }
            }
            else if(FormName.equalsIgnoreCase("StrUpSelect")) {
                if(emoteString.equalsIgnoreCase(Reaction.one)){
                    user.Account.setPlayerStatus(event.getChannel(), member, "str", 1);
                }
            }

        }
    }
}
