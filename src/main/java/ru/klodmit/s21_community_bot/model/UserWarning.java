package ru.klodmit.s21_community_bot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_warning")
public class UserWarning {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_warning_id_gen")
    @SequenceGenerator(name = "user_warning_id_gen", sequenceName = "user_warning_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "message", length = Integer.MAX_VALUE)
    private String message;

    @ColumnDefault("now()")
    @Column(name = "\"timestamp\"")
    private Instant timestamp;

}