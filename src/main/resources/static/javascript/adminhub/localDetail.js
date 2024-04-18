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
    $('#saveModalBtn').click(function () {
            var businessHours = {};
            $('.day-time-entry').each(function () {
                var day = $(this).find('label').text(); // 요일 가져오기
                var amTime = $(this).find('input[name$="_am"]').val(); // 오전 시간 가져오기
                var pmTime = $(this).find('input[name$="_pm"]').val(); // 오후 시간 가져오기
                businessHours[day] = amTime + ' - ' + pmTime; // 요일과 시간을 JSON 객체에 추가
            });

            var formData = new FormData();

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
            saveLocalBlocks(); // 로컬 스토리지 업데이트
            },
            error: function (xhr, status, error) {
                console.error('Form submission failed:', error);
                alert('저장에 실패했습니다. 다시 시도해주세요.');
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

    bindImg(); // Call the function to bind image input
});