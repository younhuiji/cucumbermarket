package com.sohwakmo.cucumbermarket.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sohwakmo.cucumbermarket.domain.KakaoProfile;
import com.sohwakmo.cucumbermarket.domain.Member;
import com.sohwakmo.cucumbermarket.domain.OAuthToken;
import com.sohwakmo.cucumbermarket.dto.MemberRegisterDto;
import com.sohwakmo.cucumbermarket.dto.ResetPasswordDto;
import com.sohwakmo.cucumbermarket.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    @Value("${cos.key}")
    private String cosKey;

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public String checkMemberId(String memberId){
        log.info("checkMemberId(memberId= {})", memberId);

        Optional<Member> result= memberRepository.findByMemberId(memberId);
        if(result.isPresent()){
            return "memberIdNok";
        }else{
            return "memberIdOk";
        }
    }

    @Transactional(readOnly = true)
    public String checkPassword(String password){
        log.info("checkPassword(password= {})", password);
        int result= password.length();
        if(8>result|| result>16){
            return "passwordNok";
        }else{
            return "passwordOk";
        }
    }

    @Transactional(readOnly = true)
    public String checkPassword2(String password, String password2){
        log.info("checkPassword2(password= {}, password2= {})", password, password2);

        if(password.equals(password2)){
            return "password2Ok";
        }else{
            return "password2Nok";
        }
    }

    @Transactional(readOnly = true)
    public String checkNickname(String nickname){
        log.info("checkNickname(nickname= {})", nickname);

        Optional<Member> result= memberRepository.findByNickname(nickname);
        if(result.isPresent()){
            return "nicknameNok";
        }else{
            return "nicknameOk";
        }
    }

    @Transactional(readOnly = true)
    public String checkEmail(String email){
        log.info("checkEmail(email= {})", email);

        Optional<Member> result= memberRepository.findByEmail(email);
        if(result.isPresent()){
            return "emailNok";
        }else{
            return "emailOk";
        }
    }

    @Transactional
    public String checkEmail4findPw(String memberId, String email){
        log.info("findPw(memberId= {}, email= {})", memberId, email);
        Member result= memberRepository.findByMemberIdAndEmail(memberId, email);
        if(result== null){
            return "emailNok";
        }else{
            return "emailOk";
        }
    }

    @Transactional
    public String checkEmail4findId(String name, String email){
        log.info("findId(name= {}, email= {})", name, email);
        Member result= memberRepository.findByNameAndEmail(name, email);
        if(result== null){
            return "emailNok";
        }else{
            return "emailOk";
        }
    }

    @Transactional
    public Member registerMember(MemberRegisterDto dto){
        log.info("registerMember(dto= {})", dto);

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        log.info("setPassword(dto= {})", dto);
        Member entity = memberRepository.save(dto.toEntity());
        log.info("entity= {}", entity);

        return entity;
    }

    @Transactional
    public Member findId(String email){
        log.info("findId(email= {})", email);
        return memberRepository.findByEmail(email).orElse(null);
    }

    @Transactional(readOnly = true)
    public Member findMemberByMemberNo(Integer memberNo) {
        return memberRepository.findById(memberNo).orElse(null);
    }

    @Transactional(readOnly = true)
    public Member findRegisterMember(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseGet(()-> {
            return new Member();
        });
        return member;
    }

    @Transactional
    public void resetPw(ResetPasswordDto dto){
        log.info("resetPw(dto= {})", dto);
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        Member entity= memberRepository.findByEmail(dto.getEmail()).orElse(null);
        log.info(entity.toString());
        Member member = entity.resetPassword(dto.getPassword(), dto.getEmailKey());
        log.info(member.toString());
    }

    @Transactional
    public MemberRegisterDto socialLogin(String code){

        RestTemplate rt = new RestTemplate();
        //HttpHeader ???????????? ??????
        HttpHeaders headers = new HttpHeaders();
        ObjectMapper objectMapper = new ObjectMapper();

        //---------------Post: ?????? ?????? ?????? start-----------------------------
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody ???????????? ??????
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "17f10af702d544834112ae55dc49d845");
        params.add("redirect_uri", "http://192.168.20.10:8889/auth/kakao/callback");
        params.add("code", code);

        //HttpHeader??? HttpBody??? ????????? ??????????????? ??????
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        //Http ????????????-Post???????????? -????????? response ????????? ?????? ??????
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        //---------------Post: ?????? ?????? ?????? end ------------------------------

        //---------------Post: ?????? ?????? ?????? read Zone------------------------------
        OAuthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonMappingException e){
            e.printStackTrace();
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }

        log.info("????????? ????????? ??????: "+ oauthToken.getAccess_token());
        //---------------Post: ?????? ?????? ?????? read Zone------------------------------

        //---------------POST: ????????? ?????? ????????? ?????? ?????? start-----------------------
        headers.add("Authorization", "Bearer "+ oauthToken.getAccess_token());
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        //HttpHeader??? HttpBody??? ????????? ??????????????? ??????
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        //Http ????????????-Post???????????? -????????? response ????????? ?????? ??????
        ResponseEntity<String> response2 = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonMappingException e){
            e.printStackTrace();
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
//
//        log.info("????????? ?????????(??????): "+kakaoProfile.getId());
//        log.info("????????? ?????????: "+kakaoProfile.getKakao_account().getEmail());
//
//        log.info("?????? ????????? ?????????:"+kakaoProfile.getKakao_account().getEmail()+"_"+kakaoProfile.getId());

        int idx = kakaoProfile.getKakao_account().getEmail().indexOf("@");
        String kakao_nickname = kakaoProfile.getKakao_account().getEmail().substring(0, idx) +"_"+kakaoProfile.getId().toString().substring(0,4);
//        log.info("kakao_memberId="+kakao_memberId);
//        log.info("kakao_nickname="+kakao_nickname);
//        log.info("?????? ?????? ?????????: "+ kakaoProfile.getKakao_account().getEmail());
//        log.info("?????? ?????? ?????????: "+ kakaoProfile.getProperties().getNickname());
//        log.info("?????? ?????? ??????: "+ "kakaoUser" + kakaoProfile.getId());
//        log.info("????????? ????????? ??????: "+ kakaoProfile.getKakao_account().getProfile().getProfile_image_url());
//        log.info("?????? ?????? ????????????: "+ cosKey);
        //---------------POST: ????????? ?????? ????????? ?????? ?????? end-----------------------

        //---------------????????? ????????? ????????? MeberRegisterDto??? ??????-------------------
        MemberRegisterDto kakaoMember = MemberRegisterDto.builder()
                .memberId(kakao_nickname)
                .password(cosKey)
                .nickname(kakao_nickname)
                .name(kakaoProfile.getProperties().getNickname())
                .email(kakaoProfile.getKakao_account().getEmail())
                .oauth("kakao")
                .userImgName("kakaoImage"+ kakaoProfile.getId())
                .userImgUrl(kakaoProfile.getKakao_account().getProfile().getProfile_image_url())
                .build();

        return kakaoMember;

    }
}
