package json_web_token.json_web_token.service;

import json_web_token.json_web_token.dto.JoinDto;
import json_web_token.json_web_token.entity.UserEntity;
import json_web_token.json_web_token.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void joinProcess(JoinDto joinDto) {

        Boolean checkUsername = userRepository.existsByUsername(joinDto.getUsername());

        if (checkUsername) {
            return;
        }

        UserEntity user = new UserEntity();
        user.setUsername(joinDto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(joinDto.getPassword()));
        user.setRole("ROLE_ADMIN");

        userRepository.save(user);
    }
}
