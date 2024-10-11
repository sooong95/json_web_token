package json_web_token.json_web_token.dto;

import lombok.Data;

@Data
public class LoginDto {

    private String username;
    private String password;
}
