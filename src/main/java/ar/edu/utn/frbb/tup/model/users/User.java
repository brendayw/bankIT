package ar.edu.utn.frbb.tup.model.users;

import ar.edu.utn.frbb.tup.model.client.Client;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Client client = null;

    //para construir de manera segura con FACTORY METHOD
    public static User createUser(String username, String encodedPassword) {
        return User.builder()
                .username(username)
                .password(encodedPassword)
                .build();
    }

    public void associateWithClient(Client client) {
        if (this.client != null && this.client.equals(client)) {
            return;
        }
        if (this.client != null) {
            Client previousClient = this.client;
            this.client = null;
            if (previousClient.getUser() == this) {
                previousClient.setUser(null);
            }
        }
        this.client = client;
        if (client != null && client.getUser() != this) {
            client.setUser(this);
        }
    }

    public void removeClient() {
        if (this.client != null) {
            Client currentClient = this.client;
            this.client = null;
            if (currentClient.getUser() == this) {
                currentClient.setUser(null);
            }
        }
    }

    public boolean canHaveClient() {
        return this.client == null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void changePassword(String newEncodedPassword) {
        if (newEncodedPassword == null || newEncodedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password no puede ser vacío");
        }
        this.password = newEncodedPassword;
    }

    public void changeUsername(String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Username no puede ser vacío");
        }
        this.username = newUsername;
    }
}