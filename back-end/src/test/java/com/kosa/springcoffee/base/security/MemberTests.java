package com.kosa.springcoffee.base.security;




import com.kosa.springcoffee.BackEndApplication;
import com.kosa.springcoffee.member.Member;
import com.kosa.springcoffee.member.MemberRepository;
import com.kosa.springcoffee.member.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

@SpringBootTest(classes = BackEndApplication.class)
public class MemberTests {
    @Autowired
    private MemberRepository memberRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertdummies(){
        //1~80 User
        //81~90 User,Manager
        //90~100 User,Manager, Admin
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Member member = Member.builder()
                    .email("user"+i+"@springCoffee.com")
                    .name("사용자"+i)
                    .fromSocial(false)
                    .password(passwordEncoder.encode("1111"))
                    .build();

            //default role
            member.addMemberRole(MemberRole.ROLE_USER);

            if(i>80){
                member.addMemberRole(MemberRole.ROLE_MANAGER);
            }
            if(i>90){
                member.addMemberRole(MemberRole.ROLE_ADMIN);
            }
            System.out.println(member.getEmail());
            System.out.println(member.getPassword());
            memberRepository.save(member);
        });

    }


//
//    @Test
//    public void testRead(){
//        Optional<Member> result = memberRepository.findByEmailOptional("user95@springCoffee.com");
//        Member member = result.get();
//
//        System.out.println(member);
//    }
}