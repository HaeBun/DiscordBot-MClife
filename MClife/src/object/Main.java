package object;
/*-------------------------------------------------------------
// 디스코드 봇 앰생 개발
// 프로그래밍 : 김인태(TeamHaeBun), 장경준(ch73)
// 기획 : 권순찬(GooGangSan)
// 개발 시작일 : 2020-07-09 ~ 2020-08-14
//-------------------------------------------------------------*/


import Listener.FormLinker;
import command.Process;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Main { //Discord와 Bot을 연결해주는 Main 메서드
    public static JDA jda;  // Java Discord API의 약자. 즉 디스코드의 모든 기능을 담고 있음

    public static void main(String[] args) throws LoginException { // 프로그램 시작 지점
        jda = new JDABuilder(AccountType.BOT).setToken("NzE1MTk5NTc3MzAwNjY0NDMx.Xs5vgg.9RaC58W1unLck443esJIZzLe8dg").build(); // 디스코드 봇 연결
        jda.getPresence().setStatus(OnlineStatus.ONLINE); // 봇 온라인 상태로 전환
        jda.getPresence().setActivity(Activity.playing("플레이타임 기록")); // 봇 활동 설정
        jda.addEventListener(new Process());
        jda.addEventListener(new user.Activity());
        jda.addEventListener(new FormLinker());
    }

}
