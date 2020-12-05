package command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Invite extends ListenerAdapter {
    public static void InviteBot(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage("https://discord.com/api/oauth2/authorize?client_id=715199577300664431&permissions=0&scope=bot").queue();
    }
}
