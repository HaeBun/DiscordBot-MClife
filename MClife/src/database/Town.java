package database;

import database.Path.DirectoryPath;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static database.Path.DirectoryPath.*;
import static database.Player.checkFolderExist;

public class Town {
    public static String getTownInfo(String townInfoType, TextChannel channel, Guild guild){
        String result = "";

        Long townIdLong = channel.getIdLong();
        checkCountryFolderExist(guild);
        checkTownFolderExist(guild, channel);
        Path townInfoPath = Paths.get(DirectoryPath.countryDataBaseDirectoryPath+guild.getIdLong()+"\\"+townIdLong+"\\Hall.txt");

        try {
            BufferedReader br = Files.newBufferedReader(townInfoPath);
            String townInfoDataLine;
            for(;;){
                townInfoDataLine = br.readLine();
                if(townInfoDataLine==null){ //모두 탐색을 끝냈을 경우
                    br.close();
                    break;
                }

                else if(townInfoDataLine.split(":")[0].equalsIgnoreCase(townInfoType)) { //값을 찾은 경우
                    if(townInfoDataLine.split(":").length==1){
                        br.close();
                        break;
                    }
                    else if(townInfoDataLine.split(":").length>2){
                        for(int i=1; i<townInfoDataLine.split(":").length; i++){
                            result += townInfoDataLine.split(":")[i];

                            if((townInfoDataLine.split(":").length-1) != i){
                                result += ":";
                            }
                        }
                    }
                    else {
                        result += townInfoDataLine.split(":")[1];
                    }
                    br.close();
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    public static void getRandomExp(GuildMessageReceivedEvent event, int length, int spaceValue) {
        Long guildIdLong = event.getGuild().getIdLong();
        Long channelIdLong = event.getChannel().getIdLong();

        String townDatabaseFolderString = countryDataBaseDirectoryPath+guildIdLong+"\\"+channelIdLong;
        String townInfoPathString = townDatabaseFolderString+"\\Info.txt";

        checkFolderExist(townDatabaseFolderString);
        checkTownInfoExist(townInfoPathString);

        Path townInfoPath = Paths.get(townInfoPathString);
        Path LevelChartPath = Paths.get(townLevelChartDirectoryPath);

        String[] statusType = {"level", "exp", "maxbuilding", "nowbuilding"};
        int [] statusValue = new int[statusType.length];
        for(int i=0; i<statusType.length; i++){
            statusValue[i] = getTownStatus(statusType[i] , guildIdLong , channelIdLong);
        }

        Random random = new Random();
        int rdExp = (random.nextInt((length+statusValue[0])+ statusValue[3]))
                / (random.nextInt((length-spaceValue+1))+1);

        statusValue[1] += rdExp;    // 경험치 추가

        int nextLvUpExp = 0;
        // --------------------- 레벨업 확인 ------------------------
        try {
            BufferedReader br = Files.newBufferedReader(LevelChartPath);
            br.readLine();
            for(int i=1; i<statusValue[0]; i++) {
                br.readLine();
            }
            String playerLevelToChart = br.readLine();
            nextLvUpExp = Integer.valueOf(playerLevelToChart.split("\t")[1])+Integer.valueOf(playerLevelToChart.split("\t")[2]);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(statusValue[1]>=nextLvUpExp) {
            statusValue[0] += 1;
            printTownLevelUp(event, statusValue[0]);
        }

        int[] playerStatus = {statusValue[0], statusValue[1], nextLvUpExp, rdExp};

        command.admin.Control.printTownLogToAdmins(event, playerStatus);
        updateTownStatus(townInfoPath, statusType, statusValue);
    }

    public static void printTownLevelUp(GuildMessageReceivedEvent event, int townLevel) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("LEVEL UP!");
        eb.setThumbnail(event.getGuild().getIconUrl());
        eb.setDescription(event.getChannel().getName()+" 마을이 Lv."+townLevel+"로 레벨업 했어요!."+"\n"
                +"마을의 레벨이 올라갈 수록 더 많은 건물을 지을 수 있습니다."+"\n"
                +"마을의 레벨이 null이 되면 마을의 회관을 지을 수 있어요."+"\n"
                +"회관 건설 시 마을 대문을 설정할 수 있습니다. ( 마을 홍보 )");

        event.getChannel().sendMessage(eb.build()).queue();
        eb.clear();
    }
    public static void updateTownStatus(Path PlayerInfoPath, String[] statusType, int[] statusValue){
        try {
            BufferedWriter bw = Files.newBufferedWriter(PlayerInfoPath);
            for(int i=0; i<statusType.length; i++){
                bw.write(statusType[i]+":"+statusValue[i]);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getTownStatus(String statType,Long guildIdLong,Long channelIdLong){
        int statusValue = 0;

        Path PlayerInfoPath = Paths.get(countryDataBaseDirectoryPath+guildIdLong+"\\"+channelIdLong+"\\Info.txt");

        try {
            BufferedReader br = Files.newBufferedReader(PlayerInfoPath);
            String userInfoDataLine;
            for(;;){
                userInfoDataLine = br.readLine();
                if(userInfoDataLine==null){ //모두 탐색을 끝냈을 경우
                    br.close();
                    break;
                }
                if(userInfoDataLine.split(":")[0].equalsIgnoreCase(statType)) { //값을 찾은 경우
                    statusValue = Integer.valueOf(userInfoDataLine.split(":")[1]);
                    br.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return statusValue;
    }

    public static void checkTownInfoExist(String PlayerInfoPathString){
        File PlayerInfoFile = new File(PlayerInfoPathString);
        if(PlayerInfoFile.exists()==false) {

            Path PlayerInfoPath = Paths.get(PlayerInfoPathString);

            String[] defaultStatusType = {"level", "exp", "maxbuilding", "nowbuilding"};
            int[] defaultStatusValue = {1, 0, 1, 0};
            try {
                BufferedWriter bw = Files.newBufferedWriter(PlayerInfoPath, StandardCharsets.UTF_8);
                for(int i=0; i<defaultStatusType.length; i++){
                    bw.write(defaultStatusType[i]+":"+defaultStatusValue[i]);
                    bw.newLine();
                }
                bw.close();
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

//    public static String[] getGoto(TextChannel channel, Guild guild){
//
//
//        Long townIdLong = channel.getIdLong();
//        checkCountryFolderExist(guild);
//        checkTownFolderExist(guild, channel);
//        checkTownInfoExist(guild, channel);
//        Path townInfoPath = Paths.get(DirectoryPath.countryDataBaseDirectoryPath+guild.getIdLong()+"\\"+townIdLong+"\\Info.txt");
//
//        int gotoSize = 0;
//        try {
//            BufferedReader br = Files.newBufferedReader(townInfoPath);
//            String townInfoDataLine;
//            boolean findGoto = false;
//            for(;;){
//                townInfoDataLine = br.readLine();
//                if(townInfoDataLine==null){ //모두 탐색을 끝냈을 경우
//                    br.close();
//                    break;
//                }
//
//                if(townInfoDataLine.split(":")[0].equalsIgnoreCase("Goto")) { //값을 찾은 경우
//                    findGoto = true;
//                }
//                else if(findGoto==true){
//                    gotoSize++;
//                }
//            }
//        } catch (IOException e) { e.printStackTrace(); }
//        String[] gotoList = new String[gotoSize];
//
//        try {
//            BufferedReader br = Files.newBufferedReader(townInfoPath);
//            int gotoWrite = 0;
//            String townInfoDataLine;
//            boolean findGoto = false;
//            for(;;) {
//                townInfoDataLine = br.readLine();
//                if (townInfoDataLine == null) { //모두 탐색을 끝냈을 경우
//                    br.close();
//                    break;
//                }
//
//                if (townInfoDataLine.split(":")[0].equalsIgnoreCase("Goto")) { //값을 찾은 경우
//                    findGoto = true;
//                } else if(findGoto==true) {
//                    gotoList[gotoWrite] = townInfoDataLine.split("[.]")[1];
//                    gotoWrite++;
//                }
//            }
//        } catch (IOException e) { e.printStackTrace(); }
//
//        return gotoList;
//    }


    public static void checkCountryFolderExist(Guild guild) {
        File userDataFile = new File(DirectoryPath.countryDataBaseDirectoryPath+guild.getIdLong()+"\\");

        if(userDataFile.exists()==false) {
            userDataFile.mkdirs();
        }
    }
    public static void checkTownFolderExist(Guild guild, TextChannel channel) {
        File userDataFile = new File(DirectoryPath.countryDataBaseDirectoryPath+guild.getIdLong()+"\\"+channel.getIdLong());

        if(userDataFile.exists()==false) {
            userDataFile.mkdirs();
        }
    }
//    public static void checkTownInfoExist(Guild guild, TextChannel channel) {
//        File userDataFile = new File(DirectoryPath.countryDataBaseDirectoryPath+guild.getIdLong()+"\\"+channel.getIdLong()+"\\Info.txt");
//
//        Path townInfoPath = Paths.get(DirectoryPath.countryDataBaseDirectoryPath+guild.getIdLong()+"\\"+channel.getIdLong()+"\\Info.txt");
//        if(userDataFile.exists()==false) {
//            try {
//                BufferedWriter bw = Files.newBufferedWriter(townInfoPath, StandardCharsets.UTF_8);
//                bw.write("Title:");
//                bw.newLine();
//                bw.write("Description:");
//                bw.newLine();
//                bw.write("Image:");
//                bw.newLine();
//                bw.write("Thumbnail:");
//                bw.newLine();
//                bw.write("Footer:");
//
//                bw.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
