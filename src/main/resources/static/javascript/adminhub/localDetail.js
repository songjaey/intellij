$(document).ready(function () {

   // CSRF 토큰 가져오기
    function getCsrfToken() {
        const cookies = document.cookie.split(';').map(cookie => cookie.trim());
        const csrfCookie = cookies.find(cookie => cookie.startsWith('XSRF-TOKEN='));

        if (csrfCookie) {
            return csrfCookie.split('=')[1];
        } else {
            return null; // CSRF 토큰을 찾지 못한 경우
        }
    }
     $('#saveModalBtn').on('click', function() {
            var formData = new FormData();
            var businessHours = {}; // 요일과 시간을 저장할 객체

            // 각 요일과 시간 값을 수집하여 객체에 저장
            $('.day-time-entry').each(function() {
                var dayOfWeek = $(this).find('label').text(); // 요일
                var amValue = $(this).find('input[name$="_am"]').val(); // 오전 시간
                var pmValue = $(this).find('input[name$="_pm"]').val(); // 오후 시간

                businessHours[dayOfWeek] = {
                    am: amValue,
                    pm: pmValue
                };
            });

            // FormData에 요일과 시간 데이터 추가
            formData.append('businessHours', businessHours);

            // 기타 필요한 데이터도 FormData에 추가 가능

            // AJAX 요청 보내기
            $.ajax({
                url: '/admin/item',
                method: 'POST',
                processData: false,
                contentType: false,
                data: formData,
                success: function(response) {
                    // 성공 시 처리
                    console.log(response);
                },
                error: function(xhr, status, error) {
                    // 실패 시 처리
                    console.error(xhr.responseText);
                }
            });
        });

     $('.delete-btn').on('click', function(event) {
         event.preventDefault(); // 기본 동작 중지

         if (!confirm('정말 삭제하시겠습니까?')) {
             return; // 사용자가 취소하면 동작 중지
         }

         // 해당 요소의 부모인 .local_block에서 data-item-id 속성 값을 가져와서 itemId로 사용
         var itemId = $(this).closest('.local_block').attr('data-item-id');


         if (itemId === undefined) {
             console.error('Item ID is undefined');
             return;
         }

         // AJAX 요청을 이용하여 아이템 삭제
         $.ajax({
             url: '/admin/item/delete/' + itemId,
             type: 'DELETE',
             headers: {
                     'X-XSRF-TOKEN': getCsrfToken()
                 },
             success: function(response) {
                 console.log('Item deleted successfully:', response);
                 // 페이지 새로고침 또는 UI 업데이트 등
                 location.reload(); // 예시: 페이지 새로고침
             },
             error: function(xhr, status, error) {
                 console.error('Error deleting item:', xhr.responseText);
                 // 에러 처리
             }
         });
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

    // 현재 URL에서 파라미터 값을 추출하는 함수
    function getUrlParameter(name) {
        name = name.replace(/[[]/, '\\[').replace(/[\]]/, '\\]');
        var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
        var results = regex.exec(location.search);
        return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
    }

    var urlParams = new URLSearchParams(window.location.search);
    var contentValue = urlParams.get('content');
    var contentTypeInput = document.getElementById('contentType');
    if (contentTypeInput) {
        contentTypeInput.value = contentValue;
    }



    bindImg(); // Call the function to bind image input


});