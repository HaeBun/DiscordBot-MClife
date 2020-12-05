package database;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import static database.Path.DirectoryPath.countryDataBaseDirectoryPath;
import static database.Player.checkFolderExist;
import static database.Town.*;
import static object.Main.jda;
import static object.Reaction.numberEmojiList;

public class Country {
    public static void selectCountry(int select, TextChannel channel , Member member, String guildId){
        Guild guild = jda.getGuilds().get(select);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("국가 : "+ guild.getName());
        String[] townList = new String[guild.getTextChannels().size()];

        String townListToString1 = "";
        String townListToString2 = "";
        for(int i=0; i<guild.getTextChannels().size(); i++){
            String townDatabaseFolderString = countryDataBaseDirectoryPath+guild.getIdLong()+"\\"+guild.getTextChannels().get(i).getIdLong();
            String townInfoPathString = townDatabaseFolderString+"\\Info.txt";

            checkFolderExist(townDatabaseFolderString);
            checkTownInfoExist(townInfoPathString);

            if(i>=10){
                break;
            }

            if(i<5){
                townListToString1 += numberEmojiList[i]+" "+guild.getTextChannels().get(i).getAsMention()
                        +"(Lv. "+getTownStatus("level",guild.getIdLong(),guild.getTextChannels().get(i).getIdLong())+")\n";
            }
            else {
                townListToString2 += numberEmojiList[i]+" "+guild.getTextChannels().get(i).getAsMention()
                        +"(Lv. "+getTownStatus("level",guild.getIdLong(),guild.getTextChannels().get(i).getIdLong())+")\n";
            }
        }

        eb.addField(":small_red_triangle_down:Select", townListToString1, true);

        if(guild.getTextChannels().size()>5){
            eb.addField("",townListToString2,true);
        }
        eb.setThumbnail(guild.getIconUrl());

        int addReactionSize = guild.getTextChannels().size();
        if( addReactionSize>10){
            addReactionSize = 10;
        }

        int finalAddReactionSize = addReactionSize;
        channel.sendMessage(eb.build()).queue(message -> {
            for(int i = 0; i< finalAddReactionSize; i++){
                message.addReaction(numberEmojiList[i]).queue();
            }

            database.player.log.writeCommandLog("CountryInfo",message.getId(), guild.getId(), "000000000000000000", member, String.valueOf(select));
        });
    }
}
