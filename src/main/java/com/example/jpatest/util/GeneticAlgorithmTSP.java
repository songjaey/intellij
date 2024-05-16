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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.desktop.SystemEventListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class GeneticAlgorithmTSP {
    private static final Random random = new Random();

    private enum ContentType {
        ORIGIN, DESTINATION, 식당, 숙박, 명소;

    }
    private static final String API_KEY = "AIzaSyBKfXUB5b-aS6hLNA93HhTe6UNIqXZkizs";
    private static final int POPULATION_SIZE = 50;
    private static final double MUTATION_RATE = 0.1;
    private static final int NUM_GENERATIONS = 1000;

    @Getter
    @Setter
    @AllArgsConstructor
    private static class City {
        double x, y;
        ContentType type;
        int index; // 인덱스 필드 추가
        int day;
        LocalDateTime arrivalTime; // LocalDateTime으로 변경
        LocalDateTime departureTime;
        int stayTime; // 머무는 시간 추가
        Long spotId; // 아이템의 고유 번호

        // 추가 생성자
//        public City(double x, double y, ContentType type, int index, int day, LocalDateTime arrivalTime, LocalDateTime departureTime, int stayTime, Long spotId) {
//            this.x = x;
//            this.y = y;
//            this.type = type;
//            this.index = index;
//            this.day = day;
//            this.arrivalTime = arrivalTime;
//            this.departureTime = departureTime;
//            this.stayTime = stayTime;
//            this.spotId = spotId;
//        }


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
        cities.add(new City(originLatLng.lat, originLatLng.lng, ContentType.ORIGIN, 0, 1, originArrivalTime, originArrivalTime ,0, 0L));


        // 중간 경유지 설정 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        for (int i = 0; i < places.size(); i++) {
            LatLng waypointLatLng = getLatLngFromAddress(places.get(i).getAddress());
            ContentType contentType = ContentType.valueOf(places.get(i).getContentType());
            int stayTime = 0; // 머무는 시간
            cities.add(new City(waypointLatLng.lat, waypointLatLng.lng, contentType, i+1, 1,null, null, stayTime, places.get(i).getId()));
        }
        // 목적지 설정 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        LatLng destinationLatLng = getLatLngFromAddress(destiAdd);
        if (destinationLatLng == null) {
            System.out.println("목적지 주소를 좌표로 변환할 수 없습니다.");
            return null;
        }
        cities.add(new City(destinationLatLng.lat, destinationLatLng.lng, ContentType.DESTINATION, places.size()+1 ,1, originDepartureTime,originDepartureTime , 0, 1000L));

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Route bestRoute = findOptimalRoute(cities, places);  //최적해 계산.
        for(int i = 0; i < bestRoute.cities.size(); i++) {
            SchedulerDto schedulerDto = new SchedulerDto();
            schedulerDto.setLat(bestRoute.cities.get(i).getX());
            schedulerDto.setLng(bestRoute.cities.get(i).getY());
            schedulerDto.setArrivalTime(bestRoute.cities.get(i).getArrivalTime());
            schedulerDto.setDepartureTime(bestRoute.cities.get(i).getDepartureTime());
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
        String nation = places.get(0).getLocal().getCountry();
        double[][] travelTimes = createTravelTimeMatrix(cities, nation);

        //전체 경로들 간의 평균 시간 계산///////////////////////////////
        double timeFactor = 1.15;
        int numCities = cities.size();
        double sum = 0.0;
        int num = 0;
        for (int i = 0; i < numCities-1; i++) {
            for (int j = i+1; j < numCities; j++) {
                if(travelTimes[i][j]>100000000) {
                    sum += 0;
                }
                else{
                    sum += travelTimes[i][j];
                    num++;
                }
            }
        }
//        int division = (numCities-1)*(numCities)/2;
        double avgTime = timeFactor * (sum / num);
        long avgMinuteTime = (long)avgTime/60;
        System.out.println("!!!!!!!!!!avgMinuteTime : "+ avgMinuteTime);

        ///////////////////도착시간 - 출발시간///////////
        LocalDateTime dateTime1 = originArrivalTime;
        LocalDateTime dateTime2 = originDepartureTime;
        Duration duration = Duration.between(dateTime1, dateTime2);
        long diffDays = duration.toDays();
        long totalMinutes = duration.toMinutes();
        long totalMinusTransfer = totalMinutes - avgMinuteTime * (cities.size()-1);


        int numAttractions = 0; // 명소 개수
        int numRestaurants = 0; // 식당 개수
        int numAccommodations = 0; // 숙박 개수
        //숙박, 식당, 명소 개수 파악. //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        for (AdminItemEntity place : places) {
            ContentType contentType = ContentType.valueOf(place.getContentType());
            if (contentType == ContentType.숙박) {
                numAccommodations++;
            } else if (contentType == ContentType.식당) {
                numRestaurants++;
            } else if (contentType == ContentType.명소) {
                numAttractions++;
            }
        }
        long netMinute = totalMinusTransfer - 630L * numAccommodations;
        double proportionOfAtt = (double) numAttractions /(numAttractions + numRestaurants);
        double proportionOfRes = 1 - proportionOfAtt;
        int stayTime = (int) netMinute / (numAttractions + numRestaurants);// 머무는 시간
        //Stay Time 계산해서 집어 넣기 ///////////////////////////////////////////////////////////////////////////////////
        for (int i = 0; i < places.size(); i++) {
            ContentType contentType = ContentType.valueOf(places.get(i).getContentType());
            if(contentType == ContentType.숙박) {
                cities.get(i + 1).setStayTime(630);
                //System.out.println("Lodging!!!!"+ cities.get(i+1).getType() + " setStayTime :" + cities.get(i+1).getSpotId());
            }else{
                cities.get(i + 1).setStayTime(stayTime);
            }
        }
        //System.out.println(cities);
        // 총 숙박일과



        // 최적 경로 계산
        List<City> optimalRoute = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        int nextCityIndex;
        City currentCity = cities.get(0); // 시작 도시
        optimalRoute.add(currentCity);  // 0번 인덱스에 시작 도시 넣기
        visited.add(0);
        //도착 시간에 머무는 시간 더해서 출발시간 만들기
        LocalDateTime currentTime = currentCity.arrivalTime; // 현재 시간을 출발 시간으로 설정.
        currentTime = currentTime.plusMinutes(currentCity.stayTime);
        currentCity.setDepartureTime(currentTime);// 출발 시간은 현재 시간에 머무는 시간을 더한다.

        //double realTime = 0;
        for (int i = 1; i < cities.size(); i++) {
            if(i < cities.size() - 1) {
                nextCityIndex = findNearestCityIndex(travelTimes, currentCity, visited,cities);
            }
            else{
                nextCityIndex = cities.size() - 1; // places.size()+1;
            }
            // 다음 도시까지 이동하는 시간 계산
            double travelTimeToNextCity = travelTimes[currentCity.getIndex()][nextCityIndex];
            // 도착 시간 업데이트 (현재 시간 + 이동 시간)
            currentTime = currentTime.plusMinutes((long) (travelTimeToNextCity / 60)); // 출발(현재)시간 + 이동시간
            City nextCity = cities.get(nextCityIndex); // 다음 도시
            nextCity.setArrivalTime(currentTime);  // 다음 도시의 도착 시간 업데이트

            System.out.println("nextCityId"+nextCity.getSpotId());
            System.out.println("StayTime"+nextCity.getStayTime());

            currentTime = currentTime.plusMinutes(nextCity.stayTime);
            nextCity.setDepartureTime(currentTime);

            optimalRoute.add(nextCity);
            visited.add(nextCityIndex);
            currentCity = nextCity; // 다음 도시를 현재 도시로 업데이트
        }
        //System.out.println("!!!!!!!!!!realTime : "+ realTime/(cities.size()-1));
        // 마지막으로 시작 도시로 돌아가는 경로 추가
        //optimalRoute.add(cities.get(0));

        return new Route(optimalRoute);
    }

    private static int findNearestCityIndex(double[][] travelTimes, City currentCity, Set<Integer> visited, List<City> cities) {
        int minIndex = -1;
        double minTravelTime = Double.MAX_VALUE;
        LocalDateTime currentTime = currentCity.getDepartureTime();
        for (int i = 1; i < travelTimes.length-1; i++) {
            if (!visited.contains(i)) {
                double travelTime = travelTimes[currentCity.getIndex()][i];
                                if (travelTime < minTravelTime) {
                    ContentType contentType = cities.get(i).getType();
                    if (shouldSkipToNextRestaurant(currentTime) && (contentType == ContentType.식당 )) {
                        minIndex = i;
                        minTravelTime = travelTime;
                    }else if (shouldSkipToNextLodging(currentTime) && (contentType == ContentType.숙박 )) {
                        minIndex = i;
                        minTravelTime = travelTime;
                    }else if ( !shouldSkipToNextRestaurant(currentTime)&&!shouldSkipToNextLodging(currentTime)&&(contentType == ContentType.명소 )) {
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

    private static double[][] createTravelTimeMatrix(List<City> cities, String nation) {
        int numCities = cities.size();
        double[][] travelTimes = new double[numCities][numCities];

        GeoApiContext context = new GeoApiContext.Builder().apiKey(API_KEY).build();
        System.out.println("nation :!!:" + nation);
        // 수정가능 ///////////////////////////////////////////////////////////////////
        for (int i = 0; i < numCities; i++) {
            for (int j = i; j < numCities; j++) {
                if (i != j) {
                    LatLng origin = new LatLng(cities.get(i).getX(), cities.get(i).getY());
                    LatLng destination = new LatLng(cities.get(j).getX(), cities.get(j).getY());
                    TravelMode travelMode = null;

                    if(nation.equals("한국"))travelMode = TravelMode.TRANSIT;
                    else if(nation.equals("일본")) travelMode = TravelMode.DRIVING;

                    DirectionsApiRequest request = new DirectionsApiRequest(context)
                            .origin(origin)
                            .destination(destination)
                            .mode(travelMode);
                    try {
                        DirectionsRoute[] routes = request.await().routes;
                        if (routes != null && routes.length > 0) {
                            // 첫 번째 경로의 총 이동 시간을 이동 시간 행렬에 저장
                            travelTimes[i][j] = routes[0].legs[0].duration.inSeconds;
                            travelTimes[j][i] = travelTimes[i][j];
                        } else {
                            // 경로를 찾을 수 없는 경우 큰 값으로 설정하여 해당 경로를 피할 수 있도록 함
                            travelTimes[i][j] = Double.MAX_VALUE;
                            travelTimes[j][i] = travelTimes[i][j];
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // 오류가 발생한 경우 큰 값으로 설정하여 해당 경로를 피할 수 있도록 함
                        travelTimes[i][j] = Double.MAX_VALUE;
                        travelTimes[j][i] = travelTimes[i][j];
                    }
                } else {
                    // 같은 도시일 경우 0으로 설정
                    travelTimes[i][j] = 0;
                }
            }
        }

        return travelTimes;
    }


}


