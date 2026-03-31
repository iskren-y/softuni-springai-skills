package com.example

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.Chat
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse
import jakarta.annotation.PreDestroy
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.metadata.Usage
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

import java.util.concurrent.ExecutorService

@Component
class TelegramBotListener {

    private final TelegramBot telegramBot
    private final ChatClient chatClient
    private final ExecutorService executorService

    TelegramBotListener(TelegramBot telegramBot,
                        ChatClient chatClient,
                        ExecutorService executorService) {
        this.telegramBot = telegramBot
        this.chatClient = chatClient
        this.executorService = executorService
    }

    private static boolean isValidPrivateMessage(Update update) {
        def message = update.message()
        if (message == null) {
            return false
        }
        if (message.from()?.isBot()) {
            return false
        }
        if (message.chat().type() != Chat.Type.Private) {
            return false
        }
        if (message.text() == null || message.text().isBlank()) {
            return false
        }
        return true
    }

    @PreDestroy
    void shutdown() {
        executorService.shutdown()
    }

    /**
     * Starts the updates listener after the Spring application is ready.
     */
    @EventListener(ApplicationReadyEvent.class)
    void init() {
        telegramBot.setUpdatesListener({ updates ->
            updates.each { Update update ->
                if (isValidPrivateMessage(update)) {
                    executorService.submit({ -> handleMessage(update) })
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL
        }, { exception ->
            println "[TelegramBot] Exception: ${exception.message}"
            if (exception.response()) {
                println "[TelegramBot] Error: ${exception.response().errorCode()} - ${exception.response().description()}"
            }
        })
        println "[TelegramBot] Started listening for messages"
    }

    /**
     * Processes the message and sends an AI-generated response.
     *
     * @param update the incoming Telegram update
     */
    private void handleMessage(Update update) {
        def chatId = update.message().chat().id()
        def userMessage = update.message().text()

        println("User message: $userMessage")

        try {

            ChatResponse response = chatClient.prompt()
                    .user(userMessage)
                    .call()
                    .chatResponse()

            Usage usage = response?.metadata?.usage

            println("OUTPUT: ${usage?.completionTokens}; INPUT: ${usage?.promptTokens}; TOTAL: ${usage?.totalTokens}")

            sendMessage(chatId, response?.result?.output?.text)
        } catch (Exception e) {
            println "[TelegramBot] Error processing message: ${e.message}"
            sendMessage(chatId, "Sorry, I encountered an error processing your message.")
        }
    }

    /**
     * Sends a text message to the specified chat.
     *
     * @param chatId the target chat ID
     * @param text the message text to send
     */
    private void sendMessage(Long chatId, String text) {
        if (text == null || text.isBlank()) {
            return
        }
        def request = new SendMessage(chatId, text).parseMode(ParseMode.HTML)
        SendResponse response = telegramBot.execute(request)
        if (!response.isOk()) {
            println "[TelegramBot] Send error: ${response.errorCode()} - ${response.description()}"
        }
    }
}
