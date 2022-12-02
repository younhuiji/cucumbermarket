package com.sohwakmo.cucumbermarket.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@SequenceGenerator(name = "MEMBERS_SEQ_GEN",sequenceName = "MEMBER_SEQ", allocationSize = 1)
public class Member{

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "MEMBERS_SEQ_GEN")
    private Integer memberNo; // Primary Key

    @Column(nullable = false, unique = true)
    private String memberId; // 2자 이상

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;


    @Column(nullable = false, unique = true)
    private String nickname;


    @Column(nullable = false)
    private String address;


    @Column(nullable = false)
    private Integer phone;


    @Column(unique = true, nullable = false)
    private String email;

    private Integer grade;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberRole> roles = new HashSet<>();
    public Member addRole(MemberRole role){
        roles.add(role);
        return this;
    }
}