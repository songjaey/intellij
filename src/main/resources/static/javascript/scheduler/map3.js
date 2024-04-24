document.addEventListener("DOMContentLoaded", () => {
    let markers = []; // 사용자가 선택한 장소의 핀을 관리하는 배열
    let spotMarkers = []; // spotMarks에 따라 생성된 핀을 관리하는 배열
    let map;
    let geocoder;
    let selectedSpots = new Set(); // 선택된 도시를 저장할 Set
    const mapElement = document.getElementById("map");
    const initialLocalValue = document.getElementById("initialLocal").value;
    const spotMarks = document.getElementById("spotMarks").value;

    console.log("initialLocal:", initialLocalValue);
    console.log("spotMarks:", spotMarks);

    initMap(initialLocalValue);

    function initMap(initialLocation) {
        map = new google.maps.Map(document.getElementById("map"), {
            center: { lat: 37.5665, lng: 126.978 }, // 초기 지도 중심 위치 (서울 시청)
            zoom: 11,
        });

        geocoder = new google.maps.Geocoder();

        // spotMarks에 있는 각 위치를 처리하여 파랑색 핀을 생성하고 spotMarkers 배열에 추가
        if (spotMarks) {
            const locations = spotMarks.split(','); // spotMarks를 쉼표로 분리하여 각 위치 배열로 변환

            locations.forEach((location) => {
                geocoder.geocode({ address: location.trim() }, (results, status) => {
                    if (status === "OK" && results && results.length > 0) {
                        const location = results[0].geometry.location;
                        const locationText = results[0].formatted_address; // 검색된 위치의 주소 문자열

                        const marker = new google.maps.Marker({
                            map: map,
                            position: location,
                            title: locationText,
                            icon: 'http://maps.google.com/mapfiles/ms/icons/blue-dot.png' // 파랑색 아이콘 사용
                        });

                        spotMarkers.push(marker); // spotMarkers 배열에 파랑색 핀 추가
                    } else {
                        console.error(`위치를 찾을 수 없습니다: ${location}`);
                    }
                });
            });
        }

        // 사용자가 선택한 장소의 핀을 관리하는 로직은 이하 동일
        const locationItems = document.querySelectorAll('.contents');

        locationItems.forEach((stay) => {
            const checkbox = stay.querySelector('.location-checkbox');
            const stayName = stay.querySelector('.stay-name').textContent;
            const stayId = stay.querySelector('.D').textContent.trim();
            const Address = stay.querySelector('.stay-address').textContent.trim();

            checkbox.addEventListener('change', (event) => {
                const locationText = stayName;
                const locationAddress = Address;

                if (event.target.checked) {
                    if (selectedSpots.has(locationText)) {
                        alert(`'${locationText}'는 이미 선택된 장소입니다.`);
                        event.target.checked = false;
                        return;
                    }

                    geocoder.geocode({ address: locationAddress }, (results, status) => {
                        if (status === "OK" && results && results.length > 0) {
                            const location = results[0].geometry.location;

                            const marker = new google.maps.Marker({
                                map: map,
                                position: location,
                                title: locationText,
                            });
                            markers.push(marker);
                            addCityToSelection(locationText, stayId, marker); // 선택된 위치에 빨간색 핀 추가
                            map.panTo(location);
                        } else {
                            console.error("위치를 찾을 수 없습니다.");
                        }
                    });
                } else {
                    const marker = markers.find((m) => m.getTitle() === locationText);
                    const locationBlock = document.querySelector(`.selected-item[data-location="${locationText}"]`);

                    if (marker && locationBlock) {
                        removeMarkerAndBlock(locationText, marker, locationBlock);
                    }
                }
            });

            const locationBlock = document.querySelector(`.selected-item[data-location="${stayName}"]`);
            if (locationBlock) {
                locationBlock.addEventListener('click', () => {
                    checkbox.checked = false; // 체크박스 상태 변경
                    removeMarkerAndBlock(stayName, markers.find((m) => m.getTitle() === stayName), locationBlock);
                });
            }
        });
    }

    // 선택된 위치에 빨간색 핀을 추가하는 함수
    function addCityToSelection(locationText, locationId, marker) {
        if (selectedSpots.has(locationText)) {
                return;
            }
            const selectItem = document.getElementById("selectItem");

            const locationBlock = document.createElement("div");
            locationBlock.classList.add("selected-item");
            locationBlock.setAttribute("data-location", locationText);
            locationBlock.textContent = locationText;

            const hiddenInput = document.createElement("input");
            hiddenInput.type = "hidden";
            hiddenInput.name = "stayId";
            hiddenInput.value = locationId;
            locationBlock.appendChild(hiddenInput);

            const hiddenInput2 = document.createElement("input");
            hiddenInput2.type = "hidden";
            hiddenInput2.name = "stayMark";
            hiddenInput2.value = locationText;
            locationBlock.appendChild(hiddenInput2);

            selectItem.appendChild(locationBlock);
            selectedSpots.add(locationText);
        }

    function removeMarkerAndBlock(locationText, marker, locationBlock) {
        const index = markers.findIndex((m) => m === marker); // marker의 인덱스 찾기
        if (index !== -1) {
            markers.splice(index, 1); // markers 배열에서 해당 marker 제거
        }
        marker.setMap(null); // 지도에서 marker 제거

        selectedSpots.delete(locationText); // 선택된 위치 세트에서 제거
        locationBlock.remove(); // 선택된 위치 블록 제거

        const relatedContent = document.querySelector(`.contents[data-content-type="${locationText}"]`);
        const checkbox = relatedContent.querySelector('.location-checkbox');
        if (checkbox) {
            checkbox.checked = false;
        }
    }
});