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
   - Create client: `product-service`
   - Set client secret
   - Details keycloak setup steps are in real-config.json under resources

3. **Testing**
   - Get the keycloak token using
   
     curl -X POST http://localhost:8080/realms/my-app-realm/protocol/openid-connect/token \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "grant_type=password" \
     -d "client_id=product-service" \
     -d "client_secret=YOUR_CLIENT_SECRET_HERE" \
     -d "username=adminuser" \
     -d "password=test123"
   
   - Access get product url, pass bearer token   
    http://localhost:8081/products