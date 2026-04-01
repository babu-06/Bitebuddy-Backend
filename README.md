# BiteBuddy Backend

Spring Boot backend for the BiteBuddy seafood ordering app.

## Live API

- API Base URL: [https://bitebuddy-production.up.railway.app/api](https://bitebuddy-production.up.railway.app/api)
- Frontend: [https://bitebuddy-frontent.vercel.app](https://bitebuddy-frontent.vercel.app)

## Tech Stack

- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- PostgreSQL
- Railway for hosting

## Features

- Customer signup and login
- Customer profile updates
- Cart management
- Order placement
- Order history

## Branches

- `deployment` - branch used for Railway deployment
- `main` - stable backend branch
- `development` - feature work and testing

## Local Run

1. Make sure Java and Maven wrapper are available
2. Configure database settings or environment variables
3. Run:

```powershell
.\mvnw.cmd spring-boot:run
```

The app uses:

- `PORT` for server port
- `APP_CORS_ALLOWED_ORIGINS` for exact allowed frontend origins
- `APP_CORS_ALLOWED_ORIGIN_PATTERNS` for wildcard frontend origins

Default port:

- `8080`

## Deployment

### Railway

1. Connect the GitHub backend repo to Railway
2. Use the `deployment` branch for production
3. Set required database and app environment variables
4. Redeploy after backend changes

## CORS

The backend is configured to allow:

- `https://bitebuddy-frontent.vercel.app`
- `https://*.vercel.app`
- local development origins such as `http://localhost:5500`

If the frontend shows `Failed to fetch`, check:

- Railway service is running
- frontend is calling the correct API URL
- CORS headers are present on `/api/**` responses
