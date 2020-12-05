package database;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
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
import static object.Main.jda;

public class Player {

    public static void checkFolderExist(String folderDirectoryString){
        File folder = new File(folderDirectoryString);
        if(folder.exists()==false){
            folder.mkdirs();
        }
    }

    public static void checkPlayerInfoExist(String PlayerInfoPathString){
        File PlayerInfoFile = new File(PlayerInfoPathString);
        if(PlayerInfoFile.exists()==false) {

            Path PlayerInfoPath = Paths.get(PlayerInfoPathString);

            String[] defaultStatusType = {"level", "exp", "str", "agi", "vit", "int", "maxhp", "nowhp", "maxmp", "nowmp", "sp"};
            int[] defaultStatusValue = {1, 0, 10, 5, 10, 5, 40, 40, 20, 20, 4};
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

    public static void printPlayerLevelUp(GuildMessageReceivedEvent event, int playerLevel) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("LEVEL UP!");
        eb.setThumbnail("https://s3.us-west-2.amazonaws.com/secure.notion-static.com/75db0062-4f81-4009-869b-d9ddb2aab684/lvUp.gif?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20200723%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20200723T222947Z&X-Amz-Expires=86400&X-Amz-Signature=8328793fd9a3d989edc2072e86097bc134bc3ca33ab2c1779dac9cce63781fec&X-Amz-SignedHeaders=host");
        eb.setDescription(event.getMember().getAsMention()+" 용사님 축하합니다!\n Lv."+playerLevel+"로 레벨업 하셨습니다."+"\nSP +4");

        event.getChannel().sendMessage(eb.build()).queue();
        eb.clear();
    }

    public static int getPlayerStatus(String statType, Long PlayerIdLong){
        int statusValue = 0;

        Path PlayerInfoPath = Paths.get(userDataBaseDirectoryPath+PlayerIdLong+"\\Info.txt");

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
    public static void getRandomExp(GuildMessageReceivedEvent event, int length, int argsLength){
        Long playerIdLong = event.getMember().getIdLong();
        String playerDatabaseFolderString = userDataBaseDirectoryPath +playerIdLong;
        String playerInfoPathString = playerDatabaseFolderString+"\\Info.txt";

        checkFolderExist(playerDatabaseFolderString);
        checkPlayerInfoExist(playerInfoPathString);

        Path PlayerInfoPath = Paths.get(playerInfoPathString);
        Path LevelChartPath = Paths.get(levelChartTXTPath);

        String[] statusType = {"level", "exp", "str", "agi", "vit", "int", "maxhp", "nowhp", "maxmp", "nowmp", "sp"};
        int [] statusValue = new int[statusType.length];
        for(int i=0; i<statusType.length; i++){
            statusValue[i] = getPlayerStatus(statusType[i], playerIdLong);
        }

        Random random = new Random();
        int rdExp = (random.nextInt((length+statusValue[0]+statusValue[3]))+ statusValue[2]+statusValue[10])
                / (random.nextInt((length-argsLength+1))+1);

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
            statusValue[7] = statusValue[6];
            statusValue[0] += 1;
            statusValue[10] += 4;
            printPlayerLevelUp(event, statusValue[0]);
        }

        int[] playerStatus = {statusValue[0], statusValue[1], nextLvUpExp, rdExp};

        command.admin.Control.printUserLogToAdmins(event, playerStatus);
        updatePlayerStatus(PlayerInfoPath, statusType, statusValue);
    }
    public static void updatePlayerStatus(Path PlayerInfoPath, String[] statusType, int[] statusValue){
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

    public static void randomActivityExp(Member member, String activityType, String activityName, int activityTime){
        Long playerIdLong = member.getIdLong();
        String playerDatabaseFolderString = userDataBaseDirectoryPath +playerIdLong;
        String playerInfoPathString = playerDatabaseFolderString+"\\Info.txt";

        checkFolderExist(playerDatabaseFolderString);
        checkPlayerInfoExist(playerInfoPathString);

        Path PlayerInfoPath = Paths.get(playerInfoPathString);
        Path LevelChartPath = Paths.get(levelChartTXTPath);

        String[] statusType = {"level", "exp", "str", "agi", "vit", "int", "maxhp", "nowhp", "maxmp", "nowmp", "sp"};
        int [] statusValue = new int[statusType.length];
        for(int i=0; i<statusType.length; i++){
            statusValue[i] = getPlayerStatus(statusType[i], playerIdLong);
        }

        if(activityType.equalsIgnoreCase("START")) {
            int nextLvUpExp = 0;
            try {
                BufferedReader br = Files.newBufferedReader(LevelChartPath);
                br.readLine();
                for(int i=1; i<statusValue[0]; i++) {
                    br.readLine();
                }
                String PlayerLeveltoChart = br.readLine();
                nextLvUpExp = Integer.valueOf(PlayerLeveltoChart.split("\t")[1])+Integer.valueOf(PlayerLeveltoChart.split("\t")[2]);
                br.close();
            } catch (IOException e) { e.printStackTrace(); }

            int finalPlayerLevel = statusValue[0];
            int finalPlayerExp1 = statusValue[1];
            int finalNextLvUpExp = nextLvUpExp;
            for(int i = 0; i< Admin.adminIdLongs.length; i++){

                jda.getGuildById(651446773260484668L).getMemberById(Admin.adminIdLongs[i]).getUser().openPrivateChannel().queue((channel)-> {
                    EmbedBuilder eb = new EmbedBuilder();

                    eb.setColor(Color.green);

                    eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());
                    eb.setFooter("ID : "+member.getIdLong());
                    eb.setTitle(member.getUser().getAsTag()+" | "+" 모험 시작 ( "+activityName+" )");
                    eb.setDescription("\n현재 레벨 : "+ finalPlayerLevel +" ("+ finalPlayerExp1 +"/"+ finalNextLvUpExp +")");
                    channel.sendMessage(eb.build()).queue();
                });
            }
        }
        else {
            Random rd = new Random();
            int minimumExp = (statusValue[2]+statusValue[10]+1)*activityTime/8;
            int maximumExp = (rd.nextInt((statusValue[2]+statusValue[10]+1)+1)*activityTime/4-minimumExp);
            int rdExp = minimumExp + maximumExp;
            statusValue[1] += rdExp;


            int nextLvUpExp = 0;
            try {
                BufferedReader br = Files.newBufferedReader(LevelChartPath);
                br.readLine();
                for(int i=1; i<statusValue[0]; i++) {
                    br.readLine();
                }
                String PlayerLeveltoChart = br.readLine();
                nextLvUpExp = Integer.valueOf(PlayerLeveltoChart.split("\t")[1])+Integer.valueOf(PlayerLeveltoChart.split("\t")[2]);
                br.close();
            } catch (IOException e) { e.printStackTrace(); }

            if(statusValue[1]>=nextLvUpExp) {
                statusValue[7] = statusValue[6];
                statusValue[0] += 1;
                statusValue[10] += 4;
            }

            int finalPlayerExp = statusValue[1];
            int finalNextLvUpExp = nextLvUpExp;
            int finalPlayerLevel = statusValue[0];
            int finalRdExp = rdExp;

            for(int i = 0; i< Admin.adminIdLongs.length; i++){
                jda.getGuildById(651446773260484668L).getMemberById(Admin.adminIdLongs[i]).getUser().openPrivateChannel().queue((channel)-> {
                    EmbedBuilder eb = new EmbedBuilder();

                    eb.setColor(Color.red);

                    eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());
                    eb.setFooter("ID : "+member.getIdLong());
                    eb.setTitle(member.getUser().getAsTag()+" | "+"모험 종료 ( "+activityName+" )");
                    eb.setDescription("\n경험치 획득 : "+ finalRdExp
                            +"\n현재 레벨 : "+ finalPlayerLevel +" ("+ finalPlayerExp+"/"+ finalNextLvUpExp+")");
                    channel.sendMessage(eb.build()).queue();
                });
            }
        }

        updatePlayerStatus(PlayerInfoPath, statusType, statusValue);
    }

    public static void randomOnlineExp(Member member, String activity, int onlineTime){
        Long playerIdLong = member.getIdLong();
        String playerDatabaseFolderString = userDataBaseDirectoryPath +playerIdLong;
        String playerInfoPathString = playerDatabaseFolderString+"\\Info.txt";

        checkFolderExist(playerDatabaseFolderString);
        checkPlayerInfoExist(playerInfoPathString);

        Path PlayerInfoPath = Paths.get(playerInfoPathString);
        Path LevelChartPath = Paths.get(levelChartTXTPath);

        String[] statusType = {"level", "exp", "str", "agi", "vit", "int", "maxhp", "nowhp", "maxmp", "nowmp", "sp"};
        int [] statusValue = new int[statusType.length];
        for(int i=0; i<statusType.length; i++){
            statusValue[i] = getPlayerStatus(statusType[i], playerIdLong);
        }

        if(activity.equalsIgnoreCase("ONLINE")) {
            int nextLvUpExp = 0;
            try {
                BufferedReader br = Files.newBufferedReader(LevelChartPath);
                br.readLine();
                for(int i=1; i<statusValue[0]; i++) {
                    br.readLine();
                }
                String PlayerLeveltoChart = br.readLine();
                nextLvUpExp = Integer.valueOf(PlayerLeveltoChart.split("\t")[1])+Integer.valueOf(PlayerLeveltoChart.split("\t")[2]);
                br.close();
            } catch (IOException e) { e.printStackTrace(); }

            int finalPlayerLevel = statusValue[0];
            int finalPlayerExp = statusValue[1];
            int finalNextLvUpExp = nextLvUpExp;

            for(int i = 0; i< Admin.adminIdLongs.length; i++){

                jda.getGuildById(651446773260484668L).getMemberById(Admin.adminIdLongs[i]).getUser().openPrivateChannel().queue((channel)-> {
                    EmbedBuilder eb = new EmbedBuilder();

                    eb.setColor(Color.orange);

                    eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());
                    eb.setFooter("ID : "+member.getIdLong());
                    eb.setTitle(member.getUser().getAsTag()+" | "+" 온라인 성장 시작");
                    eb.setDescription("\n현재 레벨 : "+ finalPlayerLevel +" ("+ finalPlayerExp +"/"+ finalNextLvUpExp +")");
                    channel.sendMessage(eb.build()).queue();
                });
            }
        }
        else {
            Random rd = new Random();
            int minimumExp = (statusValue[2]+statusValue[10])*onlineTime/8;
            int maximumExp = ( rd.nextInt((statusValue[2]+statusValue[10]+1)+1 ) *onlineTime/4-minimumExp);
            int rdExp = minimumExp + maximumExp;

            statusValue[1] += rdExp;

            int nextLvUpExp = -1;
            try {
                BufferedReader br = Files.newBufferedReader(LevelChartPath);
                br.readLine();
                for(int i=1; i<statusValue[0]; i++) {
                    br.readLine();
                }
                String PlayerLeveltoChart = br.readLine();
                nextLvUpExp = Integer.valueOf(PlayerLeveltoChart.split("\t")[1])+Integer.valueOf(PlayerLeveltoChart.split("\t")[2]);
                br.close();
            } catch (IOException e) { e.printStackTrace(); }

            if(statusValue[1]>=nextLvUpExp) {
                statusValue[7] = statusValue[8];
                statusValue[0] += 1;
                statusValue[10] += 4;
            }

            int finalPlayerExp = statusValue[1];
            int finalNextLvUpExp = nextLvUpExp;
            int finalPlayerLevel = statusValue[0];
            int finalRdExp = rdExp;

            for(int i = 0; i< Admin.adminIdLongs.length; i++){
                jda.getGuildById(651446773260484668L).getMemberById(Admin.adminIdLongs[i]).getUser().openPrivateChannel().queue((channel)-> {
                    EmbedBuilder eb = new EmbedBuilder();

                    eb.setColor(Color.orange);

                    eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());
                    eb.setFooter("ID : "+member.getIdLong());
                    eb.setTitle(member.getUser().getAsTag()+" | "+"온라인 성장 종료");
                    eb.setDescription("\n경험치 획득 : "+ finalRdExp
                            +"\n현재 레벨 : "+ finalPlayerLevel +" ("+ finalPlayerExp+"/"+ finalNextLvUpExp+")");
                    channel.sendMessage(eb.build()).queue();
                });
            }
        }
        updatePlayerStatus(PlayerInfoPath, statusType, statusValue);
    }
}
