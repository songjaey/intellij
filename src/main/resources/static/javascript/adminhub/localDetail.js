$(document).ready(function () {
    /* businessHours 맵 데이터를 폼에 전달하기 위해 input 필드 생성 */
        var businessHours = /*[[${adminItemDto.businessHours}]]*/{};

        // 각 요일에 대해 am/pm 값을 적절한 input 필드에 설정
        for (var day in businessHours) {
            if (businessHours.hasOwnProperty(day)) {
                var am = businessHours[day].split(' - ')[0]; // 오전 시간
                var pm = businessHours[day].split(' - ')[1]; // 오후 시간

                // 해당 요일의 오전/오후 input 필드에 값을 설정
                $('input[name="' + day + '_am"]').val(am);
                $('input[name="' + day + '_pm"]').val(pm);
            }
        }



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