 let markers = []; // 핀을 관리하는 배열
 let map;
 let geocoder;
 let selectedCities = new Set(); // 선택된 도시를 저장할 Set
 const MAX_SELECTED_CITIES = 4; // 선택 가능한 최대 도시 개수

document.addEventListener("DOMContentLoaded", () => {

    // 추가-----------------------------------------------//
    createInitialLocalElements();
    const selectItem = document.getElementById("selectItem");
    const initialLocalDivs = document.querySelectorAll(".initialLocals > div"); // ".initialLocals"의 자식인 <div> 요소들을 선택합니다.

    if (initialLocalDivs) {
        initialLocalDivs.forEach((div) => {
            const countryElement = div.querySelector(".initialCountry");
            const localElement = div.querySelector(".initialLocal");
            const idElement = div.querySelector(".initialId");

            if (countryElement && localElement && idElement) {
                const country = countryElement.textContent.trim();
                const local = localElement.textContent.trim();
                const initialId = idElement.textContent.trim();
                const initialLocationText = `${country}, ${local}`;

                const locationBlock = document.createElement("div");
                locationBlock.classList.add("selected-item");
                locationBlock.setAttribute("data-location", initialLocationText);
                locationBlock.textContent = initialLocationText;

                const hiddenInput = document.createElement("input");
                hiddenInput.type = "hidden";
                hiddenInput.name = "localId";
                hiddenInput.value = initialId;
                locationBlock.appendChild(hiddenInput);

                selectItem.appendChild(locationBlock);
                selectedCities.add(initialLocationText);
            }
        });
    }

     // 추가-----------------------------------------------//
    initMap();

    selectItem.addEventListener("click", (event) => {
        if (event.target && event.target.classList.contains("selected-item")) {
            const locationText = event.target.getAttribute("data-location");
            const marker = markers.find((m) => m.getTitle() === locationText);
            const locationBlock = event.target;
            removeMarkerAndBlock(locationText, marker, locationBlock);
        }
    });

function initMap() {
        map = new google.maps.Map(document.getElementById("map"), {
            center: { lat: 37.5665, lng: 126.978 }, // 서울 중심 좌표
            zoom: 8,
        });

        geocoder = new google.maps.Geocoder();


       const selectItem = document.getElementById("selectItem");
       if (selectItem) {
           const initLocation = selectItem.querySelectorAll('.selected-item');
           initLocation.forEach((item) => {
               const locationData = item.getAttribute('data-location');
               const [city, country] = locationData.split(',').map(item => item.trim());

               const locationText = `${city}, ${country}`;

               var national = document.querySelector("#national");
               var cityInput = document.querySelector("#city");
               var nationalValue = national.value;
               var cityValue = cityInput.value;

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

                       // 기존 선택된 국가 가져오기
                       const selectedCountryElement = selectItem.querySelector(".selected-item");
                       const selectedCountryText = selectedCountryElement ? selectedCountryElement.getAttribute("data-location").split(",")[1].trim() : null;

                       // 관광지의 국가 가져오기
                       const newCountryText = country;

                       // 국가가 다른 경우에 알림 표시
                       if (selectedCountryText && selectedCountryText !== newCountryText) {
                           alert("같은 국가만 선택할 수 있습니다.");
                           return; // 국가가 다르면 추가하지 않고 종료
                       }

                       // 국가가 같은 경우에만 선택 목록에 관광지 추가
                       const locationText2 = item.querySelector("input[name='localId']").value;
                       addCityToSelection(locationText, locationText2, marker);
                       map.panTo(location); // 맵을 선택된 위치로 이동
                   } else {
                       console.error("위치를 찾을 수 없습니다.");
                   }
               });
           });
       }


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

                // 국가 비교 로직 추가
                const selectedCountryElement = selectItem.querySelector(".selected-item");
                const selectedCountryText = selectedCountryElement ? selectedCountryElement.getAttribute("data-location").split(",")[1].trim() : null;
                const newCountryText = countryName;

                if (selectedCountryText && selectedCountryText !== newCountryText) {
                    alert("같은 국가만 선택할 수 있습니다.");
                    return; // 국가가 다르면 추가하지 않고 종료
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

    /*if (selectedCities.size >= MAX_SELECTED_CITIES) {
        alert("고를 수 있는 관광지는 최대 4개입니다. 먼저 등록한 관광지를 눌러 제거해주세요.");
        return;
    }

    const selectItem = document.getElementById("selectItem");

    // 동일 지역명 블록이 이미 존재하는지 확인
    const existingBlock = selectItem.querySelector(`.selected-item[data-location="${locationText}"]`);
    if (existingBlock) {
        return;
    }*/

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

function createInitialLocalElements() {

 }

});