---
name: analyst
description: Retrieves Bulgarian company and person data from the public CompanyBook.BG Trade Registry API. Use when user asks for data on individuals and/or business entities in Bulgaria.
---

# Trade Registry Analyst

Access Bulgarian Trade Registry data via CompanyBook.BG API using the secure wrapper script.

## Authentication (Required for ALL endpoints)

## Wrapper Script

Use `/app/.agent/skills/trade-registry/scripts/companybook-curl.sh` for **every** request instead of raw `curl`.  
The script automatically:
- Injects the required header automatically.
- Passes through all arguments you provide.

## LLM Command Generation Rules (Mandatory)

When generating a command the LLM **MUST**:
- Never use raw `curl`
- Include all original flags and parameters exactly as shown in the examples
- Use `--get --data-urlencode` for any query parameters (the wrapper handles them)
- Output the command as a single executable bash block
- Never include the real key value or any placeholder other than the wrapper itself

## Additional Reference
For full API documentation, parameter details, and data models, see [api-docs.md](./api-docs.md).

## Available Operations

### Company Operations

#### Get Company Data
**Purpose**: Get full company data by name or UIC

**Usage**:

# By UIC
```bash
companybook-curl.sh -s "https://api.companybook.bg/api/companies/123456789?with_data=true"
```

# By company name (search first, then get details)
```bash
companybook-curl.sh -s "https://api.companybook.bg/api/companies/search" --get --data-urlencode "name=Телерик" --data-urlencode "with_data=true" --data-urlencode "limit=1"
```

# Search companies with advanced filters
```bash
companybook-curl.sh -s "https://api.companybook.bg/api/companies/search" --get --data-urlencode "name=Телерик" --data-urlencode "limit=20"
```

# Search persons and their company affiliations
```bash
companybook-curl.sh -s "https://api.companybook.bg/api/people/search" --get --data-urlencode "name=Иван Петров" --data-urlencode "limit=20"
```

# Search both companies and people simultaneously
```bash
companybook-curl.sh -s "https://api.companybook.bg/api/shared/search" --get --data-urlencode "name=Иван" --data-urlencode "limit=10"
```

# Get total counts in the registry
```bash
companybook-curl.sh -s "https://api.companybook.bg/api/companies/count"
companybook-curl.sh -s "https://api.companybook.bg/api/people/count"
```