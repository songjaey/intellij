document.addEventListener("DOMContentLoaded", () => {
    let map;
    let geocoder;
    const locationList = document.getElementById("locationList");
    const markersByDay = {}; // 각 날짜별 마커를 저장할 객체

    initMap();

    function initMap() {
        // 지도 초기화
        map = new google.maps.Map(document.getElementById("map"), {
            center: { lat: 37.5665, lng: 126.978 }, // 서울 시청을 중심으로
            zoom: 11,
        });

        geocoder = new google.maps.Geocoder();

        const adminItemEntities = Array.from(locationList.querySelectorAll('.adminItemEntity'));
        const colors = ['#FF0000', '#FFA500', '#AEB404', '#008000', '#0000FF', '#87CEEB', '#000080', '#800080', '#FF0000', '#FFA500', '#FFFF00'];

        // 각 .adminItemEntity 요소를 처리
        for (let i = 0; i < adminItemEntities.length; i++) {
            const entity = adminItemEntities[i];
            const contentsList = Array.from(entity.children).filter(child => child.classList.contains('contents'));
            let dayNumber = i % colors.length; // 초기 색상 인덱스
            let iter = 1;

            // 각 .contents 요소를 처리
            processContents(0); // 초기 호출

            function processContents(index) {
                if (index >= contentsList.length) {
                    return; // 모든 contents 처리가 완료되면 종료
                }

                const contents = contentsList[index];
                const address = contents.querySelector('.result-address').textContent;
                const resultName = contents.querySelector('.result-name').textContent;

                // 주소값으로 지오코딩하여 위치 가져오기
                geocoder.geocode({ 'address': address }, function (results, status) {
                    if (status === 'OK') {
                        const location = results[0].geometry.location;

                        // 각 위치에 대한 마커 생성
                        const marker = new google.maps.Marker({
                            position: location,
                            map: map,
                            title: resultName, // 제목은 장소 이름으로 설정
                            label: iter.toString(), // 핀에 번호 부여,
                            icon: {
                                path: google.maps.SymbolPath.CIRCLE,
                                fillColor: colors[dayNumber], // 핀 색상 설정
                                fillOpacity: 1,
                                strokeWeight: 0,
                                scale: 10
                            }
                        });
                        iter++; // 다음 번호로 증가

                        // 해당 날짜의 마커 배열에 추가
                        markersByDay[dayNumber] = markersByDay[dayNumber] || [];
                        markersByDay[dayNumber].push(marker);

                        // 마커가 2개 이상인 경우 선으로 연결
                        if (markersByDay[dayNumber].length > 1) {
                            const prevMarker = markersByDay[dayNumber][markersByDay[dayNumber].length - 2];
                            const currentMarker = markersByDay[dayNumber][markersByDay[dayNumber].length - 1];

                            const line = new google.maps.Polyline({
                                path: [prevMarker.getPosition(), currentMarker.getPosition()],
                                map: map,
                                strokeColor: colors[dayNumber], // 선 색상 설정
                                strokeOpacity: 1.0,
                                strokeWeight: 2
                            });
                        }

                        // 지도 중심을 첫 번째 마커의 위치로 설정
                        if (iter === 2) {
                            map.setCenter(location);
                        }
                    } else {
                        console.error('지오코딩 실패: ' + status);
                    }

                    // 다음 contents 처리를 위해 재귀적으로 호출
                    processContents(index + 1);
                });
            }
        }
    }
});