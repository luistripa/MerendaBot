package com.merendabot;

import com.merendabot.polls.Poll;
import com.merendabot.polls.PollHandler;
import net.dv8tion.jda.api.entities.*;
import com.merendabot.timers.TimerHandler;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.ArrayList;
import java.util.List;

public class GuildManager {

    private Guild guild;
    private TextChannel defaultChannel;
    private PollHandler pollHandler;
    private TimerHandler timerHandler;

    public GuildManager(Guild guild, String defaultChannelId) {
        this.guild = guild;
        this.defaultChannel = guild.getTextChannelById(defaultChannelId);
        pollHandler = new PollHandler();
        timerHandler = new TimerHandler(this);
    }

    public String getId() {
        return guild.getId();
    }

    public Guild getGuild() {
        return guild;
    }

    public TextChannel getDefaultChannel() {
        return defaultChannel;
    }

    public PollHandler getPollHandler() {
        return pollHandler;
    }

    public TimerHandler getTimerHandler() {
        return timerHandler;
    }

    public List<Member> getMembers() {
        return guild.loadMembers().get();
    }

    public List<Member> getNonBotMembers() {
        List<Member> members = getMembers();
        List<Member> nonBotMembers = new ArrayList<>();
        for (Member member : members) {
            if (!member.getUser().isBot())
                nonBotMembers.add(member);
        }
        return nonBotMembers;
    }

    public MessageAction generateMessage(String message) {
        return defaultChannel.sendMessage(message);
    }

    public MessageAction generateMessageEmbed(MessageEmbed embed) {
        return defaultChannel.sendMessageEmbeds(embed);
    }
}
