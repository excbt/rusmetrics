package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import ru.excbt.datafuse.nmk.config.Constants;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDTO {
    private Long id;

    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    @Size(max = 256)
    private String imageUrl;

    private boolean activated = false;

    @Size(min = 2, max = 6)
    private String langKey;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Set<String> authorities;

    public UserDTO(){

    }

    public UserDTO(SubscrUser subscrUser) {
        this.id = subscrUser.getId();
        this.login = subscrUser.getUserName();
        this.firstName = subscrUser.getFirstName();
        this.lastName = subscrUser.getLastName();
        this.email = subscrUser.getContactEmail();
        this.activated = true;
        this.langKey = "ru";
        this.createdBy = subscrUser.getCreatedBy() != null ? subscrUser.getCreatedBy().toString() : null;
        this.lastModifiedBy = subscrUser.getLastModifiedBy() != null ? subscrUser.getLastModifiedBy().toString() : null;
        this.createdDate = subscrUser.getCreatedDate();
        this.lastModifiedDate = subscrUser.getLastModifiedDate();
        this.authorities = subscrUser.getSubscrRoles().stream().map(SubscrRole::getRoleName).collect(Collectors.toSet());
        if (Boolean.TRUE.equals(subscrUser.getIsAdmin())) {
            this.authorities.add(AuthoritiesConstants.ADMIN);
        }
    }

}
