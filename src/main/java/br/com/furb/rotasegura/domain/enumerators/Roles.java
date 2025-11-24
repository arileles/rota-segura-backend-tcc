package br.com.furb.rotasegura.domain.enumerators;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
public enum Roles {

    ADMIN("ADMIN"), USER("USER");

    private final String description;

    Roles(String description) {
        this.description = description;
    }

    public static List<Roles> getRolesAsList() {return Arrays.asList(values());}

    public static Roles getByName(String name) {
        var roleByName = getRolesAsList().stream().filter(role -> Objects.equals(role.name(), name)).findFirst();
        if (roleByName.isEmpty()) {
            throw new RuntimeException();
        }
        return roleByName.get();
    }
}
