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
                    } else {
                        console.error("위치를 찾을 수 없습니다.");
                    }
                });
        }
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
        //////////////////////////////////////////////////////////////////////////////
        const selectItem = document.getElementById("selectItem");
        const locationItems = document.querySelectorAll('.contents');
        const initItem = selectItem.querySelectorAll('.initialItem');
        if ( $(initItem).length > 0) {
           initItem.forEach((item) => {
               const stayAddress = item.querySelector('.initialLocal').textContent;
                const stayName = item.querySelector('.initialStayName').textContent;
                const initId = item.querySelector('.initialId').textContent.trim();
                const locationAddress = stayAddress;
                const locationText = stayName;

                locationItems.forEach((stay) => {
                    const checkbox = stay.querySelector('.location-checkbox');
                    const stayId = stay.querySelector('.D').textContent;
                    if (initId === stayId) {
                        checkbox.checked = true;
                    }
                });
                geocoder.geocode({ address: locationAddress }, (results, status) => {
                    if (status === "OK" && results && results.length > 0) {
                        const location = results[0].geometry.location;

                        const marker = new google.maps.Marker({
                            map: map,
                            position: location,
                            title: locationText,
                        });

                        markers.push(marker);
                        addCityToSelection(locationText,initId,locationAddress, marker);
                        map.panTo(location);
                    } else {
                        console.error("위치를 찾을 수 없습니다.");
                    }
                });
            }); // forEach 메서드의 괄호 닫기
        }

        const initialItem2 = selectItem.querySelectorAll('.initialItem2');
        //////////////////////////////////////////////////////////////////////////////

        if ( $(initialItem2).length > 0) {
           initialItem2.forEach((item) => {
               const stayAddress = item.querySelector('.initialLocal').textContent;
               const stayName = item.querySelector('.initialStayName').textContent;
               const initId = item.querySelector('.initialId').textContent.trim();
               const locationAddress = stayAddress;
               const locationText = stayName;
                locationItems.forEach((stay) => {
                   const checkbox = stay.querySelector('.location-checkbox');
                   const stayId = stay.querySelector('.D').textContent;
                   if (initId === stayId) {
                       checkbox.checked = true;
                   }
               });
               geocoder.geocode({ address: locationAddress }, (results, status) => {
                   if (status === "OK" && results && results.length > 0) {
                       const location = results[0].geometry.location;
                        const marker = new google.maps.Marker({
                           map: map,
                           position: location,
                           title: locationText,
                       });
                        markers.push(marker);
                       addCityToSelection(locationText,initId,locationAddress, marker);
                       map.panTo(location);
                   } else {
                       console.error("위치를 찾을 수 없습니다.");
                   }
               });
           }); // forEach 메서드의 괄호 닫기
        }
         //////////////////////////////////////////////////////////////////////////////
        locationItems.forEach((stay) => {
            const checkbox = stay.querySelector('.location-checkbox');
            const stayName = stay.querySelector('.stay-name').textContent;
            const stayId = stay.querySelector('.D').textContent.trim();
            const stayAddress = stay.querySelector('.stay-address').textContent.trim();

            checkbox.addEventListener('change', (event) => {
                const locationText = stayName;

                if (event.target.checked) {
                    if (selectedSpots.has(locationText)) {
                        alert(`'${locationText}'는 이미 선택된 장소입니다.`);
                        event.target.checked = false;
                        return;
                    }

                    geocoder.geocode({ address: stayAddress }, (results, status) => {
                        if (status === "OK" && results && results.length > 0) {
                            const location = results[0].geometry.location;

                            const marker = new google.maps.Marker({
                                map: map,
                                position: location,
                                title: locationText,
                            });
                            markers.push(marker);
                            addCityToSelection(locationText, stayId, stayAddress, marker); // 선택된 위치에 빨간색 핀 추가
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
        // 참조된 위치의 주소를 이용하여 가장 가까운 공항 주소를 콘솔에 출력

    }

    // 변수 초기화
    var airportCounts = {};

    function addCityToSelection(locationText, locationId, locationAddress, marker) {
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
        hiddenInput2.value = locationAddress;
        locationBlock.appendChild(hiddenInput2);

        const hiddenInput3 = document.createElement("input");
        hiddenInput3.type = "hidden";
        hiddenInput3.name = "airport";

        // 주변 공항 검색을 위해 선택된 위치의 주소를 사용
        findNearestAirport(locationAddress, (nearestAirport) => {
            if (nearestAirport) {
                const airportAddress = nearestAirport.vicinity;
                hiddenInput3.value = airportAddress; // 공항 주소를 설정
                console.log("가장 가까운 공항:", airportAddress);

                // 공항 주소를 카운트하고, 비교하여 가장 많이 중복된 공항 주소와 가장 적게 중복된 공항 주소를 갱신
                countAirport(airportAddress);
            } else {
                console.error("근처에 공항이 없습니다.");
                hiddenInput3.value = ""; // 공항이 없는 경우 빈 값으로 설정
            }
        });
        locationBlock.appendChild(hiddenInput3);

        selectItem.appendChild(locationBlock);
        selectedSpots.add(locationText);

        // 추가된 후에 공항 주소 업데이트
        updateFrequentAirports();
    }

    function countAirport(address) {
        if (!airportCounts[address]) {
            airportCounts[address] = 1; // 해당 주소가 처음 발견되는 경우 1로 초기화
        } else {
            airportCounts[address]++; // 이미 발견된 주소인 경우 카운트 증가
        }

        // 새로운 공항 주소가 추가되었으므로 가장 많이 중복된 공항과 가장 적게 중복된 공항을 갱신
        updateFrequentAirports();
    }

    function updateFrequentAirports() {
        var addresses = Object.keys(airportCounts);

        // 공항 주소를 카운트 순으로 정렬
        addresses.sort(function(a, b) {
            return airportCounts[b] - airportCounts[a];
        });

        // 가장 많이 중복된 공항을 설정
        var mostFrequentAirport = addresses[0];
        console.log("가장 많이 중복된 공항 변경:", mostFrequentAirport);
        document.getElementById('StartAirport').value = mostFrequentAirport;

        // 가장 적게 중복된 공항을 설정
        var leastFrequentAirport = addresses[addresses.length - 1];
        console.log("가장 적게 중복된 공항 변경:", leastFrequentAirport);

        // 선택된 위치가 있는 경우
        if (selectedSpots.size > 0) {
            const lastSelectedSpot = Array.from(selectedSpots)[selectedSpots.size - 1];
            const lastSelectedSpotBlock = document.querySelector(`.selected-item[data-location="${lastSelectedSpot}"]`);
            const lastSelectedSpotAirport = lastSelectedSpotBlock.querySelector('input[name="airport"]').value;

            // 마지막 선택된 위치의 공항 주소를 먼저 사용
            if (!lastSelectedSpotAirport) {
                document.getElementById('EndAirport').value = lastSelectedSpotAirport;
            } else {
                // 마지막 선택된 위치의 공항 주소가 없는 경우 새로운 공항 주소를 등록
                const hiddenInput = document.createElement("input");
                hiddenInput.type = "hidden";
                hiddenInput.name = "airport";
                hiddenInput.value = leastFrequentAirport;
                lastSelectedSpotBlock.appendChild(hiddenInput);
                document.getElementById('EndAirport').value = leastFrequentAirport;
            }
        } else {
            // 선택된 위치가 없는 경우에는 가장 적게 중복된 공항 주소를 사용
            document.getElementById('EndAirport').value = leastFrequentAirport;
        }

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

    function findNearestAirport(address, callback) {
        // 선택된 위치의 주소를 이용하여 주변 공항 검색
        geocoder.geocode({ address: address }, (results, status) => {
            console.log("Geocoding 결과:", results);
            console.log("Geocoding 상태:", status);
            if (status === "OK" && results && results.length > 0) {
                // 결과가 있고 상태가 "OK"인 경우
            } else {
                // 결과가 없거나 상태가 "OK"가 아닌 경우
            }
            if (status === "OK" && results && results.length > 0) {
                const location = results[0].geometry.location;

                // 주변 공항 검색을 위한 요청 설정
                const request = {
                    location: location,
                    radius: 1000000, // 100km 반경 내에서 검색
                    type: 'airport' // 공항 타입
                };

                // PlacesService를 이용하여 주변 공항 검색 요청
                const placesService = new google.maps.places.PlacesService(map);
                placesService.nearbySearch(request, (results, status) => {
                    console.log("Places API 결과:", results);
                    console.log("Places API 상태:", status);
                    if (status === google.maps.places.PlacesServiceStatus.OK) {

                        // 공항인 결과 필터링
                        const airportResults = results.filter(place => place.types.includes('airport'));

                        if (airportResults && airportResults.length > 0) {
                            // 가장 가까운 공항 선택
                            const nearestAirport = airportResults[0];
                            const airportAddress = nearestAirport.formatted_address;
                            // 콜백 함수 호출하여 결과 전달
                            callback(nearestAirport);
                        } else {
                            // 근처에 공항이 없는 경우
                            console.error('근처에 공항이 없습니다.');
                            callback(null);
                        }
                    } else {
                        // 주변 공항 검색 실패
                        console.error(`주변 공항 검색에 실패했습니다. 상태: ${status}`);
                        callback(null);
                    }
                });
            } else {
                // 위치를 찾을 수 없는 경우
                console.error(`위치를 찾을 수 없습니다: ${address}`);
                callback(null);
            }
        });
    }

    initMap(initialLocalValue);
});