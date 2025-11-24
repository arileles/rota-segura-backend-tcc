package br.com.furb.rotasegura.controllers;

import java.net.URI;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.com.furb.rotasegura.domain.records.UserRecord;
import br.com.furb.rotasegura.repositories.RoleRepository;
import br.com.furb.rotasegura.services.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final RoleRepository roleRepository;

    public UserController(UserService userService, ObjectMapper objectMapper, Validator validator, RoleRepository roleRepository) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.roleRepository = roleRepository;
    }

    @PostMapping
    public ResponseEntity<UserRecord> create(@RequestBody UserRecord user, UriComponentsBuilder uriBuilder) {
        UserRecord created = userService.save(user);
        URI location = uriBuilder.path("/v1/users/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<UserRecord>> list(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRecord> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserRecord> patch(@PathVariable UUID id, @RequestBody JsonNode patch) {
        UserRecord user = userService.findById(id);

        try {
            // For records (immutable), merge by converting to ObjectNode, applying patch fields and
            // converting back to UserRecord. Preserve id and createDate to avoid accidental changes.
            if (!(patch instanceof ObjectNode)) {
                throw new IllegalArgumentException("Patch must be a JSON object");
            }

            ObjectNode originalNode = objectMapper.valueToTree(user);
            ObjectNode merged = originalNode.deepCopy();
            merged.setAll((ObjectNode) patch);

            // Preserve immutable fields (id and createDate) from original user
            merged.set("id", objectMapper.valueToTree(user.id()));

            // Convert merged node back to UserRecord
            UserRecord updated = objectMapper.treeToValue(merged, UserRecord.class);
            user = updated;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON for patch request", e);
        }

        // validate merged entity
        // FIXME: this validation may be better placed in service layer.
        Set<ConstraintViolation<UserRecord>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        UserRecord saved = userService.save(user);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
