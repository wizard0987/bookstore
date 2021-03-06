package com.webservice.bookstore.web.controller;

import com.webservice.bookstore.config.security.auth.CustomUserDetails;
import com.webservice.bookstore.domain.entity.member.Member;
import com.webservice.bookstore.exception.AfterDateException;
import com.webservice.bookstore.exception.UnauthorizedException;
import com.webservice.bookstore.exception.ValidationException;
import com.webservice.bookstore.service.MemberService;
import com.webservice.bookstore.util.EmailUtil;
import com.webservice.bookstore.util.RedisUtil;
import com.webservice.bookstore.web.dto.EmailDto;
import com.webservice.bookstore.web.dto.MemberDto;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class MemberController {

    private final MemberService memberService;
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody @Valid EmailDto.SignUpRequest signUpRequest, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            throw new ValidationException("회원가입 유효성 실패", bindingResult.getFieldErrors());
        }

        memberService.signup(signUpRequest);

        return new ResponseEntity("Success", HttpStatus.OK);
    }

    @PostMapping("/signup/duplicated")
    public ResponseEntity duplicatedEmail(@RequestBody @Valid EmailDto.EmailCheckDto emailCheckDto, BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            throw new ValidationException("이메일 형식이 맞지 않습니다.", bindingResult.getFieldErrors());
        }

        memberService.duplicatedEmail(emailCheckDto.getEmail());
        return ResponseEntity.ok("이메일 중복 체크 성공");

    }

    @PostMapping("/signup/request-certificated")
    public ResponseEntity RequestCertificatedEmail(@RequestBody EmailDto.EmailCerticatedDto email) {

        log.info("email: " + email);
        String certificated = String.valueOf(EmailUtil.randomint());
        EmailUtil.sendEmail(javaMailSender, email.getEmail(), certificated);
        redisUtil.setData(certificated, certificated, 60L*5);

        return ResponseEntity.ok("이메일 인증 요청 메일을 보냈습니다.");
    }


    @PostMapping("/signup/check-certificated")
    public ResponseEntity ResponseCertificatedEmail(@RequestBody EmailDto.CeriticateCode ceriticateCode) {

        String certificateCode = ceriticateCode.getCertificated();
        String savedCode = redisUtil.getData(certificateCode);
        if (!certificateCode.equals(savedCode)) {
            throw new IllegalArgumentException("인증코드가 맞지 않습니다.");
        } else if (StringUtils.isEmpty(savedCode)) {
            throw new AfterDateException("입력 시간이 초과되었습니다.");
        }

        redisUtil.deleteData(certificateCode);
        return ResponseEntity.ok("인증 성공하였습니디.");

    }


    @PostMapping("/withdrawal")
    public ResponseEntity withDrawal(@RequestBody(required = false) WithdrawalRequest withdrawalRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String email = customUserDetails.getUsername();
        this.memberService.withdraw(email, withdrawalRequest.getPassword());
        return ResponseEntity.ok("정상적으로 회원탈퇴하였습니다.");
    }


    @Data
    static class WithdrawalRequest {
        private String password;
    }

    // 이메일 찾기 -> 어떤 기준으로 이메일을 찾을지 정해야함. 예를 들어 이름 + nickname + 생일 등
    @PostMapping("/searchid")
    public ResponseEntity searchid(@RequestBody @Valid FindIdRequest findIdRequest, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new ValidationException("아이디 찾기 실패", bindingResult.getFieldErrors());
        }
        String email = this.memberService.findId(findIdRequest.getBirth(), findIdRequest.getNickName());
        return ResponseEntity.ok(email);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class FindIdRequest {
        @NotBlank(message = "닉네임을 입력해주세요")
        private String nickName;
        @NotBlank(message = "생년월일 입력해주세요 ( ex) 980723)")
        private String birth;
    }


    /*
    비밀번호 찾기
    */
    @PostMapping("/searchpwd")
    public ResponseEntity searchpwd(@RequestBody @Valid EmailDto.findPwdRequest findPwdRequest,
                                    BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            throw new ValidationException("비밀번호 찾기 요청 실패", bindingResult.getFieldErrors());
        }

        try {
            String tempPassword = this.memberService.searchPassword(findPwdRequest);
            EmailUtil.sendEmail(javaMailSender, findPwdRequest.getEmail(), tempPassword);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity("계정이 존재하지 않습니다", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity("임시 비밀번호 이메일 전송 완료", HttpStatus.OK);
    }

}
