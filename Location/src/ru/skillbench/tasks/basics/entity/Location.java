package ru.skillbench.tasks.basics.entity;

/**
 * ЦЕЛИ ЗАДАЧИ<ul>
 * <li>Применить основы объектно-ориентированного программирования в Java.</li>
 * <li>Применить рекурсию для обхода иерархии объектов.</li>
 * <li>Научиться использовать enum.</li>
 * </ul>
 * <p/>
 * ЗАДАНИЕ<br/>
 * Реализовать класс, описывающей место на Земле (например, страну, город, улицу и т.п.).
 * В описание входит название места (например, "Россия", "пр. Строителей", "д. 10"), тип (например, страна, улица, дом)
 *  и ссылка на "родительское" место, т.е. на место, частью которого является данное место.<br/>
 * Кроме простых getter/setter-методов, нужно реализовать {@link #getTopLocation()} для поиска вершины иерархии мест,
 *  {@link #getAddress()} для представления иерархии мест (с учетом их {@link Type}) в виде строки-адреса
 *  и {@link #isCorrect()} для проверки корректности иерархии (проверки, что город не есть часть улицы и т.п).
 * <p/>
 * ПОЯСНЕНИЯ ДЛЯ НАЧИНАЮЩИХ<ul>
 * <li>Класс решения задачи должен быть объявлен так: <code>public class LocationImpl implements Location {   }</code>.</li>
 * <li>Конструкторы объявлять не требуется, но если они есть в классе, то среди них должен быть конструктор без параметров:
 *     <code>public EmployeeImpl() {  }</code></li>
 * <li>Данные должны быть объявлены как private-поля класса. Методы get* / set* должны оперировать с этими полями,
 *     но не должны рассчитывать на то, что "родительское" место является экземпляром класса <code>LocationImpl</code>.</li>
 * <li>Поскольку родительское место, установленное методом {@link #setParent(Location)}, может быть экземпляром другого класса,
 *   в методах {@link #getTopLocation()}, {@link #isCorrect()}, {@link #getAddress()}
 *   нельзя обращаться к полям класса <code>LocationImpl</code>. Более того, поскольку в интерфейсе <code>Location</code>
 *   не объявлено метода getParent(), обход иерархии мест в этих методах невозможно организовать в виде цикла.
 *   Вместо этого нужно использовать рекурсию (и это "более объектно-ориентированно").
 </li>
 * </ul>
 * <p/>
 * ПРИМЕЧАНИЕ<br/>
 * Задачу можно решать без явной обработки и генерации исключительных ситуаций (Exceptions).
 * <p/>
 * @author Alexey Evdokimov
 */
public interface Location {
    /**
     * Тип места. По сути, это поименованный уровень места в цепочке "от страны к квартире.
     */
    public static enum Type {
        /* Страна */
        COUNTRY(""),
        /* Область (провинция, штат и т.п.) */
        REGION("обл. "),
        /* Город */
        CITY("г. "),
        /* Район (города) */
        DISTRICT("р-н "),
        /* Улица (проспект, переулок и т.п.) */
        STREET("ул. "),
        /* Дом */
        BUILDING("д. "),
        /* Квартира */
        APARTMENT("кв. ");

        private String nameForAddress;

        private Type(String nameForAddress) {
            this.nameForAddress = nameForAddress;
        }

        /**
         * @return Имя типа, предназначенное для адреса, - дефолтный префикс к названию места данного типа:
         *  например, для {@link #STREET} это "ул. ", для {@link #CITY} это "г. "
         */
        public final String getNameForAddress() {
            return nameForAddress;
        }

        /**
         * Этот метод следует явно или неявно вызывать в {@link Location#toString()}
         * @return "Country" для {@link #COUNTRY}, "Region" для {@link #REGION} и т.д.
         */
        @Override
        public String toString() {
            String type = name();
            return type.charAt(0) + type.substring(1).toLowerCase();
        }

    }

    /**
     * @return Название места
     */
    String getName();

    /**
     * @param name Название места
     */
    void setName(String name);

    /**
     * @return Тип места
     * @see Type
     */
    Type getType();

    /**
     * @param type Тип места
     */
    void setType(Type type);

    /**
     * @param parent "Родительское" место - то, чьей частью является данное место.
     * Например, если данное место - это улица, то родительским местом может быть район или город.
     */
    void setParent(Location parent);

    /**
     * @return Название "родительского" места, частью которого является данное место.
     * 	 Если родительское место не задано (равно null), метод возвращает строку "--".
     */
    String getParentName();

    /**
     * Возвращает место верхнего уровня, т.е. вершину иерархии, в которую входит данное место.<br/>
     * @return Место, отличное от <code>null</code>. Если выше в иерархии больше нет мест, возвращает данное место.
     */
    Location getTopLocation();

    /**
     * Проверяет иерархию родительских мест на соответствие их типов: например, город не должен быть частью дома или города.<br/>
     * Пропуски в иерархии допустимы: например, улица может находиться в городе (не в районе), а город - в стране (не в области).
     * @return true если каждое следующее (более высокое) место в иерархии имеет {@link #getType()} меньше, чем у предыдущего места.<br/>
     * Обратите внимание: {@link Type}, как любой enum, реализует интерфейс {@link Comparable},
     *   и порядок определяется порядком записи значений в коде enum.
     */
    boolean isCorrect();

    /**
     * Адрес - это список мест, начинающийся с данного места и включающий все родительские места.
     * Элементы списка отделяются друг от друга запятой и пробелом (", ").<br/>
     * Каждый элемент списка - это:<ol>
     * <li>просто название места, если уже содержит префикс или суффикс типа, - то есть,
     *    если оно заканчиваются на точку ('.') или содержит точку среди символов от начала названия до первого пробела (' ');</li>
     * <li>в противном случае - дефолтный префикс типа ({@link Type#getNameForAddress()}) и название места.
     * Под 'названием' подразумевается результат функции {@link Location#getName()}.</li>
     * </ol>
     * Пример названий с префиксом/суффиксом, удовлетворяющих условию пункта 1: "Московская обл.", "туп. Гранитный", "оф. 321".<br/>
     * Пример названий без префикса/суффикса, НЕ удовлетворяющих этому условию: "Москва", "25 к. 2"<br/>
     * @return адрес, составленный из имен (и типов) всех мест, начиная с данного места до {@link #getTopLocation()}
     */
    String getAddress();

    /**
     * Примечание: существует одноименный метод в классе {@link Object}, поэтому, в отличие от остальных методов {@link Location},
     *  IDE автоматически НЕ создаст шаблон реализации этого метода в классе LocationImpl - следует создать его вручную.
     * @return Строковое представление места в формате:<br/>
     *  name (type)<br/>
     *  где name - это название места, а type - это его тип: см. {@link Type#toString()}. Например:<br/>
     *  34/5 (Building)
     */
    String toString();
}
