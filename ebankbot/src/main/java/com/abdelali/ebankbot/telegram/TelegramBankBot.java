package com.abdelali.ebankbot.telegram;

import com.abdelali.ebankbot.agent.EbankAiAgent;
import com.fasterxml.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(
        name = "ebank.bot.telegram.enabled",
        havingValue = "true"
)
public class TelegramBankBot {

    private static final Logger log =
            LoggerFactory.getLogger(TelegramBankBot.class);

    private final EbankAiAgent agent;
    private final RestClient telegramClient;

    private long nextOffset = 0;

    public TelegramBankBot(
            EbankAiAgent agent,
            @Value("${ebank.bot.telegram.token}") String token) {

        this.agent = agent;

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(35));

        this.telegramClient = RestClient.builder()
                .baseUrl("https://api.telegram.org/bot" + token)
                .requestFactory(factory)
                .build();
    }

    @Scheduled(fixedDelay = 1000)
    public void pollMessages() {

        try {
            JsonNode response = telegramClient.post()
                    .uri("/getUpdates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "offset", nextOffset,
                            "timeout", 20,
                            "allowed_updates", List.of("message")
                    ))
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null ||
                    !response.path("ok").asBoolean()) {
                return;
            }

            for (JsonNode update : response.path("result")) {

                long updateId =
                        update.path("update_id").asLong();

                nextOffset = Math.max(
                        nextOffset, updateId + 1
                );

                JsonNode message = update.path("message");

                if (message.isMissingNode()) {
                    continue;
                }

                String question =
                        message.path("text").asText("").trim();

                if (question.isEmpty()) {
                    continue;
                }

                long chatId =
                        message.path("chat").path("id").asLong();

                long userId =
                        message.path("from").path("id").asLong();

                if (question.startsWith("/start")) {
                    sendMessage(chatId,
                            "Bienvenue sur E-Bank ! " +
                            "Posez-moi une question sur les clients " +
                            "ou les comptes bancaires.");
                    continue;
                }

                String conversationId =
                        "telegram:" + chatId + ":" + userId;

                String answer;

                try {
                    answer = agent.chat(
                            question, conversationId
                    );
                } catch (Exception e) {
                    log.error("Telegram AI request failed", e);

                    answer = "Le service bancaire intelligent " +
                            "est temporairement indisponible.";
                }

                sendMessage(chatId, answer);
            }

        } catch (Exception e) {
            log.error("Telegram polling failed", e);
        }
    }

    private void sendMessage(long chatId, String text) {

        String reply = text == null || text.isBlank()
                ? "Aucune réponse disponible."
                : text;

        if (reply.length() > 4000) {
            reply = reply.substring(0, 3997) + "...";
        }

        telegramClient.post()
                .uri("/sendMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "chat_id", chatId,
                        "text", reply
                ))
                .retrieve()
                .toBodilessEntity();
    }
}