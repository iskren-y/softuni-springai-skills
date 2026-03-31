package com.example

import org.springframework.ai.chat.client.ChatClientRequest
import org.springframework.ai.chat.client.ChatClientResponse
import org.springframework.ai.chat.client.advisor.api.AdvisorChain
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.MessageType
import org.springframework.ai.chat.messages.ToolResponseMessage
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.model.ModelOptionsUtils
import org.springframework.util.StringUtils

import groovy.util.logging.Slf4j

@Slf4j
class CustomLoggingAdvisor implements BaseAdvisor {

    private final int order

    boolean showSystemMessage = true

    boolean showAvailableTools = true

    private CustomLoggingAdvisor(int order, boolean showSystemMessage, boolean showAvailableTools) {
        this.order = order
        this.showSystemMessage = showSystemMessage
        this.showAvailableTools = showAvailableTools
    }

    @Override
    int getOrder() {
        return this.order
    }

    @Override
    ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {

        def sb = new StringBuilder("\nUSER: ")

        if (showSystemMessage && chatClientRequest.prompt()?.getSystemMessage() != null) {
            sb.append("\n - SYSTEM: ${first(chatClientRequest.prompt().getSystemMessage().getText(), 60)}")
        }

        if (showAvailableTools) {
            def tools = "No Tools"

            ChatOptions options = chatClientRequest?.prompt()?.getOptions()
            if (options != null && options.getClass().simpleName == 'GoogleGenAiChatOptions') {
                tools = options.toolCallbacks*.toolDefinition*.name()
            }

            sb.append("\n - TOOLS: ${ModelOptionsUtils.toJsonString(tools)}")
        }

        Message lastMessage = chatClientRequest.prompt()?.getLastUserOrToolResponseMessage()

        if (lastMessage?.getMessageType() == MessageType.TOOL) {
            ToolResponseMessage toolResponseMessage = (ToolResponseMessage) lastMessage
            toolResponseMessage.responses.each { toolResponse ->
                def tr = "${toolResponse.name()}: ${first(toolResponse.responseData(), 300)}"
                sb.append("\n - TOOL-RESPONSE: ${tr}")
            }
        } else if (lastMessage?.getMessageType() == MessageType.USER) {
            if (StringUtils.hasText(lastMessage.getText())) {
                sb.append("\n - TEXT: ${first(lastMessage.getText(), 300)}")
            }
        }

        log.info(sb.toString())

        return chatClientRequest
    }

    @Override
    ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        def sb = new StringBuilder("\nASSISTANT: ")

        if (chatClientResponse?.chatResponse() == null || chatClientResponse?.chatResponse()?.getResults() == null) {
            sb.append(" No chat response ")
            log.info(sb.toString())
            return chatClientResponse
        }

        chatClientResponse.chatResponse().results.each { generation ->
            def message = generation.output
            if (message?.toolCalls) {
                message.toolCalls.each { toolCall ->
                    sb.append("\n - TOOL-CALL: ")
                            .append(toolCall.name())
                            .append(" (")
                            .append(toolCall.arguments())
                            .append(")")
                }
            }

            if (message?.text) {
                if (StringUtils.hasText(message.text)) {
                    sb.append("\n - TEXT: ${first(message.text, 200)}")
                }
            }
        }

        log.info(sb.toString())

        return chatClientResponse
    }

    private String first(String text, int n) {
        if (text.length() <= n) {
            return text
        }
        return text.substring(0, n) + "..."
    }

    static Builder builder() {
        return new Builder()
    }

    static class Builder {

        private int order = 0

        private boolean showSystemMessage = true

        private boolean showAvailableTools = true

        Builder order(int order) {
            this.order = order
            return this
        }

        Builder showSystemMessage(boolean showSystemMessage) {
            this.showSystemMessage = showSystemMessage
            return this
        }

        Builder showAvailableTools(boolean showAvailableTools) {
            this.showAvailableTools = showAvailableTools
            return this
        }

        CustomLoggingAdvisor build() {
            return new CustomLoggingAdvisor(this.order, this.showSystemMessage,
                    this.showAvailableTools)
        }

    }
}
