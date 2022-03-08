package com.talharic.referral.service;

import com.talharic.referral.exception.CodeNotFoundException;
import com.talharic.referral.exception.MaxPersonCountException;
import com.talharic.referral.exception.UserNotFoundException;
import com.talharic.referral.model.User;
import com.talharic.referral.repository.UserRepository;
import com.talharic.referral.util.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Value("${defaultRefCount}")
    private int defaultRefCount;

    private final UserRepository repository;

    private final RandomStringGenerator stringGenerator;


    public UserService(UserRepository repository,
                       RandomStringGenerator stringGenerator) {
        this.repository = repository;
        this.stringGenerator = stringGenerator;
    }


    public User getUserByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(" user not found!"));
    }

    public List<User> getAllByReferralCode(String referralCode) {
        if (!repository.existsUserByReferralCode(referralCode)) {
            throw new UserNotFoundException(" user not found with given code!");
        }
        return repository.findAllByReferredByCode(referralCode);
    }

    public User createUser(User user) {

        if (user.getReferredByCode() == null || user.getReferredByCode().isEmpty()) {
            throw new CodeNotFoundException("referral code not found");
        }

        int referredUserCount = getAllByReferralCode(user.getReferredByCode()).size();
        if (referredUserCount < defaultRefCount) {
            user.setReferralCode(generateCode());
        } else {
            throw new MaxPersonCountException("max person count has been reached!");
        }

        return repository.save(user);
    }

    private String generateCode() {
        String generated = "";
        do {
            generated = stringGenerator.generate();
        } while (repository.existsUserByReferralCode(generated));

        return generated;
    }

}
