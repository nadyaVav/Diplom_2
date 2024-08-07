# Diplom 2

Проект для задания 2: API  
Содержит тесты, проверяющие ручки для Stellar Burgers.  
В проекте применяется паттерн DTO.
Для некоторых тестов применяется параметризация.  
Генерация тестовых данных выполняется при помощи библиотеки javafaker.

## Документация
[Документация API](https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf)

## Технологии
- java 11
- JUnit 4.13.2
- allure-junit4 2.15.0
- maven 3.8.1
- rest-assured 5.4.0
- allure-rest-assured 2.15.0
- allure-maven 2.10.0
- javafaker 0.15

## Запуск
```shell
mvn clean test
```

## Сформировать отчет Allure
Для построения отчета использовать команду
```shell
mvn allure:serve
```