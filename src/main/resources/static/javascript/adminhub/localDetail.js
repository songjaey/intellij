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
        var touristSpotName = $('#touristSpotName').val();
        var address = $('input[name="address"]').val(); // 수정: 주소 필드에 대한 선택자 수정
        var contact = $('input[name="contact"]').val(); // 수정: 연락처 필드에 대한 선택자 수정
        var businessHours = '';

        // 영업시간 수집
        $('.day-of-the-week').each(function() {
            var dayElement = $(this);
            var dayText = dayElement.text().trim(); // 요일의 텍스트 가져오기

            // 해당 요일의 AM, PM 입력 필드 찾기
            var amElement = dayElement.next().find('input.am-input');
            var pmElement = dayElement.next().find('input.pm-input');

            if (dayText) {
                if (amElement.length > 0 && pmElement.length > 0) {
                    var am = amElement.val() ? amElement.val().trim() : '';
                    var pm = pmElement.val() ? pmElement.val().trim() : '';

                    businessHours += `${dayText} AM: ${am}, PM: ${pm}\n`;
                } else {
                    console.error('AM 또는 PM 요소를 찾을 수 없습니다.');
                }
            } else {
                console.error('요일 요소의 텍스트가 비어 있습니다.');
            }
        });

        if (!touristSpotName) {
            alert('관광지명을 입력해주세요.');
            return;
        }

        var formData = new FormData();
        formData.append('touristSpotName', touristSpotName);
        formData.append('address', address);
        formData.append('contact', contact);
        formData.append('features', $('#InputFeatures').val());
        formData.append('businessHours', businessHours.trim());

        var imageFile = $('#imageInput')[0].files[0];
        if (imageFile) {
            formData.append('imageFile', imageFile);
        }

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
                window.location.reload();
            },
            error: function (xhr, status, error) {
                console.error('Form submission failed:', error);
                alert('저장에 실패했습니다.');
            }
        });
    });
});