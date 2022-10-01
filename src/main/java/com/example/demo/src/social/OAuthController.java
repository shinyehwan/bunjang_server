package com.example.demo.src.social;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

@RestController
@AllArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {


    @Autowired
    private OAuthService oAuthService;


    /**
     * 카카오 callback
     * [GET] /oauth/kakao
     */
    @ResponseBody
    @GetMapping("/kakao")
    public void kakaoCallback(@RequestParam String code, HttpSession session) {
        System.out.println("code : " + code);
        String access_Token = oAuthService.getKakaoAccessToken(code);
//        System.out.println("access_Token = " + access_Token);
        oAuthService.getKakaoUserInfo(access_Token);

    }


}