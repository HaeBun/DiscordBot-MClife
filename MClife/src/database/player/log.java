package database.player;

import net.dv8tion.jda.api.entities.Member;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import static database.Path.DirectoryPath.userDataBaseDirectoryPath;

public class log {


    public static void writePlayerLog(String activityType, String activityName, Member member) {
        Calendar calendar = Calendar.getInstance();  // 현재 날짜/시간 등의 각종 정보 얻기
        Long playerIdLong = member.getIdLong();

        int year = calendar.get(Calendar.YEAR);
        int month = (calendar.get(Calendar.MONTH) + 1);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24시간제
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String dayFormat = String.format("%04d-%02d-%02d", year, month, day);
        String timeFormat = String.format("%02d:%02d:%02d", hour, minute, second);

        checkLogFolderExist(member);
        checkTodayLogFolderExist(member, dayFormat);

        String sTodayLogFolderPath = userDataBaseDirectoryPath + playerIdLong + "\\log\\"+dayFormat;

        if(activityType.equalsIgnoreCase("ONLINE") || activityType.equalsIgnoreCase("OFFLINE")) {
            String sSystemLogPath = sTodayLogFolderPath+"\\System.txt";
            checkSystemLogExist(sSystemLogPath);
            String[] playerHistory = readPlayerHistory(sSystemLogPath);

            writeSystemLog(sSystemLogPath,activityType, timeFormat, member, playerHistory);
        } else {
            String sActivityLogPath = sTodayLogFolderPath+"\\Activity\\"+activityName+".txt";
            checkActivityFolderExist(member, dayFormat);
            checkActivityLogExist(sActivityLogPath);
            String[] playerHistory = readPlayerHistory(sActivityLogPath);

            writeActivityLog(sActivityLogPath,activityType,timeFormat,activityName,member,playerHistory);
        }
    }

    public static void writeActivityLog(String sActivityLogPath, String activityType, String timeFormat, String activityName, Member member, String[] playerHistory) {
        try {
            BufferedWriter bw = Files.newBufferedWriter(Paths.get(sActivityLogPath),StandardCharsets.UTF_8);
            int historyLength = playerHistory.length;
            boolean waiting = false;
            if(historyLength > 0){
                int frontTime = Integer.valueOf(playerHistory[playerHistory.length-1].split("\t")[1].split(":")[0])*3600
                        + Integer.valueOf(playerHistory[playerHistory.length-1].split("\t")[1].split(":")[1])*60
                        + Integer.valueOf(playerHistory[playerHistory.length-1].split("\t")[1].split(":")[2]);

                int nowTime = Integer.valueOf(timeFormat.split(":")[0]) * 3600
                        + Integer.valueOf(timeFormat.split(":")[1]) * 60
                        + Integer.valueOf(timeFormat.split(":")[2]);

                if( (nowTime-frontTime) < 2
                        && playerHistory[historyLength-1].split("\t")[0].equalsIgnoreCase("START")
                        && activityType.equalsIgnoreCase("END")) {
                    historyLength --;
                    waiting = true;
                }
            }

            for(int i=0; i<historyLength; i++){
                bw.write(playerHistory[i]);
                bw.newLine();
            }
            if(waiting==true){
                bw.write("WAITING"+"\t"+timeFormat + "\t" + activityName + "\t"+ member.getUser().getAsTag());
            }
            else {
                bw.write(activityType+"\t"+timeFormat + "\t" + activityName + "\t"+ member.getUser().getAsTag());
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void checkActivityLogExist(String sActivityLogPath) {
        String[] symbols = {"\\","/",":","?","\"","<",">","|"};
//        String convertLogPath = "";
//        for(int i=0; i<sActivityLogPath.length(); i++){
//            boolean change=false;
//            for(int j=0; j<8; j++){
//                if(sActivityLogPath.split("")[i].equalsIgnoreCase(symbols[j])){
//                    convertLogPath += " ";
//                    change=true;
//                    break;
//                }
//            }
//            if(change!=true){
//                convertLogPath = sActivityLogPath.split("")[i];
//            }
//        }

        File playerLogFolder = new File(sActivityLogPath);

        if(playerLogFolder.exists()==false){
            try {
                BufferedWriter bw = Files.newBufferedWriter(Paths.get(sActivityLogPath), StandardCharsets.UTF_8);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeSystemLog(String sSystemLogPath,String activityType, String timeFormat, Member member,String[] playerHistory) {
        try {
            BufferedWriter bw = Files.newBufferedWriter(Paths.get(sSystemLogPath),StandardCharsets.UTF_8);
            for(int i=0; i<playerHistory.length; i++){
                bw.write(playerHistory[i]);
                bw.newLine();
            }

            if(activityType.equalsIgnoreCase("ONLINE") && playerHistory.length>0) {
                if(playerHistory[playerHistory.length-1].split("\t")[0].equalsIgnoreCase("ONLINE")==false) {
                    bw.write(activityType+"\t"+timeFormat + "\t" + activityType + "\t"+ member.getUser().getAsTag());
                }
                bw.close();
            } else {
                bw.write(activityType+"\t"+timeFormat + "\t" + activityType + "\t"+ member.getUser().getAsTag());
                bw.close();
            }

        } catch (IOException e) { e.printStackTrace(); }
    }
    public static void checkSystemLogExist(String sSystemLogPath){
        File playerLogFolder = new File(sSystemLogPath);

        if(playerLogFolder.exists()==false){
            try {
                BufferedWriter bw = Files.newBufferedWriter(Paths.get(sSystemLogPath), StandardCharsets.UTF_8);
                bw.close();
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public static void writeCommandLog(String Form, String messageId, String guildId, String channelId, Member member, String value){
        Long playerIdLong = member.getIdLong();

        String sCommandLogFolderPath = userDataBaseDirectoryPath + playerIdLong + "\\log\\command";
        checkCommandFolderExist(sCommandLogFolderPath);

        String sCommandLogPath = userDataBaseDirectoryPath + playerIdLong + "\\log\\command\\LatestCommand.txt";
        String[] dataType = {"MessageChannel",  "Form", "GuildLocation", "ChannelLocation","Value"};
        String[] dataValue = {messageId,Form,guildId,channelId, value};

        try {
            BufferedWriter bw = Files.newBufferedWriter(Paths.get(sCommandLogPath),StandardCharsets.UTF_8);

            for(int i=0; i<dataType.length; i++){
                if(dataValue[i]==null){
                    dataValue[i]="null";
                }
                bw.write(dataType[i]+":"+dataValue[i]);
                bw.newLine();
            }

            bw.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
    public static void checkCommandFolderExist(String sCommandLogFolderPath){
        File playerLogFolder = new File(sCommandLogFolderPath);

        if(playerLogFolder.exists()==false){
            playerLogFolder.mkdirs();
        }
    }

    public static void checkLogFolderExist(Member member){
        Long playerIdLong = member.getIdLong();
        File playerLogFolder = new File(userDataBaseDirectoryPath+playerIdLong+"\\log");

        if(playerLogFolder.exists()==false){
            playerLogFolder.mkdirs();
        }
    }
    public static void checkTodayLogFolderExist(Member member, String dayFormat){
        Long playerIdLong = member.getIdLong();
        File playerLogFolder = new File(userDataBaseDirectoryPath+playerIdLong+"\\log\\"+dayFormat);

        if(playerLogFolder.exists()==false){
            playerLogFolder.mkdirs();
        }
    }
    public static void checkActivityFolderExist(Member member,String dayFormat){
        Long playerIdLong = member.getIdLong();
        File playerLogFolder = new File(userDataBaseDirectoryPath+playerIdLong+"\\log\\"+dayFormat+"\\Activity");

        if(playerLogFolder.exists()==false){
            playerLogFolder.mkdirs();
        }
    }


    public static String[] readPlayerHistory(String sSystemLogPath) {
        Path playerLogPath = Paths.get(sSystemLogPath);
        int logLength = 0;

        try {
            BufferedReader br = Files.newBufferedReader(playerLogPath);

            for(;;){
                if(br.readLine()==null){
                    break;
                }
                else{
                    logLength++;
                }
            }
            br.close();
        } catch (IOException e) { e.printStackTrace(); }

        String[] playerHistory = new String[logLength];

        try {
            BufferedReader br = Files.newBufferedReader(playerLogPath);
            for(int i=0; i<logLength; i++){
                playerHistory[i] = br.readLine();
            }
            br.close();
        } catch (IOException e) { e.printStackTrace(); }

        return playerHistory;
    }



}
