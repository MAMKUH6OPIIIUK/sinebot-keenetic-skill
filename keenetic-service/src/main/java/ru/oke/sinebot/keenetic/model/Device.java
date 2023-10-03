package ru.oke.sinebot.keenetic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.oke.sinebot.keenetic.converter.DevicePasswordEncryptor;

/**
 * Класс описывает объект "Устройство пользователя", которое должно относиться к одной из поддерживаемых моделей
 * роутеров, должно иметь наименование и параметры подключения к этому устройству.
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "devices")
@NamedEntityGraph(name = "device-info-entity-graph", attributeNodes = {@NamedAttributeNode("model")})
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;

    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "domain_name", unique = true, nullable = false, length = 100)
    private String domainName;

    @Column(name = "login", nullable = false, length = 50)
    private String login;

    @Column(name = "password", nullable = false, length = 500)
    @Convert(converter = DevicePasswordEncryptor.class)
    private String password;
}
