package com.example.jpatest.util;

import com.example.jpatest.entity.AdminItemEntity;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;

import java.util.List;

public class GoogleMapsUtils {

    private static final String API_KEY = "YOUR_API_KEY";

    public static DirectionsRoute[] getOptimalRoute(List<AdminItemEntity> places) {
        GeoApiContext context = new GeoApiContext.Builder().apiKey(API_KEY).build();
        DirectionsApiRequest request = DirectionsApi.newRequest(context);

        // 출발지 설정
        LatLng originLatLng = getLatLngFromAddress(places.get(0).getAddress());
        if (originLatLng == null) {
            System.out.println("출발지 주소를 좌표로 변환할 수 없습니다.");
            return null;
        }
        String origin = originLatLng.lat + "," + originLatLng.lng;
        request.origin(origin);

        // 목적지 설정
        LatLng destinationLatLng = getLatLngFromAddress(places.get(places.size() - 1).getAddress());
        if (destinationLatLng == null) {
            System.out.println("목적지 주소를 좌표로 변환할 수 없습니다.");
            return null;
        }
        String destination = destinationLatLng.lat + "," + destinationLatLng.lng;
        request.destination(destination);

        // 중간 경유지 설정
        for (int i = 1; i < places.size() - 1; i++) {
            LatLng waypointLatLng = getLatLngFromAddress(places.get(i).getAddress());
            if (waypointLatLng != null) {
                request.waypoints(waypointLatLng.lat + "," + waypointLatLng.lng);
            } else {
                System.out.println("중간 경유지 주소를 좌표로 변환할 수 없습니다: " + places.get(i).getAddress());
            }
        }

        try {
            return request.await().routes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static LatLng getLatLngFromAddress(String address) {
        // 위에서 제공한 GoogleMapsUtils 클래스 내의 getLatLngFromAddress 메서드 코드 사용

        return null;
    }
}
