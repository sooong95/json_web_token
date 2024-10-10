package json_web_token.json_web_token.service;

import json_web_token.json_web_token.dto.CustomUserDetails;
import json_web_token.json_web_token.entity.UserEntity;
import json_web_token.json_web_token.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity findUser = userRepository.findByUsername(username);

        if (findUser != null) {
            return new CustomUserDetails(findUser);
        }
        return null;
    }
}
