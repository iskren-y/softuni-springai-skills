package com.example

import com.pengrad.telegrambot.TelegramBot
import org.springaicommunity.agent.tools.BraveWebSearchTool
import org.springaicommunity.agent.tools.FileSystemTools
import org.springaicommunity.agent.tools.ShellTools
import org.springaicommunity.agent.tools.SkillsTool
import org.springaicommunity.agent.tools.SmartWebFetchTool
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.ToolCallAdvisor
import org.springframework.ai.chat.model.ChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SpringBootApplication
class SpringAiSkillsExampleApp {

    @Value('${telegram.bot.apiKey}')
    String telegramBotApiKey

    @Value('${agent.brave.apikey}')
    String braveApiKey

    @Value('${agent.skills.dir}')
    List<Resource> agentSkillsDir

    @Value('${ai.system-instruction}')
    String systemInstruction

    private final ResourceLoader resourceLoader

    SpringAiSkillsExampleApp(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader
    }

    static void main(String[] args) {
        SpringApplication.run(SpringAiSkillsExampleApp, args)
    }

    @Bean
    TelegramBot telegramBot() {
        return new TelegramBot.Builder(telegramBotApiKey).build()
    }

    @Bean
    ExecutorService virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor()
    }

    @Bean
    ChatClient chatClient(ChatModel chatModel) {
        ChatClient webFetchChat = ChatClient.builder(chatModel).build()

        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultSystem(systemInstruction)
                .defaultToolCallbacks(
                        SkillsTool.builder().addSkillsResources(agentSkillsDir).build())
                .defaultTools(
                        FileSystemTools.builder().build(),
                        ShellTools.builder().build(),
                        SmartWebFetchTool.builder(webFetchChat).domainSafetyCheck(true).build(),
                        BraveWebSearchTool.builder(braveApiKey).build())
                .defaultAdvisors(
                        ToolCallAdvisor.builder().build(),
                        // Custom logging advisor
                        CustomLoggingAdvisor.builder()
                                .showAvailableTools(true)
                                .showSystemMessage(true)
                                .build()
                )
                .build()

        chatClient
    }
}
