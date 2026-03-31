# Spring AI Skills Demo

A Telegram bot powered by Spring AI (Google Gemini) that provides access to Bulgarian Trade Registry data via the [CompanyBook.BG](https://companybook.bg) API.

## Core Idea

This project demonstrates how to build AI agents with custom skills using Spring AI. The bot accepts messages via Telegram, uses Google Gemini to understand user intent, and leverages custom skills to query the Bulgarian Trade Registry for company and person data.

Key features:
- **Telegram Interface**: Users interact with the AI via a Telegram bot
- **Custom Skills**: AI can use domain-specific tools (e.g., trade registry lookups)
- **Web Search**: Integrated Brave Web Search for additional context
- **Tool Execution**: AI autonomously decides which tools to call based on user queries

> **Note**: This is a demonstration project for a SoftUni engineering seminar. Testing is out of scope.

## Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Groovy |
| JDK | Java 24 (eclipse-temurin) |
| Framework | Spring Boot 4.0.4 |
| AI | Spring AI 2.0.0-M3, Google Gemini (gemini-3-flash-preview) |
| Agent Utils | spring-ai-agent-utils 0.5.0 |
| Telegram Bot | java-telegram-bot-api 9.4.1 |
| Build System | Gradle |
| Container | Docker |

## Setup Requirements

### Prerequisites
- Java 24
- Docker & Docker Compose
- Telegram Bot Token
- API Keys:
  - `GEMINI_API_KEY` - Google Gemini API key
  - `TELEGRAM_BOT_API_KEY` - Telegram bot token
  - `BRAVE_API_KEY` - Brave Search API key
  - `COMPANYBOOK_API_KEY` - [CompanyBook.BG](https://companybook.bg) API key

### Run with Docker

Export your API keys as environment variables, build and start the docker container:

```bash
export GEMINI_API_KEY=your_gemini_key
export TELEGRAM_BOT_API_KEY=your_telegram_key
export BRAVE_API_KEY=your_brave_key
export COMPANYBOOK_API_KEY=your_companybook_key

./gradlew clean build

docker-compose up --build
```

The bot will start listening for your messages on Telegram.

## Project Structure

```
src/main/groovy/com/example/
  ├── SpringAiSkillsExampleApp.groovy   # Main app & ChatClient config
  ├── TelegramBotListener.groovy         # Telegram bot & message handling
  └── CustomLoggingAdvisor.groovy        # Request/response logging

.agent/skills/trade-registry/
  ├── SKILL.md                          # Skill definition for LLM
  ├── api-docs.md                       # CompanyBook.BG API reference
  └── scripts/companybook-curl.sh       # API wrapper script
```

## License

For educational purposes only.