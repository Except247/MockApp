# Тестовые двойники. Mock-сервис

## Создание проекта

> Возможно, первичная загрузка модулей/зависимостей будет недоступна под VPN, если зависимости берутся не из nexus.

> Для простого мокирования (stub|callback|etc) вполне может подойти готовые решения, такие как [mockserver](https://www.mock-server.com/)

Для версий Ultimate можно применить следующий простой подход создания проекта (или просто получить проект локально):

New project -> Spring Initializr

Указываем:
- имя проекта, например, simpleMockApp;
- группу, например, ru.mts.digital.mock;
- язык Java;
- сборщик maven/gradle;
- версию Java 17;
- git, при желании.

На следующей странице указать зависимость Web -> Spring Web. 
Остальное на усмотрение.


После создания проекта вызвать mvn clean install.

---

<em><b>SpringBoot поможет создать простое приложение и запускать его "по клику". 
SpringWeb (SpringMVC) поможет создавать контроллеры (endpoint)</b></em>

Т.е. Spring - семейство проектов, созданных поверх Spring Framework. Т.е. это разные "модули".


## Старт приложения

После создания приложения у вас появится класс запуска приложения. 

```java
@SpringBootApplication
public class SimpleMockAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleMockAppApplication.class, args);
    }
}
```

При старте приложения в логи будет выведен порт: ```Tomcat initialized with port(s): 8080 (http)```

Также был создан файл ```application.properties``` в ```resources```. Существует [множество настроек](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html).
Укажем новый порт (т.к. 8080 часто бывает занят) ```server.port=8888``` 

---

> Небольшое отступление: немного информации о Bean и @Component ([интересная статья](https://habr.com/ru/articles/455794/)) ([и ещё интересная статья](https://habr.com/ru/articles/334448/)) :

<details>
В Spring-е бином (bean) называют любой класс, который управляется контейнером Spring. 
Bean — создаваемый Spring-ом объект класса, который можно внедрить в качестве значения поля в другой объект.
Цель объектов JavaBean - предоставить отдельные и многоразовые единицы, которыми разработчики могут управлять как программно, так и визуально с помощью инструментов компоновки.
Spring Bean представляет собой singleton, то есть в некотором блоке приложения существует только один экземпляр данного класса. 
@Component - аннотирует класс, что он должен быть зарегистрирован как бин.
</details>

## Создание контроллеров

### @RestController

Теперь необходимо реализовать сами котроллеры для приёма входящих запросов.

Создадим класс ```ExampleController```. И отметим его аннотацией ```@RestController```. Более подробно почитать можно начиная от [данной статьи](https://www.baeldung.com/spring-controller-vs-restcontroller).

Кратко: аннотация ```@Controller``` указывает, что данный класс играет роль контроллера. 
Основная цель данной аннотации назначать шаблон данному классу, показывая его роль. 
Это значит, что диспетчер будет сканировать Controller-классы на предмет реализованных методов, проверяя ```@RequestMapping``` аннотации (о ней чуть дальше).

А аннотация ```@RestController``` указывает, что этот класс оперирует не моделями, а данными. Она состоит из аннотаций ```@Controller``` и ```@RequestBody```.
Т.е. просто специально расширенная аннотация, для упрощения реализации endpoint.

```java
@RestController
public class ExampleController {
}
```

### @RequestMapping
[Аннотация](https://www.baeldung.com/spring-requestmapping) ```@RequestMapping``` сопоставляет веб-запросы с методами Spring Controller. 
Проще говоря - это тут путь, по которому мы будем обращаться к методу. 
Может применяться как к классу, указывая общий префикс пути для всех методов внутри, так и быть отдельной для методов. 
Данная аннотация в неком смысле ограничивает возможные значения параметров при обращении к контроллеру (например, определённый тип данных).

> **value** - задаёт дополнительный путь к указанному ранее к клаасу (если у класса не указан - считается целостным)
> **method** - указывает тип запроса/метода (далее будет заменён на более простое представление)
> **headers** - позволяет сузить запрос ещё больше, указав заголовки 
> **produces/consumes** - формат данных 

Для класса
```java
@RestController
@RequestMapping("example/api/v1")
public class ExampleController {
}
```

Для класса и метода
```java
@RestController
@RequestMapping("example/api/v1")
public class ExampleController {
    
    @RequestMapping(
            value = "/ex/foos",
            method = RequestMethod.GET,
            produces = {"application/json", "application/xml"}
    )
    public String example() {
        return "";
    }
    
}
```

### @RequestBody и @ResponseBody

Аннотация ```@RequestBody``` и ```@ResponseBody``` дополнительно можно рассмотреть в [этой статье](https://www.baeldung.com/spring-request-response-body).
Если кратко - то данные аннотации позволяют принимать и возвращать данные (HttpRequest) в формате Json (или Java-объект).

Иначе говоря, обеспечивают:
- автоматическую десериализацию входящего тела HttpRequest в объект Java (request);
- автоматическую сериализацию возвращаемого объекта в JSON и передается обратно в объект HttpResponse (response).

Если рассматривать приведённый ранее пример - то, если бы мы использовали ```@Controller``` вместо ```@RestController```, 
то было бы необходимо добавить ```@ResponseBody```

Пример с ResponseBody. 

> В случае строки - разницы нет и в теории можно не использовать даже в таком случае ResponseBody, но при более сложных объектах - необходимо.

```java
@Controller
@RequestMapping("example/api/v1")
public class ExampleController {

    @RequestMapping(
            value = "/ex/foos",
            method = RequestMethod.GET,
            produces = {"application/json", "application/xml"}
    )
    @ResponseBody
    public String example() {
        return "";
    }
}
```

> Если нужно обрабатывать возвращаемый ответ с указанием кода и описания ошибки (иная информация) - можно использовать
> ```ResponseEntity<?>```. Для этого мы указываем методу возвращаемый тип и уже в теле производим обработку.
> [Немного доп информации](https://habr.com/ru/articles/675716/)
```java
@RequestMapping(
        value = "/ex/foos",
        method = RequestMethod.GET,
        produces = {"application/json", "application/xml"}
)
@ResponseBody
public ResponseEntity<?> example() {
        return ResponseEntity.status(HttpStatus.OK).body("");
}
```

---
Пример с RequestBody

> Для этого необходимо создать dto-класс в таком формате, как ожидается входящий набор данных в теле. 

```java
public class DtoExample {
    public String name;
    public int age;
    public List<String> types;
}
```

Указываем в методе как ожидаемое тело
```java
@RestController
@RequestMapping("example/api/v1")
public class ExampleController {

    @RequestMapping(
            value = "/ex/foos",
            method = RequestMethod.GET,
            produces = {"application/json", "application/xml"}
    )
    public String example(@RequestBody DtoExample dtoExample) {
        return "";
    }
}
```

### @GetMapping, @PostMapping etc
Эти и другие схожие аннотации - очередное упрощение и является просто неким альясом для ```@RequestMapping(method = RequestMethod.GET)```.
Немного уменьшают/упрощают создание, поиск и работу с контроллерами.

По умолчанию ```@RequestMapping``` производит сопоставление со всеми запросами. Специфичные запросы:
* ```@GetMapping``` — Обрабатывает get-запросы
* ```@PostMapping``` — Обрабатывает post-запросы
* ```@DeleteMapping``` — Обрабатывает delete-запросы
* ```@PutMapping``` — Обрабатывает put-запросы
* ```@PatchMapping``` — Обрабатывает patch-запросы

Пример

```java
@RestController
@RequestMapping("example/api/v1")
public class ExampleController {

    @GetMapping(value = "/ex/foos", produces = {"application/json", "application/xml"})
    public String example(@RequestBody DtoExample dtoExample) {
        return "";
    }
}
```
> Если ограничение накладывается только на путь - допустимо упростить запись

```java
@RestController
@RequestMapping("example/api/v1")
public class ExampleController {

    @GetMapping("/ex/foos")
    public String example(@RequestBody DtoExample dtoExample) {
        return "";
    }
}
```

### Параметры запроса

#### Примеры запросов

Ограничение пути задаёт сам URL ```(localhost:8888/example/api/v1/ex/foos/{id}/temp)```,
но также требуется определить: как нам работать с динамическими данными, которые мы будем передавать.
[Данная статья](https://www.baeldung.com/spring-request-param) может быть полезной.

---

Если в пути запроса передаются динамичные данные - мы можем их отловить с помощью ```@PathVariable```.
Предварительно указав в пути место и параметр ```{path}```

```java
@GetMapping(value = "/ex/foos/{id}/temp", produces = {"application/json", "application/xml"})
public String example(@RequestBody DtoExample dtoExample, @PathVariable int id) {
        return "";
        }
```
---

Также могут быть переданы параметры запроса. В URL это выглядит как ```"?param=val"```. Для них необходимо использовать ```@RequestParam```

```java
@GetMapping(value = "/ex/foos/{id}/temp", produces = {"application/json", "application/xml"})
public String example(
@RequestBody DtoExample dtoExample,
@PathVariable int id,
@RequestParam String msisdn) {
        return "";
        }
```

> То имя, которое мы указываем в виде Java-сущности (msisdn) должно совпадать с передаваемым (не всегда - уточнение далее).

---

Заголовки обрабатываются с помощью ```@RequestHeader```

```java
@GetMapping(value = "/ex/foos/{id}/temp", produces = {"application/json", "application/xml"})
    public String example(
            @RequestBody DtoExample dtoExample,
            @PathVariable int id,
            @RequestParam String msisdn,
            @RequestHeader HttpHeaders headers) {
        return "";
    }
```

---


#### Расширения параметров запроса

Дополнительно можно рассмотреть полезные свойства рассмотренных выше параметров. 
Они указываются в формате ```@Param(args...)```

Если мы считаем данный параметр опциональным - можно указать ```(required = false)``` и у нас не возникнет ошибки, если параметр не будет передан.

Если же параметр не был передан, но нам нужно иметь стандартное значение - используйте ```(defaultValue = "test")```

Если приходит параметр, имя которого не очень нравится и хотим в коде использовать иное - 
можно сослаться явно на него, а создать то, что больше подходит ```(name = "user-id")```

```java
@GetMapping(value = "/ex/foos/{id}/temp", produces = {"application/json", "application/xml"})
    public String example(
            @RequestBody DtoExample dtoExample,
            @PathVariable int id,
            @RequestParam(defaultValue = "test") String msisdn,
            @RequestHeader(required = false) HttpHeaders headers,
            @CookieValue(name = "user-id", defaultValue = "default-user-id") String userId) {
        return "";
    }
```

### Работа с файлами

Ранее созданного подхода достаточно для простой stub-реализации. 
Но для более гибкой работы можно использовать файлы и отдавать ответы на основе полученного "ключа" в запросе.

#### ApplicationStartup

Создадим новый класс в корне приложения ```ApplicationStartup```. Его цель - считывать файлы при старте приложения и формировать в ```target``` ресурсы. 
Создадим в ```resources``` фолдер с именем сервиса, который хотим замокировать: ```example_file_response```. 
Теперь в методе ```init``` необходимо "извлечь" файлы из данной директории для последующей работы с ними.
Реализация ```extractResource``` представлена ниже в этом же классе. 
Расширять же, при новых мокируемых системах, необходимо будет только сам метод ```init```
```java
public void init() throws IOException, URISyntaxException {
        extractResource("example_file_response");
}
```

И пропишем вызов данного метода в класс ```SimpleMockAppApplication```
```java
public static void main(String[] args) throws IOException, URISyntaxException {
        new ApplicationStartup().init();
        SpringApplication.run(SimpleMockAppApplication.class, args);
}
```

#### DataService

Для считывания содержимого из файла можно реализовать свой подход. Ниже представлен один из простейших вариантов.
```DataService``` формирует путь к файлу. Если файл существует - считывает его. Иначе считывает "дефолтный" файл.

```java
public Response getData(String name, String pathToFolder, String fileType) throws IOException {
        Path path = Paths.get(pathToFolder, name + fileType);
        var body = Files.exists(path) ? storage.readFile(path) : storage.readFile(Paths.get(pathToFolder, "default" + fileType));
        return new Response(200, body);
}
```

> Дефолтный файл ```default``` должен обязательно присутствовать в директории, иначе не получится вернуть базовый ответ и будет ошибка 

Чтение файла происходит с помощью сервиса ```FileStorage```. Если файл есть - считывает. Если по какой-то причине его нет - 
выбрасывает кастомное исключение ```NotFoundException``.

```java
public String readFile(Path path) throws IOException {
        log.debug("File search in {}", path);
        if (!Files.exists(path)) {
            throw new NotFoundException("Not found :" + path);
        }
        return Files.readAllLines(path).stream().map(String::trim).collect(Collectors.joining(" "));
}
```

Ответ от сервиса ```DataService``` возвращается в формате ```Response```, подходящим для ```ResponseEntity<?>```.

В самом контроллере также выбирается расширение файла. Сам запрос выглядит так:
```java
return dataService.getData(msisdn, BASE_PATH, Extension.XML.getExtension()).toResponseEntity();
```







#   M o c k A p p  
 