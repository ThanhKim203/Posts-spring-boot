package project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.model.Role;
import project.service.RoleService;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping(path = "api/v1")
public class RoleController {
    @Autowired
    RoleService roleService;

    @PostMapping("/role")
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        role.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        return new ResponseEntity<Role>(roleService.save(role), HttpStatus.CREATED);
    }
}
