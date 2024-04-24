document.addEventListener("DOMContentLoaded", () => {
    let map;
    let geocoder;
    let markers = [];
    const locationList = document.getElementById("locationList");

    initMap();

    function initMap() {
        map = new google.maps.Map(document.getElementById("map"), {
            center: { lat: 37.5665, lng: 126.978 }, // 서울 시청을 중심으로 지도 초기화
            zoom: 12,
        });

        geocoder = new google.maps.Geocoder();

        // 모든 contents 요소를 가져와서 처리
        const contents = Array.from(locationList.querySelectorAll('.contents'));

        // 각 contents를 거리순으로 정렬
        contents.sort((a, b) => {
            // 각 contents의 주소 정보를 가져옴
            const addressA = a.querySelector('.result-address').textContent.trim();
            const addressB = b.querySelector('.result-address').textContent.trim();

            // 각 주소를 이용하여 거리 비교 (실제로는 Geocoding API를 이용하여 거리를 계산해야 함)
            // 여기서는 예시로 주소 비교
            return addressA.localeCompare(addressB);
        });

        // 각 contents를 처리하면서 마커 추가
        contents.forEach((content, index) => {
            const resultName = content.querySelector('.result-name').textContent;
            const resultAddress = content.querySelector('.result-address').textContent;

            geocoder.geocode({ address: resultAddress }, (results, status) => {
                if (status === "OK" && results && results.length > 0) {
                    const location = results[0].geometry.location;

                    const marker = new google.maps.Marker({
                        map: map,
                        position: location,
                        label: (index + 1).toString(), // 순서대로 번호 부여
                        title: resultName
                    });

                    markers.push(marker);

                    // 마커가 2개 이상 생성된 경우, 이전 마커와 현재 마커를 선으로 연결
                    if (markers.length > 1) {
                        const prevMarker = markers[markers.length - 2];
                        const currentMarker = markers[markers.length - 1];

                        const line = new google.maps.Polyline({
                            path: [prevMarker.getPosition(), currentMarker.getPosition()],
                            map: map,
                            strokeColor: "#FF0000",
                            strokeOpacity: 1.0,
                            strokeWeight: 2
                        });
                    }

                    // 지도 중심을 첫 번째 마커 위치로 변경
                    if (index === 0) {
                        map.setCenter(location);
                    }
                } else {
                    console.error(`위치를 찾을 수 없습니다: ${resultAddress}`);
                }
            });
        });
    }
});