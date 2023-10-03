package ru.oke.sinebot.keenetic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.oke.sinebot.keenetic.model.wifi.AccessPoint;
import ru.oke.sinebot.keenetic.model.action.Action;

import java.util.List;

/**
 * Класс описывает объект "Модель роутера" определенного производителя, характеризующийся наименованием модели,
 * наличием одной или нескольких точек доступа и поддержкой простых удаленных операций
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "models")
@NamedEntityGraph(name = "model-vendor-entity-graph", attributeNodes = {@NamedAttributeNode("vendor")})
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 4)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "models_access_points", joinColumns = @JoinColumn(name = "model_id"),
            inverseJoinColumns = @JoinColumn(name = "access_point_id"))
    private List<AccessPoint> accessPoints;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 4)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "models_actions", joinColumns = @JoinColumn(name = "model_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id"))
    private List<Action> supportedActions;
}
