# Stock-app-BE (WealthGrow backend)

Spring Boot 3.5.7 / Java 21 REST + WebSocket backend for the Stock-App frontend.
It stores users, wallets, portfolios and audit logs in MySQL (JPA) and stock
quotes pulled from TwelveData in MongoDB.

Package root: `com.Calculator.Stock`.

## Requirements

- JDK 21 (the project targets `release 21`; newer JDKs work as long as the
  Lombok annotation processor supports them)
- A reachable MySQL instance (schema is created/updated by `spring.jpa.hibernate.ddl-auto=update`)
- A reachable MongoDB instance (Atlas by default, see below)
- An SMTP account (Gmail by default) for transactional emails
- A TwelveData API key for stock quotes

## Configuration

All configuration lives in `src/main/resources/application.properties`.

### Environment variables that are already required

| Variable       | Used by                        | Purpose                      |
|----------------|--------------------------------|------------------------------|
| `USER_MONGO`   | `spring.data.mongodb.uri`      | MongoDB Atlas user name      |
| `PAROLA_MONGO` | `spring.data.mongodb.uri`      | MongoDB Atlas password       |

Example:

```bash
export USER_MONGO=my-mongo-user
export PAROLA_MONGO=my-mongo-password
```

### Optional properties

| Property                   | Default                 | Purpose                                                                 |
|----------------------------|-------------------------|-------------------------------------------------------------------------|
| `app.cors.allowed-origins` | `http://localhost:5173` | Comma-separated list of origins allowed by CORS (e.g. the frontend URL) |

It can be set in `application.properties` or as the environment variable
`APP_CORS_ALLOWED_ORIGINS`.

### Secrets that should be moved to environment variables (recommended)

The following values are currently hard-coded. Moving them out of the
repository is strongly recommended before deploying anywhere:

| Secret               | Where it is today                                  | Suggested change                                                                                      |
|----------------------|----------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| MySQL password       | `spring.datasource.password` in `application.properties` | `spring.datasource.password=${MYSQL_PASSWORD}`                                                  |
| Mail (SMTP) password | `spring.mail.password` in `application.properties` | `spring.mail.password=${MAIL_PASSWORD}`                                                               |
| TwelveData API key   | `twelvedata.apikey` in `application.properties`    | `twelvedata.apikey=${TWELVEDATA_API_KEY}`                                                             |
| JWT signing secret   | `SECRET_KEY` constant in `Util/JwtUtil.java`       | Add `app.jwt.secret=${JWT_SECRET}` to the properties and inject it with `@Value("${app.jwt.secret}")` (constructor injection) instead of the constant. The secret must be at least 64 bytes for HS512. |

Spring resolves `${VAR}` placeholders from environment variables, so after the
change you only need to `export` the variables before starting the app. You can
also supply a default for local development with `${VAR:default}`.

## Running

```bash
export USER_MONGO=... PAROLA_MONGO=...
./mvnw spring-boot:run
```

The server listens on `http://localhost:8080`.

Build a jar:

```bash
./mvnw -DskipTests package
java -jar target/Stock-0.0.1-SNAPSHOT.jar
```

Run the unit tests (they use Mockito only and need no database):

```bash
./mvnw test -Dtest='PortofolioServiceImplTest,UsersServiceImplTest,WalletServiceImplTest' -Dsurefire.failIfNoSpecifiedTests=false
```

`StockApplicationTests` boots the full context and therefore needs MySQL and
MongoDB.

## Authentication

Every endpoint except `/api/users/**`, `/ws/**` and `/error` requires a JWT:

```
Authorization: Bearer <token>
```

Obtain the token from `POST /api/users/login`. Tokens are valid for 10 hours.

## REST endpoints

Errors are returned as `{"message": "...", "statusCode": 400, "timestamp": "..."}`.
Bean Validation errors are returned as `{"<field>": "<message>", ...}` with
status 400.

### Users (`/api/users`, public)

| Method | Path                       | Body                                   | Response                                     |
|--------|----------------------------|----------------------------------------|----------------------------------------------|
| POST   | `/api/users/register`      | `{email, password, firstName, lastName}` | `{"message": "User registered successfully"}` (409 if the email exists) |
| POST   | `/api/users/login`         | `{email, password}`                    | `{"token": "<jwt>"}` (401 on a wrong password) |
| GET    | `/api/users`               | –                                      | current user (requires token)                |
| POST   | `/api/users/forgotPassword`| `{email}`                              | plain text, sends a 6-digit code by email    |
| PUT    | `/api/users/resetPassword` | `{email, password, code}`              | `{"message": "Password changed successfully"}` |

### Wallet (`/api/wallet`)

| Method | Path                     | Body       | Response                                  |
|--------|--------------------------|------------|-------------------------------------------|
| GET    | `/api/wallet/balance`    | –          | `{id, balance, investment}`               |
| POST   | `/api/wallet/AddFounds`  | `{amount}` | `{"message": "Deposit successful"}`       |
| POST   | `/api/wallet/Withdraw`   | `{amount}` | `{"message": "Withdraw successful"}`      |

### Portfolio (`/api/portofolio`)

| Method | Path                              | Body                                     | Response |
|--------|-----------------------------------|------------------------------------------|----------|
| POST   | `/api/portofolio/buy`             | `{symbol, currentPrice, amountToInvest}` | `{"message": "Your purchase was a success"}`. Deducts `amountToInvest` from the wallet and writes a `BUY` audit log. |
| POST   | `/api/portofolio/sell`            | `{symbol, currentPrice, withdrawAmount}` | `{"message": "The withdraw was a success"}`. Credits `withdrawAmount` to the wallet and writes a `SELL` audit log. A position whose shares reach 0 is removed. |
| GET    | `/api/portofolio/{symbol}`        | –                                        | `{id, symbol, averagePrice, amountOwned, shares, profit, total, userId}` (zeros if the symbol is not held) |
| GET    | `/api/portofolio/total`           | –                                        | see below |
| GET    | `/api/portofolio/profit/{symbol}` | –                                        | `{user_id, dailyProfitValue, dailyProfitPercentage, totalPortfolioProfitDaily, totalPortfolioProfitDailyPercentage}` (zeros if the symbol is not held) |

`GET /api/portofolio/total` returns:

```json
{
  "user_Id": 1,
  "total": 1500.0,
  "investedValue": 1500.0,
  "marketValue": 1620.5,
  "totalProfit": 120.5,
  "balance": 250.0,
  "portfolioChart": [
    { "symbol": "AAPL", "value": 1000.0, "percentage": 66.7, "marketValue": 1080.0, "profit": 80.0 }
  ]
}
```

`total` is the cost basis (kept for backward compatibility and equal to
`investedValue`); `marketValue` uses the latest stored quote of each symbol and
falls back to the cost basis when no quote exists.

### Stocks (`/api/stocks`)

| Method | Path                  | Body                           | Response |
|--------|-----------------------|--------------------------------|----------|
| POST   | `/api/stocks`         | raw symbol string, e.g. `AAPL` | TwelveData time-series response; new candles are persisted in MongoDB |
| GET    | `/api/stocks/values`  | –                              | latest quote for each tracked symbol (`AAPL, TSLA, GOOGL, MSFT, AMZN, NVDA, META, NFLX`) |
| GET    | `/api/stocks/charts`  | –                              | all stored candles as `{symbol, time, date, value}` |

Quotes are refreshed automatically every 30 minutes by a scheduled job.

### Audit log (`/api/audit`)

| Method | Path              | Body                                     | Response |
|--------|-------------------|------------------------------------------|----------|
| POST   | `/api/audit/add`  | `{symbol, total, date, price, shares, action}` | the saved entry |
| GET    | `/api/audit/get`  | –                                        | the current user's entries, newest first: `[{id, symbol, total, date, price, shares, action, user_id}]` with `action` = `BUY` or `SELL` |

## WebSocket

- Endpoint: `ws://localhost:8080/ws` (STOMP over SockJS, all origins allowed)
- Application prefix: `/app`; broker prefixes: `/topic`, `/queue`; user prefix: `/user`
- Topics published by the server:
  - `/topic/{email}` (email lower-cased and trimmed) – `{message, Status|status}` after a buy, sell, deposit or withdrawal
  - `/user/topic/audit` – the audit entry posted through `POST /api/audit/add`

Example payload on `/topic/{email}` after a buy:

```json
{ "message": "Your purchase was a success", "Status": "Success" }
```

## Known limitations

- Secrets (MySQL, SMTP, TwelveData, JWT) are still hard-coded; see the
  configuration section for how to externalise them.
- `GET /api/users` returns the full `UserDTO`, including the BCrypt password
  hash and the pending reset code. The frontend should not rely on these fields
  and the DTO should be trimmed in a future change.
- Money and share quantities are stored as `float`; rounding errors can
  accumulate over many trades.
- Buying and selling use the `currentPrice` sent by the client rather than the
  last stored quote, so prices are not validated server-side.
- The WebSocket broker is the in-memory simple broker and the `/ws` endpoint
  accepts any origin; messages on `/topic/{email}` can be subscribed to by any
  connected client that knows the email.
- The scheduled quote refresh sleeps 15 s between symbols on the scheduler
  thread; with 8 symbols a refresh takes about 2 minutes.
- Emails are sent asynchronously and failures are only logged, so a failed
  SMTP delivery is not reported to the caller.
- The password-reset code is a 6-digit number valid for 5 minutes and there is
  no rate limiting on `/api/users/forgotPassword` or `/api/users/login`.
