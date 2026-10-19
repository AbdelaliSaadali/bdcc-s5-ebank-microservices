package com.abdelali.ebankbot.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;

@Service
public class EbankAiAgent {

    private final ChatClient chatClient;

    public EbankAiAgent(ChatClient.Builder builder, SyncMcpToolCallbackProvider mcpTools) {

        ChatMemory memory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();

        this.chatClient = builder
                .defaultSystem("""
                    Tu es un assistant bancaire intelligent pour E-Bank.
                    Tu réponds en français de manière claire et précise.

                    Tu es spécialisé dans les clients et les comptes bancaires.

                    Si une question concerne des données bancaires
                    auxquelles tu n'as pas accès, indique-le clairement.
                    N'invente jamais les clients, les comptes ou les soldes.

                    Pour les questions sans rapport avec le domaine
                    bancaire, explique poliment ton domaine de compétence.
                    """)
                .defaultAdvisors(
                    MessageChatMemoryAdvisor.builder(memory).build()
                )
                .defaultToolCallbacks(mcpTools)
                .build();
    }

    public String chat(String query, String conversationId) {

        return chatClient.prompt()
                .user(query)
                .advisors(advisor -> advisor.param(
                    ChatMemory.CONVERSATION_ID,
                    conversationId
                ))
                .call()
                .content();
    }
}