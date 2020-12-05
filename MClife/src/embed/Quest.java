package embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static database.Path.DirectoryPath.questDirectoryPathString;

public class Quest {
    public static void Select(String commandType, Member member, TextChannel channel, String guildId, String channelId ,int communication) {
        boolean firstForm = false;
        boolean lastForm = false;

        String questName = commandType.split("/")[1];
        String footerLore = "";

        Path questForm = Paths.get(questDirectoryPathString + "\\" + questName +"\\Communication\\"+communication+".txt");
        EmbedBuilder eb = new EmbedBuilder();
        try {
            BufferedReader br = Files.newBufferedReader(questForm);
                for(;;){
                String readLine = br.readLine();
                if(readLine == null){
                    break;
                }
                if(readLine.split(":")[0].equalsIgnoreCase("Title")){
                    eb.setTitle(readLine.split(":")[1]);
                }
                else if(readLine.split(":")[0].equalsIgnoreCase("Description")){
                    eb.setDescription(readLine.split(":")[1]);
                }
                else if(readLine.split(":")[0].equalsIgnoreCase("Thumbnail")){
                    eb.setThumbnail(readLine.split(":")[1]+":"+readLine.split(":")[2]);
                }

                if(readLine.split(":")[0].equalsIgnoreCase("Form")){
                    if(readLine.split(":")[1].equalsIgnoreCase("First")){
                        firstForm = true;
                    }
                    else if(readLine.split(":")[1].equalsIgnoreCase("Last")){
                        lastForm = true;
                    }
                }
            }

            br.close();
        } catch (IOException e) { e.printStackTrace(); }

        if(firstForm==false){
            footerLore += "⬅ 이전 ";
        }

        if(lastForm==false){
            footerLore += "➡ 다음 ";
        }
        else {
            footerLore += "✅ 수락 "
                        + "❎ 거절 ";
        }
        eb.setFooter(footerLore);
        boolean finalFirstForm = firstForm;
        boolean finalLastForm = lastForm;
        channel.sendMessage(eb.build()).queue(message -> {

            if(finalFirstForm==false){
               message.addReaction("⬅").queue();
            }

            if(finalLastForm==false){
                message.addReaction("➡").queue();
            }
            else {
                message.addReaction("✅").queue();
                message.addReaction("❎").queue();
            }
            database.player.log.writeCommandLog(commandType,message.getId(), guildId, channelId, member, String.valueOf(communication));
        });
    }
}
