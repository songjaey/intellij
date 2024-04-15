$(document).ready(function () {

    function getCsrfToken() {
        const cookies = document.cookie.split(';').map(cookie => cookie.trim());
        const csrfCookie = cookies.find(cookie => cookie.startsWith('XSRF-TOKEN='));

        if (csrfCookie) {
            return csrfCookie.split('=')[1];
        } else {
            return null; // CSRF 토큰을 찾지 못한 경우
        }
    }

    function saveLocalBlocks() {
        var localBlocks = [];
        $('.local_block').each(function() {
            var blockData = {};
            blockData.imageUrl = $(this).find('img').attr('src');
            blockData.touristSpotName = $(this).find('p').text();
            localBlocks.push(blockData);
        });
        localStorage.setItem('localBlocks', JSON.stringify(localBlocks));
    }

    $('#saveModalBtn').click(function () {
        var touristSpotName = $('#touristSpotName').val();
        var address = $('input[name="address"]').val();
        var contact = $('input[name="contact"]').val();
        var features = $('#InputFeatures').val();
        alert("입력좀해라");
        if (!touristSpotName || !address || !contact || !features) {
            alert('모든 필수 항목을 입력해주세요.');
            return;
        }

        var businessHours = {};
        $('.day-time-entry').each(function () {
            var day = $(this).find('label').text(); // 요일 가져오기
            var amTime = $(this).find('input[name$="_am"]').val(); // 오전 시간 가져오기
            var pmTime = $(this).find('input[name$="_pm"]').val(); // 오후 시간 가져오기
            businessHours[day] = amTime + ' - ' + pmTime; // 요일과 시간을 JSON 객체에 추가
        });

        var formData = new FormData();
        var imageFile = $('#imageInput')[0].files[0];

        if (!imageFile) {
            alert('이미지 파일을 선택해주세요.');
            return;
        }

        formData.append('imageFile', imageFile);
        formData.append('touristSpotName', touristSpotName);
        formData.append('address', address);
        formData.append('contact', contact);
        formData.append('features', features);
        formData.append('businessHours', JSON.stringify(businessHours)); // JSON 객체를 문자열로 변환하여 추가

        $.ajax({
            type: 'POST',
            url: '/admin/item',
            data: formData,
            processData: false,
            contentType: false,
            headers: {
                'X-XSRF-TOKEN': getCsrfToken()
            },
            success: function (response) {
                console.log('Form submitted successfully!');
                alert('저장되었습니다!');

                var savedTouristSpotName = $('#touristSpotName').val();
                var imageUrl = URL.createObjectURL(imageFile); // 이미지 URL 생성

                var newBlock = $('<div style="position:relative;" class="local_block"></div>');
                newBlock.append('<img style="height:150px;width:100%;object-fit:cover;" src="' + file:///C:/TravelGenius/item/blockData.imgUrl + '" alt="Tourist Spot Image">');
                newBlock.append('<p style="height:20px;background:black;color:white;font-size:20px;padding:0;margin:0;font-weight:bold;">' + savedTouristSpotName + '</p>');
                newBlock.append('<button style="position:absolute;left:0;top:0;" class="delete-btn">삭제</button>');
                $('.content_box').append(newBlock);

                saveLocalBlocks(); // 로컬 스토리지 업데이트
                $('#myModal').modal('hide'); // 모달 닫기
            },
            error: function (xhr, status, error) {
                console.error('Form submission failed:', error);
                alert('저장에 실패했습니다. 다시 시도해주세요.');
            }
        });
    });

    // 모달 열기 전에 데이터 채우기
    $('.content_box').on('click', '.local_block', function() {
        var imageUrl = $(this).find('img').attr('src');
        var touristSpotName = $(this).find('p').text();

        $('#modalImage').attr('src', imageUrl);
        $('#touristSpotName').val(touristSpotName);

        $('#myModal').modal('show');
    });

    // 동적으로 생성된 삭제 버튼에 이벤트 핸들러 추가
    $('.content_box').on('click', '.delete-btn', function() {
        $(this).closest('.local_block').remove();
        saveLocalBlocks(); // 삭제 후 로컬 스토리지 업데이트
    });

    function loadLocalBlocks() {
        var localBlocks = JSON.parse(localStorage.getItem('localBlocks')) || [];
        localBlocks.forEach(function(blockData) {
            // 이미지 URL이 undefined가 아닌 경우에만 블록 생성
            if (blockData.imageUrl !== undefined) {
                var newBlock = $('<div style="position:relative;" class="local_block"></div>');
                newBlock.append('<img style="height:150px;width:100%;object-fit:cover;" src="' + blockData.imageUrl + '" alt="Tourist Spot Image">');
                newBlock.append('<p style="height:20px;background:black;color:white;font-size:20px;padding:0;margin:0;font-weight:bold;">' + blockData.touristSpotName + '</p>');
                newBlock.append('<button style="position:absolute;left:0;top:0;" class="delete-btn">삭제</button>');
                $('.content_box').append(newBlock);
            }
        });
    }

    loadLocalBlocks(); // 페이지 로드 시 로컬 블록 로드

    function bindImg() {
        $("#imageInput").on("change", function () {
            var filename = $(this).val().split("\\").pop(); // Extract file name
            var fileExt = filename.split('.').pop().toLowerCase(); // Extract file extension

            var validExtensions = ["jpg", "jpeg", "png", "gif", "bmp"];
            if (!validExtensions.includes(fileExt)) {
                alert("Only JPG, JPEG, PNG, GIF, BMP files are allowed.");
                $(this).val(''); // Clear file input
                return;
            }

            $(this).prev(".input-group-text").html(filename);
        });
    }

    bindImg(); // Call the function to bind image input
});