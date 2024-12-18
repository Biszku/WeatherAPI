# WeatherAPI

## Overview
WeatherAPI is a simple API that provides weather information for a given city.

## Features
- Fetch weather information for a specified city.
- Rate limiting to control the number of requests from a client.

## Prerequisites
- Java 11 or higher
- Maven
- Redis server

## Installation

1. **Clone the repository:**
    ```sh
    git clone https://github.com/Biszku/WeatherAPI.git
    cd WeatherAPI
    ```

2. **Set up Redis:**
    - Install and start a Redis server. Follow the instructions on the [Redis website](https://redis.io/download).

    - Configure environment variables:
       - Set the `REDIS_CONN_STRING` environment variable to your Redis connection string.
         <br>
            <br>
       On Windows:
       ```sh
       set REDIS_CONN_STRING=your_redis_connection_string
       ```
       On Linux: 
       ```sh
       export REDIS_CONN_STRING=your_redis_connection_string
       ```
       - Set the `WEATHER_SECRET` environment variable to your VisualCrossing API key.
         <br>
            <br>
       On Windows:
       ```sh
       set WEATHER_SECRET=your_api_key
       ```
      On Linux:
       ```sh
       export WEATHER_SECRET=your_api_key
       ```

4. **Build the project:**
    ```sh
    mvn clean package
    ```

5. **Run the application:**
    ```sh
    java -jar .\target\WeatherAPI-1.0-SNAPSHOT.jar
    ```

## Usage

- **Endpoint:**
    ```
    GET /weather/{city}
    ```

- **Example Request:**
    ```
    GET http://localhost:8080/weather/London
    ```
  
- **Rate Limiting:**
    - The API allows a maximum of 5 requests per minute per client. If the limit is exceeded, a `429 Too Many Requests` response is returned.
  
