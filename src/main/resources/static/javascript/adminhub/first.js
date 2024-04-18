$(document).ready(function() {

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

    // 페이지 로드 시 서버로부터 모든 국가와 지역 정보를 요청하여 블록 생성
    $.ajax({
        type: "GET",
        url: "/admin/getAllLocals",
        success: function(data) {
            data.forEach(function(local) {
                var newLocalBlock = '<div class="local_block">' +
                    '<p class="country">' + local.country + '</p>' +
                    '<p class="local">(' + local.local + ')</p>' +
                    '<button class="delete">삭제</button>' +
                    '</div>' ;


                var $newLocalElement = $(newLocalBlock);
                $('.content_box').append($newLocalElement);

                $newLocalElement.on('click', function() {
                    var country = $(this).find('.country').text().trim();
                    var local = $(this).find('.local').text().trim().slice(1, -1);

                    window.location.href = '/admin/localDetail?country=' + encodeURIComponent(country) + '&local=' + encodeURIComponent(local)+ '&content=명소';
                });
            });
        },
        error: function(xhr, status, error) {
            console.error("Failed to fetch data:", error);
        }
    });

// 삭제 버튼 클릭 이벤트 핸들러
    $(document).on('click', '.delete', function() {
        var $localBlock = $(this).closest('.local_block'); // 클릭된 삭제 버튼의 상위 .local_block 요소 선택

        // 삭제할 국가와 지역 정보 가져오기
        var country = $localBlock.find('.country').text().trim();
        var local = $localBlock.find('.local').text().trim().slice(1, -1);

        // CSRF 토큰 가져오기
        var csrfToken = getCsrfToken();

        // 서버에 delete 요청 보내기
        $.ajax({
            type: 'DELETE',
            url: '/admin/deleteLocal',
            data: {
                country: country,
                local: local
            },
            headers: {
                'X-XSRF-TOKEN': csrfToken // 헤더에 CSRF 토큰 추가
            },
            success: function(response) {
                // 성공한 경우 해당 지역 블록을 화면에서 제거
                $localBlock.remove();
                alert('삭제되었습니다.');
            },
            error: function(xhr, status, error) {
                // 오류 발생 시 처리
                console.error('오류 발생:', error);
                alert('오류가 발생했습니다: ' + error);
            }
        });
    });



    // 모달 폼에서 데이터를 추가하고 저장할 경우
    $('#modal_click').click(function() {
        //        event.preventDefault(); // 기본 동작 방지

        var country = $('#countryInput').val();
        var local = $('#localInput').val();
        var csrfToken = getCsrfToken(); // CSRF 토큰 가져오기

        // 서버에 데이터를 전송하여 저장
        $.ajax({
            type: 'POST',
            url: '/admin/saveLocal',
            data: {
                country: country,
                local: local
            },
            headers: {
                'X-XSRF-TOKEN': csrfToken // 헤더에 CSRF 토큰 추가
            },
            success: function (response) {
                if (response === "alreadyExists") {
                    alert('이미 존재하는 지역입니다.');
                } else {
                    alert('저장되었습니다!');

                    // 저장 후, 해당 블록을 다시 불러와서 추가
                    var newLocalBlock = '<div class="local_block">' +
                    '<p class="country">' + local.country + '</p>' +
                    '<p class="local">(' + local.local + ')</p>' +
                    '<button class="delete">삭제</button>' +
                    '</div>' ;


                    // Append new local block to content box
                    var $newLocalElement = $(newLocalBlock);
                    $('.content_box').append($newLocalElement);

                    // 입력 필드 초기화
                    $('#countryInput').val('');
                    $('#localInput').val('');
                }
            },
            error: function (xhr, status, error) {
                // 오류 발생 시 처리
                console.error('오류 발생:', error);
                alert('오류가 발생했습니다: ' + error);
            }
        });

    });


});