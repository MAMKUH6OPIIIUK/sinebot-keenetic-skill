package ru.oke.sinebot.keenetic.model.wifi;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.oke.sinebot.keenetic.converter.AccessPointTypeConverter;
import ru.oke.sinebot.keenetic.converter.WifiFrequencyConverter;
import ru.oke.sinebot.keenetic.dto.types.AccessPointType;
import ru.oke.sinebot.keenetic.dto.types.WifiFrequency;

import java.util.List;

/**
 * Класс описывает объект с параметрами точки доступа (тип точки доступа (основная домашняя, либо гостевая), частотный
 * диапазон, наименование сущности (интерфейса), ответственной за взаимодействие с этой точкой доступа в
 * API/CLI производителя. Не предполагает пользовательских настроек - только параметры по умолчанию
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "access_points")
public class AccessPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_point_id")
    private Long id;

    @Column(name = "type", nullable = false, length = 50)
    @Convert(converter = AccessPointTypeConverter.class)
    private AccessPointType type;

    @Column(name = "band", nullable = false, length = 10)
    @Convert(converter = WifiFrequencyConverter.class)
    private WifiFrequency band;

    @Column(name = "interface_name", nullable = false, length = 50)
    private String interfaceName;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 5)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "access_points_properties", joinColumns = @JoinColumn(name = "access_point_id"),
            inverseJoinColumns = @JoinColumn(name = "property_id"))
    private List<Property> properties;
}
