package ru.oke.sinebot.keenetic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oke.sinebot.keenetic.model.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
}
