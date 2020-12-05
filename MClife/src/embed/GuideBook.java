package embed;

import database.Path.DirectoryPath;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GuideBook {
    public static void two(String commandType, Member member, TextChannel channel, String guildId, String channelId) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("가이드북");
        eb.setThumbnail("https://koreasouth1-mediap.svc.ms/transform/thumbnail?provider=spo&inputFormat=png&cs=fFNQTw&docid=https%3A%2F%2Fo365seoil-my.sharepoint.com%3A443%2F_api%2Fv2.0%2Fdrives%2Fb!OKfALBAy-kiYbaBwVf2erowdgby7WsFEkHPWCMDqyB6Xs3VYxbHQTLgCl2GJbRyo%2Fitems%2F01NDXQEF6OLZ4V337ICVBKBQALLXH3M3S5%3Fversion%3DPublished&access_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJub25lIn0.eyJhdWQiOiIwMDAwMDAwMy0wMDAwLTBmZjEtY2UwMC0wMDAwMDAwMDAwMDAvbzM2NXNlb2lsLW15LnNoYXJlcG9pbnQuY29tQDgyNDQzZWFmLTc0ODEtNGI4MS05NDQ4LWRlYjU1ZjgwMDUyYiIsImlzcyI6IjAwMDAwMDAzLTAwMDAtMGZmMS1jZTAwLTAwMDAwMDAwMDAwMCIsIm5iZiI6IjE1OTYwMjQxNTYiLCJleHAiOiIxNTk2MDQ1NzU2IiwiZW5kcG9pbnR1cmwiOiJuNFRDZ2ltN3ludGY5T00raXAwTTVtNWVUbU5QcElPTVJmVlE1bXl2KzhVPSIsImVuZHBvaW50dXJsTGVuZ3RoIjoiMTE5IiwiaXNsb29wYmFjayI6IlRydWUiLCJjaWQiOiJNR05qWVRaaE9XWXRPREEyWmkxaU1EQXdMV1JqWldZdFltUmlNakU0TW1ObE5qTTEiLCJ2ZXIiOiJoYXNoZWRwcm9vZnRva2VuIiwic2l0ZWlkIjoiTW1Oak1HRTNNemd0TXpJeE1DMDBPR1poTFRrNE5tUXRZVEEzTURVMVptUTVaV0ZsIiwic2lnbmluX3N0YXRlIjoiW1wia21zaVwiXSIsIm5hbWVpZCI6IjAjLmZ8bWVtYmVyc2hpcHxraW1pdDEyNzlAb2ZmaWNlLnNlb2lsLmFjLmtyIiwibmlpIjoibWljcm9zb2Z0LnNoYXJlcG9pbnQiLCJpc3VzZXIiOiJ0cnVlIiwiY2FjaGVrZXkiOiIwaC5mfG1lbWJlcnNoaXB8MTAwMzIwMDA5ZWFhOGE4NUBsaXZlLmNvbSIsInR0IjoiMCIsInVzZVBlcnNpc3RlbnRDb29raWUiOiIzIn0.MU0yT3pJdnZhajVLVXVQOXJWRzBsS0pjN2FNaVVqaUs3MzJzbWlYM2l0dz0&encodeFailures=1&srcWidth=&srcHeight=&width=1345&height=617&action=Access");
        eb.setDescription("각종 도감을 볼 수 있습니다.");
        eb.addField(":small_red_triangle_down: Select",
                ":one: 검"+"\n"
                        +":two: 채찍" +"\n"
                        +":three: 조류"+"\n"
                        +":four: 책"+"\n"
                        +"\n"
                        +":five: 글리프"+"\n"
                ,true);
        eb.addField("", "",true);
        eb.setFooter("");
        channel.sendMessage(eb.build()).queue(message -> {
            message.addReaction("1️⃣").queue();
            message.addReaction("2️⃣").queue();
            message.addReaction("3️⃣").queue();
            message.addReaction("4️⃣").queue();
            message.addReaction("◀").queue();
            database.player.log.writeCommandLog(commandType,message.getId(), guildId, channelId, member, null);
        });
    }

    public static void getItemInfo(String commandType, Member member, TextChannel channel, String guildId, String channelId, int value){
        int itemCode = value;
        Path itemPath = Paths.get(DirectoryPath.itemDataBaseDirectoryPath+itemCode+".txt");
        EmbedBuilder eb = new EmbedBuilder();
        try {
            BufferedReader br = Files.newBufferedReader(itemPath);
            for(;;){
                String line = br.readLine();
                if(line == null){
                    break;
                }

                if(line.split(":")[0].equalsIgnoreCase("name")){
                    eb.setTitle(line.split(":")[1]);
                }
                else if(line.split(":")[0].equalsIgnoreCase("lore")){
                    String[] loreSplit = line.split(":")[1].split("%n");
                    String description = "";
                    for(int i=0; i<loreSplit.length; i++){
                        description += loreSplit[i]+"\n";
                    }
                    eb.setDescription(description);
                }
                else if(line.split(":")[0].equalsIgnoreCase("imageURL")){
                    eb.setImage(line.split(":")[1]+":"+line.split(":")[2]);
                }

                else if(line.split(":")[0].equalsIgnoreCase("Thumbnail")){
                    eb.setThumbnail(line.split(":")[1]+":"+line.split(":")[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        channel.sendMessage(eb.build()).queue(message -> {
            message.addReaction("⬅").queue();
            message.addReaction("➡").queue();
            message.addReaction("◀").queue();
            database.player.log.writeCommandLog(commandType, message.getId(),guildId, channelId, member, String.valueOf(value));
        });
    }
}
