package br.com.furb.rotasegura.controllers;

import br.com.furb.rotasegura.domain.entities.Role;
import br.com.furb.rotasegura.services.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Set<Role>> getRolesByUser(@PathVariable UUID userId) {
        return roleService.findRolesByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
