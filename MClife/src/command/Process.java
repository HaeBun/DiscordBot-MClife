package command;

import command.admin.Control;
import database.Admin;
import database.Player;
import database.Town;
import embed.Form;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;

import static database.Path.DirectoryPath.userDataBaseDirectoryPath;
import static object.Main.jda;
import static database.Command.*;

public class Process extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if(args[0].equalsIgnoreCase("mr")) {
            if(args.length==1) { // mr 만 적은 경우
                Long playerIdLong = event.getMember().getIdLong();
                String CommandLogFolderPathString = userDataBaseDirectoryPath + playerIdLong + "\\log\\command\\LatestCommand.txt";

                File playerLogFolder = new File(CommandLogFolderPathString);
                if(playerLogFolder.exists()==true){ // 명령어 사용 기록이 있을 경우
                    event.getChannel().deleteMessageById(readCommandLogType("MessageChannel", event.getMember())).queue();  // 이전 메세지 삭제
                }
                Form.Menu("Menu",event.getMember(), event.getChannel(), event.getGuild().getId(), event.getChannel().getId()); // 호출
            }

            else {
                if(args[1].equalsIgnoreCase("스텟")){
                    if(args.length==2){
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("스테이터스 Test");
                        eb.setDescription("mr 스텟 [status](str/agi/vit/int) [point]로 스텟 찍기\n" +
                                "\nex)```mr 스텟 vit 10```");
                        event.getChannel().sendMessage(eb.build()).queue();
                        eb.clear();
                    }
                    else {
                        if(args[2].equalsIgnoreCase("str")){
                            if(args.length==3){
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("스테이터스 Test");
                                eb.setDescription("수치를 입력해 주세요." +
                                        "\nex)```mr 스텟 str [ ]```");
                                event.getChannel().sendMessage(eb.build()).queue();
                                eb.clear();
                            }
                            else {
                                int value = Integer.valueOf(args[3]);

                                user.Account.setPlayerStatus(event.getChannel(), event.getMember(), args[2],value);
                            }
                        }
                        if(args[2].equalsIgnoreCase("agi")){
                            if(args.length==3){
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("스테이터스 Test");
                                eb.setDescription("수치를 입력해 주세요." +
                                        "\nex)```mr 스텟 agi [ ]```");
                                event.getChannel().sendMessage(eb.build()).queue();
                                eb.clear();
                            }
                            else {
                                int value = Integer.valueOf(args[3]);

                                user.Account.setPlayerStatus(event.getChannel(), event.getMember(), args[2],value);
                            }
                        }
                        if(args[2].equalsIgnoreCase("vit")){
                            if(args.length==3){
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("스테이터스 Test");
                                eb.setDescription("수치를 입력해 주세요." +
                                        "\nex)```mr 스텟 vit [ ]```");
                                event.getChannel().sendMessage(eb.build()).queue();
                                eb.clear();
                            }
                            else {
                                int value = Integer.valueOf(args[3]);

                                user.Account.setPlayerStatus(event.getChannel(), event.getMember(), args[2],value);
                            }
                        }
                        if(args[2].equalsIgnoreCase("int")){
                            if(args.length==3){
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("스테이터스 Test");
                                eb.setDescription("수치를 입력해 주세요." +
                                        "\nex)```mr 스텟 int [ ]```");
                                event.getChannel().sendMessage(eb.build()).queue();
                                eb.clear();
                            }
                            else {
                                int value = Integer.valueOf(args[3]);

                                user.Account.setPlayerStatus(event.getChannel(), event.getMember(), args[2],value);
                            }
                        }
                    }
                }

                else if(args[1].equalsIgnoreCase("초대"))
                    command.Invite.InviteBot(event);
                else if(args[1].equalsIgnoreCase("아이템")){
                    if(args.length!=2){
                        int itemCode = Integer.valueOf(args[2]);
//                        object.Item.getItemInfo(event, itemCode);
                    }
                }

                else if(args[1].equalsIgnoreCase("!UserDataBase"))
                    Admin.printUserDatabase(event);

                else if(args[1].equalsIgnoreCase("!UserFileCreate"))
                    Control.createUserFile(event);

                else if(args[1].equalsIgnoreCase("!UserLevelRank"))
                    Admin.createUserRankFile();

                else if(args[1].equalsIgnoreCase("!UserList")){
                    event.getChannel().sendMessage(jda.getGuilds().size()+"").queue();
                    String guilds = "";

                    for(int i=0; i<jda.getGuilds().size(); i++){
                        guilds += jda.getGuilds().get(i)+"\n";
                    }
                    event.getChannel().sendMessage(guilds).queue();
                }
            }
        }
        else {
            if(event.getMember().getUser().isBot()==false) {
                Player.getRandomExp(event, event.getMessage().getContentRaw().length(), args.length);
                Town.getRandomExp(event, event.getMessage().getContentRaw().length(), args.length);
            }
        }
    }
}
