package ru.oke.sinebot.keenetic.model.wifi;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.oke.sinebot.keenetic.converter.PropertyTypeConverter;
import ru.oke.sinebot.keenetic.dto.types.PropertyType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long id;

    @Column(name = "type", nullable = false, length = 50)
    @Convert(converter = PropertyTypeConverter.class)
    private PropertyType type;

    private boolean retrievable;
}
