package user;


import database.Player;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.EventListener;


import java.util.Calendar;

import static database.Path.DirectoryPath.userDataBaseDirectoryPath;
import static object.Main.jda;

// #-------------------------------------------------------------------------------------------#
// 2020-07-17 | TeamHaeBun
//
// 한 사람이 여러 길드에 bot과 함께 있는 경우 GenericEvent 가 길드 수 만큼 반복됨.
// 나름의 고안 : 데이터베이스에 유저 로그를 만들어서 같은 시간 내에 일어난 여러 일을 하나로 처리(중복 실행 방지).
// #-------------------------------------------------------------------------------------------#

public class Activity implements EventListener {

    public void onEvent(GenericEvent event){
        Calendar oCalendar = Calendar.getInstance();  // 현재 날짜/시간 등의 각종 정보 얻기

        int year = oCalendar.get(Calendar.YEAR);
        int month = (oCalendar.get(Calendar.MONTH) + 1);
        int day = oCalendar.get(Calendar.DAY_OF_MONTH);

        int hour = oCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = oCalendar.get(Calendar.MINUTE);
        int second = oCalendar.get(Calendar.SECOND);
        String timeFormat = String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
        String dayFormat = String.format("%04d-%02d-%02d", year, month, day);
        if(event instanceof UserActivityEndEvent) {
            UserActivityEndEvent event2 = (UserActivityEndEvent) event;
            if(! event2.getUser().isBot()&& jda.getUserById(event2.getMember().getIdLong()).getMutualGuilds().get(0).equals(event2.getGuild())) {
                //IDE Print
                System.out.println("END\t"+timeFormat + "\t" + event2.getOldActivity().getName() + "\t"+ event2.getMember().getUser().getAsTag());

                database.player.log.writePlayerLog("END",event2.getOldActivity().getName(), event2.getMember());

                String activityName = event2.getOldActivity().getName();
                Long playerIdLong = event2.getMember().getIdLong();
                String sTodayLogFolderPath = userDataBaseDirectoryPath + playerIdLong + "\\log\\"+dayFormat;
                String sActivityLogPath = sTodayLogFolderPath+"\\Activity\\"+activityName+".txt";

                String[] playerHistory = database.player.log.readPlayerHistory(sActivityLogPath);

                int totalOnlineTime = getActivityTime(playerHistory);
                if(totalOnlineTime!=0 && playerHistory[playerHistory.length-1].split("\t")[0].equalsIgnoreCase("END")){
                    Player.randomActivityExp(event2.getMember(),"END",event2.getOldActivity().getName(), totalOnlineTime);
                }
            }
        }
        if(event instanceof UserActivityStartEvent) {
            UserActivityStartEvent event2 = (UserActivityStartEvent) event;
            if(! event2.getUser().isBot() && jda.getUserById(event2.getMember().getIdLong()).getMutualGuilds().get(0).equals(event2.getGuild())) {
                //IDE Print
                System.out.println("START"+"\t"+timeFormat + "\t" + event2.getNewActivity().getName()+"\t"+ event2.getMember().getUser().getAsTag());

                database.player.log.writePlayerLog("START",event2.getNewActivity().getName(), event2.getMember());
                Player.randomActivityExp(event2.getMember(),"START",event2.getNewActivity().getName(),0);
            }
        }
        else if(event instanceof UserUpdateOnlineStatusEvent){
            UserUpdateOnlineStatusEvent event2 = (UserUpdateOnlineStatusEvent) event;

            if(! event2.getUser().isBot()&& jda.getUserById(event2.getMember().getIdLong()).getMutualGuilds().get(0).equals(event2.getGuild())) {
                //IDE Print
                if(event2.getNewOnlineStatus().toString().equalsIgnoreCase("OFFLINE")){
                    System.out.println("OFFLINE"+"\t"+timeFormat+"\t"+"OFFLINE"+"\t"+event2.getMember().getUser().getAsTag());
                    database.player.log.writePlayerLog("OFFLINE","OFFLINE", event2.getMember());

                    Long playerIdLong = event2.getMember().getIdLong();
                    String sTodayLogFolderPath = userDataBaseDirectoryPath + playerIdLong + "\\log\\"+dayFormat;
                    String sSystemLogPath = sTodayLogFolderPath+"\\System.txt";

                    String[] playerHistory = database.player.log.readPlayerHistory(sSystemLogPath);
                    int totalOnlineTime = getOnlineTime(playerHistory);

                    if(totalOnlineTime!=0){
                        Player.randomOnlineExp(event2.getMember(),"OFFLINE", totalOnlineTime);
                    }
                }

                else if(event2.getNewOnlineStatus().toString().equalsIgnoreCase("ONLINE")){
                    System.out.println("ONLINE"+"\t"+timeFormat+"\t"+"ONLINE"+"\t"+event2.getMember().getUser().getAsTag());
                    Long playerIdLong = event2.getMember().getIdLong();
                    String sTodayLogFolderPath = userDataBaseDirectoryPath + playerIdLong + "\\log\\"+dayFormat;

                    database.player.log.writePlayerLog("ONLINE","ONLINE", event2.getMember());
                    Player.randomOnlineExp(event2.getMember(),"ONLINE", 0);
                }
            }
        }
    }

    public static int getOnlineTime(String[] playerHistory) {
        int latestActivityTime = 0;
        int nowActivityTime = 0;

        if(playerHistory.length>1){
            String[] latestSplit = playerHistory[playerHistory.length-2].split("\t");
            String[] nowSplit = playerHistory[playerHistory.length-1].split("\t");

            latestActivityTime = Integer.valueOf(latestSplit[1].split(":")[0]) * 3600
                    + Integer.valueOf(latestSplit[1].split(":")[1]) * 60
                    + Integer.valueOf(latestSplit[1].split(":")[2]);

            nowActivityTime = Integer.valueOf(nowSplit[1].split(":")[0]) * 3600
                    + Integer.valueOf(nowSplit[1].split(":")[1]) * 60
                    + Integer.valueOf(nowSplit[1].split(":")[2]);

            int totalOnlineTime = (nowActivityTime-latestActivityTime) / 60;
            return totalOnlineTime;
        }
        else if(playerHistory.length==1){
            String[] nowSplit = playerHistory[playerHistory.length-1].split("\t");
            nowActivityTime = Integer.valueOf(nowSplit[1].split(":")[0]) * 3600
                    + Integer.valueOf(nowSplit[1].split(":")[1]) * 60
                    + Integer.valueOf(nowSplit[1].split(":")[2]);

            int totalOnlineTime = (nowActivityTime-latestActivityTime) / 60;
            return totalOnlineTime;
        }
        return 0;
    }

    public static int getActivityTime(String[] playerHistory) {
        int latestActivityTime = 0;
        int nowActivityTime = 0;

        if(playerHistory.length>1){
            String latestStartTime = "00:00:00";
            String nowEndTime = playerHistory[playerHistory.length-1].split("\t")[1];

            for(int i=0; i<playerHistory.length; i++){
                if(playerHistory[i].split("\t")[0].equalsIgnoreCase("START")){
                    latestStartTime = playerHistory[i].split("\t")[1];
                }
            }

            latestActivityTime = Integer.valueOf(latestStartTime.split(":")[0]) * 3600
                    + Integer.valueOf(latestStartTime.split(":")[1]) * 60
                    + Integer.valueOf(latestStartTime.split(":")[2]);

            nowActivityTime = Integer.valueOf(nowEndTime.split(":")[0]) * 3600
                    + Integer.valueOf(nowEndTime.split(":")[1]) * 60
                    + Integer.valueOf(nowEndTime.split(":")[2]);

            int totalOnlineTime = (nowActivityTime-latestActivityTime) / 60;
            return totalOnlineTime;
        }
        else if(playerHistory.length==1){
            String[] nowSplit = playerHistory[playerHistory.length-1].split("\t");
            nowActivityTime = Integer.valueOf(nowSplit[1].split(":")[0]) * 3600
                    + Integer.valueOf(nowSplit[1].split(":")[1]) * 60
                    + Integer.valueOf(nowSplit[1].split(":")[2]);

            int totalOnlineTime = (nowActivityTime-latestActivityTime) / 60;
            return totalOnlineTime;
        }
        return 0;
    }
}
