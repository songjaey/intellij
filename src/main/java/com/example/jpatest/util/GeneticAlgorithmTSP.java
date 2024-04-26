package com.example.jpatest.util;

import com.example.jpatest.dto.SchedulerDto;
import com.example.jpatest.entity.AdminItemEntity;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import lombok.Getter;
import lombok.Setter;

import java.awt.desktop.SystemEventListener;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class GeneticAlgorithmTSP {
    private static final Random random = new Random();

    private enum ContentType {
        ORIGIN, DESTINATION, 식당, 숙박, 명소
    }
    private static final String API_KEY = "AIzaSyBKfXUB5b-aS6hLNA93HhTe6UNIqXZkizs";
    private static final int POPULATION_SIZE = 50;
    private static final double MUTATION_RATE = 0.1;
    private static final int NUM_GENERATIONS = 1000;

    @Getter
    @Setter
    private static class City {
        double x, y;
        ContentType type;
        int index; // 인덱스 필드 추가
        int day;
        LocalDateTime arrivalTime; // LocalDateTime으로 변경
        int stayTime; // 머무는 시간 추가
        Long spotId; // 아이템의 고유 번호

        public City(double x, double y, ContentType type, int index, int day, LocalDateTime arrivalTime, int stayTime, Long spotId) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.index = index; // 생성자에서 인덱스 설정
            this.day = day;
            this.arrivalTime = arrivalTime; // 도착 시간 설정
            this.stayTime = stayTime; // 머무는 시간 설정
            this.spotId = spotId;
        }

        public double distanceTo(City other) {
            double dx = x - other.x;
            double dy = y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ") - arrivalTime: " + arrivalTime;
        }
    }
    @Getter
    @Setter
    private static class Route {
        List<City> cities;
        double distance;

        public Route(List<City> cities) {
            this.cities = cities;
            calculateDistance();
        }

        private void calculateDistance() {
            distance = 0;
            for (int i = 0; i < cities.size() - 1; i++) {
                distance += cities.get(i).distanceTo(cities.get(i + 1));
            }
            distance += cities.get(cities.size() - 1).distanceTo(cities.get(0));
        }
    }
    private static LocalDateTime originArrivalTime;
    private static LocalDateTime originDepartureTime;
    public static void setOriginStartTime(LocalDateTime arrivalTime) {
        GeneticAlgorithmTSP.originArrivalTime = arrivalTime;
    }
    public static void setOriginEndTime(LocalDateTime  departureTime) {
        GeneticAlgorithmTSP.originDepartureTime =  departureTime;
    }


    public static List<SchedulerDto> getOptimalRoute(String originAdd, String destiAdd, List<AdminItemEntity> places) {

        GeoApiContext context = new GeoApiContext.Builder().apiKey(API_KEY).build();
        DirectionsApiRequest request = DirectionsApi.newRequest(context);
        List<City> cities = new ArrayList<>();
        List<SchedulerDto> bestScheduler = new ArrayList<>();
        // 출발지 설정 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        LatLng originLatLng = getLatLngFromAddress(originAdd);
        if (originLatLng == null) {
            System.out.println("출발지 주소를 좌표로 변환할 수 없습니다.");
            return null;
        }
        //LocalDateTime originArrivalTime = LocalDateTime.of(2024, 4, 25, 10, 0); // 출발 시간을 설정
        cities.add(new City(originLatLng.lat, originLatLng.lng, ContentType.ORIGIN, 0, 1, originArrivalTime, 0, 0L));

        // 중간 경유지 설정 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        for (int i = 0; i < places.size(); i++) {
            LatLng waypointLatLng = getLatLngFromAddress(places.get(i).getAddress());
            ContentType contentType = ContentType.valueOf(places.get(i).getContentType());
            int stayTime = 0; // 머무는 시간
            if (contentType == ContentType.숙박) {
                stayTime = 600; // 분 단위로 변환
            }
            if (contentType == ContentType.식당 ) {
                stayTime = 60; // 10시간 -> 분 단위로 변환
            }
            if (contentType == ContentType.명소 ) {
                stayTime = 90; // 분 단위로 변환
            }

            cities.add(new City(waypointLatLng.lat, waypointLatLng.lng, contentType, i+1, 1,null, stayTime, places.get(i).getId()));
        }
        // 목적지 설정 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        LatLng destinationLatLng = getLatLngFromAddress(destiAdd);
        if (destinationLatLng == null) {
            System.out.println("목적지 주소를 좌표로 변환할 수 없습니다.");
            return null;
        }
        cities.add(new City(destinationLatLng.lat, destinationLatLng.lng, ContentType.DESTINATION, places.size()+1 ,1, null, 0, 1000L));

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Route bestRoute = findOptimalRoute(cities, places);
        for(int i = 0; i < bestRoute.cities.size(); i++) {
            SchedulerDto schedulerDto = new SchedulerDto();
            schedulerDto.setLat(bestRoute.cities.get(i).getX());
            schedulerDto.setLng(bestRoute.cities.get(i).getY());
            schedulerDto.setArrivalTime(bestRoute.cities.get(i).getArrivalTime());
            schedulerDto.setResultItemId(bestRoute.cities.get(i).getSpotId());
            bestScheduler.add(schedulerDto);
        }

        return bestScheduler;
    }

    public static LatLng getLatLngFromAddress(String address) {
        GeoApiContext context = new GeoApiContext.Builder().apiKey(API_KEY).build();
        GeocodingResult[] results;
        try {
            results = GeocodingApi.geocode(context, address).await();
            if (results != null && results.length > 0) {
                return results[0].geometry.location;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Route findOptimalRoute(List<City> cities, List<AdminItemEntity> places) {
        // 도시 유형에 따른 이동 시간 행렬 생성
        double[][] travelTimes = createTravelTimeMatrix(cities);

        // 최적 경로 계산
        List<City> optimalRoute = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        int nextCityIndex;  // 0번은 가장가까운 도시, 1번은 places에 저장된 고유 id
        City currentCity = cities.get(0); // 시작 도시
        optimalRoute.add(currentCity);  // 0번 인덱스에 시작 도시 넣기
        visited.add(0);
        LocalDateTime currentTime = currentCity.arrivalTime; // 현재 시간을 출발 시간으로 설정.

        for (int i = 0; i < cities.size() - 1; i++) {
            if(i < cities.size() - 2) {
                nextCityIndex = findNearestCityIndex(travelTimes, currentCity, visited, currentTime, places);
            }
            else{
                nextCityIndex = cities.size() - 1;
            }

            City nextCity = cities.get(nextCityIndex); // 다음 도시
            // 다음 도시까지 이동하는 시간 계산
            double travelTimeToNextCity = travelTimes[currentCity.getIndex()][nextCityIndex];

            // 도착 시간 업데이트 (현재 시간 + 이동 시간 + 머무는 시간)
            currentTime = currentTime.plusMinutes((long) (travelTimeToNextCity / 60)); // 이동 시간....
            currentTime = currentTime.plusMinutes(currentCity.stayTime); // 머무는 시간 추가
            nextCity.arrivalTime = currentTime; // 다음 도시의 도착 시간 업데이트
//            if (currentTime.toLocalTime().isAfter(LocalTime.of(23, 59))) {
//                currentCity.setDay(currentCity.getDay() + 1); // 다음 날로 넘어감
//            }

            optimalRoute.add(nextCity);
            visited.add(nextCityIndex);
            currentCity = nextCity; // 다음 도시를 현재 도시로 업데이트
        }

        // 마지막으로 시작 도시로 돌아가는 경로 추가
        //optimalRoute.add(cities.get(0));

        return new Route(optimalRoute);
    }

    private static int findNearestCityIndex(double[][] travelTimes, City currentCity, Set<Integer> visited, LocalDateTime currentTime, List<AdminItemEntity> places) {
        int minIndex = -1;
        double minTravelTime = Double.MAX_VALUE;
        for (int i = 1; i < travelTimes.length-1; i++) {
            if (!visited.contains(i)) {
                double travelTime = travelTimes[currentCity.getIndex()][i];
                if (travelTime < minTravelTime) {
                    String contentType = places.get(i - 1).getContentType().trim();

                    if (shouldSkipToNextRestaurant(currentTime) && contentType.equals("식당")) {
                        System.out.println("currentTime1 : " + currentTime);
                        minIndex = i;
                        minTravelTime = travelTime;
                    }else if (shouldSkipToNextLodging(currentTime) && contentType.equals("숙박")) {
                        System.out.println("currentTime2 : " + currentTime);
                        minIndex = i;
                        minTravelTime = travelTime;
                    }else if (!(shouldSkipToNextRestaurant(currentTime) || shouldSkipToNextLodging(currentTime))) {
                        minIndex = i;
                        minTravelTime = travelTime;
                    }
                }
            }
        }

        // 만약 식당이나 숙박 시설이 없을 때
        if (minIndex == -1) {
            // 방문하지 않은 도시 중 가장 가까운 도시로 이동
            for (int i = 1; i < travelTimes.length - 1; i++) {
                if (!visited.contains(i)) {
                    double travelTime = travelTimes[currentCity.getIndex()][i];
                    if (travelTime < minTravelTime) {
                        minIndex = i;
                        minTravelTime = travelTime;
                    }
                }
            }
        }

        return minIndex;
    }
    private static boolean shouldSkipToNextRestaurant(LocalDateTime currentTime) {
        // arrivalTime이 12:00가 지나거나 18:00이 지났으면서 place가 식당이면 true 반환
        LocalTime timeOfDay = currentTime.toLocalTime(); // 현재 시간의 시분을 가져옴
        return (timeOfDay.isAfter(LocalTime.of(11, 30)) && timeOfDay.isBefore(LocalTime.of(13, 40))) ||
                (timeOfDay.isAfter(LocalTime.of(17, 40)) && timeOfDay.isBefore(LocalTime.of(19, 0)));
    }

    private static boolean shouldSkipToNextLodging(LocalDateTime currentTime) {
        LocalTime timeOfDay = currentTime.toLocalTime();
        System.out.println("timeofLodging"+timeOfDay);
        return (timeOfDay.isAfter(LocalTime.of(20, 0)) && timeOfDay.isBefore(LocalTime.MAX)) ||
                (timeOfDay.isAfter(LocalTime.MIN) && timeOfDay.isBefore(LocalTime.of(5, 0)));
    }

    private static List<Route> generateInitialPopulation(List<City> cities) {
        List<Route> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<City> shuffledCities = new ArrayList<>(cities);
            Collections.shuffle(shuffledCities);
            population.add(new Route(shuffledCities));
        }
        return population;
    }

    private static List<Route> evolvePopulation(List<Route> population) {
        List<Route> newPopulation = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Route parent1 = selectParent(population);
            Route parent2 = selectParent(population);
            Route child = crossover(parent1, parent2);
            mutate(child);
            newPopulation.add(child);
        }
        return newPopulation;
    }

    private static Route selectParent(List<Route> population) {
        return population.get(random.nextInt(population.size()));
    }

    private static Route crossover(Route parent1, Route parent2) {
        int startPos = random.nextInt(parent1.cities.size());
        int endPos = random.nextInt(parent1.cities.size());
        List<City> childCities = new ArrayList<>();
        for (int i = 0; i < parent1.cities.size(); i++) {
            if (startPos < endPos && i > startPos && i < endPos) {
                childCities.add(parent1.cities.get(i));
            } else if (startPos > endPos && !(i < startPos && i > endPos)) {
                childCities.add(parent1.cities.get(i));
            }
        }
        for (City city : parent2.cities) {
            if (!childCities.contains(city)) {
                childCities.add(city);
            }
        }
        return new Route(childCities);
    }

    private static void mutate(Route route) {
        if (random.nextDouble() < MUTATION_RATE) {
            int pos1 = random.nextInt(route.cities.size());
            int pos2 = random.nextInt(route.cities.size());
            Collections.swap(route.cities, pos1, pos2);
            route.calculateDistance();
        }
    }

    private static double[][] createTravelTimeMatrix(List<City> cities) {
        int numCities = cities.size();
        double[][] travelTimes = new double[numCities][numCities];

        GeoApiContext context = new GeoApiContext.Builder().apiKey(API_KEY).build();

        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                if (i != j) {
                    LatLng origin = new LatLng(cities.get(i).getX(), cities.get(i).getY());
                    LatLng destination = new LatLng(cities.get(j).getX(), cities.get(j).getY());

                    DirectionsApiRequest request = new DirectionsApiRequest(context)
                            .origin(origin)
                            .destination(destination)
                            .mode(TravelMode.TRANSIT);

                    try {
                        DirectionsRoute[] routes = request.await().routes;
                        if (routes != null && routes.length > 0) {
                            // 첫 번째 경로의 총 이동 시간을 이동 시간 행렬에 저장
                            travelTimes[i][j] = routes[0].legs[0].duration.inSeconds;
                        } else {
                            // 경로를 찾을 수 없는 경우 큰 값으로 설정하여 해당 경로를 피할 수 있도록 함
                            travelTimes[i][j] = Double.MAX_VALUE;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // 오류가 발생한 경우 큰 값으로 설정하여 해당 경로를 피할 수 있도록 함
                        travelTimes[i][j] = Double.MAX_VALUE;
                    }
                } else {
                    // 같은 도시일 경우 0으로 설정
                    travelTimes[i][j] = 0;
                }
            }
        }

        return travelTimes;
    }

    private static double calculateTravelTime(City city1, City city2) {
        // 여기에서 도시 유형에 따른 이동 시간 계산
        // 예를 들어, 식당에서 식당으로 이동하는 경우는 빠르게 이동할 수 있도록 설정
        // 숙박지에서 숙박지로 이동하는 경우도 마찬가지
        // 식당에서 숙박지로 이동하는 경우는 조금 더 시간이 걸릴 수 있음
        // 등등...
        return 0.0; // 임시로 0으로 설정
    }

}


