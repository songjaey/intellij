document.addEventListener("DOMContentLoaded", () => {
    let markers = []; // Array to manage markers
    let map;
    let geocoder;
    let selectedSpots = new Set(); // Set to store selected locations
    const mapElement = document.getElementById("map");
    const initialLocalValue = document.getElementById("initialLocal").value;

    console.log("initialLocal:", initialLocalValue);

    function initMap(initialLocation) {
        map = new google.maps.Map(document.getElementById("map"), {
            center: { lat: 37.5665, lng: 126.978 }, // Default map center (Seoul City Hall)
            zoom: 11,
        });

        geocoder = new google.maps.Geocoder();

        if (initialLocation) {
            const locationText = initialLocation;

            geocoder.geocode({ address: locationText }, (results, status) => {
                if (status === "OK" && results && results.length > 0) {
                    const location = results[0].geometry.location;
                    // Set the map center to the searched location
                    map.setCenter(location);
                } else {
                    console.error("위치를 찾을 수 없습니다.");
                }
            });
        }

        //////////////////////////////////////////////////////////////////////////////
            const selectItem = document.getElementById("selectItem");
            const locationItems = document.querySelectorAll('.contents');
            const initItem = selectItem.querySelectorAll('.initialItem');
            if ( $(initItem).length > 0) {
               initItem.forEach((item) => {
                   const spotAddress = item.querySelector('.initialLocal').textContent;
                   const spotName = item.querySelector('.initialSpotName').textContent;
                   const initId = item.querySelector('.initialId').textContent.trim();
                   const contentType = item.querySelector('.initialContentType').textContent;
                   const locationAddress = spotAddress;
                   const locationText = spotName;

                   locationItems.forEach((spot) => {
                       const checkbox = spot.querySelector('.location-checkbox');
                       const spotId = spot.querySelector('.D').textContent;
                       if (initId === spotId) {
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
                           addCityToSelection(locationText,initId,locationAddress,contentType, marker);
                           map.panTo(location);
                       } else {
                           console.error("위치를 찾을 수 없습니다.");
                       }
                   });
               }); // forEach 메서드의 괄호 닫기
            }

            const initialItem2 = selectItem.querySelectorAll('.initialItem2');
        //    //////////////////////////////////////////////////////////////////////////////
            if ( $(initialItem2).length > 0) {
               initialItem2.forEach((item) => {
                   const spotAddress = item.querySelector('.initialLocal').textContent;
                   const spotName = item.querySelector('.initialSpotName').textContent;
                   const initId = item.querySelector('.initialId').textContent.trim();
                   const contentType = item.querySelector('.initialContentType').textContent;
                   const locationAddress = spotAddress;
                   const locationText = spotName;

                   locationItems.forEach((spot) => {
                       const checkbox = spot.querySelector('.location-checkbox');
                       const spotId = spot.querySelector('.D').textContent;
                       if (initId === spotId) {
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
                           addCityToSelection(locationText,initId,locationAddress,contentType, marker);
                           map.panTo(location);
                       } else {
                           console.error("위치를 찾을 수 없습니다.");
                       }
                   });
               }); // forEach 메서드의 괄호 닫기
            }
            //////////////////////////////////////////////////////////////////////////////

            //////////////////////////////////////////////////////////////////////////////


        locationItems.forEach((spot) => {
            const checkbox = spot.querySelector('.location-checkbox');
            const spotName = spot.querySelector('.spot-name').textContent;
            const spotId = spot.querySelector('.D').textContent.trim();
            const spotAddress = spot.querySelector('.spot-address').textContent.trim();
            const contentType = spot.getAttribute('data-content-type');

            checkbox.addEventListener('change', (event) => {
                const locationText = spotName;

                if (event.target.checked) {
                    if (selectedSpots.has(locationText)) {
                        alert(`'${locationText}'는 이미 선택된 장소입니다.`);
                        event.target.checked = false;
                        return;
                    }

                    geocoder.geocode({ address: spotAddress }, (results, status) => {
                        if (status === "OK" && results && results.length > 0) {
                            const location = results[0].geometry.location;

                            const marker = new google.maps.Marker({
                                map: map,
                                position: location,
                                title: locationText,
                            });

                            markers.push(marker);
                            addCityToSelection(locationText, spotId, spotAddress,contentType, marker);
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

            const locationBlock = document.querySelector(`.selected-item[data-location="${spotName}"]`);
            if (locationBlock) {
                locationBlock.addEventListener('click', () => {
                    checkbox.checked = false;
                    removeMarkerAndBlock(spotName, markers.find((m) => m.getTitle() === spotName), locationBlock);
                });
            }
        });
    }

    function addCityToSelection(locationText, locationId, locationAddress,contentType, marker) {
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
        hiddenInput.name = "spotId";
        hiddenInput.value = locationId;
        locationBlock.appendChild(hiddenInput);

        const hiddenInput2 = document.createElement("input");
        hiddenInput2.type = "hidden";
        hiddenInput2.name = "spotMark";
        hiddenInput2.value = locationAddress;
        locationBlock.appendChild(hiddenInput2);

        const hiddenInput3 = document.createElement("input");
        hiddenInput3.type = "hidden";
        hiddenInput3.name = "contentType";
        hiddenInput3.value = contentType;
        locationBlock.appendChild(hiddenInput3);

        selectItem.appendChild(locationBlock);
        selectedSpots.add(locationText);
    }

    function removeMarkerAndBlock(locationText, marker, locationBlock) {
        const index = markers.findIndex((m) => m === marker);
        if (index !== -1) {
            markers.splice(index, 1);
        }
        marker.setMap(null);

        selectedSpots.delete(locationText);
        locationBlock.remove();

        // Uncheck the checkbox of the related content
        const relatedContent = document.querySelector(`.contents[data-content-type="${locationText}"]`);
        const checkbox = relatedContent.querySelector('.location-checkbox');
        if (checkbox) {
            checkbox.checked = false;
        }
    }

    initMap(initialLocalValue);
});