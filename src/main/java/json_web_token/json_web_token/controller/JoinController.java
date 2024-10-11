package json_web_token.json_web_token.controller;

import json_web_token.json_web_token.dto.JoinDto;
import json_web_token.json_web_token.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @PostMapping("/join")
    public String joinProcess(@RequestBody JoinDto joinDto) {

        joinService.joinProcess(joinDto);

        return "ok";
    }
}
