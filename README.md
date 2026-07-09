# Order Service

Serwis do zarzadzania zamowieniami napisany w Javie ze Spring Boot. Projekt demonstruje typowy backendowy stack uzywany w komercyjnych aplikacjach: REST API, baze danych, kolejke wiadomosci i podstawowy wzorzec projektowy.

## Co robi aplikacja

- Przyjmuje zamowienia (klient + lista produktow) przez REST API i zapisuje je w PostgreSQL.
- Wylicza cene koncowa zamowienia z uwzglednieniem opcjonalnego rabatu (wzorzec **Strategy**).
- Po zapisaniu zamowienia publikuje event `OrderCreated` do **RabbitMQ**.
- Osobny listener odbiera ten event asynchronicznie i oznacza zamowienie jako przetworzone (`PROCESSED`).
- Zwraca czytelne bledy JSON (404, gdy zamowienie nie istnieje; 400, gdy dane wejsciowe sa niepoprawne) zamiast surowego stack trace'a.

## Stack technologiczny

| Element | Wybor | Rola |
|---|---|---|
| Jezyk | Java 17 | glowny jezyk aplikacji |
| Build tool | Maven | zarzadzanie zaleznosciami, budowanie projektu |
| Framework | Spring Boot 3 | szkielet aplikacji (Web + Data JPA + AMQP) |
| Baza danych | PostgreSQL | przechowywanie zamowien |
| ORM | Spring Data JPA (Hibernate) | mapowanie obiektow Java na tabele SQL |
| Broker wiadomosci | RabbitMQ | asynchroniczna komunikacja (eventy) |
| Konteneryzacja | Docker (docker-compose) | odpalanie Postgresa i RabbitMQ lokalnie |

## Wymagania

- JDK 17+
- Maven 3.9+ (lub uzyj polecen `mvn`, jesli masz go zainstalowanego globalnie)
- Docker + Docker Compose

## Jak uruchomic

**1. Odpal baze danych i RabbitMQ:**

```bash
docker-compose up -d
```

Sprawdzi sie to poleceniem `docker ps` - powinny wystartowac dwa kontenery: `order-service-postgres` i `order-service-rabbitmq`. Panel zarzadzania RabbitMQ jest dostepny pod `http://localhost:15672` (login/haslo: `guest`/`guest`).

**2. Uruchom aplikacje:**

```bash
mvn spring-boot:run
```

Aplikacja wystartuje na `http://localhost:8080`. Przy pierwszym uruchomieniu Hibernate automatycznie utworzy potrzebne tabele w bazie (`ddl-auto: update`).

**3. Zatrzymanie srodowiska:**

```bash
docker-compose down
```

## Endpointy API

### `POST /orders` - utworzenie zamowienia

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Jan Kowalski",
    "discountCode": "PERCENTAGE10",
    "items": [
      { "productName": "Klawiatura mechaniczna", "quantity": 1, "price": 350.00 },
      { "productName": "Mysz", "quantity": 2, "price": 120.00 }
    ]
  }'
```

Zwraca `201 Created` z utworzonym zamowieniem (naglowek `Location` wskazuje na nowy zasob).

Dostepne kody rabatowe (pole `discountCode`, opcjonalne):
- brak pola / `NONE` - bez rabatu
- `PERCENTAGE10` - 10% od sumy zamowienia
- `FIXED20` - staly rabat 20 (nie wiecej niz wartosc zamowienia)

### `GET /orders/{id}` - pobranie zamowienia

```bash
curl http://localhost:8080/orders/1
```

Zwraca `200 OK` z danymi zamowienia albo `404 Not Found`, jesli zamowienie o podanym ID nie istnieje.

### `GET /orders` - lista wszystkich zamowien

```bash
curl http://localhost:8080/orders
```

## Architektura i zastosowane wzorce

**Warstwy aplikacji:**
```
Controller -> Service -> Repository -> Baza danych
                |
                +-> Strategy (rabaty)
                +-> RabbitTemplate -> RabbitMQ -> Listener
```

**DTO (Data Transfer Object)** - `OrderRequestDto`/`OrderResponseDto` sa oddzielone od encji JPA (`Order`). Dzieki temu struktura bazy danych nie jest bezposrednio wystawiona na zewnatrz przez API, a walidacja wejscia (`@NotBlank`, `@Positive`) dziala niezaleznie od modelu bazodanowego.

**Strategy pattern** - rabaty sa liczone przez interfejs `DiscountStrategy`, ktory ma kilka implementacji (`NoDiscountStrategy`, `PercentageDiscountStrategy`, `FixedAmountDiscountStrategy`). `OrderService` dostaje wszystkie strategie wstrzykniete jako mape (`Map<String, DiscountStrategy>`) i wybiera wlasciwa na podstawie kodu rabatu. Dzieki temu dodanie nowego rodzaju rabatu wymaga tylko nowej klasy `@Component` - bez modyfikowania istniejacego kodu serwisu (zasada Open/Closed z SOLID).

**Asynchroniczna komunikacja przez RabbitMQ** - po zapisaniu zamowienia w bazie, `OrderService` publikuje event `OrderCreated` do exchange'a i wraca z odpowiedzia do klienta bez czekania na jego obsluge. Osobny komponent (`OrderCreatedListener`, oznaczony `@RabbitListener`) odbiera ten event w tle i aktualizuje status zamowienia na `PROCESSED`. To rozdziela "przyjecie zamowienia" od "jego dalszego przetworzenia" (np. wyslania powiadomienia), co w prawdziwym systemie pozwala latwo dodawac kolejnych konsumentow eventu bez zmiany logiki tworzenia zamowienia.

**Globalna obsluga bledow** - `@RestControllerAdvice` + `@ExceptionHandler` przechwytuja wyjatki (`OrderNotFoundException`, bledy walidacji) i zamieniaja je na spojny JSON z kodem HTTP, zamiast domyslnego, malo czytelnego stack trace'a.

## Struktura projektu

```
src/main/java/com/example/orderservice/
├── OrderServiceApplication.java   # punkt wejscia aplikacji
├── model/                          # encje JPA (Order, OrderItem, OrderStatus)
├── repository/                     # OrderRepository (Spring Data JPA)
├── dto/                            # obiekty do komunikacji z API
├── controller/                     # OrderController (REST endpoints)
├── service/                        # logika biznesowa (OrderService)
├── strategy/                       # wzorzec Strategy - rabaty
├── messaging/                      # konfiguracja RabbitMQ, event, listener
└── exception/                      # wlasne wyjatki i globalny handler bledow
```
