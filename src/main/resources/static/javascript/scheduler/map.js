let markers = []; // 핀을 관리하는 배열
let map;
let geocoder;
let selectedCities = new Set(); // 선택된 도시를 저장할 Set
const MAX_SELECTED_CITIES = 4; // 선택 가능한 최대 도시 개수



document.addEventListener("DOMContentLoaded", () => {
    initMap();

    const selectItem = document.getElementById("selectItem");

    selectItem.addEventListener("click", (event) => {
        if (event.target && event.target.classList.contains("selected-item")) {
            const locationText = event.target.getAttribute("data-location");
            const marker = markers.find((m) => m.getTitle() === locationText);
            const locationBlock = event.target;
            removeMarkerAndBlock(locationText, marker, locationBlock);
        }
    });
});
function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        center: { lat: 37.5665, lng: 126.978 }, // 서울 중심 좌표
        zoom: 8,
    });

    geocoder = new google.maps.Geocoder();

    const locationItems = document.querySelectorAll('#locationList li');

    locationItems.forEach((item) => {
        item.addEventListener('click', () => {
            const cityName = item.querySelector('.B').textContent.trim();
            const countryName = item.querySelector('.C').textContent.trim();
            const localId = item.querySelector('.D').textContent.trim();

            const locationText = `${cityName}, ${countryName}`;
            const locationText2 = `${localId}`;

            if (selectedCities.size >= MAX_SELECTED_CITIES) {
                alert("고를 수 있는 관광지는 최대 4개입니다. 먼저 등록한 관광지를 눌러 제거해주세요.");
                return;
            }

            if (selectedCities.has(locationText)) {
                alert(`'${locationText}'는 이미 선택된 지역입니다.`);
                return;
            }
            var national = document.querySelector("#national");
            var city = document.querySelector("#city");
            var nationalValue= national.value;
            var cityValue=city.value;
            var id = item.querySelector('.C').dataset.id;
            var local = item.querySelector('.B').dataset.location;
            var local_Id = item.querySelector('.D').dataset.localID;

            national.value=nationalValue+","+id;
            city.value=cityValue+","+local;



            geocoder.geocode({ address: locationText }, (results, status) => {
                if (status === "OK" && results && results.length > 0) {
                    const location = results[0].geometry.location;

                    const marker = new google.maps.Marker({
                        map: map,
                        position: location,
                        title: locationText,
                    });

                    const infowindow = new google.maps.InfoWindow({
                        content: locationText,
                    });

                    marker.addListener("click", () => {
                        infowindow.open(map, marker);
                    });

                    markers.push(marker);

                    addCityToSelection(locationText, locationText2, marker);
                    map.panTo(location); // 맵을 선택된 위치로 이동
                } else {
                    console.error("위치를 찾을 수 없습니다.");
                }
            });
        });
    });
}
function addCityToSelection(locationText,locationText2, marker) {
    if (selectedCities.has(locationText)) {
        return;
    }

    if (selectedCities.size >= MAX_SELECTED_CITIES) {
        alert("고를 수 있는 관광지는 최대 4개입니다. 먼저 등록한 관광지를 눌러 제거해주세요.");
        return;
    }

    const selectItem = document.getElementById("selectItem");

    // 동일 지역명 블록이 이미 존재하는지 확인
    const existingBlock = selectItem.querySelector(`.selected-item[data-location="${locationText}"]`);
    if (existingBlock) {
        return;
    }

    const locationBlock = document.createElement("div");
    locationBlock.classList.add("selected-item");
    locationBlock.setAttribute("data-location", locationText);
    locationBlock.textContent = locationText;

    const hiddenInput = document.createElement("input");
    hiddenInput.type = "hidden";
    hiddenInput.name = "localId";
    hiddenInput.value = locationText2;
    locationBlock.appendChild(hiddenInput);

    locationBlock.addEventListener("click", () => {
        removeMarkerAndBlock(locationText, marker, locationBlock);
    });

    selectItem.appendChild(locationBlock);
    selectedCities.add(locationText);
}
function removeMarkerAndBlock(locationText, marker, locationBlock) {
    const index = markers.findIndex((m) => m === marker);
    if (index !== -1) {
        markers.splice(index, 1);
    }
    marker.setMap(null);

    selectedCities.delete(locationText);
    locationBlock.remove();
}