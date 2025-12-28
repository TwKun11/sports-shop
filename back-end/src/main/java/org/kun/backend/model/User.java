package org.kun.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(length = 20)
    private String status;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_"+ getRole().getName().toUpperCase()));

        return authorityList;
    }
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Có thể thêm logic kiểm tra ngày hết hạn nếu cần
    }

    @Override
    public boolean isAccountNonLocked() {
        // Kiểm tra status - không cho phép đăng nhập nếu bị khóa
        return status != null && !status.equalsIgnoreCase("LOCKED");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Có thể thêm logic kiểm tra password expiration nếu cần
    }

    @Override
    public boolean isEnabled() {
        // Chỉ cho phép đăng nhập nếu status là ACTIVE
        return status != null && status.equalsIgnoreCase("ACTIVE");
    }
}
