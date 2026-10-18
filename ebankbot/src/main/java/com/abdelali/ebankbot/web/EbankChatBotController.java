package com.abdelali.ebankbot.web;

import com.abdelali.ebankbot.agent.EbankAiAgent;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EbankChatBotController {

    private final EbankAiAgent agent;

    public EbankChatBotController(EbankAiAgent agent) {
        this.agent = agent;
    }

    @GetMapping("/chat")
    public String chat(
            @RequestParam String query,
            @RequestParam String conversationId) {

        return agent.chat(query, conversationId);
    }
}