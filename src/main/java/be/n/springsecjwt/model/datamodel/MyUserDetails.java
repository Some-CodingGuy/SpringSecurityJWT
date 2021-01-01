package be.n.springsecjwt.model.datamodel;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MyUserDetails implements UserDetails {

    private String userName;
    private String password;
    private List<GrantedAuthority> autorities;

    public MyUserDetails (User user){
        this.userName = user.getUserName();
        this.password = user.getPassword();
        /* user.getRoles() get the roles from the user object, but it's a String object, and we need to convert it to a
         * list of GrantedAuthorities type (SimpleGrantedAuthority implements the GrantedAuthorities interface).
         * Here we use the "Arrays" class to directly create an array from a string object.
         * The first line says that we are going to use a stream over the list of roles
         *      (the string is split at the "," and put into an array, that's the array on which the stream will be executed).
         * The second line gets each element at a time and transforms it from a string to a SimpleGrantedAuthority type
         *      by using the string value as an argument for the SimpleGrantedAuthority constructor:
         *          This applies "new SimpleGrantedAuthority(s)" to all s, s being each element in the array, using a stream.
         * The third line takes all the new SimpleGrantedAuthority objects and puts them in a List, making sure that at
         *      the end of the stream the result is in the correct format.
         */
        this.autorities = Arrays.stream(user.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.autorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    /* The following methods will be hard-coded to true, since we don't need them for now */

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
