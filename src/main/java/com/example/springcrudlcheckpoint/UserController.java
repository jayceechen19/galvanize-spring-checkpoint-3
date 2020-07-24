package com.example.springcrudlcheckpoint;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    public Iterable<User> all() {
        return this.repository.findAll();
    }
    @PostMapping("")
    public User create(@RequestBody User user) {
        return this.repository.save(user);
    }
    @GetMapping("/{id}")
    public User getOne(@PathVariable("id")Long id){
        return this.repository.findById(id).get();
    }
    @PatchMapping("/{id}")
    public User patchOne(@PathVariable("id")Long id,
                         @RequestBody Map<String,String> body){
        User user = this.repository.findById(id).get();

        user.setEmail(body.get("email"));
        if(body.get("password") != null){
            user.setPassword(body.get("password"));
        }
        return this.repository.save(user);
    }
    @DeleteMapping("/{id}")
    public Count delete(@PathVariable("id") Long id){
        this.repository.deleteById(id);
        Count count = new Count(this.repository.count());
        return count;
    }

    @PostMapping("/authenticate")
    public Authenticate checkPassword(@RequestBody Map<String,String> body){
        User user = this.repository.findByEmail(body.get("email"));
        if(user == null){
            return new Authenticate(false, null);
        }
        if(user.getPassword().equals(body.get("password"))){
            return new Authenticate(true, user);
        }else{
            return new Authenticate(false, null);
        }
    }

    private static class Count{
        private long count;
        Count(long count){
            this.count = count;
        }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Authenticate{
        private boolean authenticated;
        private User user;

        Authenticate(boolean result, User user){
            this.authenticated = result;
            this.user= user;
        }
        public boolean isAuthenticated() { return authenticated; }
        public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }
}
