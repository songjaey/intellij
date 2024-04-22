document.addEventListener("DOMContentLoaded", () => {
let markers = []; // 핀을 관리하는 배열
let map;
let geocoder;
const mapElement = document.getElementById("map");
const initialLocalValue = document.getElementById("initialLocal").value;

initMap(initialLocalValue);

function initMap(initialLocation) {
    map = new google.maps.Map(document.getElementById("map"), {
        center: { lat: 37.5665, lng: 126.978 }, // 초기 지도 중심 위치 (서울 시청)
        zoom: 11,
    });

    geocoder = new google.maps.Geocoder();

    if (initialLocation) {
        const locationText = initialLocation;

        geocoder.geocode({ address: locationText }, (results, status) => {
            if (status === "OK" && results && results.length > 0) {
                const location = results[0].geometry.location;

                // 검색된 위치를 지도의 중심으로 설정
                map.setCenter(location);

                // 마커를 markers 배열에 추가
                markers.push(marker);
            } else {
                console.error("위치를 찾을 수 없습니다.");
                // 초기 위치 설정에 실패한 경우, 지도는 서울 시청을 중심으로 유지됩니다.
            }
        });
    }
}
});