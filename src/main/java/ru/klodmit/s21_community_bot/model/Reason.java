package ru.klodmit.s21_community_bot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "reason")
public class Reason {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reason_id_gen")
    @SequenceGenerator(name = "reason_id_gen", sequenceName = "reason_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @OneToMany(mappedBy = "reason")
    private Set<BanHistory> banHistories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "reason")
    private Set<WhiteList> whiteLists = new LinkedHashSet<>();

}