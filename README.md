# DiscordBot-MClife
디스코드 유저의 모든 활동 정보를 모으고 순위를 매겨주는 봇(앰생이) - 하다만 프로젝트

2020 07-01 ~ 2020 08-21 기간동안 기능 구현하는 것에만 집중해보았음

내가 쓸 목적으로 만들었기에 코드가독성은 개나 주었음.

구글에 검색해도 이와 관련된 아이디어와 기능이 담긴 봇은 하나도 나오지 않았음.

구현하는데 많은 시행착오도 있었는데, 가장 어려웠던 점은 유저들이 플레이하는 게임을 변경할 때

게임1 start

게임2 start

게임1 end

이딴식으로 정보가 와서 이걸 내가 원하는 대로 저장할 때 애를 많이 먹음.

저 기능을 구현하는데 크게 오래 걸리지 않았다. (2일 고민) 

그냥 단순히 기능만 구현된 봇을 누가 쓰기라도 하냐, 게임으로라도 만들어보자 해서 시작된 고생.

어떤식으로 UI를 만들지 부터 시작해서 몇번의 큰 수정을 하게됨.


유저데이터는 약 260명 분의 데이터를 수집했음. ( 문제 시 DB 공유 중지 )

https://o365seoil-my.sharepoint.com/:f:/g/personal/kimit1279_office_seoil_ac_kr/Er4CBl5z_IROoeuaCcNTzwABzgTAkeWxWU8vZRxHEP3NcQ?e=DhBoeV

개인 DM으로 봇 로그를 수집해 보았는데, 약 20만 5천개의 활동로그를 전송받음. ( 7월 21일 ~ End )

그 이외에는 파일 입출력으로 고생하다보니 DB의 소중함을 알게 됨

당시 DB를 배우지 않아 파일 입출력으로 모든 것을 처리했음.

지금은 MySQL을 다룰 수 있기에 12월 중반에 MySQL로 따로 처리해서 기록봇을 제작하려고 함. ( 의뢰받은 교육 앱개발이 먼저지만 )

이걸 하면서 느낀점.

게임은 혼자 만들지 말자. 너무 스케일이 크다.

+ 너무 많이 우려먹었는지 Discord로부터 무언가 메시지가 왔음.

Hey 팀해분!

It's your friendly neighborhood Bots & API Team checking in. If you're reading this message, it's because you created an application in our Developer Portal: https://discord.com/developers.

We come bearing news, updates, and a one-time "Get Out of Rate Limits Free" card! Okay well, not the last one, but it caught your attention. We've got a few things to share with you, so let's get on with it.

Developer Policy and Terms of Service Update

Everyone's favorite time of the year, Terms of Service Update Season, is upon us. We're updating our Developer Terms of Service — which you can find here: https://discord.com/developers/docs/legal — to make it more human-readable.

That's why we've also split it into two documents: our Developer Terms of Service: https://discord.com/developers/docs/legal and our Developer Policy: https://discord.com/developers/docs/policy. The Terms of Service covers all of the strictly legal bits of operating on our platform. Our Developer Policy covers our beliefs and guidelines on good use of our APIs that we use to guide our actions. Both are at the heart of what we believe being a good developer on Discord is about.

We hope these changes make our terms and policy easier to understand.

Discord API Domain Migration

Last month, we excitedly announced our official move to discord.com. It was a long time in the making, and the work isn't done yet! For now, our API will continue to handle requests made to discordapp.com. On November 7, 2020 we will be dropping support for the discordapp.com domain in favor of discord.com. Please ensure that your libraries, bots, and applications are updated accordingly.

Due to technical constraints, our CDN domain will not be migrated and will remain cdn.discordapp.com for the foreseeable future.

Bot Verification Reminder

Thanks to all of you that have gone through our bot verification process so far. The initial wave of applications was huge, and we recognize that we were slower than promised. Thank you all for your continued patience and support as we made it through all your applications. To date, nearly half of all eligible bots have been verified. Great work!

For those of you that have not yet gone through our verification process, you can do so today by visiting your app in the Developer Portal. If your bot is in more than 75 guilds, you can begin the process. Reminder that the unrestricted period ends on October 6, 2020. On that date, we will be imposing the restrictions outlined in our previous blog post: https://blog.discord.com/the-future-of-bots-on-discord-4e6e050ab52e.

You can still get verified after October 6, but go get a head start now and beat the rush!

For those of you excited for the fancy new features shown in our blog post...keep being patient! We're putting some finishing touches on new API tools that will enable features like the ones you saw, and we can't wait to show them to you. Be prepared SLASH excited for the next coming couple months. If you haven't seen our blog post, go read it now: https://blog.discord.com/the-future-of-bots-on-discord-4e6e050ab52e.

Happy coding!

대충 11월까지 내가만든 봇 다른 서버에 많이 안뿌려두면 못쓰게 한다는 내용이다.

새로운 계정 만들어다가 다시 하던가 앱 개발 배우던가 뭐 하던가 해야겠다.
