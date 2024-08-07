package com.danjam.Users;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false)
    private int phoneNum;

    @Enumerated(EnumType.STRING) // enumtype.string 옵션 사용하면 enum 이름 그대로 db에 저장
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createDate;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updateDate;

    @Column(name = "status")
    @ColumnDefault("Y")
    private char status;

    @Builder
    public Users(int id, String email, String password, String name, int phoneNum, Role role, Date createDate, Date updateDate, char status) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNum = phoneNum;
        this.role = role.ROLE_USER;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.status = status;
    }

    public Users(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = Role.ROLE_USER;
    }
}
