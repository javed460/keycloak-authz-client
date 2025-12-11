# Keycloak Authz Client Example

Spring Boot application demonstrating Keycloak Authorization Client integration.

## Features
- Spring Boot 3.2.3 + Java 21
- Keycloak 26.x Authz Client
- JWT authentication with Spring Security
- Single protected endpoint
- Single Authz Client demo endpoint

## Quick Start

1. **Install & Start Keycloak** (v26.0.0)
   - Download from keycloak.org
   - Run: `bin/kc.bat start-dev` (Windows)

2. **Configure Keycloak**
   - Create realm: `my-app-realm`
   - Create client: `spring-boot-app`
   - Set client secret

3. **Update Application**
   ```yaml
   # application.yml
   keycloak:
     auth-server-url: http://localhost:8080
     realm: spring-authz-demo
     client-id: spring-boot-app
     credentials:
       secret: your-client-secret-here

3. **Testing**
   - Get the keycloak token using
   
     curl -X POST http://localhost:8080/realms/spring-authz-demo/protocol/openid-connect/token \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "grant_type=password" \
     -d "client_id=spring-boot-app" \
     -d "client_secret=YOUR_CLIENT_SECRET_HERE" \
     -d "username=testuser" \
     -d "password=test123"