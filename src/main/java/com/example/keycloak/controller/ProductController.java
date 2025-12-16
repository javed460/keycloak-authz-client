package com.example.keycloak.controller;

import com.example.keycloak.dto.Product;
import lombok.RequiredArgsConstructor;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.resource.AuthorizationResource;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.Permission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final AuthzClient authzClient;
    private static final String PRODUCT_RESOURCE_NAME = "Product Resource";

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(Authentication authentication) {
        String accessToken = extractToken(authentication);

        if (!checkPermission(PRODUCT_RESOURCE_NAME, "product:view", accessToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied: Requires 'product:view' permission.");
        }

        List<Product> products = Stream.of(
                new Product("p001", "Laptop", "A powerful machine."),
                new Product("p002", "Monitor", "4K display.")
        ).collect(Collectors.toList());

        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product, Authentication authentication) {
        String accessToken = extractToken(authentication);

        if (!checkPermission(PRODUCT_RESOURCE_NAME, "product:create", accessToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied: Requires 'product:create' permission (Admin role).");
        }

        product.setId(UUID.randomUUID().toString());

        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id, Authentication authentication) {
        String accessToken = extractToken(authentication);

        if (!checkPermission(PRODUCT_RESOURCE_NAME, "product:delete", accessToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied: Requires 'product:delete' permission (Admin role).");
        }

        return ResponseEntity.noContent().build();
    }

    private String extractToken(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getTokenValue();
        }
        throw new IllegalStateException("Authentication principal is not a JWT token.");
    }

    private boolean checkPermission(String resourceName, String scope, String accessToken) {
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission(resourceName, scope);

        try {
            AuthorizationResource authorization = authzClient.authorization(accessToken);
            AuthorizationResponse response = authorization.authorize(request);

            String rptToken = response.getToken();

            if (rptToken == null || rptToken.isEmpty()) {
                return false;
            }

            AuthorizationRequest.Metadata metadata = new AuthorizationRequest.Metadata();
            metadata.setResponseMode("permissions");
            request.setMetadata(metadata);

            List<Permission> permissions = authorization.getPermissions(request);

            if (permissions != null) {
                for (Object rawPermission : permissions) {
                    if (rawPermission instanceof Permission) {
                        Permission p = (Permission) rawPermission;

                        if (resourceName.equals(p.getResourceName()) && p.getScopes().contains(scope)) {
                            return true;
                        }
                    } else if (rawPermission instanceof Map) {
                        Map map = (Map) rawPermission;

                        if (resourceName.equals(map.get("rsname")) &&
                                map.get("scopes") instanceof java.util.Collection &&
                                ((java.util.Collection<?>) map.get("scopes")).contains(scope)) {
                            return true;
                        }
                    }
                }
            }
            return false;

        } catch (Exception e) {
            return false;
        }
    }
}