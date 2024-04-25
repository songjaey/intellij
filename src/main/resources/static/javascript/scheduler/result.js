document.addEventListener("DOMContentLoaded", () => {
    let map;
    let geocoder;
    let markers = [];
    const locationList = document.getElementById("locationList");

    initMap();

    function initMap() {
        // 지도 초기화
        map = new google.maps.Map(document.getElementById("map"), {
            center: { lat: 37.5665, lng: 126.978 }, // 지도를 서울 시청을 중심으로 초기화
            zoom: 11,
        });

        geocoder = new google.maps.Geocoder();

        // 모든 .resultContents 요소를 가져와서 처리
        const contents = Array.from(locationList.querySelectorAll('.resultContents'));

        // 각 .resultContents 요소를 처리하고 지도에 마커를 추가
        contents.forEach((content, index) => {
            // 위도와 경도 값 가져오기
            alert(parseFloat(content.querySelector('.resultRoute:nth-child(1)').value));
            const lat = parseFloat(content.querySelector('.resultRoute:nth-child(1)').value);
            const lng = parseFloat(content.querySelector('.resultRoute:nth-child(2)').value);
            // 장소 이름 가져오기
            const resultName = "여행계획"

            // LatLng 객체 생성
            const location = { lat: lat, lng: lng };

            // 각 위치에 대한 마커 생성
            const marker = new google.maps.Marker({
                position: location,
                map: map,
                label: (index + 1).toString(), // 레이블 부여 (1, 2, 3, ...)
                title: resultName // 제목은 장소 이름으로 설정
            });

            markers.push(marker);

            // 마커가 여러 개인 경우 현재 마커와 이전 마커 사이에 선을 그림
            if (markers.length > 1) {
                const prevMarker = markers[markers.length - 2];
                const currentMarker = markers[markers.length - 1];

                const line = new google.maps.Polyline({
                    path: [prevMarker.getPosition(), currentMarker.getPosition()],
                    map: map,
                    strokeColor: "#FF0000", // 빨간색 선
                    strokeOpacity: 1.0,
                    strokeWeight: 2
                });
            }

            // 지도 중심을 첫 번째 마커의 위치로 설정
            if (index === 0) {
                map.setCenter(location);
            }
        });
    }
});