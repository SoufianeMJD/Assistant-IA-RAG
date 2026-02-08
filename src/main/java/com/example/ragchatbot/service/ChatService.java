package com.example.ragchatbot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final InMemoryChatMemory chatMemory;

    public ChatService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.chatMemory = new InMemoryChatMemory();

        this.chatClient = chatClientBuilder
                .defaultSystem(
                        "You are an expert assistant specialized in answering questions based on provided documents. " +
                                "Answer in a concise manner using only the information from the context. " +
                                "If you don't know the answer based on the context, say so.")
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new SimpleLoggerAdvisor())
                .build();
    }

    public ChatResponse ask(String question, String conversationId, Set<String> sourceFiles) {
        Filter.Expression filterExpression = null;

        if (sourceFiles != null && !sourceFiles.isEmpty()) {
            FilterExpressionBuilder builder = new FilterExpressionBuilder();
            filterExpression = builder.in("source", sourceFiles.toArray(new String[0]));
        }

        SearchRequest searchRequest = SearchRequest.query(question)
                .withTopK(5)
                .withSimilarityThreshold(0.7);

        if (filterExpression != null) {
            searchRequest = searchRequest.withFilterExpression(filterExpression);
        }

        QuestionAnswerAdvisor questionAnswerAdvisor = new QuestionAnswerAdvisor(vectorStore, searchRequest);

        return chatClient.prompt()
                .user(question)
                .advisors(advisorSpec -> advisorSpec
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId)
                        .param(QuestionAnswerAdvisor.FILTER_EXPRESSION, filterExpression))
                .advisors(questionAnswerAdvisor)
                .call()
                .chatResponse();
    }
}
