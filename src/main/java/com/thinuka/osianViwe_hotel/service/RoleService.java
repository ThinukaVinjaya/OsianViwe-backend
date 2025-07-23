package com.thinuka.osianViwe_hotel.service;

import com.thinuka.osianViwe_hotel.exception.RoleAlreadyExistException;
import com.thinuka.osianViwe_hotel.exception.UserAlreadyExistsException;
import com.thinuka.osianViwe_hotel.model.Role;
import com.thinuka.osianViwe_hotel.model.User;
import com.thinuka.osianViwe_hotel.repository.RoleRepository;
import com.thinuka.osianViwe_hotel.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService{

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role theRole) {
        String roleName = "ROLE_"+ theRole.getName().toUpperCase();
        Role role = new Role(roleName);
        if(roleRepository.existsByName(role)){
            throw new RoleAlreadyExistException(theRole.getName() + "role already exists");
        }
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        this.removeAllUsersFromRole(roleId);
        roleRepository.deleteById(roleId);

    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).get();
    }

    @Override
    public User removeUserFromRole(Long userId, Long roleId) {
        Optional <User> user  = userRepository.findById(userId);
        Optional <Role>  role = roleRepository.findById(roleId);
        if(role.isPresent() && role.get().getUsers().contains(user.get())){
            role.get().removeUserFromRole(user.get());
            roleRepository.save(role.get());
            return user.get();
        }
        throw new UsernameNotFoundException("User not found");
    }

    @Override
    public User assignRoleToUser(Long userId, Long roleId) {
        Optional <User> user  = userRepository.findById(userId);
        Optional <Role>  role = roleRepository.findById(roleId);
        if(user.isPresent() && user.get().getRoles().contains(role.get())){
            throw new UserAlreadyExistsException(
                    user.get().getFirstName()+ "is already assigned to the" + role.get().getName()+"role");
        }
        if(role.isPresent()){
            role.get().assignRoleToUser(user.get());
            roleRepository.save(role.get());
        }
        return  user.get();
    }

    /*@Override
    public Role removeAllUsersFromRole(Long roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        role.isPresent(Role :: removeAllUsersFromRole);
        return roleRepository.save(role.get());
    }*/

    @Override
    public Role removeAllUsersFromRole(Long roleId) {
        Optional<Role> optionalRole = roleRepository.findById(roleId);

        if (optionalRole.isPresent()) {
            Role role = optionalRole.get();
            role.removeAllUsersFromRole(); // This should be a method in the Role entity
            return roleRepository.save(role);
        } else {
            throw new EntityNotFoundException("Role not found with id: " + roleId);
        }
    }

}
