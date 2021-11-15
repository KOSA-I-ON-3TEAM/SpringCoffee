package com.kosa.springcoffee.controller;

import com.kosa.springcoffee.dto.LoginRequestDTO;
import com.kosa.springcoffee.dto.LoginResponseDTO;
import com.kosa.springcoffee.dto.ModifyMemberReqeustDTO;
import com.kosa.springcoffee.dto.SignUpDTO;
import com.kosa.springcoffee.entity.Member;
import com.kosa.springcoffee.repository.MemberRepository;
import com.kosa.springcoffee.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.kosa.springcoffee.entity.MemberRole.ROLE_ADMIN;
import static com.kosa.springcoffee.entity.MemberRole.ROLE_USER;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/v5")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/email-check")
    public Boolean checkEmail(@RequestParam String email){
        return memberService.checkEmail(email);
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignUpDTO dto){
        Member member = Member.builder()
                        .email(dto.getEmail())
                        .password(dto.getPassword())
                        .name(dto.getName())
                        .address(dto.getAddress())
                        .fromSocial(dto.isFromSocial()).build();

        if(dto.getIsAdmin() == 1){
            member.addMemberRole(ROLE_ADMIN);
        }
        member.addMemberRole(ROLE_USER);
        memberService.joinMember(member);
        return "redirect:/login";
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO loginDTO) throws Exception {
        LoginResponseDTO dto = memberService.login(loginDTO);
        Optional<Member> member = memberRepository.getByEmail(loginDTO.getEmail(), false);
        Member m = member.get();
        if (m == null){
            return new ResponseEntity<String>("아이디가 존재하지 않습니다.", HttpStatus.FORBIDDEN);
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), m.getPassword())){
            return new ResponseEntity<String>("비밀번호가 틀립니다.", HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<LoginResponseDTO>(dto, HttpStatus.OK);
    }

    @PostMapping("/modify")
    public ResponseEntity modifyUserInfo(@RequestBody ModifyMemberReqeustDTO dto) {
        memberService.modify(dto);

        return new ResponseEntity<String>("회원 정보가 수정되었습니다.", HttpStatus.OK);
    }

}