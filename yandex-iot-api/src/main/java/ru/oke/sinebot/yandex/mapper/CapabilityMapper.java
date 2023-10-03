package ru.oke.sinebot.yandex.mapper;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.api.action.ActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.AccessPointResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.ActionDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.types.AccessPointType;
import ru.oke.sinebot.keenetic.dto.types.ActionType;
import ru.oke.sinebot.keenetic.dto.types.PropertyType;
import ru.oke.sinebot.keenetic.dto.types.WifiFrequency;
import ru.oke.sinebot.yandex.dto.capability.Capability;
import ru.oke.sinebot.yandex.dto.capability.CapabilityActionState;
import ru.oke.sinebot.yandex.dto.capability.CapabilityState;
import ru.oke.sinebot.yandex.dto.capability.CapabilityType;
import ru.oke.sinebot.yandex.dto.capability.mode.Mode;
import ru.oke.sinebot.yandex.dto.capability.mode.ModeActionState;
import ru.oke.sinebot.yandex.dto.capability.mode.ModeInstance;
import ru.oke.sinebot.yandex.dto.capability.mode.ModeParameters;
import ru.oke.sinebot.yandex.dto.capability.mode.ModeState;
import ru.oke.sinebot.yandex.dto.capability.mode.ModeValue;
import ru.oke.sinebot.yandex.dto.capability.onoff.OnOffActionState;
import ru.oke.sinebot.yandex.dto.capability.onoff.OnOffParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Стыдные алгоритмы маппинга информации о точках доступа роутера к возможностям устройства Яндекса и обратно.<p>
 * Ключевая сложность данного микросервиса именно тут о_О<p>
 * Яндекс не поддерживает никаких умений для взаимодействия с роутерами, поэтому мною были придуманы костыли следующего
 * характера: состояние определенного свойства на нескольких точках доступа определенного типа суммарно представляет
 * собой программу / подрежим работы роутера. Каждая "программа" имеет определенный номер, который кодируется так (на
 * примере, включения свойства WPS на домашних точках доступа):<p>
 * - one   - (home_5_wps_off, home_2.4_wps_off),<p>
 * - two   - (home_5_wps_off, home_2.4_wps_on),<p>
 * - three - (home_5_wps_on, home_2.4_wps_off),<p>
 * - four  - (home_5_wps_on, home_2.4_wps_on),<p>
 * т.е. при включении через Яндекс, например, программы 3 (three), функционал WPS включится на домашней точке доступа
 * 5 ГГц и выключится на домашней точке доступа 2.4 ГГц. И эта программа должна передаваться Яндексу как доступная для
 * устройства только в том случае, если устройство поддерживает 5 ГГц.<p>
 * Аналогичным образом кодируется включение/выключение гостевых точек доступа, но в рамках другой функции умения mode -
 * input_source<p>
 * В основном, с этим костылем и не совсем удачным API микросервиса для работы с роутерами этот класс и пытается
 * бороться.
 *
 * @author k.oshoev
 */
@Component
public class CapabilityMapper {
    /**
     * мапа сопоставляет функцию умения mode с параметрами точки доступа - её типом (домашняя/гостевая) и типом свойства
     * на точке доступа (wps на домашних точках доступа, state - на гостевых)
     */
    private static final Map<ModeInstance, AccessPointFilters> MODE_TO_ACCESS_POINT_MAP = new ConcurrentHashMap<>();

    /**
     * мапа сопоставляет целочисленный номер "программы" с её буквенным обозначением в Яндексе со сдвигом на единицу:
     * 0 - one, 1 - two и т.д.
     */
    private static final Map<Integer, ModeValue> INT_TO_MODE_VALUE_MAP = new ConcurrentHashMap<>();

    /**
     * мапа сопоставляет буквенное обозначение программы в Яндексе с её целочисленным номером
     */
    private static final Map<ModeValue, Integer> MODE_VALUE_TO_INT = new ConcurrentHashMap<>();

    /**
     * Метод выполняет преобразование информации об известных / поддерживаемых точках доступа устройства к списку
     * информации о соответствующих умениях для Яндекса. Принцип маппинга см. {@link CapabilityMapper}.<p>
     * Будет возвращен пустой список, если нет точек домашних точек доступа, поддерживающих wps и гостевых точек доступа
     *
     * @param accessPoints список поддерживаемых точек доступа роутера с информацией об их управляемых (включаемых и
     *                     выключаемых) свойствах
     * @return список с информацией об умениях устройства в терминологии Яндекса
     */
    public List<Capability> mapToModeCapabilities(List<AccessPointResponseDto> accessPoints) {
        List<Capability> capabilities = new ArrayList<>();
        MODE_TO_ACCESS_POINT_MAP.keySet().forEach(instance -> {
            Capability capability = this.findModeCapability(instance, accessPoints);
            if (capability != null) {
                capabilities.add(capability);
            }
        });
        return capabilities;
    }

    /**
     * Метод выполняет преобразование информации об известных / поддерживаемых для роутера активных действиях к базовому
     * умению в Яндексе - ON_OFF. Фактически это умение будет отвечать не за включение или выключение устройства, а за
     * его перезагрузку. Это умение будет без возможности запроса текущего статуса
     *
     * @param actionDtos список поддерживаемых активных действий, которые можно выполнить над роутером
     * @return информация об умении ON_OFF, если устройство поддерживает удаленную перезагрузку
     */
    public Capability mapToOnOffCapability(List<ActionDto> actionDtos) {
        for (ActionDto action : actionDtos) {
            if (action.getType() == ActionType.RELOAD) {
                OnOffParameters parameters = new OnOffParameters(false);
                return new Capability(parameters, false);
            }
        }
        return null;
    }

    /**
     * Метод выполняет преобразование информации о текущих статусах всех известных свойств точек доступа роутера к
     * информации о текущем состоянии умений устройства в терминологии Яндекса <p>
     * Будет возвращен пустой список, если нет точек домашних точек доступа, поддерживающих wps и гостевых точек доступа
     *
     * @param accessPoints список с информацией о текущем состоянии точек доступа
     * @param errorsHolder список, в который будут добавляться ошибки получения каких-либо свойств (если информация о
     *                     таких ошибках была возвращена сервисом работы с роутерами). Должен быть инициализирован
     * @return список текущих состояний функций умения mode
     */
    public List<CapabilityState> mapToCapabilityStates(List<AccessPointStatusResponseDto> accessPoints,
                                                       List<ErrorDto> errorsHolder) {
        List<CapabilityState> capabilityStates = new ArrayList<>();
        MODE_TO_ACCESS_POINT_MAP.keySet().forEach(instance -> {
            CapabilityState capabilityState = this.findModeCapabilityState(instance, accessPoints, errorsHolder);
            if (capabilityState != null) {
                capabilityStates.add(capabilityState);
            }
        });
        return capabilityStates;
    }

    /**
     * Метод выполняет преобразование списка устанавливаемых Яндексом состояний умений устройства к списку требуемых
     * состояний точек доступа роутера<p>
     * Обрабатывает только умения типа mode. Если устанавливаемый режим функции mode не поддерживается роутером, он
     * будет проигнорирован полностью или частично. Например, если передается функция input_source со значением three
     * для роутера, который поддерживает только частоту 2.4 ГГц, на нем будет только осуществлена попытка выключить
     * гостевой Wi-Fi 2.4 ГГц. Если же аналогичные параметры передаются для роутера, который вообще не поддерживает
     * гостевой Wi-Fi, то умение будет полностью проигнорировано. С другой стороны, подобных запросов приходить не
     * должно, т.к. перед работой с устройством Яндексом специально запрашиваются поддерживаемые им умения <p>
     * Все другие умения будут проигнорированы данным методом
     *
     * @param requestedStates   список устанавливаемых Яндексом состояний умений
     * @param knownAccessPoints информация об известных точках доступа роутера
     * @return список уникальных точек доступа и устанавливаемых состояний свойств этих точек доступа
     */
    public List<AccessPointChangeStatusRequestDto> mapToAccessPoints(List<CapabilityState> requestedStates,
                                                                     List<AccessPointResponseDto> knownAccessPoints) {
        Map<AccessPointId, List<PropertyChangeStatusRequestDto>> accessPointsMap = generateAccessPointPropertiesMap(
                requestedStates, knownAccessPoints);
        List<AccessPointChangeStatusRequestDto> result = new ArrayList<>();
        accessPointsMap.keySet().forEach(key -> {
            List<PropertyChangeStatusRequestDto> properties = accessPointsMap.get(key);
            if (!properties.isEmpty()) {
                result.add(new AccessPointChangeStatusRequestDto(key.getType(), key.getBand(), properties));
            }
        });
        return result;
    }

    /**
     * Метод выполняет преобразование списка устанавливаемых Яндексом состояний умений устройства к активному действию
     * над роутером. Будет возвращен null, если в списке умений из запроса отсутствует умение ON_OFF. В противном случае
     * будет возвращен объект для выполнения перезагрузки
     *
     * @param requestedStates список устанавливаемых Яндексом состояний умений
     * @return dto с описанием активного действия для перезагрузки роутера, либо null, если Яндексом не запрашивалось
     * включение / выключение
     */
    public ActionDto mapToActionDto(List<CapabilityState> requestedStates) {
        CapabilityState onOffCapability = requestedStates.stream()
                .filter(capability -> capability.getType() == CapabilityType.ON_OFF)
                .findFirst()
                .orElse(null);
        return onOffCapability == null ? null : new ActionDto(ActionType.RELOAD);
    }

    /**
     * Метод выполняет преобразование результата выполнения активного действия на роутере к результату обработки запроса
     * на изменение состояния умения от Яндекса
     *
     * @param actionResponse результат выполнения активного действия, полученный от сервиса для взаимодействия с
     *                       роутерами
     * @return список (для удобства дальнейшего маппинга) с результатом обработки запроса на изменение состояния умения
     * ON_OFF, если выполнялось действие по перезагрузке роутера, либо пустой список
     */
    public List<CapabilityActionState> mapToCapabilityStates(ActionResponseDto actionResponse) {
        List<CapabilityActionState> result = new ArrayList<>();
        if (actionResponse.getType() == ActionType.RELOAD) {
            CapabilityActionState state;
            if (actionResponse.isCompleted()) {
                state = new CapabilityActionState(new OnOffActionState());
            } else {
                ErrorCode errorCode = actionResponse.getError().getErrorCode();
                String errorMessage = actionResponse.getError().getErrorMessage();
                state = new CapabilityActionState(new OnOffActionState(errorCode, errorMessage));
            }
            result.add(state);
        }
        return result;
    }

    /**
     * Метод выполняет преобразование результата изменения статусов свойств точек доступа к списку результатов изменения
     * статусов умений в терминологии Яндекса
     *
     * @param accessPoints список со статусами изменения состояний свойств точек доступа
     * @return список состояний умений. Теоретически может быть возвращен пустой список, если никакие свойства не
     * менялись
     */
    public List<CapabilityActionState> mapToCapabilityStates(List<AccessPointChangeStatusResponseDto> accessPoints) {
        List<CapabilityActionState> capabilityStates = new ArrayList<>();
        MODE_TO_ACCESS_POINT_MAP.keySet().forEach(instance -> {
            CapabilityActionState capabilityState = this.findModeCapabilityState(instance, accessPoints);
            if (capabilityState != null) {
                capabilityStates.add(capabilityState);
            }
        });
        return capabilityStates;
    }

    public List<CapabilityActionState> mergeCapabilities(List<CapabilityActionState> states1,
                                                         List<CapabilityActionState> states2) {
        if (states1 == null && states2 == null) {
            return new ArrayList<>();
        } else if (states1 == null) {
            return states2;
        } else if (states2 == null) {
            return states1;
        }
        List<CapabilityActionState> merged = new ArrayList<>();
        merged.addAll(states1);
        merged.addAll(states2);
        return merged;
    }

    /**
     * Метод выполняет поиск свойств точек доступа, подходящих для управления при помощи указанной функции умения mode,
     * и на основе найденной информации генерирует объект capability c перечнем поддерживаемых режимов работы указанной
     * функции. В соответствии с требованиями API Яндекса, допустимые режимы работы функции сортируются для обеспечения
     * постоянности их порядка
     *
     * @param instance     функция умения mode (program / input_source)
     * @param accessPoints список известных точек доступа и их свойств
     * @return информация о поддерживаемом умении mode
     */
    private Capability findModeCapability(ModeInstance instance, List<AccessPointResponseDto> accessPoints) {
        AccessPointFilters filters = MODE_TO_ACCESS_POINT_MAP.get(instance);
        AtomicBoolean isRetrievable = new AtomicBoolean(true);
        Set<ModeValue> modeValues = this.findBaseModes(accessPoints, filters, isRetrievable);
        if (modeValues.isEmpty()) {
            return null;
        } else if (modeValues.contains(ModeValue.TWO) && modeValues.contains(ModeValue.THREE)) {
            modeValues.add(ModeValue.FOUR);
        }
        modeValues.add(ModeValue.ONE);
        List<Mode> modes = modeValues.stream()
                .sorted()
                .map(Mode::new)
                .collect(Collectors.toList());
        ModeParameters parameters = new ModeParameters(instance, modes);
        return new Capability(parameters, isRetrievable.get());
    }

    /**
     * Метод для фильтации списка точек доступа по заданным критериями и определения возможности включения свойства
     * указанного типа на точках доступа 2.4 и / или 5 ГГц
     *
     * @param accessPoints  список известных точек доступа и их свойств
     * @param filters       критерии фильтрации точек доступа и свойств (тип искомой точки доступа и тип свойства на
     *                      ней)
     * @param isRetrievable поддерживается ли получение текущего статуса умения. Объект должен быть инициализирован и
     *                      может быть изменен, если однотипное свойство не поддерживает запрос статуса хотя бы на одной
     *                      точке доступа
     * @return набор базовых допустимых режимов работы функции (есть ли реализация функции для точек доступа 2.4 ГГц -
     * программа two - и 5 ГГц - программа three)
     */
    private Set<ModeValue> findBaseModes(List<AccessPointResponseDto> accessPoints, AccessPointFilters filters,
                                         AtomicBoolean isRetrievable) {
        Set<ModeValue> modeValues = new HashSet<>();
        accessPoints.stream()
                .filter(accessPoint -> accessPoint.getType() == filters.getAccessPointType())
                .forEach(accessPoint -> accessPoint.getProperties().stream()
                        .filter(property -> property.getType() == filters.getPropertyType())
                        .forEach(property -> {
                            if (!property.isRetrievable()) {
                                isRetrievable.set(false);
                            }
                            if (accessPoint.getBand() == WifiFrequency.BAND_2_4) {
                                modeValues.add(ModeValue.TWO);
                            } else if (accessPoint.getBand() == WifiFrequency.BAND_5) {
                                modeValues.add(ModeValue.THREE);
                            }
                        }));
        return modeValues;
    }

    /**
     * Метод для определения текущего состояния определенной функции умения mode на основе информации о текущем
     * состоянии точек доступа
     *
     * @param instance     функция умения mode (program / input_source)
     * @param accessPoints список текущих состояний свойств точек доступа на роутере
     * @param errorsHolder список ошибок, в который могут быть добавлены ошибки получения статусов свойств
     * @return объект с текущим состоянием указанной функции умения mode, если статусы свойств точек доступа,
     * управляемых данной функцией, успешно получены, либо null, если не найдено статусов этих свойств
     */
    private CapabilityState findModeCapabilityState(ModeInstance instance,
                                                    List<AccessPointStatusResponseDto> accessPoints,
                                                    List<ErrorDto> errorsHolder) {
        AccessPointFilters filters = MODE_TO_ACCESS_POINT_MAP.get(instance);
        ModeValue modeValue = this.findModeValue(accessPoints, filters, errorsHolder);
        if (modeValue == null) {
            return null;
        }
        ModeState modeState = new ModeState(instance, modeValue);
        return new CapabilityState(modeState);
    }

    /**
     * Метод для определения текущего режима / состояния функции умения mode. По дефолту предполагает, что все свойства
     * отключены (режим one с целочисленным номером 0), затем при обнаружении включенных свойств нужного типа при
     * помощи побитового "или" уточняет номер режима
     *
     * @param accessPoints список текущих состояний свойств точек доступа на роутере
     * @param filters      критерии фильтрации точек доступа и свойств (тип искомых точек доступа и тип свойства на
     *                     них)
     * @param errorsHolder список ошибок, в который могут быть добавлены ошибки получения статусов свойств
     * @return номер текущей "программы" в терминологии яндекса, либо null, если не найдены точки доступа / свойства
     * соотвествующие этой программой
     */
    private ModeValue findModeValue(List<AccessPointStatusResponseDto> accessPoints, AccessPointFilters filters,
                                    List<ErrorDto> errorsHolder) {
        AtomicInteger currentMode = new AtomicInteger(0);
        AtomicBoolean propertiesFound = new AtomicBoolean(false);
        accessPoints.stream()
                .filter(accessPoint -> accessPoint.getType() == filters.getAccessPointType())
                .forEach(accessPoint -> accessPoint.getProperties().stream()
                        .filter(property -> property.getType() == filters.getPropertyType())
                        .forEach(property -> {
                            propertiesFound.set(true);
                            if (property.getError() != null) {
                                errorsHolder.add(property.getError());
                            } else if (property.getEnabled()) {
                                if (accessPoint.getBand() == WifiFrequency.BAND_2_4) {
                                    currentMode.set(currentMode.get() | 1);
                                } else if (accessPoint.getBand() == WifiFrequency.BAND_5) {
                                    currentMode.set(currentMode.get() | 2);
                                }
                            }
                        }));
        return propertiesFound.get() ? INT_TO_MODE_VALUE_MAP.get(currentMode.get()) : null;
    }

    /**
     * Метод преобразует список устанавливаемых Яндексом состояний умений к мапе точек доступа и их изменяемых свойств.
     * Вводит в оборот понятие идентификатора точки доступа, состоящего из типа точки доступа (домашняя/гостевая) и
     * частоты, используемого в качестве ключа мапы. Это необходимо, чтобы объединять все управляемые параметры 1 точки
     * доступа в 1 объект, как этого требует микросервис управления роутерами.
     *
     * @param requestedStates   список устанавливаемых Яндексом состояний умений
     * @param knownAccessPoints информация об известных точках доступа роутера
     * @return мапа со связкой идентификатора точки доступа и её изменяемых свойств
     */
    private Map<AccessPointId, List<PropertyChangeStatusRequestDto>> generateAccessPointPropertiesMap(
            List<CapabilityState> requestedStates, List<AccessPointResponseDto> knownAccessPoints) {
        Map<AccessPointId, List<PropertyChangeStatusRequestDto>> accessPointsMap = new HashMap<>();
        requestedStates.stream()
                .filter(capability -> capability.getType() == CapabilityType.MODE)
                .forEach(capability -> {
                    ModeState modeState = (ModeState) capability.getState();
                    int targetState = MODE_VALUE_TO_INT.get(modeState.getValue());
                    AccessPointFilters filters = MODE_TO_ACCESS_POINT_MAP.get(modeState.getInstance());
                    knownAccessPoints.stream()
                            .filter(accessPoint -> accessPoint.getType() == filters.getAccessPointType())
                            .forEach(accessPoint -> this.addAccessPointPropertiesToMap(accessPointsMap, accessPoint,
                                    filters.getPropertyType(), targetState)
                            );
                });
        return accessPointsMap;
    }

    /**
     * Метод для добавления нового свойства в мапу изменяемых статусов свойств точек доступа. При помощи побитового "и"
     * определяет, должно ли быть включено свойство (если найдено среди поддерживаемых) в соответствии с устанавливаемым
     * номером программы
     *
     * @param accessPointsMap мапа со связкой идентификатора точки доступа и её изменяемых свойств. Должна быть
     *                        инициализирована. В неё будет добавляться устанавливаемое значение свойства точки доступа
     * @param accessPoint     информация о точке доступа и её поддерживаемых свойствах
     * @param propertyType    тип свойства точки доступа
     * @param targetState     целочисленное обозначение требуемой "программы" для свойств указанного типа
     */
    private void addAccessPointPropertiesToMap(Map<AccessPointId, List<PropertyChangeStatusRequestDto>> accessPointsMap,
                                               AccessPointResponseDto accessPoint, PropertyType propertyType,
                                               int targetState) {
        AccessPointId id = new AccessPointId(accessPoint.getType(), accessPoint.getBand());
        accessPointsMap.putIfAbsent(id, new ArrayList<>());
        accessPoint.getProperties().stream()
                .filter(property -> property.getType() == propertyType)
                .forEach(property -> {
                    boolean propertyEnabled = false;
                    if (accessPoint.getBand() == WifiFrequency.BAND_2_4) {
                        propertyEnabled = (targetState & 1) != 0;
                    } else if (accessPoint.getBand() == WifiFrequency.BAND_5) {
                        propertyEnabled = (targetState & 2) != 0;
                    }
                    PropertyChangeStatusRequestDto propertyChangeDto = new PropertyChangeStatusRequestDto(propertyType,
                            propertyEnabled);
                    accessPointsMap.get(id).add(propertyChangeDto);
                });
    }

    /**
     * Метод ищет все статусы изменения свойств точек доступа, связанных с указанной функцией умения, и конструирует из
     * них итоговый результат с информацией о статусе изменении всего состояния умения
     *
     * @param instance     функция умения mode (program / input_source)
     * @param accessPoints список статусов изменений свойств точек доступа
     * @return результат изменения состояния умения, либо null, если умение не менялось
     */
    private CapabilityActionState findModeCapabilityState(ModeInstance instance,
                                                          List<AccessPointChangeStatusResponseDto> accessPoints) {
        List<ErrorDto> capabilityErrors = new ArrayList<>();
        AtomicBoolean existCompleted = new AtomicBoolean(false);
        AccessPointFilters filters = MODE_TO_ACCESS_POINT_MAP.get(instance);
        accessPoints.stream()
                .filter(accessPoint -> accessPoint.getType() == filters.getAccessPointType())
                .forEach(accessPoint -> accessPoint.getProperties().stream()
                        .filter(property -> property.getType() == filters.getPropertyType())
                        .forEach(property -> {
                            if (property.getError() != null) {
                                capabilityErrors.add(property.getError());
                            } else {
                                existCompleted.set(true);
                            }
                        })
                );
        return this.createModeCapabilityState(instance, existCompleted.get(), capabilityErrors);
    }

    /**
     * Метод создает объект с результатом изменения состояния умения
     *
     * @param instance         функция умения mode (program / input_source)
     * @param existsCompleted  есть ли успешные изменения каких-либо свойств точек доступа, связанных с данной функцией
     * @param capabilityErrors произошли ли какие-либо ошибки при изменении статусов свойств точек доступа, связанных с
     *                         данной функцией
     * @return null, если нет ни успешных, ни провальных изменений свойств. Объект, содержащий ошибку, если при
     * изменении статуса хотя бы одного свойства точки доступа, связанного с данным умением, произошла ошибка. В
     * противном случае - объект с успешным результатом выполнения состояния умения
     */
    private CapabilityActionState createModeCapabilityState(ModeInstance instance, boolean existsCompleted,
                                                            List<ErrorDto> capabilityErrors) {
        if (capabilityErrors.isEmpty() && existsCompleted) {
            ModeActionState state = new ModeActionState(instance);
            return new CapabilityActionState(state);
        } else if (!capabilityErrors.isEmpty()) {
            ErrorCode errorCode = capabilityErrors.get(0).getErrorCode();
            String errorMessage = capabilityErrors.stream()
                    .map(ErrorDto::getErrorMessage)
                    .collect(Collectors.joining("; "));
            ModeActionState state = new ModeActionState(instance, errorCode, errorMessage);
            return new CapabilityActionState(state);
        } else {
            return null;
        }
    }

    static {
        AccessPointFilters inputSourceFilters = new AccessPointFilters(AccessPointType.GUEST, PropertyType.STATE);
        MODE_TO_ACCESS_POINT_MAP.put(ModeInstance.INPUT_SOURCE, inputSourceFilters);
        AccessPointFilters programFilters = new AccessPointFilters(AccessPointType.HOME, PropertyType.WPS);
        MODE_TO_ACCESS_POINT_MAP.put(ModeInstance.PROGRAM, programFilters);
        INT_TO_MODE_VALUE_MAP.put(0, ModeValue.ONE);
        INT_TO_MODE_VALUE_MAP.put(1, ModeValue.TWO);
        INT_TO_MODE_VALUE_MAP.put(2, ModeValue.THREE);
        INT_TO_MODE_VALUE_MAP.put(3, ModeValue.FOUR);
        MODE_VALUE_TO_INT.put(ModeValue.ONE, 0);
        MODE_VALUE_TO_INT.put(ModeValue.TWO, 1);
        MODE_VALUE_TO_INT.put(ModeValue.THREE, 2);
        MODE_VALUE_TO_INT.put(ModeValue.FOUR, 3);
    }

    /**
     * Класс описывает объект с параметрами фильтрации точек доступа и их свойств
     */
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    private static class AccessPointFilters {
        private AccessPointType accessPointType;

        private PropertyType propertyType;
    }

    /**
     * Класс описывает объект, представляющий собой идентификатор точки доступа
     */
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    private static class AccessPointId {
        private AccessPointType type;

        private WifiFrequency band;
    }
}
