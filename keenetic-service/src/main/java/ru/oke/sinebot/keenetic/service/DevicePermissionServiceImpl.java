package ru.oke.sinebot.keenetic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oke.sinebot.keenetic.model.Device;

/**
 * Возможно, не самое лучшее решение - выносить выдачу разрешений на работу с устройствами в отдельный сервис c
 * отдельными транзакциями, но checkstyle сказал, что в основном сервисе для работы с устройствами у меня слишком
 * много зависимостей внедряется через конструктор. На мелкие части своевременно его раздробить не удалось
 *
 * @author k.oshoev
 */
@Service
@RequiredArgsConstructor
public class DevicePermissionServiceImpl implements DevicePermissionService {
    private final MutableAclService mutableAclService;

    /**
     * Метод выдает разрешения на чтение/редактирование информации об устройстве, а так же на управление им
     *
     * @param id идентификатор устройства
     */
    @Transactional
    public void grantPermissions(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Sid owner = new PrincipalSid(authentication);
        ObjectIdentity oid = new ObjectIdentityImpl(Device.class, id);
        MutableAcl acl = mutableAclService.createAcl(oid);
        acl.setOwner(owner);
        acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, owner, true);
        mutableAclService.updateAcl(acl);
    }

    @Transactional
    public void deletePermissions(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Sid owner = new PrincipalSid(authentication);
        ObjectIdentity oid = new ObjectIdentityImpl(Device.class, id);
        mutableAclService.deleteAcl(oid, true);
    }
}
