/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.model.service;

import hu.dpc.ob.domain.entity.User;
import hu.dpc.ob.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NotNull
    @Transactional
    public User getUserByApiId(String apiUserId) {
        User user = userRepository.findByApiUserId(apiUserId);
        if (user == null)
            throw new UnsupportedOperationException("User not found for id " + apiUserId);

        return user;
    }

    @NotNull
    @Transactional
    public User getUserById(Long id) {
        return userRepository.getOne(id);
    }
}
