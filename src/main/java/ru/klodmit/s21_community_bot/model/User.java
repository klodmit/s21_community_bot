package ru.klodmit.s21_community_bot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "school_name")
    private String schoolName;

    @ColumnDefault("false")
    @Column(name = "is_admin")
    private Boolean isAdmin;

    @ColumnDefault("false")
    @Column(name = "is_validated")
    private Boolean isValidated;

    @OneToMany(mappedBy = "user")
    private Set<ActivityLog> activityLogs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<BanHistory> banHistories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserWarning> userWarnings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<WhiteList> whiteLists = new LinkedHashSet<>();

}