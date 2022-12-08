package com.sohwakmo.cucumbermarket.domain;


import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@DynamicInsert
@SequenceGenerator(name = "MEMBERS_SEQ_GEN",sequenceName = "MEMBER_SEQ", allocationSize = 1)
public class Member {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "MEMBERS_SEQ_GEN")
    private Integer memberNo; // Primary Key

    @NotNull
    @Column(nullable = false, unique = true)
    private String memberId; // 2자 이상

    @NotNull
    @Column(nullable = false)
    private String password;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false, unique = true)
    private String nickname;

    @NotNull
    @Column(nullable = false)
    private String address;

    @NotNull
    @Column(nullable = false)
    private String phone;

    @NotNull
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, name= "email_key")
    private String emailKey;

    @Column(name= "email_auth")
    private Integer emailAuth;


    @ColumnDefault("0")
    private Integer grade;


    @ColumnDefault("'/images/mypage/default.jpg'")
    private String userImgUrl;

    @ColumnDefault("'default.jpg'")
    private String userImgName;


    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberRole> roles = new HashSet<>();
    public Member addRole(MemberRole role){
        roles.add(role);
        return this;
    }

    // 회원정보 수정 업데이트
    public Member memberUpdate( String name, String nickname, String password, String address, String phone, String email){
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.email = email;
        return this;
    }

    // 회원 등급 수정 업데이트
    public Member gradeUpdate(Integer grade){
        this.grade = grade;
        return this;
    }

    //회원 이미지 수정 업데이트
    public Member userImageUpdate(String userImgName, String userImgUrl){
        this.userImgName = userImgName;
        this.userImgUrl = userImgUrl;
        return this;
    }

}
