package com.abdelali.ebankbot.discord;

import com.abdelali.ebankbot.agent.EbankAiAgent;

import jakarta.annotation.PreDestroy;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@ConditionalOnProperty(
        name = "ebank.bot.discord.enabled",
        havingValue = "true"
)
public class DiscordBankBot extends ListenerAdapter {

    private static final Logger log =
            LoggerFactory.getLogger(DiscordBankBot.class);

    private final EbankAiAgent agent;
    private final ExecutorService executor =
            Executors.newFixedThreadPool(4);

    private final JDA jda;

    public DiscordBankBot(
            EbankAiAgent agent,
            @Value("${ebank.bot.discord.token}") String token) {

        this.agent = agent;

        this.jda = JDABuilder.createDefault(
                        token,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .addEventListeners(this)
                .build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) {
            return;
        }

        String content =
                event.getMessage().getContentRaw().trim();

        if (content.isEmpty()) {
            return;
        }

        String question;

        if (event.isFromGuild()) {

            if (!content.startsWith("!bank ")) {
                return;
            }

            question = content.substring(6).trim();

        } else {
            question = content;
        }

        if (question.isBlank()) {
            return;
        }

        String conversationId =
                "discord:" + event.getChannel().getId()
                + ":" + event.getAuthor().getId();

        event.getChannel().sendTyping().queue();

        executor.submit(() -> {

            try {
                String answer = agent.chat(
                        question, conversationId
                );

                if (answer == null || answer.isBlank()) {
                    answer = "Aucune réponse disponible.";
                }

                // Discord text messages have a 2000-character limit.
                if (answer.length() > 1900) {
                    answer = answer.substring(0, 1897) + "...";
                }

                event.getChannel()
                        .sendMessage(answer)
                        .queue();

            } catch (Exception e) {

                log.error("Discord AI request failed", e);

                event.getChannel()
                        .sendMessage(
                                "Le service bancaire intelligent " +
                                "est temporairement indisponible."
                        )
                        .queue();
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
        jda.shutdown();
    }
}