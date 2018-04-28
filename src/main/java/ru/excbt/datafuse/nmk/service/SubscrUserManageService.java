package ru.excbt.datafuse.nmk.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.service.dto.SubscrUserDTO;
import ru.excbt.datafuse.nmk.service.mapper.SubscrUserMapper;
import ru.excbt.datafuse.nmk.service.validators.UsernameValidator;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@Transactional
public class SubscrUserManageService {

    private final static UsernameValidator usernameValidator = new UsernameValidator();

    private final SubscrUserRepository subscrUserRepository;

    private final SubscrUserMapper subscrUserMapper;

    private final SubscrUserService subscrUserService;

    public SubscrUserManageService(SubscrUserRepository subscrUserRepository, SubscrUserMapper subscrUserMapper, SubscrUserService subscrUserService) {
        this.subscrUserRepository = subscrUserRepository;
        this.subscrUserMapper = subscrUserMapper;
        this.subscrUserService = subscrUserService;
    }

    /**
     *
     * @param rmaSubscriber
     * @param subscrUserDTO
     * @param password
     * @return
     */
    public Optional<SubscrUser> createSubscrUser(final Subscriber rmaSubscriber,
                                                 final SubscrUserDTO subscrUserDTO,
                                                 final String password) {
        Objects.requireNonNull(rmaSubscriber);
        Objects.requireNonNull(rmaSubscriber);
        Objects.requireNonNull(rmaSubscriber.getId());
        Objects.requireNonNull(subscrUserDTO);

        if (subscrUserDTO.getUserName() != null) {
            subscrUserDTO.setUserName(subscrUserDTO.getUserName().toLowerCase());
        }

        if (!usernameValidator.validate(subscrUserDTO.getUserName())) {
            Optional.empty();
        }

        Optional<SubscrUser> checkUser = subscrUserRepository.findOneByUserNameIgnoreCase(subscrUserDTO.getUserName());
        if (checkUser.isPresent()) {
            new PersistenceException("User with name " + subscrUserDTO.getUserName() + "already exists");
        }


        SubscrUser subscrUser = subscrUserMapper.toEntity(subscrUserDTO);

        List<SubscrRole> subscrRoles = subscrUserService.processSubscrRoles(rmaSubscriber,
            subscrUserDTO.isAdmin() && !subscrUserDTO.isReadonly(), subscrUserDTO.isReadonly());

        subscrUser.getSubscrRoles().clear();
        subscrUser.getSubscrRoles().addAll(subscrRoles);

        SubscrUser result = subscrUserService.createSubscrUser(subscrUser, password);

        return Optional.of(result);
    }

}
