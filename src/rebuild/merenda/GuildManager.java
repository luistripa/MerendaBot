package rebuild.merenda;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class GuildManager {

    private Guild guild;
    private TextChannel defaultChannel;

    public GuildManager(Guild guild, String defaultChannelId) {
        this.guild = guild;
        this.defaultChannel = guild.getTextChannelById(defaultChannelId);
    }

    public String getId() {
        return guild.getId();
    }

    public TextChannel getDefaultChannel() {
        return defaultChannel;
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

    public void sendMessage(String message) {
        defaultChannel.sendMessage(message).queue();
    }

    public void sendEmbed(MessageEmbed embed) {
        defaultChannel.sendMessageEmbeds(embed).queue();
    }
}
