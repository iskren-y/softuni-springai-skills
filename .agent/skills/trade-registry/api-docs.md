# CompanyBook.BG API Reference

> Bulgarian company registry providing comprehensive data on companies and individuals through a public API.

## Architecture

- **Base URL**: `https://api.companybook.bg/api`
- **Authentication**: None required - completely free access
- **Response Format**: JSON
- **Caching**: 24-hour cache with 48-hour stale-while-revalidate

---

## Companies API

### GET /api/companies/:uic

Get detailed company information by UIC (Unique Identification Code).

**Parameters:**
- `uic` (required): Company UIC, accepts with or without "BG" prefix
- `with_data` (optional): Boolean - returns expanded data when `true`

**Response with with_data=false:**
```json
{
  "uic": "123456789",
  "name": "Example Ltd",
  "legalForm": "ООД",
  "status": "N",
  "transliteration": "Example OOD"
}
```

**Response with with_data=true:**
```json
{
  "company": {
    "id": "...",
    "uic": "123456789",
    "companyName": { "name": "...", "name_tags": [...] },
    "legalForm": "ООД",
    "status": "N",
    "seat": { "country": "...", "region": "...", "address": "..." },
    "contacts": { "email": "...", "phone": "...", "website": "..." },
    "managers": [...],
    "capital": { "amount": "...", "currency": "BGN" },
    "partners": [...],
    "nkids": [...],
    "lastUpdated": "..."
  },
  "history": [...],
  "daughters": [...]
}
```

---

### GET /api/companies/search

Search companies with multiple filters.

**Query Parameters:**
- `uic` (string): Exact UIC match
- `name` (string): Company name (partial match)
- `district` (number): District ID (1-56)
- `status` (boolean): `true` for active, `false` for inactive
- `legal_form` (string): Legal form
- `with_data` (boolean): Include full company data
- `limit` (integer): Results per page (default: 20)

---

### GET /api/companies/count

Get total number of companies in registry.

**Response:**
```json
{
  "total": 1234567
}
```

---

## People API

### GET /api/people/:indent

Get person information by identifier (Indent/LNCh).

**Parameters:**
- `indent` (required): Person identifier (Indent or LNCh number)
- `with_data` (optional): Boolean - returns full company list when `true`

**Response with with_data=false:**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "name": "Иван Петров",
  "indent": "1234567890",
  "companies": 3
}
```

**Response with with_data=true:**
```json
{
  "_id": "507f1f77bcf86cd799439011",
  "indent": "1234567890",
  "name": "Иван Петров",
  "personCompanies": [
    {
      "uic": "123456789",
      "company_name": { "name": "Example Ltd" },
      "legalForm": "Едноличен Търговец",
      "share": "20",
      "roles": [
        {
          "position": "Partner",
          "from": "2020-01-01"
        }
      ]
    }
  ],
  "nameTags": ["ivan", "petrov"]
}
```

---

### GET /api/people/search

Search people by name.

**Query Parameters:**
- `name` (required): Person name (minimum 3 characters)
- `with_data` (boolean): Include full company lists
- `limit` (integer): Results per page (default: 20)

**Response:**
```json
{
  "results": [
    {
      "id": "507f1f77bcf86cd799439011",
      "name": "Иван Петров",
      "indent": "1234567890",
      "companies": 3
    }
  ],
  "total": 42
}
```

---

### GET /api/people/count

Get total number of people in registry.

---

## Unified Search API

### GET /api/shared/search

Search both companies and people simultaneously.

**Query Parameters:**
- `name` (required): Search query (minimum 3 characters)
- `limit` (integer): Results per type (default: 3)

## Reference Data

### Districts (28 total)

| ID | Name |
|----|------|
| 1 | София (столица) / Sofia (capital) |
| 30 | Благоевград / Blagoevgrad |
| 31 | Бургас / Burgas |
| 32 | Варна / Varna |
| 33 | Велико Търново / Veliko Tarnovo |
| 34 | Видин / Vidin |
| 35 | Враца / Vratsa |
| 36 | Габрово / Gabrovo |
| 37 | Добрич / Dobrich |
| 38 | Кърджали / Kardzhali |
| 39 | Кюстендил / Kyustendil |
| 40 | Ловеч / Lovech |
| 41 | Монтана / Montana |
| 42 | Пазарджик / Pazardzhik |
| 43 | Перник / Pernik |
| 44 | Плевен / Pleven |
| 45 | Пловдив / Plovdiv |
| 46 | Разград / Razgrad |
| 47 | Русе / Ruse |
| 48 | Силистра / Silistra |
| 49 | Сливен / Sliven |
| 50 | Смолян / Smolyan |
| 51 | София / Sofia (region) |
| 52 | Стара Загора / Stara Zagora |
| 53 | Търговище / Targovishte |
| 54 | Хасково / Haskovo |
| 55 | Шумен / Shumen |
| 56 | Ямбол / Yambol |

### Legal Forms (partial list)

- Еднолично дружество с ограничена отговорност (EOOD) - Single-member LLC
- Дружество с ограничена отговорност (OOD) - LLC
- Акционерно дружество (AD) - Joint-stock company
- Едноличен търговец (ET) - Sole trader
- Командитно дружество - Limited partnership
- Сдружение - Association

---

